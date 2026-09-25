package br.com.radarfornecedor.radar.model;

import javax.persistence.*;

@Entity
@Table(name = "configuracoes_email")
public class ConfiguracaoEmail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Configurações SMTP
    @Column(length = 200)
    private String smtpHost;

    @Column
    private Integer smtpPort;

    @Column(length = 200)
    private String smtpUsuario;

    @Column(length = 200)
    private String smtpSenha;

    @Column
    private Boolean smtpTls = true;

    @Column(length = 200)
    private String emailRemetente;

    @Column(length = 200)
    private String nomeRemetente;

    // Templates de Email
    @Column(length = 2000)
    private String mensagemAprovacao;

    @Column(length = 2000)
    private String mensagemRejeicao;

    @Column(length = 2000)
    private String mensagemCadastroCompleto;

    @Column(length = 2000)
    private String mensagemPreCadastro;

    @Column(length = 500)
    private String assinatura;

    @Column(length = 500)
    private String cumprimento = "Prezado(a)";

    // Logos e Imagens (URL ou Base64)
    @Column(length = 5000)
    private String logoUrl;

    @Column(length = 5000)
    private String rodapeImagemUrl;

    @Column
    private Boolean emailAtivo = false;

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSmtpHost() {
        return smtpHost;
    }

    public void setSmtpHost(String smtpHost) {
        this.smtpHost = smtpHost;
    }

    public Integer getSmtpPort() {
        return smtpPort;
    }

    public void setSmtpPort(Integer smtpPort) {
        this.smtpPort = smtpPort;
    }

    public String getSmtpUsuario() {
        return smtpUsuario;
    }

    public void setSmtpUsuario(String smtpUsuario) {
        this.smtpUsuario = smtpUsuario;
    }

    public String getSmtpSenha() {
        return smtpSenha;
    }

    public void setSmtpSenha(String smtpSenha) {
        this.smtpSenha = smtpSenha;
    }

    public Boolean getSmtpTls() {
        return smtpTls;
    }

    public void setSmtpTls(Boolean smtpTls) {
        this.smtpTls = smtpTls;
    }

    public String getEmailRemetente() {
        return emailRemetente;
    }

    public void setEmailRemetente(String emailRemetente) {
        this.emailRemetente = emailRemetente;
    }

    public String getNomeRemetente() {
        return nomeRemetente;
    }

    public void setNomeRemetente(String nomeRemetente) {
        this.nomeRemetente = nomeRemetente;
    }

    public String getMensagemAprovacao() {
        return mensagemAprovacao;
    }

    public void setMensagemAprovacao(String mensagemAprovacao) {
        this.mensagemAprovacao = mensagemAprovacao;
    }

    public String getMensagemRejeicao() {
        return mensagemRejeicao;
    }

    public void setMensagemRejeicao(String mensagemRejeicao) {
        this.mensagemRejeicao = mensagemRejeicao;
    }

    public String getMensagemCadastroCompleto() {
        return mensagemCadastroCompleto;
    }

    public void setMensagemCadastroCompleto(String mensagemCadastroCompleto) {
        this.mensagemCadastroCompleto = mensagemCadastroCompleto;
    }

    public String getMensagemPreCadastro() {
        return mensagemPreCadastro;
    }

    public void setMensagemPreCadastro(String mensagemPreCadastro) {
        this.mensagemPreCadastro = mensagemPreCadastro;
    }

    public String getAssinatura() {
        return assinatura;
    }

    public void setAssinatura(String assinatura) {
        this.assinatura = assinatura;
    }

    public String getCumprimento() {
        return cumprimento;
    }

    public void setCumprimento(String cumprimento) {
        this.cumprimento = cumprimento;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public String getRodapeImagemUrl() {
        return rodapeImagemUrl;
    }

    public void setRodapeImagemUrl(String rodapeImagemUrl) {
        this.rodapeImagemUrl = rodapeImagemUrl;
    }

    public Boolean getEmailAtivo() {
        return emailAtivo;
    }

    public void setEmailAtivo(Boolean emailAtivo) {
        this.emailAtivo = emailAtivo;
    }
}
