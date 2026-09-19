package br.com.radarfornecedor.radar.controller;

import br.com.radarfornecedor.radar.model.Representante;
import br.com.radarfornecedor.radar.service.RepresentanteService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/representantes")
public class RepresentanteController {

    private final RepresentanteService representanteService;

    public RepresentanteController(RepresentanteService representanteService) {
        this.representanteService = representanteService;
    }

    @PostMapping
    public ResponseEntity<Representante> cadastrar(@Valid @RequestBody Representante representante, HttpSession session) {
        Representante novo = representanteService.cadastrar(representante, session);
        return new ResponseEntity<>(novo, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Representante>> listarTodos(HttpSession session) {
        List<Representante> lista = representanteService.listarTodos(session);
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Representante> buscarPorId(@PathVariable Long id) {
        return representanteService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Representante> atualizar(@PathVariable Long id, @Valid @RequestBody Representante representante, HttpSession session) {
        try {
            Representante atualizado = representanteService.atualizar(id, representante, session);
            return ResponseEntity.ok(atualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id, HttpSession session) {
        try {
            representanteService.excluir(id, session);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
