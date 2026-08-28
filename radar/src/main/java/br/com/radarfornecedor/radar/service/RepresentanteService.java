package br.com.radarfornecedor.radar.service;

import br.com.radarfornecedor.radar.model.Representante;
import br.com.radarfornecedor.radar.model.Usuario;
import br.com.radarfornecedor.radar.model.TipoUsuario;
import br.com.radarfornecedor.radar.model.Fornecedor;
import br.com.radarfornecedor.radar.repository.RepresentanteRepository;
import br.com.radarfornecedor.radar.repository.FornecedorRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RepresentanteService {

    private final RepresentanteRepository representanteRepository;
    private final FornecedorRepository fornecedorRepository;

    public RepresentanteService(RepresentanteRepository representanteRepository, FornecedorRepository fornecedorRepository) {
        this.representanteRepository = representanteRepository;
        this.fornecedorRepository = fornecedorRepository;
    }

    public Representante cadastrar(Representante representante, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario != null && usuario.getTipo() == TipoUsuario.FORNECEDOR) {
            String cnpjClean = usuario.getUsername().replaceAll("\\D", "");
            Optional<Fornecedor> fornecedorOpt = fornecedorRepository.findByCnpj(cnpjClean);
            
            // Fallback for default 'fornecedor' login
            if (fornecedorOpt.isEmpty()) {
                fornecedorOpt = fornecedorRepository.findAll().stream().findFirst();
            }

            if (fornecedorOpt.isPresent()) {
                Fornecedor fornecedor = fornecedorOpt.get();
                representante.setCnpjFornecedor(fornecedor.getCnpj());
                representante.setCodEmpresa(fornecedor.getId());
            }
        } else {
            if (representante.getCnpjFornecedor() == null || representante.getCnpjFornecedor().isBlank()) {
                throw new RuntimeException("O CNPJ do fornecedor é obrigatório.");
            }
            String cnpjFornecedorClean = representante.getCnpjFornecedor().replaceAll("\\D", "");
            Optional<Fornecedor> fornecedorOpt = fornecedorRepository.findByCnpj(cnpjFornecedorClean);
            if (fornecedorOpt.isEmpty()) {
                throw new RuntimeException("O CNPJ do fornecedor informado não existe. É necessário que o mesmo seja cadastrado pela empresa fornecedora.");
            }
            Fornecedor fornecedor = fornecedorOpt.get();
            representante.setCnpjFornecedor(fornecedor.getCnpj());
            representante.setCodEmpresa(fornecedor.getId());
        }

        // Standardize CNPJs to only digits before saving
        if (representante.getCnpj() != null) {
            representante.setCnpj(representante.getCnpj().replaceAll("\\D", ""));
        }
        if (representante.getCnpjFornecedor() != null) {
            representante.setCnpjFornecedor(representante.getCnpjFornecedor().replaceAll("\\D", ""));
        }

        return representanteRepository.save(representante);
    }

    public List<Representante> listarTodos(HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario != null) {
            if (usuario.getTipo() == TipoUsuario.FORNECEDOR) {
                String cnpjClean = usuario.getUsername().replaceAll("\\D", "");
                Optional<Fornecedor> fornecedorOpt = fornecedorRepository.findByCnpj(cnpjClean);
                
                // Fallback for default 'fornecedor' login
                if (fornecedorOpt.isEmpty()) {
                    fornecedorOpt = fornecedorRepository.findAll().stream().findFirst();
                }

                if (fornecedorOpt.isPresent()) {
                    Fornecedor fornecedor = fornecedorOpt.get();
                    // Filter by CodEmpresa (ID) + CNPJ de cadastro do Fornecedor
                    return representanteRepository.findByCodEmpresaAndCnpjFornecedor(fornecedor.getId(), fornecedor.getCnpj());
                }
            } else if (usuario.getTipo() == TipoUsuario.REPRESENTANTE) {
                String cnpjClean = usuario.getUsername().replaceAll("\\D", "");
                // Find Representative with matching CNPJ
                Optional<Representante> repOpt = representanteRepository.findAll().stream()
                        .filter(r -> r.getCnpj() != null && r.getCnpj().replaceAll("\\D", "").equals(cnpjClean))
                        .findFirst();
                
                // Fallback for default 'representante' login
                if (repOpt.isEmpty()) {
                    repOpt = representanteRepository.findAll().stream().findFirst();
                }

                if (repOpt.isPresent()) {
                    return List.of(repOpt.get());
                } else {
                    return List.of();
                }
            } else if (usuario.getTipo() == TipoUsuario.CLIENTE) {
                // Return only representatives whose supplier accepts CPF (aceitaCpf == true)
                return representanteRepository.findAll().stream()
                        .filter(rep -> {
                            if (rep.getCnpjFornecedor() == null) return false;
                            Optional<Fornecedor> fornOpt = fornecedorRepository.findByCnpj(rep.getCnpjFornecedor().replaceAll("\\D", ""));
                            return fornOpt.isPresent() && Boolean.TRUE.equals(fornOpt.get().getAceitaCpf());
                        })
                        .toList();
            }
        }
        return representanteRepository.findAll();
    }

    public Optional<Representante> buscarPorId(Long id) {
        return representanteRepository.findById(id);
    }

    public Representante atualizar(Long id, Representante dadosNovos, HttpSession session) {
        Representante existente = representanteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Representante não encontrado com o ID: " + id));

        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario != null && usuario.getTipo() == TipoUsuario.FORNECEDOR) {
            String cnpjClean = usuario.getUsername().replaceAll("\\D", "");
            Optional<Fornecedor> fornecedorOpt = fornecedorRepository.findByCnpj(cnpjClean);
            
            // Fallback for default 'fornecedor' login
            if (fornecedorOpt.isEmpty()) {
                fornecedorOpt = fornecedorRepository.findAll().stream().findFirst();
            }

            if (fornecedorOpt.isPresent()) {
                Fornecedor fornecedor = fornecedorOpt.get();
                // Check if existing representative belongs to other supplier
                if (existente.getCodEmpresa() != null && !existente.getCodEmpresa().equals(fornecedor.getId())) {
                    throw new RuntimeException("Você não tem permissão para editar representantes de outro fornecedor.");
                }
                // Force link to stay to own supplier
                dadosNovos.setCnpjFornecedor(fornecedor.getCnpj());
                dadosNovos.setCodEmpresa(fornecedor.getId());
            }
        } else {
            if (dadosNovos.getCnpjFornecedor() == null || dadosNovos.getCnpjFornecedor().isBlank()) {
                throw new RuntimeException("O CNPJ do fornecedor é obrigatório.");
            }
            String cnpjFornecedorClean = dadosNovos.getCnpjFornecedor().replaceAll("\\D", "");
            Optional<Fornecedor> fornecedorOpt = fornecedorRepository.findByCnpj(cnpjFornecedorClean);
            if (fornecedorOpt.isEmpty()) {
                throw new RuntimeException("O CNPJ do fornecedor informado não existe. É necessário que o mesmo seja cadastrado pela empresa fornecedora.");
            }
            Fornecedor fornecedor = fornecedorOpt.get();
            dadosNovos.setCnpjFornecedor(fornecedor.getCnpj());
            dadosNovos.setCodEmpresa(fornecedor.getId());
        }

        // Standardize CNPJs to only digits before saving
        if (dadosNovos.getCnpj() != null) {
            dadosNovos.setCnpj(dadosNovos.getCnpj().replaceAll("\\D", ""));
        }
        if (dadosNovos.getCnpjFornecedor() != null) {
            dadosNovos.setCnpjFornecedor(dadosNovos.getCnpjFornecedor().replaceAll("\\D", ""));
        }

        existente.setNome(dadosNovos.getNome());
        existente.setStatus(dadosNovos.getStatus());
        existente.setCnpj(dadosNovos.getCnpj());
        existente.setCnpjFornecedor(dadosNovos.getCnpjFornecedor());
        existente.setCodEmpresa(dadosNovos.getCodEmpresa());

        return representanteRepository.save(existente);
    }

    public void excluir(Long id, HttpSession session) {
        Representante existente = representanteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Representante não encontrado com o ID: " + id));

        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario != null && usuario.getTipo() == TipoUsuario.FORNECEDOR) {
            String cnpjClean = usuario.getUsername().replaceAll("\\D", "");
            Optional<Fornecedor> fornecedorOpt = fornecedorRepository.findByCnpj(cnpjClean);
            
            // Fallback for default 'fornecedor' login
            if (fornecedorOpt.isEmpty()) {
                fornecedorOpt = fornecedorRepository.findAll().stream().findFirst();
            }

            if (fornecedorOpt.isPresent()) {
                Fornecedor fornecedor = fornecedorOpt.get();
                // Check if existing representative belongs to other supplier
                if (existente.getCodEmpresa() != null && !existente.getCodEmpresa().equals(fornecedor.getId())) {
                    throw new RuntimeException("Você não tem permissão para remover representantes de outro fornecedor.");
                }
            }
        }

        existente.setStatus("INATIVO");
        representanteRepository.save(existente);
    }
}
