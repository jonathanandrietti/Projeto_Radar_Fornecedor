package br.com.radarfornecedor.radar.config;

import java.util.regex.Pattern;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * Ponto único para alterações manuais e permanentes no esquema SQLite.
 *
 * <p>Cada atualização recebe uma versão única. Depois de executada com sucesso,
 * a versão é registrada na tabela {@code schema_migrations} e não volta a ser
 * executada nas próximas inicializações.</p>
 */
@Component
public class AtualizaBancoSQLite implements ApplicationRunner {

    private static final Pattern IDENTIFICADOR_SEGURO = Pattern.compile("[A-Za-z_][A-Za-z0-9_]*");

    private final JdbcTemplate jdbcTemplate;

    public AtualizaBancoSQLite(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(ApplicationArguments args) {
        criarTabelaDeHistorico();
        executarAtualizacoes();
    }

    private void criarTabelaDeHistorico() {
        jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS schema_migrations (
                    versao INTEGER PRIMARY KEY,
                    descricao TEXT NOT NULL,
                    executada_em TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP
                )
                """);
    }

    // =========================================================================
    // ÁREA DE EDIÇÃO DO SCRIPT (Histórico cronológico de atualizações)
    // =========================================================================
    private void executarAtualizacoes() {

        // --- LOCALIZAÇÃO IBGE -------------------------------------------------
        aplicarAtualizacao(10, "Cria tabelas Paises e Cidades", () -> {
            executarSql("""
                    CREATE TABLE IF NOT EXISTS Paises (
                        CodPais INTEGER PRIMARY KEY,
                        Pais TEXT NOT NULL
                    )
                    """);
            executarSql("""
                    CREATE TABLE IF NOT EXISTS Cidades (
                        CodCidade INTEGER PRIMARY KEY,
                        Cidade TEXT NOT NULL,
                        UF TEXT NOT NULL,
                        CodRegiao INTEGER,
                        Alterado INTEGER DEFAULT 0,
                        CodCidadeIBGE INTEGER NOT NULL UNIQUE,
                        CodPais INTEGER NOT NULL,
                        FOREIGN KEY (CodPais) REFERENCES Paises(CodPais)
                    )
                    """);
        });
        aplicarAtualizacao(11, "Adiciona CodCidade a fornecedores e compradores", () -> {
            adicionarColunaSeNaoExistir("Fornecedores", "CodCidade", "INTEGER");
            adicionarColunaSeNaoExistir("Compradores", "CodCidade", "INTEGER");
        });
        aplicarAtualizacao(12, "Carga inicial de paises", this::importarPaises);

        aplicarAtualizacao(20, "Cria tabelas de Usuários, Representantes e Clientes", () -> {
            executarSql("""
                    CREATE TABLE IF NOT EXISTS Usuarios (
                        ID INTEGER PRIMARY KEY AUTOINCREMENT,
                        Username TEXT NOT NULL UNIQUE,
                        Senha TEXT NOT NULL,
                        Tipo TEXT NOT NULL
                    )
                    """);
            executarSql("""
                    CREATE TABLE IF NOT EXISTS Representantes (
                        ID INTEGER PRIMARY KEY AUTOINCREMENT,
                        Nome TEXT NOT NULL,
                        status TEXT
                    )
                    """);
            executarSql("""
                    CREATE TABLE IF NOT EXISTS Clientes (
                        ID INTEGER PRIMARY KEY AUTOINCREMENT,
                        Nome TEXT NOT NULL,
                        status TEXT
                    )
                    """);
        });

        aplicarAtualizacao(21, "Carga inicial de usuários", () -> {
            executarSql("INSERT OR IGNORE INTO Usuarios (Username, Senha, Tipo) VALUES ('fornecedor', '123', 'FORNECEDOR')");
            executarSql("INSERT OR IGNORE INTO Usuarios (Username, Senha, Tipo) VALUES ('comprador', '123', 'COMPRADOR')");
            executarSql("INSERT OR IGNORE INTO Usuarios (Username, Senha, Tipo) VALUES ('representante', '123', 'REPRESENTANTE')");
            executarSql("INSERT OR IGNORE INTO Usuarios (Username, Senha, Tipo) VALUES ('cliente', '123', 'CLIENTE')");
            executarSql("INSERT OR IGNORE INTO Usuarios (Username, Senha, Tipo) VALUES ('admin', 'admin', 'ADMIN')");
        });

        aplicarAtualizacao(22, "Adiciona coluna Ativo a Usuarios", () -> {
            adicionarColunaSeNaoExistir("Usuarios", "Ativo", "INTEGER DEFAULT 1");
        });

        aplicarAtualizacao(23, "Adiciona colunas booleanas de tipos a Usuarios", () -> {
            adicionarColunaSeNaoExistir("Usuarios", "Fornecedor", "INTEGER DEFAULT 0");
            adicionarColunaSeNaoExistir("Usuarios", "Comprador", "INTEGER DEFAULT 0");
            adicionarColunaSeNaoExistir("Usuarios", "Representante", "INTEGER DEFAULT 0");
            adicionarColunaSeNaoExistir("Usuarios", "Cliente", "INTEGER DEFAULT 0");
            
            // Inicializar dados existentes
            executarSql("UPDATE Usuarios SET Fornecedor = 1 WHERE Tipo = 'FORNECEDOR'");
            executarSql("UPDATE Usuarios SET Comprador = 1 WHERE Tipo = 'COMPRADOR'");
            executarSql("UPDATE Usuarios SET Representante = 1 WHERE Tipo = 'REPRESENTANTE'");
            executarSql("UPDATE Usuarios SET Cliente = 1 WHERE Tipo = 'CLIENTE'");
        });

        aplicarAtualizacao(24, "Adiciona colunas AceitaCPF e CNPJ em Fornecedores e Representantes", () -> {
            adicionarColunaSeNaoExistir("Fornecedores", "AceitaCPF", "INTEGER DEFAULT 0");
            adicionarColunaSeNaoExistir("Representantes", "Cnpj", "TEXT");
            adicionarColunaSeNaoExistir("Representantes", "CnpjFornecedor", "TEXT");
        });

        aplicarAtualizacao(25, "Adiciona CpfCnpj e TipoPessoa em Clientes, e CodEmpresa em Representantes", () -> {
            adicionarColunaSeNaoExistir("Clientes", "CpfCnpj", "TEXT");
            adicionarColunaSeNaoExistir("Clientes", "TipoPessoa", "TEXT"); // 'PF' ou 'PJ'
            adicionarColunaSeNaoExistir("Representantes", "CodEmpresa", "INTEGER");
        });

        aplicarAtualizacao(26, "Carga inicial de Clientes e Representantes para teste", () -> {
            executarSql("INSERT OR IGNORE INTO Clientes (Nome, status, cpf_cnpj, tipo_pessoa) VALUES ('João da Silva (PF)', 'ATIVO', '12345678901', 'PF')");
            executarSql("INSERT OR IGNORE INTO Clientes (Nome, status, cpf_cnpj, tipo_pessoa) VALUES ('Empresa Alfa (PJ)', 'ATIVO', '12345678000199', 'PJ')");
            executarSql("INSERT OR IGNORE INTO Clientes (Nome, status, cpf_cnpj, tipo_pessoa) VALUES ('Maria Souza (PF)', 'ATIVO', '98765432100', 'PF')");
            executarSql("INSERT OR IGNORE INTO Clientes (Nome, status, cpf_cnpj, tipo_pessoa) VALUES ('Empresa Beta (PJ)', 'ATIVO', '98765432000188', 'PJ')");
            
            // Fornecedor que aceita CPF
            executarSql("INSERT OR IGNORE INTO Fornecedores (ID, Empresa, CNPJ, status, pontuacao_risco, AceitaCPF) VALUES (1, 'Fornecedor de Teste CPF', '12345678000100', 'APROVADO', 5.0, 1)");
            // Fornecedor que não aceita CPF
            executarSql("INSERT OR IGNORE INTO Fornecedores (ID, Empresa, CNPJ, status, pontuacao_risco, AceitaCPF) VALUES (2, 'Fornecedor Sem CPF', '98765432000100', 'APROVADO', 3.0, 0)");

            // Associar representantes aos fornecedores
            executarSql("INSERT OR IGNORE INTO Representantes (Nome, status, Cnpj, cnpj_fornecedor, cod_empresa) VALUES ('Representante Alfa', 'ATIVO', '11122233344', '12345678000100', 1)");
            executarSql("INSERT OR IGNORE INTO Representantes (Nome, status, Cnpj, cnpj_fornecedor, cod_empresa) VALUES ('Representante Beta', 'ATIVO', '55566677788', '98765432000100', 2)");
            
            // Criar Compradores para teste
            executarSql("INSERT OR IGNORE INTO Compradores (ID, Empresa, CNPJ, status, pontuacao_risco) VALUES (1, 'Comprador de Teste 1', '11223344000199', 'APROVADO', 4.5)");
            executarSql("INSERT OR IGNORE INTO Compradores (ID, Empresa, CNPJ, status, pontuacao_risco) VALUES (2, 'Comprador de Teste 2', '55667788000199', 'APROVADO', 2.0)");

            // Criar Usuários vinculados a esses Fornecedores (com o username sendo o CNPJ ou o nome)
            executarSql("INSERT OR IGNORE INTO Usuarios (Username, Senha, Tipo, Fornecedor) VALUES ('12345678000100', '123', 'FORNECEDOR', 1)");
            executarSql("INSERT OR IGNORE INTO Usuarios (Username, Senha, Tipo, Fornecedor) VALUES ('98765432000100', '123', 'FORNECEDOR', 1)");
            
            // Criar Usuário vinculado ao Comprador (com o username sendo o CNPJ)
            executarSql("INSERT OR IGNORE INTO Usuarios (Username, Senha, Tipo, Comprador) VALUES ('11223344000199', '123', 'COMPRADOR', 1)");

            // Criar Usuários vinculados aos Representantes (com o username sendo o CNPJ do representante)
            executarSql("INSERT OR IGNORE INTO Usuarios (Username, Senha, Tipo, Representante) VALUES ('11122233344', '123', 'REPRESENTANTE', 1)");
            executarSql("INSERT OR IGNORE INTO Usuarios (Username, Senha, Tipo, Representante) VALUES ('55566677788', '123', 'REPRESENTANTE', 1)");

            // Criar Usuários vinculados aos Clientes (com o username sendo o CPF do cliente)
            executarSql("INSERT OR IGNORE INTO Usuarios (Username, Senha, Tipo, Cliente) VALUES ('12345678901', '123', 'CLIENTE', 1)");
            executarSql("INSERT OR IGNORE INTO Usuarios (Username, Senha, Tipo, Cliente) VALUES ('98765432100', '123', 'CLIENTE', 1)");
        });

        // --- CRIAR TABELA -----------------------------------------------------
        // Descomente, ajuste e use sempre uma nova versão.
        // aplicarAtualizacao(1, "Cria tabela Mercadorias", () ->
        //         executarSql("""
        //                 CREATE TABLE IF NOT EXISTS Mercadorias (
        //                     ID INTEGER PRIMARY KEY,
        //                     Nome TEXT NOT NULL,
        //                     Codigo INTEGER
        //                 )
        //                 """));

        // --- ADICIONAR COLUNA -------------------------------------------------
        // O método verifica a coluna antes de executar ALTER TABLE.
        // aplicarAtualizacao(2, "Adiciona Codigo em Mercadorias", () ->
        //         adicionarColunaSeNaoExistir("Mercadorias", "Codigo", "INTEGER"));

        // --- REMOVER COLUNA ---------------------------------------------------
        // SQLite moderno aceita DROP COLUMN. Faça backup antes de descomentar.
        // aplicarAtualizacao(3, "Remove coluna antiga", () ->
        //         removerColunaSeExistir("Mercadorias", "ColunaAntiga"));

        // --- REMOVER TABELA ---------------------------------------------------
        // Esta operação apaga dados. Descomente somente quando for intencional.
        // aplicarAtualizacao(4, "Remove tabela obsoleta", () ->
        //         removerTabelaSeExistir("TabelaObsoleta"));
    }

    private void aplicarAtualizacao(int versao, String descricao, Runnable atualizacao) {
        Integer encontrada = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM schema_migrations WHERE versao = ?", Integer.class, versao);

        if (encontrada != null && encontrada > 0) {
            return;
        }

        atualizacao.run();
        jdbcTemplate.update("INSERT INTO schema_migrations (versao, descricao) VALUES (?, ?)", versao, descricao);
    }

    private void executarSql(String sql) {
        jdbcTemplate.execute(sql);
    }

    private void importarPaises() {
        String paises = """
                132|AFEGANISTAO
                175|ALBANIA REPUBLICA DA
                230|ALEMANHA
                310|BURKINA FASO
                370|ANDORRA
                400|ANGOLA
                418|ANGUILLA
                434|ANTIGUA E BARBUDA
                477|ANTILHAS HOLANDESAS
                531|ARABIA SAUDITA
                590|ARGELIA
                639|ARGENTINA
                647|ARMENIA REPUBLICA DA
                655|ARUBA
                698|AUSTRALIA
                728|AUSTRIA
                736|AZERBAIJAO REPUBLICA DO
                779|BAHAMAS ILHAS
                809|BAHREIN ILHAS
                817|BANGLADESH
                833|BARBADOS
                850|BELARUS REPUBLICA DA
                876|BELGICA
                884|BELIZE
                906|BERMUDAS
                930|MIANMAR
                973|BOLIVIA
                981|BOSNIA-HERZEGOVINA
                1015|BOTSUANA
                1058|BRASIL
                1082|BRUNEI
                1112|BULGARIA REPUBLICA DA
                1155|BURUNDI
                1198|BUTAO
                1279|CABO VERDE REPUBLICA DE
                1376|CAYMAN ILHAS
                1414|CAMBOJA
                1457|CAMAROES
                1490|CANADA
                1504|GUERNSEY ILHA DO CANAL
                1508|JERSEY, ILHA DO CANAL
                1511|CANARIAS, ILHAS
                1538|CAZAQUISTAO, REPUBLICA DO
                1546|CATAR
                1589|CHILE
                1600|CHINA REPUBLICA POPULAR
                1619|FORMOSA TAIWAN
                1635|CHIPRE
                1651|COCOS
                1694|COLOMBIA
                1732|COMORES ILHAS
                1775|CONGO
                1830|COOK ILHAS
                1872|COREIA REP.POP.DEMOCRATICA
                1902|COREIA REPUBLICA DA
                1937|COSTA DO MARFIM
                1953|CROACIA
                1961|COSTA RICA
                1988|COVEITE
                1996|CUBA
                2291|BENIN
                2321|DINAMARCA
                2356|DOMINICA ILHA
                2399|EQUADOR
                2402|EGITO
                2437|ERITREIA
                2445|EMIRADOS ARABES UNIDOS
                2453|ESPANHA
                2461|ESLOVENIA REPUBLICA DA
                2470|ESLOVACA REPUBLICA
                2496|ESTADOS UNIDOS
                2518|ESTONIA REPUBLICA DA
                2534|ETIOPIA
                2550|FALKLAND
                2593|FEROE, ILHAS
                2674|FILIPINAS
                2712|FINLANDIA
                2755|FRANCA
                2810|GABAO
                2852|GAMBIA
                2895|GANA
                2917|GEORGIA, REPUBLICA DA
                2933|GIBRALTAR
                2976|GRANADA
                3018|GRECIA
                3050|GROENLANDIA
                3093|GUADALUPE
                3131|GUAM
                3174|GUATEMALA
                3255|GUIANA FRANCESA
                3298|GUINE
                3310|GUINE-EQUATORIAL
                3344|GUINE-BISSAU
                3379|GUIANA
                3417|HAITI
                3450|HONDURAS
                3514|HONG KONG
                3557|HUNGRIA, REPUBLICA DA
                3573|IEMEN
                3595|MAN ILHA DE
                3611|INDIA
                3654|INDONESIA
                3697|IRAQUE
                3727|IRA REPUBLICA ISLAMICA DO
                3751|IRLANDA
                3794|ISLANDIA
                3832|ISRAEL
                3867|ITALIA
                3913|JAMAICA
                3964|JOHNSTON ILHAS
                3999|JAPAO
                4030|JORDANIA
                4111|KIRIBATI
                4200|LAOS REP.POP.DEMOCR.DO
                4235|LEBUAN ILHAS
                4260|LESOTO
                4278|LETONIA REPUBLICA DA
                4316|LIBANO
                4340|LIBERIA
                4383|LIBIA
                4405|LIECHTENSTEIN
                4421|LITUANIA REPUBLICA DA
                4456|LUXEMBURGO
                4472|MACAU
                4499|MACEDONIA ANT.REP.IUGOSLAVA
                4502|MADAGASCAR
                4525|MADEIRA, ILHA DA
                4553|MALASIA
                4588|MALAVI
                4618|MALDIVAS
                4642|MALI
                4677|MALTA
                4723|MARIANAS DO NORTE
                4740|MARROCOS
                4766|MARSHALL,ILHAS
                4774|MARTINICA
                4855|MAURICIO
                4880|MAURITANIA
                4901|MIDWAY, ILHAS
                4936|MEXICO
                4944|MOLDAVIA, REPUBLICA DA
                4952|MONACO
                4979|MONGOLIA
                4985|MONTENEGRO
                4995|MICRONESIA
                5010|MONTSERRAT ILHAS
                5053|MOCAMBIQUE
                5070|NAMIBIA
                5088|NAURU
                5118|CHRISTMAS ILHA
                5177|NEPAL
                5215|NICARAGUA
                5258|NIGER
                5282|NIGERIA
                5312|NIUE,ILHA
                5355|NORFOLK,ILHA
                5380|NORUEGA
                5428|NOVA CALEDONIA
                5452|PAPUA NOVA GUINE
                5487|NOVA ZELANDIA
                5517|VANUATU
                5568|OMA
                5665|PACIFICO ILHAS DO
                5738|PAISES BAIXOS (HOLANDA);
                5754|PALAU
                5762|PAQUISTAO
                5800|PANAMA
                5860|PARAGUAI
                5894|PERU
                5932|PITCAIRN,ILHA
                5991|POLINESIA FRANCESA
                6033|POLONIA, REPUBLICA DA
                6076|PORTUGAL
                6114|PORTO RICO
                6238|QUENIA
                6254|QUIRGUIZ, REPUBLICA
                6289|REINO UNIDO
                6408|REPUBLICA CENTRO-AFRICANA
                6475|REPUBLICA DOMINICANA
                6602|REUNIAO ILHA
                6653|ZIMBABUE
                6700|ROMENIA
                6750|RUANDA
                6769|RUSSIA FEDERACAO DA
                6777|SALOMAO ILHAS
                6781|SAINT KITTS E NEVIS
                6858|SAARA OCIDENTAL
                6874|EL SALVADOR
                6904|SAMOA
                6912|SAMOA AMERICANA
                6955|SAO CRISTOVAO E NEVES,ILHAS
                6971|SAN MARINO
                7005|SAO PEDRO E MIQUELON
                7056|SAO VICENTE E GRANADINAS
                7102|SANTA HELENA
                7153|SANTA LUCIA
                7285|SENEGAL
                7315|SEYCHELLES
                7358|SERRA LEOA
                7370|SERVIA
                7412|CINGAPURA
                7447|SIRIA, REPUBLICA ARABE DA
                7480|SOMALIA
                7501|SRI LANKA
                7544|SUAZILANDIA
                7560|AFRICA DO SUL
                7595|SUDAO
                7641|SUECIA
                7676|SUICA
                7706|SURINAME
                7722|TADJIQUISTAO, REPUBLICA DO
                7765|TAILANDIA
                7803|TANZANIA, REP.UNIDA DA
                7820|TERRITORIO BRIT.OC.INDICO
                7838|DJIBUTI
                7889|CHADE
                7919|TCHECA, REPUBLICA
                7951|TIMOR LESTE
                8001|TOGO
                8052|TOQUELAU,ILHAS
                8109|TONGA
                8150|TRINIDAD E TOBAGO
                8206|TUNISIA
                8230|TURCAS E CAICOS,ILHAS
                8249|TURCOMENISTAO, REPUBLICA DO
                8273|TURQUIA
                8281|TUVALU
                8311|UCRANIA
                8338|UGANDA
                8451|URUGUAI
                8478|UZBEQUISTAO, REPUBLICA DO
                8486|VATICANO, EST.DA CIDADE DO
                8508|VENEZUELA
                8583|VIETNA
                8630|VIRGENS ILHAS (BRITANICAS)
                8664|VIRGENS ILHAS
                8702|FIJI
                8737|WAKE ILHA
                8885|CONGO, REPUBLICA DEMOCRATICA DO
                8907|ZAMBIA
                9999999|EXTERIOR
                """;

        for (String linha : paises.split("\\R")) {
            String[] valores = linha.split("\\|", 2);
            jdbcTemplate.update("INSERT OR IGNORE INTO Paises (CodPais, Pais) VALUES (?, ?)",
                    Integer.parseInt(valores[0]), valores[1]);
        }
    }

    private void adicionarColunaSeNaoExistir(String tabela, String coluna, String definicao) {
        validarIdentificador(tabela);
        validarIdentificador(coluna);

        boolean colunaExiste = jdbcTemplate.query("PRAGMA table_info(" + tabela + ")",
                (rs, rowNum) -> rs.getString("name")).stream().anyMatch(coluna::equalsIgnoreCase);

        if (!colunaExiste) {
            jdbcTemplate.execute("ALTER TABLE " + tabela + " ADD COLUMN " + coluna + " " + definicao);
        }
    }

    private void removerColunaSeExistir(String tabela, String coluna) {
        validarIdentificador(tabela);
        validarIdentificador(coluna);

        boolean colunaExiste = jdbcTemplate.query("PRAGMA table_info(" + tabela + ")",
                (rs, rowNum) -> rs.getString("name")).stream().anyMatch(coluna::equalsIgnoreCase);

        if (colunaExiste) {
            jdbcTemplate.execute("ALTER TABLE " + tabela + " DROP COLUMN " + coluna);
        }
    }

    private void removerTabelaSeExistir(String tabela) {
        validarIdentificador(tabela);
        jdbcTemplate.execute("DROP TABLE IF EXISTS " + tabela);
    }

    private void validarIdentificador(String identificador) {
        if (!IDENTIFICADOR_SEGURO.matcher(identificador).matches()) {
            throw new IllegalArgumentException("Identificador SQL inválido: " + identificador);
        }
    }
}
