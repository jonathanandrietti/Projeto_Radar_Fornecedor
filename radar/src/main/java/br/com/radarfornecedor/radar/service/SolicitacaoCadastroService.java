package br.com.radarfornecedor.radar.service;

import br.com.radarfornecedor.radar.model.SolicitacaoCadastro;
import br.com.radarfornecedor.radar.model.Usuario;
import br.com.radarfornecedor.radar.model.TipoUsuario;
import br.com.radarfornecedor.radar.repository.SolicitacaoCadastroRepository;
import br.com.radarfornecedor.radar.repository.UsuarioRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class SolicitacaoCadastroService {

    private final SolicitacaoCadastroRepository solicitacaoRepository;
    private final UsuarioRepository usuarioRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public SolicitacaoCadastroService(
            SolicitacaoCadastroRepository solicitacaoRepository,
            UsuarioRepository usuarioRepository) {
        this.solicitacaoRepository = solicitacaoRepository;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    /**
     * Criar nova solicitação de cadastro
     */
    public SolicitacaoCadastro criarSolicitacao(SolicitacaoCadastro solicitacao) {
        // Validar se usuário já existe
        if (usuarioRepository.findByUsername(solicitacao.getUsuario()).isPresent()) {
            throw new RuntimeException("Usuário '" + solicitacao.getUsuario() + "' já existe");
        }

        // Validar se CNPJ/CPF já foi solicitado
        if (solicitacaoRepository.findByCnpjOuCpf(solicitacao.getCnpjOuCpf()).isPresent()) {
            throw new RuntimeException("CNPJ/CPF '" + solicitacao.getCnpjOuCpf() + "' já possui solicitação");
        }

        // Criptografar senha
        solicitacao.setSenha(passwordEncoder.encode(solicitacao.getSenha()));

        // Definir como pendente
        solicitacao.setAprovado(false);
        solicitacao.setCriadaEm(LocalDateTime.now());

        SolicitacaoCadastro salva = solicitacaoRepository.save(solicitacao);
        System.out.println("[SOLICITACAO CADASTRO] Nova solicitação criada: " + solicitacao.getUsuario() + " (" + solicitacao.getTipo() + ")");
        return salva;
    }

    /**
     * Aprovar solicitação de cadastro e criar usuário
     */
    public Usuario aprovarSolicitacao(Long solicitacaoId) {
        Optional<SolicitacaoCadastro> solicitacaoOpt = solicitacaoRepository.findById(solicitacaoId);
        if (solicitacaoOpt.isEmpty()) {
            throw new RuntimeException("Solicitação não encontrada");
        }

        SolicitacaoCadastro solicitacao = solicitacaoOpt.get();

        if (solicitacao.getAprovado()) {
            throw new RuntimeException("Solicitação já foi aprovada");
        }

        // Criar usuário
        Usuario usuario = new Usuario();
        usuario.setUsername(solicitacao.getUsuario());
        usuario.setSenha(solicitacao.getSenha()); // Já criptografada
        usuario.setTipo(TipoUsuario.CLIENTE); // Padrão: CLIENTE
        usuario.setAtivo(true);
        usuario.setAguardandoAprovacao(false); // ✅ Aprovado - pode fazer login
        
        // Transferir perfis da solicitação para o usuário
        usuario.setFornecedor(solicitacao.getFornecedor());
        usuario.setComprador(solicitacao.getComprador());
        usuario.setRepresentante(solicitacao.getRepresentante());
        usuario.setCliente(solicitacao.getCliente());

        Usuario usuarioSalvo = usuarioRepository.save(usuario);

        // Atualizar solicitação
        solicitacao.setAprovado(true);
        solicitacao.setAprovadaEm(LocalDateTime.now());
        solicitacaoRepository.save(solicitacao);

        System.out.println("[SOLICITACAO CADASTRO] Solicitação aprovada: " + solicitacao.getUsuario());
        return usuarioSalvo;
    }

    /**
     * Rejeitar solicitação de cadastro
     */
    public void rejeitarSolicitacao(Long solicitacaoId, String motivo) {
        Optional<SolicitacaoCadastro> solicitacaoOpt = solicitacaoRepository.findById(solicitacaoId);
        if (solicitacaoOpt.isEmpty()) {
            throw new RuntimeException("Solicitação não encontrada");
        }

        SolicitacaoCadastro solicitacao = solicitacaoOpt.get();

        if (solicitacao.getAprovado() != null && solicitacao.getAprovado()) {
            throw new RuntimeException("Solicitação já foi aprovada");
        }

        solicitacao.setAprovado(false);
        solicitacao.setMotivoRejeicao(motivo);
        solicitacao.setRejeitadaEm(LocalDateTime.now());
        solicitacaoRepository.save(solicitacao);

        System.out.println("[SOLICITACAO CADASTRO] Solicitação rejeitada: " + solicitacao.getUsuario() + " - Motivo: " + motivo);
    }

    /**
     * Listar solicitações pendentes de aprovação
     */
    public List<SolicitacaoCadastro> listarPendentes() {
        return solicitacaoRepository.findByAprovadoFalse();
    }

    /**
     * Listar solicitações aprovadas
     */
    public List<SolicitacaoCadastro> listarAprovadas() {
        return solicitacaoRepository.findByAprovadoTrue();
    }

    /**
     * Buscar solicitação por ID
     */
    public Optional<SolicitacaoCadastro> buscarPorId(Long id) {
        return solicitacaoRepository.findById(id);
    }

    /**
     * Buscar solicitação por CNPJ/CPF
     */
    public Optional<SolicitacaoCadastro> buscarPorCnpjOuCpf(String cnpjOuCpf) {
        return solicitacaoRepository.findByCnpjOuCpf(cnpjOuCpf);
    }

    /**
     * Buscar solicitação por usuário
     */
    public Optional<SolicitacaoCadastro> buscarPorUsuario(String usuario) {
        return solicitacaoRepository.findByUsuario(usuario);
    }
}
