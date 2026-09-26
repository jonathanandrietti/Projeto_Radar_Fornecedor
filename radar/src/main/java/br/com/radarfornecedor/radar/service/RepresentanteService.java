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
            // ✅ ADMIN, MANUTENCAO, EDICAO: Vê tudo
            if (usuario.getTipo() == TipoUsuario.ADMIN ||
                usuario.getTipo() == TipoUsuario.MANUTENCAO ||
                usuario.getTipo() == TipoUsuario.EDICAO) {
                return representanteRepository.findAll();
            }
            
            // ✅ PADRAO ou RESTRITO com perfil FORNECEDOR: Vê representantes vinculados a ele
            if (Boolean.TRUE.equals(usuario.getFornecedor())) {
                String cnpjOuCpf = usuario.getCnpjOuCpf();
                if (cnpjOuCpf != null) {
                    String cnpjClean = cnpjOuCpf.replaceAll("\\D", "");
                    Optional<Fornecedor> fornecedorOpt = fornecedorRepository.findByCnpj(cnpjClean);
                    if (fornecedorOpt.isPresent()) {
                        Fornecedor fornecedor = fornecedorOpt.get();
                        return representanteRepository.findByCodEmpresaAndCnpjFornecedor(fornecedor.getId(), fornecedor.getCnpj());
                    }
                }
                return List.of();
            }
            
            // ✅ PADRAO ou RESTRITO com perfil REPRESENTANTE: Vê apenas seu próprio cadastro
            if (Boolean.TRUE.equals(usuario.getRepresentante())) {
                String cnpjOuCpf = usuario.getCnpjOuCpf();
                if (cnpjOuCpf != null) {
                    final String cnpjClean = cnpjOuCpf.replaceAll("\\D", "");
                    Optional<Representante> repOpt = representanteRepository.findAll().stream()
                            .filter(r -> r.getCnpj() != null && r.getCnpj().replaceAll("\\D", "").equals(cnpjClean))
                            .findFirst();
                    return repOpt.map(List::of).orElse(List.of());
                }
                return List.of();
            }
            
            // ✅ PADRAO ou RESTRITO com perfil CLIENTE: Vê representantes de fornecedores que aceitam CPF
            if (Boolean.TRUE.equals(usuario.getCliente())) {
                return representanteRepository.findAll().stream()
                        .filter(rep -> {
                            if (rep.getCnpjFornecedor() == null) return false;
                            Optional<Fornecedor> fornOpt = fornecedorRepository.findByCnpj(rep.getCnpjFornecedor().replaceAll("\\D", ""));
                            return fornOpt.isPresent() && Boolean.TRUE.equals(fornOpt.get().getAceitaCpf());
                        })
                        .collect(Collectors.toList());
            }
        }
        return List.of();
    }

    public Optional<Representante> buscarPorId(Long id) {
        return representanteRepository.findById(id);
    }

    public Representante atualizar(Long id, Representante dadosNovos, HttpSession session) {
        Representante existente = representanteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Representante nao encontrado: " + id));

        Usuario usuario = (Usuario) session.getAttribute("usuario");
        
        if (usuario != null) {
            // ✅ ADMIN, MANUTENCAO, EDICAO: Podem editar qualquer representante
            if (usuario.getTipo() == TipoUsuario.ADMIN ||
                usuario.getTipo() == TipoUsuario.MANUTENCAO ||
                usuario.getTipo() == TipoUsuario.EDICAO) {
                // Sem restrições
            }
            // ✅ RESTRITO: Não pode editar nada
            else if (usuario.getTipo() == TipoUsuario.RESTRITO) {
                throw new RuntimeException("Você não tem permissão para editar cadastros.");
            }
            // ✅ PADRAO com perfil FORNECEDOR: Pode editar representantes vinculados a ele
            else if (Boolean.TRUE.equals(usuario.getFornecedor())) {
                String cnpjOuCpf = usuario.getCnpjOuCpf();
                if (cnpjOuCpf != null) {
                    String cnpjClean = cnpjOuCpf.replaceAll("\\D", "");
                    Optional<Fornecedor> fornecedorOpt = fornecedorRepository.findByCnpj(cnpjClean);
                    
                    if (fornecedorOpt.isPresent()) {
                        Fornecedor fornecedor = fornecedorOpt.get();
                        if (existente.getCodEmpresa() != null && !existente.getCodEmpresa().equals(fornecedor.getId())) {
                            throw new RuntimeException("Você só pode editar representantes vinculados à sua empresa.");
                        }
                        // Força os dados do fornecedor
                        dadosNovos.setCnpjFornecedor(fornecedor.getCnpj());
                        dadosNovos.setCodEmpresa(fornecedor.getId());
                        
                        // ✅ PADRAO não pode alterar o STATUS
                        if (usuario.getTipo() == TipoUsuario.PADRAO) {
                            dadosNovos.setStatus(existente.getStatus());
                        }
                    } else {
                        throw new RuntimeException("Fornecedor não encontrado.");
                    }
                } else {
                    throw new RuntimeException("Usuário sem CNPJ vinculado.");
                }
            }
            // ✅ PADRAO com perfil REPRESENTANTE: Pode editar apenas seu próprio cadastro
            else if (Boolean.TRUE.equals(usuario.getRepresentante())) {
                String cnpjOuCpf = usuario.getCnpjOuCpf();
                if (cnpjOuCpf != null) {
                    String cnpjClean = cnpjOuCpf.replaceAll("\\D", "");
                    Optional<Representante> repOpt = representanteRepository.findAll().stream()
                            .filter(r -> r.getCnpj() != null && r.getCnpj().replaceAll("\\D", "").equals(cnpjClean))
                            .findFirst();

                    if (repOpt.isPresent()) {
                        Representante rep = repOpt.get();
                        if (!rep.getId().equals(id)) {
                            throw new RuntimeException("Você só tem permissão para editar o seu próprio cadastro.");
                        }
                        // ✅ PADRAO não pode alterar o STATUS
                        if (usuario.getTipo() == TipoUsuario.PADRAO) {
                            dadosNovos.setStatus(existente.getStatus());
                        }
                    } else {
                        throw new RuntimeException("Você não tem permissão para editar este cadastro.");
                    }
                } else {
                    throw new RuntimeException("Usuário sem CNPJ vinculado.");
                }
            } else {
                throw new RuntimeException("Você não tem permissão para editar representantes.");
            }
            
            // Validação padrão para ADMIN/MANUTENCAO/EDICAO (quando não é fornecedor)
            if (!Boolean.TRUE.equals(usuario.getFornecedor()) && 
                (usuario.getTipo() == TipoUsuario.ADMIN || 
                 usuario.getTipo() == TipoUsuario.MANUTENCAO || 
                 usuario.getTipo() == TipoUsuario.EDICAO)) {
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
