package br.com.radarfornecedor.radar.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "Clientes")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @NotBlank
    @Column(name = "Nome", nullable = false, length = 100)
    private String nome;

    @Column(name = "status")
    private String status;

    public Cliente() {}

    public Cliente(Long id, String nome, String status) {
        this.id = id;
        this.nome = nome;
        this.status = status;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
