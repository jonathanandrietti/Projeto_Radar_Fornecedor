package br.com.radarfornecedor.radar.controller;

import br.com.radarfornecedor.radar.model.Comprador;
import br.com.radarfornecedor.radar.service.CompradorService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/compradores")
public class CompradorController {

    private final CompradorService compradorService;

    public CompradorController(CompradorService compradorService) {
        this.compradorService = compradorService;
    }

    @PostMapping
    public ResponseEntity<Comprador> cadastrar(@Valid @RequestBody Comprador comprador) {
        Comprador novoComprador = compradorService.cadastrar(comprador);
        return new ResponseEntity<>(novoComprador, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Comprador>> listarTodos(jakarta.servlet.http.HttpSession session) {
        List<Comprador> lista = compradorService.listarTodos(session);
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Comprador> buscarPorId(@PathVariable Long id) {
        return compradorService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/cnpj/{cnpj}")
    public ResponseEntity<Comprador> buscarPorCnpj(@PathVariable String cnpj) {
        return compradorService.buscarPorCnpj(cnpj)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Comprador> atualizar(@PathVariable Long id, @Valid @RequestBody Comprador comprador) {
        try {
            Comprador atualizado = compradorService.atualizar(id, comprador);
            return ResponseEntity.ok(atualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        try {
            compradorService.excluir(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
