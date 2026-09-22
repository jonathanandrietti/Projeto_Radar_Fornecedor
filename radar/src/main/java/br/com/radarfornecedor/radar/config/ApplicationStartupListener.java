package br.com.radarfornecedor.radar.config;

import br.com.radarfornecedor.radar.service.BackupService;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class ApplicationStartupListener {

    private final BackupService backupService;

    public ApplicationStartupListener(BackupService backupService) {
        this.backupService = backupService;
    }

    /**
     * Fazer backup automático quando a aplicação inicia
     */
    @EventListener(ApplicationReadyEvent.class)
    public void fazerBackupAoIniciar() {
        System.out.println("[APPLICATION STARTUP] Iniciando backup automático do banco de dados...");
        boolean sucesso = backupService.fazerBackup();
        if (sucesso) {
            System.out.println("[APPLICATION STARTUP] Backup automático concluído com sucesso.");
        } else {
            System.err.println("[APPLICATION STARTUP] Falha ao fazer backup automático.");
        }
    }
}
