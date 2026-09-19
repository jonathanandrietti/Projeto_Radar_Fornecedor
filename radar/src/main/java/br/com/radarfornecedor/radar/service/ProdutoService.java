package br.com.radarfornecedor.radar.service;

import br.com.radarfornecedor.radar.model.CategoriaProduto;
import br.com.radarfornecedor.radar.model.Produto;
import br.com.radarfornecedor.radar.repository.ProdutoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProdutoService {
    private final ProdutoRepository produtoRepository;

    public ProdutoService(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    public List<Produto> listar(CategoriaProduto categoria) {
        return categoria == null ? produtoRepository.findAll() : produtoRepository.findByCategoria(categoria);
    }

    public Produto salvar(Produto produto) {
        return produtoRepository.save(produto);
    }
}
