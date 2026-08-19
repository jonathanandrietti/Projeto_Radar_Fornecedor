package br.com.radarfornecedor.radar.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Entity
@Table(name = "tb_fornecedor")
public class Fornecedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String nome;

    @NotBlank
    @Pattern(regexp = "\\d{14}", message = "O CNPJ deve conter exatamente 14 dígitos numéricos")
    @Column(unique = true)
    private String cnpj;

    private String status;

    private Double pontuacaoRisco;

    public Fornecedor() {}

    public Fornecedor(Long id, String nome, String cnpj, String status, Double pontuacaoRisco) {
        this.id = id;
        this.nome = nome;
        this.cnpj = cnpj;
        this.status = status;
        this.pontuacaoRisco = pontuacaoRisco;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getCnpj() { return cnpj; }
    public void setCnpj(String cnpj) { this.cnpj = cnpj; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Double getPontuacaoRisco() { return pontuacaoRisco; }
    public void setPontuacaoRisco(Double pontuacaoRisco) { this.pontuacaoRisco = pontuacaoRisco; }
}
