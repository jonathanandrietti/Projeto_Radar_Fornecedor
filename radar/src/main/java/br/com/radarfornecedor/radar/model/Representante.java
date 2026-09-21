package br.com.radarfornecedor.radar.model;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Email;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import br.com.radarfornecedor.radar.config.CategoriaDeserializer;
import br.com.radarfornecedor.radar.config.AtividadeDeserializer;

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

    @Column(name = "Contato", length = 30)
    private String contato;

    @Email(message = "Informe um e-mail válido para o representante.")
    @Column(name = "Email", length = 120)
    private String email;

    // Endereco e geolocalizacao
    @Column(name = "Logradouro")
    private String logradouro;

    @Column(name = "Numero")
    private String numero;

    @Column(name = "Complemento")
    private String complemento;

    @Column(name = "Bairro")
    private String bairro;

    @Column(name = "Cidade")
    private String cidade;

    @Column(name = "Estado")
    private String estado;

    @Column(name = "Cep")
    private String cep;

    @Column(name = "Latitude")
    private Double latitude;

    @Column(name = "Longitude")
    private Double longitude;

    // Relacionamento com Categoria e Atividade
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id")
    @JsonDeserialize(using = CategoriaDeserializer.class)
    private Categoria categoria;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "atividade_id")
    @JsonDeserialize(using = AtividadeDeserializer.class)
    private Atividade atividade;

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

    public String getContato() { return contato; }
    public void setContato(String contato) { this.contato = contato; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
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

    public Categoria getCategoria() { return categoria; }
    public void setCategoria(Categoria categoria) { this.categoria = categoria; }

    public Atividade getAtividade() { return atividade; }
    public void setAtividade(Atividade atividade) { this.atividade = atividade; }
}
