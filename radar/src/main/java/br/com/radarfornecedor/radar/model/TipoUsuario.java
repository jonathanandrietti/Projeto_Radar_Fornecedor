package br.com.radarfornecedor.radar.model;

public enum TipoUsuario {
    FORNECEDOR("Fornecedor"),
    COMPRADOR("Comprador"),
    REPRESENTANTE("Representante"),
    CLIENTE("Cliente"),
    ADMIN("Administrador"),
    MANUTENCAO("Manutenção"),
    EDICAO("Edição");

    private final String descricao;

    TipoUsuario(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() { return descricao; }
    public String getCodigo() { return name(); }
}
