package br.com.radarfornecedor.radar.controller;

import br.com.radarfornecedor.radar.model.SolicitacaoCadastro;
import br.com.radarfornecedor.radar.model.Usuario;
import br.com.radarfornecedor.radar.service.SolicitacaoCadastroService;
import br.com.radarfornecedor.radar.service.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/cadastro")
public class CadastroController {

    private final SolicitacaoCadastroService solicitacaoService;
    private final UsuarioService usuarioService;

    public CadastroController(SolicitacaoCadastroService solicitacaoService, UsuarioService usuarioService) {
        this.solicitacaoService = solicitacaoService;
        this.usuarioService = usuarioService;
    }

    /**
     * Receber nova solicitação de cadastro
     */
    @PostMapping("/solicitar")
    public ResponseEntity<?> solicitarCadastro(@RequestBody SolicitacaoCadastro solicitacao) {
        try {
            // Validações básicas
            if (solicitacao.getTipo() == null || (!solicitacao.getTipo().equals("CNPJ") && !solicitacao.getTipo().equals("CPF"))) {
                return ResponseEntity.status(400).body(Map.of("erro", "Tipo deve ser CNPJ ou CPF"));
            }

            if (solicitacao.getCnpjOuCpf() == null || solicitacao.getCnpjOuCpf().isEmpty()) {
                return ResponseEntity.status(400).body(Map.of("erro", "CNPJ/CPF é obrigatório"));
            }

            if (solicitacao.getUsuario() == null || solicitacao.getUsuario().isEmpty()) {
                return ResponseEntity.status(400).body(Map.of("erro", "Usuário é obrigatório"));
            }

            if (solicitacao.getSenha() == null || solicitacao.getSenha().isEmpty()) {
                return ResponseEntity.status(400).body(Map.of("erro", "Senha é obrigatória"));
            }

            if (solicitacao.getEmail() == null || solicitacao.getEmail().isEmpty()) {
                return ResponseEntity.status(400).body(Map.of("erro", "E-mail é obrigatório"));
            }

            if (solicitacao.getCelular() == null || solicitacao.getCelular().isEmpty()) {
                return ResponseEntity.status(400).body(Map.of("erro", "Celular é obrigatório"));
            }

            if (solicitacao.getNomeContato() == null || solicitacao.getNomeContato().isEmpty()) {
                return ResponseEntity.status(400).body(Map.of("erro", "Nome de contato é obrigatório"));
            }

            // Criar solicitação
            SolicitacaoCadastro novasolicitacao = solicitacaoService.criarSolicitacao(solicitacao);

            System.out.println("[CADASTRO CONTROLLER] Solicitação criada: ID=" + novasolicitacao.getId());
            return ResponseEntity.ok(Map.of(
                    "mensagem", "Solicitação de cadastro enviada com sucesso",
                    "id", novasolicitacao.getId(),
                    "usuario", novasolicitacao.getUsuario()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(400).body(Map.of("erro", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("erro", "Erro ao criar solicitação: " + e.getMessage()));
        }
    }

    /**
     * Admin: Listar solicitações pendentes
     */
    @GetMapping("/pendentes")
    public ResponseEntity<?> listarPendentes() {
        try {
            List<SolicitacaoCadastro> pendentes = solicitacaoService.listarPendentes();
            return ResponseEntity.ok(Map.of(
                    "total", pendentes.size(),
                    "solicitacoes", pendentes
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("erro", e.getMessage()));
        }
    }

    /**
     * Admin: Aprovar solicitação
     */
    @PostMapping("/aprovar/{id}")
    public ResponseEntity<?> aprovarSolicitacao(@PathVariable Long id) {
        try {
            Usuario usuarioCriado = solicitacaoService.aprovarSolicitacao(id);
            System.out.println("[CADASTRO CONTROLLER] Solicitação aprovada: ID=" + id);
            return ResponseEntity.ok(Map.of(
                    "mensagem", "Solicitação aprovada com sucesso",
                    "usuarioId", usuarioCriado.getId(),
                    "usuario", usuarioCriado.getUsername()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(400).body(Map.of("erro", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("erro", "Erro ao aprovar: " + e.getMessage()));
        }
    }

    /**
     * Admin: Rejeitar solicitação
     */
    @PostMapping("/rejeitar/{id}")
    public ResponseEntity<?> rejeitarSolicitacao(@PathVariable Long id) {
        try {
            solicitacaoService.rejeitarSolicitacao(id, "Rejeitada pelo administrador");
            System.out.println("[CADASTRO CONTROLLER] Solicitação rejeitada: ID=" + id);
            return ResponseEntity.ok(Map.of("mensagem", "Solicitação rejeitada com sucesso"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(400).body(Map.of("erro", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("erro", "Erro ao rejeitar: " + e.getMessage()));
        }
    }

    /**
     * Buscar solicitação por ID (para verificar status)
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarSolicitacao(@PathVariable Long id) {
        try {
            Optional<SolicitacaoCadastro> solicitacao = solicitacaoService.buscarPorId(id);
            if (solicitacao.isEmpty()) {
                return ResponseEntity.status(404).body(Map.of("erro", "Solicitação não encontrada"));
            }
            return ResponseEntity.ok(solicitacao.get());
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("erro", e.getMessage()));
        }
    }

    /**
     * Retorna os dados da solicitação de cadastro do usuário logado na sessão.
     */
    @GetMapping("/dados-solicitacao")
    public ResponseEntity<?> obterDadosSolicitacao(javax.servlet.http.HttpSession session) {
        Usuario usuarioLogado = (Usuario) session.getAttribute("usuario");
        if (usuarioLogado == null) {
            return ResponseEntity.status(401).body(Map.of("erro", "Usuário não autenticado"));
        }
        
        Optional<SolicitacaoCadastro> solicitacaoOpt = solicitacaoService.buscarPorUsuario(usuarioLogado.getUsername());
        if (solicitacaoOpt.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("erro", "Solicitação de cadastro não encontrada"));
        }
        
        return ResponseEntity.ok(solicitacaoOpt.get());
    }

    /**
     * Finaliza o cadastro marcando cadastroCompleto = true e atualizando a sessão.
     */
    @PostMapping("/finalizar")
    public ResponseEntity<?> finalizarCadastro(javax.servlet.http.HttpSession session) {
        Usuario usuarioLogado = (Usuario) session.getAttribute("usuario");
        if (usuarioLogado == null) {
            return ResponseEntity.status(401).body(Map.of("erro", "Usuário não autenticado"));
        }
        
        Optional<Usuario> usuarioOpt = usuarioService.buscarPorId(usuarioLogado.getId());
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("erro", "Usuário não encontrado"));
        }
        
        Usuario usuario = usuarioOpt.get();
        usuario.setCadastroCompleto(true);
        Usuario usuarioSalvo = usuarioService.salvar(usuario);
        
        // Atualizar usuário na sessão
        session.setAttribute("usuario", usuarioSalvo);
        
        System.out.println("[CADASTRO CONTROLLER] Cadastro finalizado para o usuário: " + usuario.getUsername());
        return ResponseEntity.ok(usuarioSalvo);
    }
}
