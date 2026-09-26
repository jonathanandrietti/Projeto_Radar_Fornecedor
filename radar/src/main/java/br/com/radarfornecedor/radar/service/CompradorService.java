package br.com.radarfornecedor.radar.service;

import br.com.radarfornecedor.radar.model.Comprador;
import br.com.radarfornecedor.radar.repository.CompradorRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CompradorService {

    private final CompradorRepository compradorRepository;

    public CompradorService(CompradorRepository compradorRepository) {
        this.compradorRepository = compradorRepository;
    }

    public Comprador cadastrar(Comprador comprador) {
        Optional<Comprador> existente = compradorRepository.findByCnpj(comprador.getCnpj());
        if (existente.isPresent()) {
            throw new RuntimeException("Já existe um comprador cadastrado com este CNPJ.");
        }

        if (comprador.getStatus() == null) {
            comprador.setStatus("EM_ANALISE");
        }
        if (comprador.getPontuacaoRisco() == null) {
            comprador.setPontuacaoRisco(0.0);
        }

        return compradorRepository.save(comprador);
    }

    public List<Comprador> listarTodos() {
        return compradorRepository.findAll();
    }

    public List<Comprador> listarTodos(javax.servlet.http.HttpSession session) {
        br.com.radarfornecedor.radar.model.Usuario usuario = (br.com.radarfornecedor.radar.model.Usuario) session.getAttribute("usuario");
        if (usuario != null) {
            // ✅ ADMIN, MANUTENCAO, EDICAO: Vê tudo
            if (usuario.getTipo() == br.com.radarfornecedor.radar.model.TipoUsuario.ADMIN ||
                usuario.getTipo() == br.com.radarfornecedor.radar.model.TipoUsuario.MANUTENCAO ||
                usuario.getTipo() == br.com.radarfornecedor.radar.model.TipoUsuario.EDICAO) {
                return compradorRepository.findAll();
            }
            
            // ✅ Cliente não vê compradores
            if (Boolean.TRUE.equals(usuario.getCliente())) {
                return java.util.Collections.emptyList();
            }
            
            // ✅ PADRAO ou RESTRITO com perfil COMPRADOR: Vê apenas sua própria empresa
            if (Boolean.TRUE.equals(usuario.getComprador())) {
                String cnpjOuCpf = usuario.getCnpjOuCpf();
                if (cnpjOuCpf != null) {
                    String cnpjClean = cnpjOuCpf.replaceAll("\\D", "");
                    Optional<Comprador> compradorOpt = compradorRepository.findByCnpj(cnpjClean);
                    
                    if (compradorOpt.isPresent()) {
                        return List.of(compradorOpt.get());
                    }
                }
                return List.of();
            }
        }
        // Fornecedor, Representante: vê tudo
        return compradorRepository.findAll();
    }

    public Optional<Comprador> buscarPorId(Long id) {
        return compradorRepository.findById(id);
    }

    public Optional<Comprador> buscarPorCnpj(String cnpj) {
        return compradorRepository.findByCnpj(cnpj);
    }

    public Comprador atualizar(Long id, Comprador dadosNovos, javax.servlet.http.HttpSession session) {
        Comprador existente = compradorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comprador não encontrado com o ID: " + id));

        br.com.radarfornecedor.radar.model.Usuario usuario = (br.com.radarfornecedor.radar.model.Usuario) session.getAttribute("usuario");
        
        if (usuario != null) {
            // ✅ ADMIN, MANUTENCAO, EDICAO: Podem editar qualquer comprador
            if (usuario.getTipo() == br.com.radarfornecedor.radar.model.TipoUsuario.ADMIN ||
                usuario.getTipo() == br.com.radarfornecedor.radar.model.TipoUsuario.MANUTENCAO ||
                usuario.getTipo() == br.com.radarfornecedor.radar.model.TipoUsuario.EDICAO) {
                // Sem restrições
            }
            // ✅ RESTRITO: Não pode editar nada
            else if (usuario.getTipo() == br.com.radarfornecedor.radar.model.TipoUsuario.RESTRITO) {
                throw new RuntimeException("Você não tem permissão para editar cadastros.");
            }
            // ✅ PADRAO com perfil COMPRADOR: Pode editar apenas seu próprio cadastro
            else if (Boolean.TRUE.equals(usuario.getComprador())) {
                String cnpjOuCpf = usuario.getCnpjOuCpf();
                if (cnpjOuCpf != null) {
                    String cnpjClean = cnpjOuCpf.replaceAll("\\D", "");
                    Optional<Comprador> compradorOpt = compradorRepository.findByCnpj(cnpjClean);

                    if (compradorOpt.isPresent()) {
                        Comprador comprador = compradorOpt.get();
                        if (!comprador.getId().equals(id)) {
                            throw new RuntimeException("Você só tem permissão para editar o seu próprio cadastro.");
                        }
                        // ✅ PADRAO não pode alterar o STATUS
                        if (usuario.getTipo() == br.com.radarfornecedor.radar.model.TipoUsuario.PADRAO) {
                            dadosNovos.setStatus(existente.getStatus()); // Manter status original
                        }
                    } else {
                        throw new RuntimeException("Você não tem permissão para editar este cadastro.");
                    }
                } else {
                    throw new RuntimeException("Usuário sem CNPJ vinculado.");
                }
            } else {
                throw new RuntimeException("Você não tem permissão para editar compradores.");
            }
        }

        Optional<Comprador> comMesmoCnpj = compradorRepository.findByCnpj(dadosNovos.getCnpj());
        if (comMesmoCnpj.isPresent() && !comMesmoCnpj.get().getId().equals(id)) {
            throw new RuntimeException("Já existe outro comprador cadastrado com este CNPJ.");
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
        
        // Atualizar categoria e atividade
        if (dadosNovos.getCategoria() != null) {
            existente.setCategoria(dadosNovos.getCategoria());
        }
        if (dadosNovos.getAtividade() != null) {
            existente.setAtividade(dadosNovos.getAtividade());
        }

        return compradorRepository.save(existente);
    }

    public void excluir(Long id) {
        Comprador existente = compradorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comprador não encontrado com o ID: " + id));
        existente.setStatus("INATIVO");
        compradorRepository.save(existente);
    }
}
