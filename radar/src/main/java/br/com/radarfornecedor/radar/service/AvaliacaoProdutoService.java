package br.com.radarfornecedor.radar.service;

import br.com.radarfornecedor.radar.dto.AvaliacaoRequest;
import br.com.radarfornecedor.radar.dto.RankingEmpresaResponse;
import br.com.radarfornecedor.radar.model.AvaliacaoProduto;
import br.com.radarfornecedor.radar.model.Fornecedor;
import br.com.radarfornecedor.radar.model.Produto;
import br.com.radarfornecedor.radar.model.Usuario;
import br.com.radarfornecedor.radar.repository.AvaliacaoProdutoRepository;
import br.com.radarfornecedor.radar.repository.FornecedorRepository;
import br.com.radarfornecedor.radar.repository.ProdutoRepository;
import javax.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AvaliacaoProdutoService {
    private final AvaliacaoProdutoRepository avaliacaoRepository;
    private final ProdutoRepository produtoRepository;
    private final FornecedorRepository fornecedorRepository;

    public AvaliacaoProdutoService(AvaliacaoProdutoRepository avaliacaoRepository, ProdutoRepository produtoRepository,
                                   FornecedorRepository fornecedorRepository) {
        this.avaliacaoRepository = avaliacaoRepository;
        this.produtoRepository = produtoRepository;
        this.fornecedorRepository = fornecedorRepository;
    }

    public AvaliacaoProduto avaliar(AvaliacaoRequest dados, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null || usuario.getTipo() != br.com.radarfornecedor.radar.model.TipoUsuario.CLIENTE) {
            throw new IllegalStateException("Apenas clientes podem avaliar produtos.");
        }

        Produto produto = produtoRepository.findById(dados.getProdutoId())
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado."));
        if (produto.getFornecedorId() == null) {
            throw new IllegalArgumentException("Este produto ainda não está vinculado a uma empresa.");
        }

        AvaliacaoProduto avaliacao = new AvaliacaoProduto();
        avaliacao.setProdutoId(produto.getId());
        avaliacao.setFornecedorId(produto.getFornecedorId());
        avaliacao.setCliente(usuario.getUsername());
        avaliacao.setNota(dados.getNota());
        avaliacao.setComentario(dados.getComentario().trim());
        return avaliacaoRepository.save(avaliacao);
    }

    public List<AvaliacaoProduto> listarPorProduto(Long produtoId) {
        return avaliacaoRepository.findByProdutoIdOrderByCriadoEmDesc(produtoId);
    }

    public List<RankingEmpresaResponse> rankingEmpresas() {
        Map<Long, List<AvaliacaoProduto>> porEmpresa = avaliacaoRepository.findAll().stream()
                .collect(Collectors.groupingBy(AvaliacaoProduto::getFornecedorId));
        return porEmpresa.entrySet().stream().map(entry -> {
            Fornecedor fornecedor = fornecedorRepository.findById(entry.getKey()).orElse(null);
            String empresa = fornecedor == null ? "Empresa não encontrada" : fornecedor.getNome();
            double media = entry.getValue().stream().mapToInt(AvaliacaoProduto::getNota).average().orElse(0);
            return new RankingEmpresaResponse(entry.getKey(), empresa, media, entry.getValue().size());
        }).sorted(Comparator.comparingDouble(RankingEmpresaResponse::getMedia).reversed()
                .thenComparing(Comparator.comparingLong(RankingEmpresaResponse::getTotalAvaliacoes).reversed()))
                .collect(Collectors.toList());
    }
}
