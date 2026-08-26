package br.com.radarfornecedor.radar.service;

import br.com.radarfornecedor.radar.model.Usuario;
import br.com.radarfornecedor.radar.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public Optional<Usuario> login(String username, String senha) {
        return usuarioRepository.findByUsername(username)
                .filter(u -> u.getSenha().equals(senha))
                .filter(u -> u.getAtivo() == null || u.getAtivo());
    }

    public Usuario salvar(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    public Optional<Usuario> buscarPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    public Optional<Usuario> buscarPorUsername(String username) {
        return usuarioRepository.findByUsername(username);
    }

    public void excluir(Long id) {
        usuarioRepository.deleteById(id);
    }
}
