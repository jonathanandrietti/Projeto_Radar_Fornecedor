package br.com.radarfornecedor.radar.controller;

import br.com.radarfornecedor.radar.service.BackupService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/backup")
public class BackupController {

    private final BackupService backupService;

    public BackupController(BackupService backupService) {
        this.backupService = backupService;
    }

    @PostMapping("/fazer")
    public ResponseEntity<?> fazerBackup() {
        try {
            boolean sucesso = backupService.fazerBackup();
            if (sucesso) {
                return ResponseEntity.ok(Map.of("mensagem", "Backup realizado com sucesso"));
            } else {
                return ResponseEntity.status(500).body(Map.of("erro", "Erro ao fazer backup"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("erro", e.getMessage()));
        }
    }

    @PostMapping("/restaurar")
    public ResponseEntity<?> restaurarBackup() {
        try {
            boolean sucesso = backupService.restaurarBackup();
            if (sucesso) {
                return ResponseEntity.ok(Map.of("mensagem", "Banco restaurado com sucesso. Reinicie o servidor para aplicar as mudanças."));
            } else {
                return ResponseEntity.status(500).body(Map.of("erro", "Erro ao restaurar backup"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("erro", e.getMessage()));
        }
    }

    @GetMapping("/listar")
    public ResponseEntity<?> listarBackups() {
        try {
            return ResponseEntity.ok(Map.of("backups", backupService.listarBackups()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("erro", e.getMessage()));
        }
    }
}
