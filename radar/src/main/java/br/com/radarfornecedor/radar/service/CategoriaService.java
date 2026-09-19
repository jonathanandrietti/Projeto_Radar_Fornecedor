package br.com.radarfornecedor.radar.service;

import br.com.radarfornecedor.radar.model.Categoria;
import br.com.radarfornecedor.radar.repository.CategoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoriaService {

    @Autowired
    private CategoriaRepository categoriaRepository;

    public Categoria cadastrar(Categoria categoria) {
        return categoriaRepository.save(categoria);
    }

    public List<Categoria> listarTodas() {
        return categoriaRepository.findAll();
    }

    public List<Categoria> listarAtivas() {
        return categoriaRepository.findByAtivaTrue();
    }

    public Optional<Categoria> buscarPorId(Long id) {
        return categoriaRepository.findById(id);
    }

    public Optional<Categoria> buscarPorNome(String nome) {
        return categoriaRepository.findByNome(nome);
    }

    public Categoria atualizar(Long id, Categoria categoria) {
        return categoriaRepository.findById(id).map(cat -> {
            cat.setNome(categoria.getNome());
            cat.setDescricao(categoria.getDescricao());
            cat.setIcone(categoria.getIcone());
            cat.setAtiva(categoria.getAtiva());
            return categoriaRepository.save(cat);
        }).orElseThrow(() -> new RuntimeException("Categoria não encontrada com id: " + id));
    }

    public void excluir(Long id) {
        categoriaRepository.deleteById(id);
    }

    public void desativar(Long id) {
        categoriaRepository.findById(id).ifPresent(cat -> {
            cat.setAtiva(false);
            categoriaRepository.save(cat);
        });
    }
}
