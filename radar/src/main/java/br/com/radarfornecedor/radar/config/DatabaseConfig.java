package br.com.radarfornecedor.radar.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import java.io.File;

/**
 * Configuração para garantir que o banco de dados está sempre no caminho correto:
 * ...Projeto_Radar_Fornecedor\radar\DBLRadar.db
 * 
 * Conforme REGRA 01 do projeto.
 */
@Configuration
public class DatabaseConfig {
    
    static {
        // Define o caminho correto do banco de dados
        // Garante que está na pasta /radar/ independente de onde o Spring Boot é executado
        String correctDbPath = resolveDatabasePath();
        System.setProperty("spring.datasource.url", "jdbc:sqlite:" + correctDbPath);
    }
    
    /**
     * Resolve o caminho correto do banco de dados
     * Procura por "radar/DBLRadar.db" relativo à raiz do projeto
     */
    private static String resolveDatabasePath() {
        // Tentar encontrar a pasta "radar" na estrutura do projeto
        String userDir = System.getProperty("user.dir");
        File radarDir = new File(userDir);
        
        // Se estamos em /radar, o banco está aqui
        if (radarDir.getName().equals("radar")) {
            String dbPath = new File(radarDir, "DBLRadar.db").getAbsolutePath();
            System.out.println("[DATABASE CONFIG] Usando banco em: " + dbPath);
            return dbPath;
        }
        
        // Se estamos em /Projeto_Radar_Fornecedor, o banco está em /radar
        if (new File(radarDir, "radar").exists()) {
            String dbPath = new File(new File(radarDir, "radar"), "DBLRadar.db").getAbsolutePath();
            System.out.println("[DATABASE CONFIG] Usando banco em: " + dbPath);
            return dbPath;
        }
        
        // Fallback: procurar subpasta "radar" no diretório atual
        for (File dir : radarDir.listFiles((d, n) -> d.isDirectory())) {
            if (dir.getName().equals("radar")) {
                String dbPath = new File(dir, "DBLRadar.db").getAbsolutePath();
                System.out.println("[DATABASE CONFIG] Usando banco em: " + dbPath);
                return dbPath;
            }
        }
        
        // Último fallback (não deve chegar aqui se tudo estiver correto)
        String fallbackPath = new File(radarDir, "DBLRadar.db").getAbsolutePath();
        System.out.println("[DATABASE CONFIG] AVISO: Usando fallback em: " + fallbackPath);
        return fallbackPath;
    }
}
