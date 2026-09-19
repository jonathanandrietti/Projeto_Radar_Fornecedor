package br.com.radarfornecedor.radar.controller;

import br.com.radarfornecedor.radar.dto.AvaliacaoRequest;
import br.com.radarfornecedor.radar.dto.RankingEmpresaResponse;
import br.com.radarfornecedor.radar.model.AvaliacaoProduto;
import br.com.radarfornecedor.radar.service.AvaliacaoProdutoService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/avaliacoes")
public class AvaliacaoProdutoController {
    private final AvaliacaoProdutoService avaliacaoService;

    public AvaliacaoProdutoController(AvaliacaoProdutoService avaliacaoService) {
        this.avaliacaoService = avaliacaoService;
    }

    @PostMapping
    public ResponseEntity<?> avaliar(@Valid @RequestBody AvaliacaoRequest dados, HttpSession session) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(avaliacaoService.avaliar(dados, session));
        } catch (IllegalArgumentException | IllegalStateException erro) {
            return ResponseEntity.badRequest().body(erro.getMessage());
        }
    }

    @GetMapping("/produto/{produtoId}")
    public List<AvaliacaoProduto> listarPorProduto(@PathVariable Long produtoId) {
        return avaliacaoService.listarPorProduto(produtoId);
    }

    @GetMapping("/ranking-empresas")
    public List<RankingEmpresaResponse> rankingEmpresas() {
        return avaliacaoService.rankingEmpresas();
    }
}
