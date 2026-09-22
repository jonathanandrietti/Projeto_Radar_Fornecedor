package br.com.radarfornecedor.radar.service;

import org.springframework.stereotype.Service;
import java.io.*;
import java.nio.file.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class BackupService {

    private static final String BACKUP_DIR = "BackUpBanco";
    private static final String DB_FILE = "DBLRadar.mv.db";
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd_HHmmss");

    public BackupService() {
        // Criar pasta de backup se não existir
        Path backupPath = Paths.get(BACKUP_DIR);
        if (!Files.exists(backupPath)) {
            try {
                Files.createDirectories(backupPath);
                System.out.println("[BACKUP SERVICE] Pasta BackUpBanco criada com sucesso.");
            } catch (IOException e) {
                System.err.println("[BACKUP SERVICE] Erro ao criar pasta BackUpBanco: " + e.getMessage());
            }
        }
    }

    /**
     * Fazer backup do banco de dados via H2 Backup
     * Mantém apenas o último backup
     */
    public boolean fazerBackup() {
        try {
            // Criar timestamp
            String timestamp = LocalDateTime.now().format(DATE_FORMAT);
            String backupFileName = "backup_" + timestamp + ".zip";
            Path backupPath = Paths.get(BACKUP_DIR, backupFileName);

            // Usar comando H2 BACKUP para fazer backup com banco aberto
            String backupSQL = "BACKUP TO '" + backupPath.toString().replace("\\", "/") + "'";
            
            // Executar via JDBC (via DataSource do Spring)
            // Para isso, usar arquivo de script SQL
            executarBackup(backupSQL);

            System.out.println("[BACKUP SERVICE] Backup criado: " + backupPath.toString());

            // DEPOIS de criar o novo backup, deletar antigos
            limparBackupsAntigos();
            
            return true;
        } catch (Exception e) {
            System.err.println("[BACKUP SERVICE] Erro ao fazer backup: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Executar comando de backup SQL
     * (será chamado por um cliente que consegue acessar a conexão)
     */
    private void executarBackup(String sql) throws Exception {
        // Conectar ao banco H2
        Class.forName("org.h2.Driver");
        try (Connection conn = DriverManager.getConnection("jdbc:h2:./DBLRadar", "sa", "")) {
            try (Statement stmt = conn.createStatement()) {
                stmt.execute(sql);
                System.out.println("[BACKUP SERVICE] Comando BACKUP executado com sucesso.");
            }
        }
    }

    /**
     * Restaurar banco de dados a partir do último backup
     */
    public boolean restaurarBackup() {
        try {
            // Encontrar último backup
            Path ultimoBackup = encontrarUltimoBackup();
            if (ultimoBackup == null) {
                System.err.println("[BACKUP SERVICE] Nenhum backup encontrado para restaurar.");
                return false;
            }

            // Executar RESTORE via SQL
            String restoreSQL = "RESTORE FROM '" + ultimoBackup.toString().replace("\\", "/") + "'";
            
            // Parar conexões antes de restaurar
            Class.forName("org.h2.Driver");
            try (Connection conn = DriverManager.getConnection("jdbc:h2:./DBLRadar", "sa", "")) {
                try (Statement stmt = conn.createStatement()) {
                    stmt.execute(restoreSQL);
                    System.out.println("[BACKUP SERVICE] Banco restaurado de: " + ultimoBackup.toString());
                }
            }
            return true;
        } catch (Exception e) {
            System.err.println("[BACKUP SERVICE] Erro ao restaurar backup: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Deletar todos os backups antigos, mantendo apenas o último
     */
    private void limparBackupsAntigos() {
        try {
            Path backupDir = Paths.get(BACKUP_DIR);
            if (!Files.exists(backupDir)) {
                return;
            }

            // Listar todos os backups
            DirectoryStream<Path> stream = Files.newDirectoryStream(backupDir, "backup_*.zip");
            java.util.List<Path> backups = new java.util.ArrayList<>();
            for (Path path : stream) {
                backups.add(path);
            }
            stream.close();

            // Ordenar por data (mais recente primeiro)
            backups.sort((a, b) -> b.getFileName().toString().compareTo(a.getFileName().toString()));

            // Deletar todos exceto o último
            for (int i = 1; i < backups.size(); i++) {
                Files.delete(backups.get(i));
                System.out.println("[BACKUP SERVICE] Backup antigo removido: " + backups.get(i).getFileName());
            }
        } catch (IOException e) {
            System.err.println("[BACKUP SERVICE] Erro ao limpar backups antigos: " + e.getMessage());
        }
    }

    /**
     * Encontrar o último backup criado
     */
    private Path encontrarUltimoBackup() {
        try {
            Path backupDir = Paths.get(BACKUP_DIR);
            if (!Files.exists(backupDir)) {
                return null;
            }

            DirectoryStream<Path> stream = Files.newDirectoryStream(backupDir, "backup_*.zip");
            Path ultimoBackup = null;
            for (Path path : stream) {
                if (ultimoBackup == null || 
                    path.getFileName().toString().compareTo(ultimoBackup.getFileName().toString()) > 0) {
                    ultimoBackup = path;
                }
            }
            stream.close();
            return ultimoBackup;
        } catch (IOException e) {
            System.err.println("[BACKUP SERVICE] Erro ao encontrar último backup: " + e.getMessage());
            return null;
        }
    }

    /**
     * Listar todos os backups disponíveis
     */
    public java.util.List<String> listarBackups() {
        java.util.List<String> backups = new java.util.ArrayList<>();
        try {
            Path backupDir = Paths.get(BACKUP_DIR);
            if (!Files.exists(backupDir)) {
                return backups;
            }

            DirectoryStream<Path> stream = Files.newDirectoryStream(backupDir, "backup_*.zip");
            for (Path path : stream) {
                backups.add(path.getFileName().toString());
            }
            stream.close();
            backups.sort(java.util.Collections.reverseOrder());
        } catch (IOException e) {
            System.err.println("[BACKUP SERVICE] Erro ao listar backups: " + e.getMessage());
        }
        return backups;
    }
}

