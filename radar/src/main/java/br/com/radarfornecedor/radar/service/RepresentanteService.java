package br.com.radarfornecedor.radar.service;

import br.com.radarfornecedor.radar.model.Representante;
import br.com.radarfornecedor.radar.model.Usuario;
import br.com.radarfornecedor.radar.model.TipoUsuario;
import br.com.radarfornecedor.radar.model.Fornecedor;
import br.com.radarfornecedor.radar.repository.RepresentanteRepository;
import br.com.radarfornecedor.radar.repository.FornecedorRepository;
import javax.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
                throw new RuntimeException("O CNPJ do fornecedor e obrigatorio.");
            }
            String cnpjFornecedorClean = representante.getCnpjFornecedor().replaceAll("\\D", "");
            Optional<Fornecedor> fornecedorOpt = fornecedorRepository.findByCnpj(cnpjFornecedorClean);
            if (fornecedorOpt.isEmpty()) {
                throw new RuntimeException("O CNPJ do fornecedor informado nao existe.");
            }
            Fornecedor fornecedor = fornecedorOpt.get();
            representante.setCnpjFornecedor(fornecedor.getCnpj());
            representante.setCodEmpresa(fornecedor.getId());
        }
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
                if (fornecedorOpt.isEmpty()) {
                    fornecedorOpt = fornecedorRepository.findAll().stream().findFirst();
                }
                if (fornecedorOpt.isPresent()) {
                    Fornecedor fornecedor = fornecedorOpt.get();
                    return representanteRepository.findByCodEmpresaAndCnpjFornecedor(fornecedor.getId(), fornecedor.getCnpj());
                }
            } else if (usuario.getTipo() == TipoUsuario.REPRESENTANTE) {
                String cnpjClean = usuario.getUsername().replaceAll("\\D", "");
                Optional<Representante> repOpt = representanteRepository.findAll().stream()
                        .filter(r -> r.getCnpj() != null && r.getCnpj().replaceAll("\\D", "").equals(cnpjClean))
                        .findFirst();
                if (repOpt.isEmpty()) {
                    repOpt = representanteRepository.findAll().stream().findFirst();
                }
                return repOpt.map(List::of).orElse(List.of());
            } else if (usuario.getTipo() == TipoUsuario.CLIENTE) {
                return representanteRepository.findAll().stream()
                        .filter(rep -> {
                            if (rep.getCnpjFornecedor() == null) return false;
                            Optional<Fornecedor> fornOpt = fornecedorRepository.findByCnpj(rep.getCnpjFornecedor().replaceAll("\\D", ""));
                            return fornOpt.isPresent() && Boolean.TRUE.equals(fornOpt.get().getAceitaCpf());
                        })
                        .collect(Collectors.toList());
            }
        }
        return representanteRepository.findAll();
    }

    public Optional<Representante> buscarPorId(Long id) {
        return representanteRepository.findById(id);
    }

    public Representante atualizar(Long id, Representante dadosNovos, HttpSession session) {
        Representante existente = representanteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Representante nao encontrado: " + id));

        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario != null && usuario.getTipo() == TipoUsuario.FORNECEDOR) {
            String cnpjClean = usuario.getUsername().replaceAll("\\D", "");
            Optional<Fornecedor> fornecedorOpt = fornecedorRepository.findByCnpj(cnpjClean);
            if (fornecedorOpt.isEmpty()) {
                fornecedorOpt = fornecedorRepository.findAll().stream().findFirst();
            }
            if (fornecedorOpt.isPresent()) {
                Fornecedor fornecedor = fornecedorOpt.get();
                if (existente.getCodEmpresa() != null && !existente.getCodEmpresa().equals(fornecedor.getId())) {
                    throw new RuntimeException("Sem permissao para editar representantes de outro fornecedor.");
                }
                dadosNovos.setCnpjFornecedor(fornecedor.getCnpj());
                dadosNovos.setCodEmpresa(fornecedor.getId());
            }
        } else {
            if (dadosNovos.getCnpjFornecedor() == null || dadosNovos.getCnpjFornecedor().isBlank()) {
                throw new RuntimeException("O CNPJ do fornecedor e obrigatorio.");
            }
            String cnpjFornecedorClean = dadosNovos.getCnpjFornecedor().replaceAll("\\D", "");
            Optional<Fornecedor> fornecedorOpt = fornecedorRepository.findByCnpj(cnpjFornecedorClean);
            if (fornecedorOpt.isEmpty()) {
                throw new RuntimeException("O CNPJ do fornecedor informado nao existe.");
            }
            Fornecedor fornecedor = fornecedorOpt.get();
            dadosNovos.setCnpjFornecedor(fornecedor.getCnpj());
            dadosNovos.setCodEmpresa(fornecedor.getId());
        }

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
        existente.setContato(dadosNovos.getContato());
        existente.setEmail(dadosNovos.getEmail());
        existente.setCep(dadosNovos.getCep());
        existente.setLogradouro(dadosNovos.getLogradouro());
        existente.setNumero(dadosNovos.getNumero());
        existente.setComplemento(dadosNovos.getComplemento());
        existente.setBairro(dadosNovos.getBairro());
        existente.setCidade(dadosNovos.getCidade());
        existente.setEstado(dadosNovos.getEstado());
        existente.setLatitude(dadosNovos.getLatitude());
        existente.setLongitude(dadosNovos.getLongitude());
        
        // Atualizar categoria e atividade
        if (dadosNovos.getCategoria() != null) {
            existente.setCategoria(dadosNovos.getCategoria());
        }
        if (dadosNovos.getAtividade() != null) {
            existente.setAtividade(dadosNovos.getAtividade());
        }

        return representanteRepository.save(existente);
    }

    public void excluir(Long id, HttpSession session) {
        Representante existente = representanteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Representante nao encontrado: " + id));

        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario != null && usuario.getTipo() == TipoUsuario.FORNECEDOR) {
            String cnpjClean = usuario.getUsername().replaceAll("\\D", "");
            Optional<Fornecedor> fornecedorOpt = fornecedorRepository.findByCnpj(cnpjClean);
            if (fornecedorOpt.isEmpty()) {
                fornecedorOpt = fornecedorRepository.findAll().stream().findFirst();
            }
            if (fornecedorOpt.isPresent()) {
                Fornecedor fornecedor = fornecedorOpt.get();
                if (existente.getCodEmpresa() != null && !existente.getCodEmpresa().equals(fornecedor.getId())) {
                    throw new RuntimeException("Sem permissao para remover representantes de outro fornecedor.");
                }
            }
        }

        existente.setStatus("INATIVO");
        representanteRepository.save(existente);
    }
}
