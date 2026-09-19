package br.com.radarfornecedor.radar.config;

import java.util.regex.Pattern;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

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
        String sql = "CREATE TABLE IF NOT EXISTS schema_migrations " +
                     "(versao INTEGER PRIMARY KEY, " +
                     "descricao TEXT NOT NULL, " +
                     "executada_em TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP)";
        jdbcTemplate.execute(sql);
    }

    private void executarAtualizacoes() {
        // Versao 10
        aplicarAtualizacao(10, "Cria tabelas Paises e Cidades", () -> {
            String sql1 = "CREATE TABLE IF NOT EXISTS Paises " +
                         "(CodPais INTEGER PRIMARY KEY, Pais TEXT NOT NULL)";
            String sql2 = "CREATE TABLE IF NOT EXISTS Cidades " +
                         "(CodCidade INTEGER PRIMARY KEY, Cidade TEXT NOT NULL, " +
                         "UF TEXT NOT NULL, CodRegiao INTEGER, Alterado INTEGER DEFAULT 0, " +
                         "CodCidadeIBGE INTEGER NOT NULL UNIQUE, CodPais INTEGER NOT NULL)";
            executarSql(sql1);
            executarSql(sql2);
        });

        // Versao 11
        aplicarAtualizacao(11, "Adiciona CodCidade a fornecedores e compradores", () -> {
            adicionarColunaSeNaoExistir("Fornecedores", "CodCidade", "INTEGER");
            adicionarColunaSeNaoExistir("Compradores", "CodCidade", "INTEGER");
        });

        // Versao 12
        aplicarAtualizacao(12, "Carga inicial de paises", this::importarPaises);

        // Versao 20
        aplicarAtualizacao(20, "Cria tabelas de Usuarios, Representantes e Clientes", () -> {
            String sql1 = "CREATE TABLE IF NOT EXISTS Usuarios " +
                         "(ID INTEGER PRIMARY KEY AUTOINCREMENT, Username TEXT NOT NULL UNIQUE, " +
                         "Senha TEXT NOT NULL, Tipo TEXT NOT NULL)";
            String sql2 = "CREATE TABLE IF NOT EXISTS Representantes " +
                         "(ID INTEGER PRIMARY KEY AUTOINCREMENT, Nome TEXT NOT NULL, status TEXT)";
            String sql3 = "CREATE TABLE IF NOT EXISTS Clientes " +
                         "(ID INTEGER PRIMARY KEY AUTOINCREMENT, Nome TEXT NOT NULL, status TEXT)";
            executarSql(sql1);
            executarSql(sql2);
            executarSql(sql3);
        });
    }

    private void aplicarAtualizacao(int versao, String descricao, Runnable atualizacao) {
        try {
            Integer encontrada = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM schema_migrations WHERE versao = ?", Integer.class, versao);
            if (encontrada != null && encontrada > 0) {
                return;
            }
        } catch (Exception e) {
            // Tabela pode nao existir ainda
        }

        try {
            atualizacao.run();
            jdbcTemplate.update("INSERT INTO schema_migrations (versao, descricao) VALUES (?, ?)", versao, descricao);
        } catch (Exception e) {
            System.err.println("Erro ao aplicar atualizacao " + versao + ": " + e.getMessage());
        }
    }

    private void executarSql(String sql) {
        jdbcTemplate.execute(sql);
    }

    private void importarPaises() {
        jdbcTemplate.update("INSERT OR IGNORE INTO Paises (CodPais, Pais) VALUES (?, ?)", 1058, "BRASIL");
        jdbcTemplate.update("INSERT OR IGNORE INTO Paises (CodPais, Pais) VALUES (?, ?)", 9999999, "EXTERIOR");
    }

    private void adicionarColunaSeNaoExistir(String tabela, String coluna, String definicao) {
        validarIdentificador(tabela);
        validarIdentificador(coluna);

        try {
            boolean colunaExiste = jdbcTemplate.query("PRAGMA table_info(" + tabela + ")",
                    (rs, rowNum) -> rs.getString("name")).stream().anyMatch(coluna::equalsIgnoreCase);

            if (!colunaExiste) {
                String sql = "ALTER TABLE " + tabela + " ADD COLUMN " + coluna + " " + definicao;
                jdbcTemplate.execute(sql);
            }
        } catch (Exception e) {
            System.err.println("Erro ao adicionar coluna " + coluna + " em " + tabela + ": " + e.getMessage());
        }
    }

    private void validarIdentificador(String identificador) {
        if (!IDENTIFICADOR_SEGURO.matcher(identificador).matches()) {
            throw new IllegalArgumentException("Identificador SQL invalido: " + identificador);
        }
    }
}
