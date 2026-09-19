package br.com.radarfornecedor.radar.controller;

import br.com.radarfornecedor.radar.model.Atividade;
import br.com.radarfornecedor.radar.service.AtividadeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/atividades")
public class AtividadeController {

    @Autowired
    private AtividadeService atividadeService;

    @PostMapping
    public ResponseEntity<Atividade> cadastrar(@RequestBody Atividade atividade) {
        Atividade nova = atividadeService.cadastrar(atividade);
        return ResponseEntity.status(HttpStatus.CREATED).body(nova);
    }

    @GetMapping
    public ResponseEntity<List<Atividade>> listarTodas() {
        List<Atividade> atividades = atividadeService.listarTodas();
        return ResponseEntity.ok(atividades);
    }

    @GetMapping("/ativas")
    public ResponseEntity<List<Atividade>> listarAtivas() {
        List<Atividade> atividades = atividadeService.listarAtivas();
        return ResponseEntity.ok(atividades);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Atividade> buscarPorId(@PathVariable Long id) {
        return atividadeService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/cnae/{cnae}")
    public ResponseEntity<Atividade> buscarPorCnae(@PathVariable String cnae) {
        return atividadeService.buscarPorCnae(cnae)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/descricao/{descricao}")
    public ResponseEntity<Atividade> buscarPorDescricao(@PathVariable String descricao) {
        return atividadeService.buscarPorDescricao(descricao)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/secao/{secao}")
    public ResponseEntity<List<Atividade>> buscarPorSecao(@PathVariable String secao) {
        List<Atividade> atividades = atividadeService.buscarPorSecao(secao);
        return ResponseEntity.ok(atividades);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Atividade> atualizar(@PathVariable Long id, @RequestBody Atividade atividade) {
        try {
            Atividade atualizada = atividadeService.atualizar(id, atividade);
            return ResponseEntity.ok(atualizada);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        atividadeService.excluir(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/desativar")
    public ResponseEntity<Void> desativar(@PathVariable Long id) {
        atividadeService.desativar(id);
        return ResponseEntity.noContent().build();
    }
}
