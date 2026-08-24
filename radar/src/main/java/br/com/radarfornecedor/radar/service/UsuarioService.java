package br.com.radarfornecedor.radar.service;

import br.com.radarfornecedor.radar.model.Usuario;
import br.com.radarfornecedor.radar.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public Optional<Usuario> login(String username, String senha) {
        return usuarioRepository.findByUsername(username)
                .filter(u -> u.getSenha().equals(senha)); // In a real app, use password hashing!
    }

    public Usuario salvar(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }
}
