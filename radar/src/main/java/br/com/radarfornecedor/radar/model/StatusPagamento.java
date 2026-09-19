package br.com.radarfornecedor.radar.model;

public enum StatusPagamento {
    PENDENTE("Pendente"),
    CONFIRMADO("Confirmado"),
    EXPIRADO("Expirado"),
    CANCELADO("Cancelado");

    private final String descricao;

    StatusPagamento(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() { return descricao; }
    public String getCodigo() { return name(); }
}
