package br.com.radarfornecedor.radar.dto;

public class CategoriaProdutoDTO {
    private String codigo;
    private String descricao;
    private String icone;

    public CategoriaProdutoDTO(String codigo, String descricao, String icone) {
        this.codigo = codigo;
        this.descricao = descricao;
        this.icone = icone;
    }

    public String getCodigo() { return codigo; }
    public String getDescricao() { return descricao; }
    public String getIcone() { return icone; }
}
