package br.com.radarfornecedor.radar.controller;

import br.com.radarfornecedor.radar.model.Usuario;
import br.com.radarfornecedor.radar.service.UsuarioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/login")
public class LoginController {

    private final UsuarioService usuarioService;

    public LoginController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials, HttpSession session) {
        String username = credentials.get("username");
        String senha = credentials.get("senha");

        return usuarioService.login(username, senha)
                .map(usuario -> {
                    session.setAttribute("usuario", usuario);
                    return ResponseEntity.ok(usuario);
                })
                .orElse(ResponseEntity.status(401).build());
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
