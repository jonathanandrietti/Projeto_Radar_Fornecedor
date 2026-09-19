package br.com.radarfornecedor.radar.model;

import javax.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "Pagamentos")
public class Pagamento {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;
    @Column(name = "ProdutoId", nullable = false)
    private Long produtoId;
    @Column(name = "FornecedorId", nullable = false)
    private Long fornecedorId;
    @Column(name = "Cliente", nullable = false, length = 100)
    private String cliente;
    @Enumerated(EnumType.STRING) @Column(name = "Forma", nullable = false, length = 30)
    private FormaPagamento forma;
    @Enumerated(EnumType.STRING) @Column(name = "Status", nullable = false, length = 20)
    private StatusPagamento status;
    @Column(name = "Valor", nullable = false, precision = 12, scale = 2)
    private BigDecimal valor;
    @Column(name = "CepEntrega", nullable = false, length = 9)
    private String cepEntrega;
    @Column(name = "CriadoEm", nullable = false)
    private LocalDateTime criadoEm;
    @Column(name = "VencimentoBoleto")
    private LocalDateTime vencimentoBoleto;

    @PrePersist void criar() { if (criadoEm == null) criadoEm = LocalDateTime.now(); if (status == null) status = StatusPagamento.PENDENTE; }
    public Long getId() { return id; }
    public Long getProdutoId() { return produtoId; }
    public void setProdutoId(Long produtoId) { this.produtoId = produtoId; }
    public Long getFornecedorId() { return fornecedorId; }
    public void setFornecedorId(Long fornecedorId) { this.fornecedorId = fornecedorId; }
    public String getCliente() { return cliente; }
    public void setCliente(String cliente) { this.cliente = cliente; }
    public FormaPagamento getForma() { return forma; }
    public void setForma(FormaPagamento forma) { this.forma = forma; }
    public StatusPagamento getStatus() { return status; }
    public void setStatus(StatusPagamento status) { this.status = status; }
    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }
    public String getCepEntrega() { return cepEntrega; }
    public void setCepEntrega(String cepEntrega) { this.cepEntrega = cepEntrega; }
    public LocalDateTime getCriadoEm() { return criadoEm; }
    public LocalDateTime getVencimentoBoleto() { return vencimentoBoleto; }
    public void setVencimentoBoleto(LocalDateTime vencimentoBoleto) { this.vencimentoBoleto = vencimentoBoleto; }
}
