package br.com.radarfornecedor.radar.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Entity
@Table(name = "Fornecedores")
public class Fornecedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @NotBlank
    @Column(name = "Empresa", nullable = false, length = 100)
    private String nome;

    @NotBlank
    @Pattern(regexp = "\\d{14}", message = "O CNPJ deve conter exatamente 14 dígitos numéricos")
    @Column(name = "CNPJ", unique = true, nullable = false)
    private String cnpj;

    @Column(name = "status")
    private String status;

    @Column(name = "pontuacaoRisco")
    private Double pontuacaoRisco;

    // Endereço para uso em Mapa
    @Column(name = "logradouro")
    private String logradouro;

    @Column(name = "numero")
    private String numero;

    @Column(name = "complemento")
    private String complemento;

    @Column(name = "bairro")
    private String bairro;

    @Column(name = "cidade")
    private String cidade;

    @Column(name = "estado")
    private String estado;

    @Column(name = "cep")
    private String cep;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "CodCidade")
    private Long codCidade;
    
    @Column(name = "AceitaCPF")
    private Boolean aceitaCpf;

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

    // Getters e Setters de Endereço
    public String getLogradouro() { return logradouro; }
    public void setLogradouro(String logradouro) { this.logradouro = logradouro; }

    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }

    public String getComplemento() { return complemento; }
    public void setComplemento(String complemento) { this.complemento = complemento; }

    public String getBairro() { return bairro; }
    public void setBairro(String bairro) { this.bairro = bairro; }

    public String getCidade() { return cidade; }
    public void setCidade(String cidade) { this.cidade = cidade; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getCep() { return cep; }
    public void setCep(String cep) { this.cep = cep; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public Long getCodCidade() { return codCidade; }
    public void setCodCidade(Long codCidade) { this.codCidade = codCidade; }

    public Boolean getAceitaCpf() { return aceitaCpf; }
    public void setAceitaCpf(Boolean aceitaCpf) { this.aceitaCpf = aceitaCpf; }
}
