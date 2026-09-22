package br.com.radarfornecedor.radar.service;

import br.com.radarfornecedor.radar.model.Usuario;
import br.com.radarfornecedor.radar.model.TipoUsuario;
import br.com.radarfornecedor.radar.repository.UsuarioRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    @PostConstruct
    public void inicializarDados() {
        // Criar usuário admin padrão se não existir
        if (usuarioRepository.findByUsername("admin").isEmpty()) {
            Usuario admin = new Usuario();
            admin.setUsername("admin");
            admin.setSenha(passwordEncoder.encode("admin")); // Criptografa a senha
            admin.setTipo(TipoUsuario.ADMIN);
            admin.setAtivo(true);
            admin.setCadastroCompleto(true); // Admin já está completo
            usuarioRepository.save(admin);
            System.out.println("[USUARIO SERVICE] Usuário admin criado com sucesso (admin/admin)");
            System.out.println("[USUARIO SERVICE] Senha criptografada com BCrypt");
        }
    }

    /**
     * Login com validação de senha criptografada
     */
    public Optional<Usuario> login(String username, String senha) {
        return usuarioRepository.findByUsername(username)
                .filter(u -> passwordEncoder.matches(senha, u.getSenha())) // Compara hash
                .filter(u -> u.getAtivo() == null || u.getAtivo());
    }

    /**
     * Salvar usuário criptografando a senha
     */
    public Usuario salvar(Usuario usuario) {
        // Se a senha não está já criptografada (não começa com $2a$ ou $2b$ ou $2y$ de BCrypt)
        if (usuario.getSenha() != null && !usuario.getSenha().startsWith("$2")) {
            usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        }
        return usuarioRepository.save(usuario);
    }

    /**
     * Atualizar senha do usuário
     */
    public Usuario alterarSenha(Long usuarioId, String novaSenha) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(usuarioId);
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            usuario.setSenha(passwordEncoder.encode(novaSenha));
            return usuarioRepository.save(usuario);
        }
        return null;
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
