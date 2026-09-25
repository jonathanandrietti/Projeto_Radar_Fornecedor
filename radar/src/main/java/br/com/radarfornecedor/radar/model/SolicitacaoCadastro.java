package br.com.radarfornecedor.radar.model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "SolicitacoesCadastro")
public class SolicitacaoCadastro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Tipo de cadastro: CNPJ ou CPF
    @Column(nullable = false)
    private String tipo; // "CNPJ" ou "CPF"

    // Dados básicos
    @Column(nullable = false, unique = true)
    private String cnpjOuCpf; // CNPJ ou CPF sem formatação

    @Column(nullable = false)
    private String nomeEmpresaOuPessoa; // Nome da empresa ou nome livre

    @Column(nullable = false)
    private String nomeContato; // Pessoa de contato

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String telefoneFIXO;

    @Column(nullable = false)
    private String celular;

    @Column(nullable = false)
    private Boolean celularComWhatsapp; // true/false se celular tem WhatsApp

    // Perfis/Tipos de Usuário
    @Column(nullable = false)
    private Boolean fornecedor = false;

    @Column(nullable = false)
    private Boolean comprador = false;

    @Column(nullable = false)
    private Boolean representante = false;

    @Column(nullable = false)
    private Boolean cliente = false;

    @Column(name = "AceitouTermos")
    private Boolean aceitouTermos = true; // Padrão TRUE para compatibilidade com registros antigos

    // Credenciais
    @Column(nullable = false, unique = true)
    private String usuario;

    @Column(nullable = false)
    private String senha; // Será criptografada ao salvar

    // Status de aprovação
    @Column(nullable = false)
    private Boolean aprovado; // true = aprovado, false = pendente

    @Column
    private String motivoRejeicao; // Motivo se rejeitado

    // Timestamps
    @Column(nullable = false)
    private LocalDateTime criadaEm;

    @Column
    private LocalDateTime aprovadaEm;

    @Column
    private LocalDateTime rejeitadaEm;

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getCnpjOuCpf() {
        return cnpjOuCpf;
    }

    public void setCnpjOuCpf(String cnpjOuCpf) {
        this.cnpjOuCpf = cnpjOuCpf;
    }

    public String getNomeEmpresaOuPessoa() {
        return nomeEmpresaOuPessoa;
    }

    public void setNomeEmpresaOuPessoa(String nomeEmpresaOuPessoa) {
        this.nomeEmpresaOuPessoa = nomeEmpresaOuPessoa;
    }

    public String getNomeContato() {
        return nomeContato;
    }

    public void setNomeContato(String nomeContato) {
        this.nomeContato = nomeContato;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefoneFIXO() {
        return telefoneFIXO;
    }

    public void setTelefoneFIXO(String telefoneFIXO) {
        this.telefoneFIXO = telefoneFIXO;
    }

    public String getCelular() {
        return celular;
    }

    public void setCelular(String celular) {
        this.celular = celular;
    }

    public Boolean getCelularComWhatsapp() {
        return celularComWhatsapp;
    }

    public void setCelularComWhatsapp(Boolean celularComWhatsapp) {
        this.celularComWhatsapp = celularComWhatsapp;
    }

    public Boolean getFornecedor() {
        return fornecedor;
    }

    public void setFornecedor(Boolean fornecedor) {
        this.fornecedor = fornecedor;
    }

    public Boolean getComprador() {
        return comprador;
    }

    public void setComprador(Boolean comprador) {
        this.comprador = comprador;
    }

    public Boolean getRepresentante() {
        return representante;
    }

    public void setRepresentante(Boolean representante) {
        this.representante = representante;
    }

    public Boolean getCliente() {
        return cliente;
    }

    public void setCliente(Boolean cliente) {
        this.cliente = cliente;
    }

    public Boolean getAceitouTermos() {
        return aceitouTermos;
    }

    public void setAceitouTermos(Boolean aceitouTermos) {
        this.aceitouTermos = aceitouTermos;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public Boolean getAprovado() {
        return aprovado;
    }

    public void setAprovado(Boolean aprovado) {
        this.aprovado = aprovado;
    }

    public String getMotivoRejeicao() {
        return motivoRejeicao;
    }

    public void setMotivoRejeicao(String motivoRejeicao) {
        this.motivoRejeicao = motivoRejeicao;
    }

    public LocalDateTime getCriadaEm() {
        return criadaEm;
    }

    public void setCriadaEm(LocalDateTime criadaEm) {
        this.criadaEm = criadaEm;
    }

    public LocalDateTime getAprovadaEm() {
        return aprovadaEm;
    }

    public void setAprovadaEm(LocalDateTime aprovadaEm) {
        this.aprovadaEm = aprovadaEm;
    }

    public LocalDateTime getRejeitadaEm() {
        return rejeitadaEm;
    }

    public void setRejeitadaEm(LocalDateTime rejeitadaEm) {
        this.rejeitadaEm = rejeitadaEm;
    }
}
