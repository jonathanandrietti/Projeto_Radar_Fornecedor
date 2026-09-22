import java.sql.*;

/**
 * Script de inspecao do banco de dados SQLite DBLRadar.db
 */
public class InspectDatabase {
    private static final String DB_URL = "jdbc:sqlite:DBLRadar.db";
    private static Connection conn;

    public static void main(String[] args) {
        try {
            connectToDatabase();
            
            System.out.println("\n" + "=".repeat(60));
            System.out.println("INSPECAO COMPLETA DO BANCO DBLRadar.db");
            System.out.println("=".repeat(60));
            
            listAllTables();
            checkSchemaMigrationsTable();
            countRecords();
            listTableSchemas();
            showSampleData();
            
            System.out.println("\n" + "=".repeat(60));
            System.out.println("INSPECAO CONCLUIDA");
            System.out.println("=".repeat(60) + "\n");
            
            closeConnection();
        } catch (SQLException e) {
            System.err.println("ERRO: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void connectToDatabase() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.err.println("Driver SQLite nao encontrado.");
        }
        
        conn = DriverManager.getConnection(DB_URL);
        System.out.println("[OK] Conectado ao banco: " + DB_URL);
    }

    private static void listAllTables() throws SQLException {
        System.out.println("\n" + "-".repeat(60));
        System.out.println("1. TODAS AS TABELAS DO BANCO");
        System.out.println("-".repeat(60));
        
        String sql = "SELECT name, type, sql FROM sqlite_master WHERE type='table' ORDER BY name";
        
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            int count = 0;
            while (rs.next()) {
                count++;
                String name = rs.getString("name");
                String sql_def = rs.getString("sql");
                System.out.printf("  %d. %s\n", count, name);
                if (sql_def != null && !sql_def.isEmpty()) {
                    System.out.printf("     DDL: %s\n", sql_def.substring(0, Math.min(80, sql_def.length())));
                }
            }
            
            if (count == 0) {
                System.out.println("  [AVISO] Nenhuma tabela encontrada!");
            }
        }
    }

    private static void checkSchemaMigrationsTable() throws SQLException {
        System.out.println("\n" + "-".repeat(60));
        System.out.println("2. VERIFICANDO TABELA 'schema_migrations'");
        System.out.println("-".repeat(60));
        
        String sql = "SELECT name FROM sqlite_master WHERE type='table' AND name='schema_migrations'";
        
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                System.out.println("[OK] Tabela 'schema_migrations' EXISTE");
                
                String contentSql = "SELECT * FROM schema_migrations ORDER BY version";
                try (Statement stmt2 = conn.createStatement();
                     ResultSet rs2 = stmt2.executeQuery(contentSql)) {
                    
                    System.out.println("\n  Versoes executadas:");
                    while (rs2.next()) {
                        try {
                            long version = rs2.getLong("version");
                            String description = rs2.getString("description");
                            String type = rs2.getString("type");
                            int success = rs2.getInt("success");
                            String status = success == 1 ? "[OK]" : "[ERRO]";
                            System.out.printf("    - v%d: %s [%s] %s\n", version, description, type, status);
                        } catch (SQLException e) {
                            System.out.println("    " + rs2.getString(1));
                        }
                    }
                }
            } else {
                System.out.println("[AVISO] Tabela 'schema_migrations' NAO EXISTE");
                System.out.println("  Migracoes nao foram registradas!");
            }
        }
    }

    private static void countRecords() throws SQLException {
        System.out.println("\n" + "-".repeat(60));
        System.out.println("3. CONTAGEM DE REGISTROS NAS TABELAS PRINCIPAIS");
        System.out.println("-".repeat(60));
        
        String[] tables = {"Categorias", "Atividades", "fornecedores", "compradores", "representantes"};
        
        for (String table : tables) {
            try {
                String sql = "SELECT COUNT(*) as count FROM " + table;
                try (Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery(sql)) {
                    
                    if (rs.next()) {
                        long count = rs.getLong("count");
                        String status = count == 0 ? "[VAZIO]" : "[OK]";
                        System.out.printf("  %-20s: %6d registros %s\n", table, count, status);
                    }
                }
            } catch (SQLException e) {
                System.out.printf("  %-20s: [ERRO] %s\n", table, e.getMessage());
            }
        }
    }

    private static void listTableSchemas() throws SQLException {
        System.out.println("\n" + "-".repeat(60));
        System.out.println("4. SCHEMA (COLUNAS) DE CADA TABELA PRINCIPAL");
        System.out.println("-".repeat(60));
        
        String[] tables = {"Categorias", "Atividades", "fornecedores", "compradores", "representantes"};
        
        for (String table : tables) {
            System.out.println("\n  --- TABELA: " + table + " ---");
            try {
                String sql = "PRAGMA table_info(" + table + ")";
                try (Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery(sql)) {
                    
                    System.out.println("    | cid | name                 | type         | notnull | dflt | pk |");
                    System.out.println("    |----|----------------------|--------------|---------|------|-----|");
                    
                    while (rs.next()) {
                        int cid = rs.getInt("cid");
                        String name = rs.getString("name");
                        String type = rs.getString("type");
                        int notnull = rs.getInt("notnull");
                        String dflt = rs.getString("dflt_value");
                        int pk = rs.getInt("pk");
                        
                        String dfltStr = dflt != null ? dflt : "null";
                        System.out.printf("    | %d | %-20s | %-12s | %7s | %-4s | %3d |\n", 
                            cid, name, type, (notnull == 1 ? "YES" : "NO"), dfltStr, pk);
                    }
                }
            } catch (SQLException e) {
                System.out.printf("    [ERRO] %s\n", e.getMessage());
            }
        }
    }

    private static void showSampleData() throws SQLException {
        System.out.println("\n" + "-".repeat(60));
        System.out.println("5. PRIMEIROS 3 REGISTROS DE CADA TABELA");
        System.out.println("-".repeat(60));
        
        String[] tables = {"Categorias", "Atividades", "fornecedores", "compradores", "representantes"};
        
        for (String table : tables) {
            System.out.println("\n  --- TABELA: " + table + " ---");
            try {
                String sql = "SELECT * FROM " + table + " LIMIT 3";
                try (Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery(sql)) {
                    
                    ResultSetMetaData meta = rs.getMetaData();
                    int columnCount = meta.getColumnCount();
                    
                    System.out.print("    ");
                    for (int i = 1; i <= columnCount; i++) {
                        System.out.print(String.format("%-15s ", meta.getColumnName(i)));
                    }
                    System.out.println();
                    
                    int rowCount = 0;
                    while (rs.next()) {
                        rowCount++;
                        System.out.print("    ");
                        for (int i = 1; i <= columnCount; i++) {
                            Object val = rs.getObject(i);
                            String valStr = val == null ? "null" : val.toString();
                            if (valStr.length() > 15) {
                                valStr = valStr.substring(0, 12) + "...";
                            }
                            System.out.print(String.format("%-15s ", valStr));
                        }
                        System.out.println();
                    }
                    
                    if (rowCount == 0) {
                        System.out.println("    (nenhum registro)");
                    }
                }
            } catch (SQLException e) {
                System.out.printf("    [ERRO] %s\n", e.getMessage());
            }
        }
    }

    private static void closeConnection() throws SQLException {
        if (conn != null && !conn.isClosed()) {
            conn.close();
            System.out.println("[OK] Conexao fechada");
        }
    }
}
