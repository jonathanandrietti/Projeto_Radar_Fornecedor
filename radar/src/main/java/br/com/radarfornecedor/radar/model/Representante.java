package br.com.radarfornecedor.radar.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "Representantes")
public class Representante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @NotBlank
    @Column(name = "Nome", nullable = false, length = 100)
    private String nome;

    @Column(name = "status")
    private String status;

    @Column(name = "Cnpj")
    private String cnpj;
    
    @Column(name = "CnpjFornecedor")
    private String cnpjFornecedor;

    @Column(name = "CodEmpresa")
    private Long codEmpresa;

    public Representante() {}

    public Representante(Long id, String nome, String status) {
        this.id = id;
        this.nome = nome;
        this.status = status;
    }

    public Representante(Long id, String nome, String status, String cnpj, String cnpjFornecedor, Long codEmpresa) {
        this.id = id;
        this.nome = nome;
        this.status = status;
        this.cnpj = cnpj;
        this.cnpjFornecedor = cnpjFornecedor;
        this.codEmpresa = codEmpresa;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getCnpj() { return cnpj; }
    public void setCnpj(String cnpj) { this.cnpj = cnpj; }

    public String getCnpjFornecedor() { return cnpjFornecedor; }
    public void setCnpjFornecedor(String cnpjFornecedor) { this.cnpjFornecedor = cnpjFornecedor; }

    public Long getCodEmpresa() { return codEmpresa; }
    public void setCodEmpresa(Long codEmpresa) { this.codEmpresa = codEmpresa; }
}
