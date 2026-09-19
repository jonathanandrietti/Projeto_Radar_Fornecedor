package br.com.radarfornecedor.radar.repository;

import br.com.radarfornecedor.radar.model.AvaliacaoProduto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AvaliacaoProdutoRepository extends JpaRepository<AvaliacaoProduto, Long> {
    List<AvaliacaoProduto> findByProdutoIdOrderByCriadoEmDesc(Long produtoId);
}
