package br.com.radarfornecedor.radar.model;

/** Categorias disponíveis para facilitar a navegação no catálogo. */
public enum CategoriaProduto {
    AUTOMOTIVO("Automotivo", "fa-car"),
    MOTOCICLETAS("Motocicletas", "fa-motorcycle"),
    MODA_E_TEXTIL("Moda e Têxtil", "fa-shirt"),
    EMBALAGENS("Embalagens", "fa-box"),
    TECNOLOGIA("Tecnologia", "fa-laptop"),
    CONSTRUCAO("Construção", "fa-helmet-safety"),
    MOVEIS_E_ESCRITORIO("Móveis e Escritório", "fa-chair"),
    ELETRICA_E_ELETRONICA("Elétrica e Eletrônica", "fa-plug"),
    FERRAMENTAS_E_EQUIPAMENTOS("Ferramentas e Equipamentos", "fa-screwdriver-wrench"),
    BELEZA_E_COSMETICOS("Beleza e Cosméticos", "fa-pump-soap"),
    SAUDE_E_HOSPITALAR("Saúde e Hospitalar", "fa-kit-medical"),
    ALIMENTOS_E_BEBIDAS("Alimentos e Bebidas", "fa-utensils"),
    AGRONEGOCIO("Agronegócio", "fa-seedling"),
    LIMPEZA_E_HIGIENE("Limpeza e Higiene", "fa-broom"),
    PET("Pet", "fa-paw"),
    ESPORTES_E_FITNESS("Esportes e Fitness", "fa-dumbbell"),
    BRINQUEDOS_E_INFANTIL("Brinquedos e Infantil", "fa-puzzle-piece"),
    CASA_E_DECORACAO("Casa e Decoração", "fa-house"),
    INDUSTRIAL("Industrial", "fa-industry"),
    SEGURANCA("Segurança", "fa-shield-halved"),
    ENERGIA("Energia", "fa-solar-panel"),
    GRAFICA_E_PERSONALIZADOS("Gráfica e Personalizados", "fa-print"),
    ARTESANATO_E_HOBBIES("Artesanato e Hobbies", "fa-palette"),
    OUTROS("Outros", "fa-boxes-stacked");

    private final String descricao;
    private final String icone;

    CategoriaProduto(String descricao, String icone) {
        this.descricao = descricao;
        this.icone = icone;
    }

    public String getDescricao() { return descricao; }
    public String getIcone() { return icone; }
    public String getCodigo() { return name(); }
}
