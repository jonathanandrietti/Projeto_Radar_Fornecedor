package br.com.radarfornecedor.radar.controller;

import br.com.radarfornecedor.radar.model.Usuario;
import br.com.radarfornecedor.radar.service.UsuarioService;
import br.com.radarfornecedor.radar.service.SolicitacaoCadastroService;
import javax.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/login")
public class LoginController {

    private final UsuarioService usuarioService;
    private final SolicitacaoCadastroService solicitacaoService;

    public LoginController(UsuarioService usuarioService, SolicitacaoCadastroService solicitacaoService) {
        this.usuarioService = usuarioService;
        this.solicitacaoService = solicitacaoService;
    }

    @PostMapping
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials, HttpSession session) {
        String username = credentials.get("username");
        String senha = credentials.get("senha");

        if (username == null || username.isEmpty() || senha == null || senha.isEmpty()) {
            return ResponseEntity.status(400).body(Map.of("erro", "Usuário e senha são obrigatórios"));
        }

        // ✅ PRIMEIRO: Verificar se há solicitação pendente
        var solicitacaoPendente = solicitacaoService.buscarPorUsuario(username);
        if (solicitacaoPendente.isPresent()) {
            var solicitacao = solicitacaoPendente.get();
            if (!solicitacao.getAprovado()) {
                System.out.println("[LOGIN] Solicitação pendente para: " + username);
                return ResponseEntity.status(403).body(Map.of(
                    "erro", "Sua solicitação de cadastro ainda está aguardando aprovação do administrador. Você será notificado quando for aprovado."
                ));
            }
        }

        // ✅ SEGUNDO: Tentar login normal
        var loginOpt = usuarioService.login(username, senha);
        if (loginOpt.isPresent()) {
            Usuario usuario = loginOpt.get();
            
            // ✅ TERCEIRO: Verificar se usuário está aguardando aprovação (redundância)
            if (usuario.getAguardandoAprovacao()) {
                System.out.println("[LOGIN] Usuário '" + username + "' aguarda aprovação do administrador.");
                return ResponseEntity.status(403).body(Map.of(
                    "erro", "Sua solicitação de cadastro ainda está aguardando aprovação do administrador. Você será notificado quando for aprovado."
                ));
            }
            
            session.setAttribute("usuario", usuario);
            System.out.println("[LOGIN] Usuário '" + username + "' autenticado com sucesso. Senha validada com BCrypt.");
            return ResponseEntity.ok(usuario);
        } else {
            return ResponseEntity.status(401).body(Map.of("erro", "Usuário ou senha incorretos"));
        }
    }

    @GetMapping("/session")
    public ResponseEntity<?> getSession(HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario != null) {
            return ResponseEntity.ok(usuario);
        }
        return ResponseEntity.status(401).build();
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok().build();
    }
}
