package br.com.radarfornecedor.radar.service;

import br.com.radarfornecedor.radar.model.Fornecedor;
import br.com.radarfornecedor.radar.model.Representante;
import br.com.radarfornecedor.radar.repository.FornecedorRepository;
import br.com.radarfornecedor.radar.repository.RepresentanteRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FornecedorService {

    private final FornecedorRepository fornecedorRepository;
    private final RepresentanteRepository representanteRepository;

    public FornecedorService(FornecedorRepository fornecedorRepository, RepresentanteRepository representanteRepository) {
        this.fornecedorRepository = fornecedorRepository;
        this.representanteRepository = representanteRepository;
    }

    public Fornecedor cadastrar(Fornecedor fornecedor) {
        Optional<Fornecedor> existente = fornecedorRepository.findByCnpj(fornecedor.getCnpj());
        if (existente.isPresent()) {
            throw new RuntimeException("Já existe um fornecedor cadastrado com este CNPJ.");
        }

        if (fornecedor.getStatus() == null) {
            fornecedor.setStatus("EM_ANALISE");
        }
        if (fornecedor.getPontuacaoRisco() == null) {
            fornecedor.setPontuacaoRisco(0.0);
        }

        return fornecedorRepository.save(fornecedor);
    }

    public List<Fornecedor> listarTodos() {
        return fornecedorRepository.findAll();
    }

    public List<Fornecedor> listarTodos(jakarta.servlet.http.HttpSession session) {
        br.com.radarfornecedor.radar.model.Usuario usuario = (br.com.radarfornecedor.radar.model.Usuario) session.getAttribute("usuario");
        if (usuario != null) {
            if (usuario.getTipo() == br.com.radarfornecedor.radar.model.TipoUsuario.FORNECEDOR) {
                String cnpjClean = usuario.getUsername().replaceAll("\\D", "");
                Optional<Fornecedor> fornecedorOpt = fornecedorRepository.findByCnpj(cnpjClean);
                
                // Fallback for default 'fornecedor' login
                if (fornecedorOpt.isEmpty()) {
                    fornecedorOpt = fornecedorRepository.findAll().stream().findFirst();
                }

                if (fornecedorOpt.isPresent()) {
                    return List.of(fornecedorOpt.get());
                } else {
                    return List.of();
                }
            } else if (usuario.getTipo() == br.com.radarfornecedor.radar.model.TipoUsuario.REPRESENTANTE) {
                String cnpjClean = usuario.getUsername().replaceAll("\\D", "");
                // Find Representative matching CNPJ
                Optional<Representante> repOpt = representanteRepository.findAll().stream()
                        .filter(r -> r.getCnpj() != null && r.getCnpj().replaceAll("\\D", "").equals(cnpjClean))
                        .findFirst();
                
                // Fallback for default 'representante' login
                if (repOpt.isEmpty()) {
                    repOpt = representanteRepository.findAll().stream().findFirst();
                }

                if (repOpt.isPresent()) {
                    Representante rep = repOpt.get();
                    String cnpjFornecedor = rep.getCnpjFornecedor();
                    if (cnpjFornecedor != null) {
                        Optional<Fornecedor> fornecedorOpt = fornecedorRepository.findByCnpj(cnpjFornecedor.replaceAll("\\D", ""));
                        if (fornecedorOpt.isPresent()) {
                            return List.of(fornecedorOpt.get());
                        }
                    }
                    // Fallback to first Supplier if represented supplier is not found or null
                    return fornecedorRepository.findAll().stream().findFirst().map(List::of).orElse(List.of());
                } else {
                    return List.of();
                }
            } else if (usuario.getTipo() == br.com.radarfornecedor.radar.model.TipoUsuario.CLIENTE) {
                // Return only suppliers who accept CPF
                return fornecedorRepository.findAll().stream()
                        .filter(f -> Boolean.TRUE.equals(f.getAceitaCpf()))
                        .toList();
            }
        }
        return fornecedorRepository.findAll();
    }

    public Optional<Fornecedor> buscarPorId(Long id) {
        return fornecedorRepository.findById(id);
    }

    public Optional<Fornecedor> buscarPorCnpj(String cnpj) {
        return fornecedorRepository.findByCnpj(cnpj);
    }

    public Fornecedor atualizar(Long id, Fornecedor dadosNovos, jakarta.servlet.http.HttpSession session) {
        Fornecedor existente = fornecedorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Fornecedor não encontrado com o ID: " + id));

        br.com.radarfornecedor.radar.model.Usuario usuario = (br.com.radarfornecedor.radar.model.Usuario) session.getAttribute("usuario");
        if (usuario != null && usuario.getTipo() == br.com.radarfornecedor.radar.model.TipoUsuario.FORNECEDOR) {
            String cnpjClean = usuario.getUsername().replaceAll("\\D", "");
            Optional<Fornecedor> fornecedorOpt = fornecedorRepository.findByCnpj(cnpjClean);
            
            // Fallback for default 'fornecedor' login
            if (fornecedorOpt.isEmpty()) {
                fornecedorOpt = fornecedorRepository.findAll().stream().findFirst();
            }

            if (fornecedorOpt.isPresent()) {
                Fornecedor fornecedor = fornecedorOpt.get();
                if (!fornecedor.getId().equals(id)) {
                    throw new RuntimeException("Você só tem permissão para editar o seu próprio cadastro de fornecedor.");
                }
            }
        }

        Optional<Fornecedor> comMesmoCnpj = fornecedorRepository.findByCnpj(dadosNovos.getCnpj());
        if (comMesmoCnpj.isPresent() && !comMesmoCnpj.get().getId().equals(id)) {
            throw new RuntimeException("Já existe outro fornecedor cadastrado com este CNPJ.");
        }

        existente.setNome(dadosNovos.getNome());
        existente.setCnpj(dadosNovos.getCnpj());
        existente.setStatus(dadosNovos.getStatus());
        existente.setPontuacaoRisco(dadosNovos.getPontuacaoRisco());
        
        // Atualizar campos de endereço
        existente.setCep(dadosNovos.getCep());
        existente.setLogradouro(dadosNovos.getLogradouro());
        existente.setNumero(dadosNovos.getNumero());
        existente.setComplemento(dadosNovos.getComplemento());
        existente.setBairro(dadosNovos.getBairro());
        existente.setCidade(dadosNovos.getCidade());
        existente.setEstado(dadosNovos.getEstado());
        existente.setLatitude(dadosNovos.getLatitude());
        existente.setLongitude(dadosNovos.getLongitude());
        existente.setCodCidade(dadosNovos.getCodCidade());

        return fornecedorRepository.save(existente);
    }

    public void excluir(Long id) {
        Fornecedor existente = fornecedorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Fornecedor não encontrado com o ID: " + id));
        existente.setStatus("INATIVO");
        fornecedorRepository.save(existente);
    }
}
