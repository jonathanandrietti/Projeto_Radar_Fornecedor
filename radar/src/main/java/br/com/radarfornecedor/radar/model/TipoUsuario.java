package br.com.radarfornecedor.radar.model;

public enum TipoUsuario {
    FORNECEDOR("Fornecedor"),      // ❌ DEPRECADO - Não usar mais
    COMPRADOR("Comprador"),         // ❌ DEPRECADO - Não usar mais
    REPRESENTANTE("Representante"), // ❌ DEPRECADO - Não usar mais
    CLIENTE("Cliente"),             // ❌ DEPRECADO - Não usar mais
    ADMIN("Administrador"),         // ✅ Acesso total + gerencia usuários
    MANUTENCAO("Manutenção"),       // ✅ Acesso total (exceto admin)
    EDICAO("Edição"),               // ✅ Acesso total (exceto admin)
    PADRAO("Padrão"),               // ✅ Vê e edita apenas seu cadastro
    RESTRITO("Restrito");           // ✅ Vê apenas seu cadastro, não edita

    private final String descricao;

    TipoUsuario(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() { return descricao; }
    public String getCodigo() { return name(); }
}

