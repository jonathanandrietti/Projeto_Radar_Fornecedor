package br.com.radarfornecedor.radar.controller;

import br.com.radarfornecedor.radar.dto.PagamentoRequest;
import br.com.radarfornecedor.radar.model.Pagamento;
import br.com.radarfornecedor.radar.service.PagamentoService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController @RequestMapping("/api/pagamentos")
public class PagamentoController {
    private final PagamentoService pagamentoService;
    public PagamentoController(PagamentoService pagamentoService) { this.pagamentoService = pagamentoService; }
    @PostMapping public ResponseEntity<?> criar(@Valid @RequestBody PagamentoRequest dados, HttpSession session) { try { return ResponseEntity.status(HttpStatus.CREATED).body(pagamentoService.criar(dados, session)); } catch (IllegalArgumentException | IllegalStateException e) { return ResponseEntity.badRequest().body(e.getMessage()); } }
    @PutMapping("/{id}/confirmar") public ResponseEntity<?> confirmar(@PathVariable Long id, HttpSession session) { try { return ResponseEntity.ok(pagamentoService.confirmar(id, session)); } catch (IllegalArgumentException | IllegalStateException e) { return ResponseEntity.badRequest().body(e.getMessage()); } }
    @GetMapping public ResponseEntity<?> listar(HttpSession session) { try { return ResponseEntity.ok(pagamentoService.listarDoCliente(session)); } catch (IllegalStateException e) { return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage()); } }
}
