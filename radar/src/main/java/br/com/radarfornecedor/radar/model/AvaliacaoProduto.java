package br.com.radarfornecedor.radar.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Entity
@Table(name = "AvaliacoesProdutos")
public class AvaliacaoProduto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @NotNull
    @Column(name = "ProdutoId", nullable = false)
    private Long produtoId;

    @NotNull
    @Column(name = "FornecedorId", nullable = false)
    private Long fornecedorId;

    @NotBlank
    @Column(name = "Cliente", nullable = false, length = 100)
    private String cliente;

    @NotNull
    @Min(1)
    @Max(5)
    @Column(name = "Nota", nullable = false)
    private Integer nota;

    @NotBlank
    @Column(name = "Comentario", nullable = false, length = 1000)
    private String comentario;

    @Column(name = "CriadoEm", nullable = false)
    private LocalDateTime criadoEm;

    @PrePersist
    void definirDataDeCriacao() {
        if (criadoEm == null) criadoEm = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public Long getProdutoId() { return produtoId; }
    public void setProdutoId(Long produtoId) { this.produtoId = produtoId; }
    public Long getFornecedorId() { return fornecedorId; }
    public void setFornecedorId(Long fornecedorId) { this.fornecedorId = fornecedorId; }
    public String getCliente() { return cliente; }
    public void setCliente(String cliente) { this.cliente = cliente; }
    public Integer getNota() { return nota; }
    public void setNota(Integer nota) { this.nota = nota; }
    public String getComentario() { return comentario; }
    public void setComentario(String comentario) { this.comentario = comentario; }
    public LocalDateTime getCriadoEm() { return criadoEm; }
}
