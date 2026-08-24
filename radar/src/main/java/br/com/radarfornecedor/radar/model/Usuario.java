package br.com.radarfornecedor.radar.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "Usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @NotBlank
    @Column(name = "Username", unique = true, nullable = false)
    private String username;

    @NotBlank
    @Column(name = "Senha", nullable = false)
    private String senha;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "Tipo", nullable = false)
    private TipoUsuario tipo;

    public Usuario() {}

    public Usuario(String username, String senha, TipoUsuario tipo) {
        this.username = username;
        this.senha = senha;
        this.tipo = tipo;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }

    public TipoUsuario getTipo() { return tipo; }
    public void setTipo(TipoUsuario tipo) { this.tipo = tipo; }
}
