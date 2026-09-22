package br.com.radarfornecedor.radar.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.time.LocalDateTime;

public class AtualizaBancoSQL {
    public static void main(String[] args) {
        String dbPath = "C:\\Visão_Futura\\Projeto_Radar_Fornecedor\\radar\\DBLRadar.db";
        String url = "jdbc:sqlite:" + dbPath;

        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {

            // SQL para adicionar colunas
            String[] alterTableCommands = {
                "ALTER TABLE Categorias ADD COLUMN criadaEm TIMESTAMP",
                "ALTER TABLE Categorias ADD COLUMN atualizadaEm TIMESTAMP",
                "ALTER TABLE Atividades ADD COLUMN criadaEm TIMESTAMP",
                "ALTER TABLE Atividades ADD COLUMN atualizadaEm TIMESTAMP"
            };

            for (String sql : alterTableCommands) {
                try {
                    stmt.executeUpdate(sql);
                    System.out.println("✅ Executado: " + sql);
                } catch (Exception e) {
                    if (e.getMessage().contains("duplicate column name")) {
                        System.out.println("⚠️  Coluna já existe: " + sql);
                    } else {
                        System.out.println("❌ Erro: " + sql);
                        e.printStackTrace();
                    }
                }
            }

            // Atualizar os valores das colunas criadas
            String now = LocalDateTime.now().toString();
            String[] updateCommands = {
                "UPDATE Categorias SET criadaEm = '" + now + "' WHERE criadaEm IS NULL",
                "UPDATE Categorias SET atualizadaEm = '" + now + "' WHERE atualizadaEm IS NULL",
                "UPDATE Atividades SET criadaEm = '" + now + "' WHERE criadaEm IS NULL",
                "UPDATE Atividades SET atualizadaEm = '" + now + "' WHERE atualizadaEm IS NULL"
            };

            for (String sql : updateCommands) {
                try {
                    int rowsAffected = stmt.executeUpdate(sql);
                    System.out.println("✅ Atualizado " + rowsAffected + " registros: " + sql.substring(0, 50) + "...");
                } catch (Exception e) {
                    System.out.println("⚠️  " + sql.substring(0, 50) + "... - " + e.getMessage());
                }
            }

            System.out.println("\n✅ Banco atualizado com sucesso!");

        } catch (Exception e) {
            System.err.println("❌ Erro ao conectar ao banco: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
