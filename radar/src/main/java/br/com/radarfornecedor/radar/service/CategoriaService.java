package br.com.radarfornecedor.radar.service;

import br.com.radarfornecedor.radar.model.Categoria;
import br.com.radarfornecedor.radar.repository.CategoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Optional;

@Service
public class CategoriaService {

    @Autowired
    private CategoriaRepository categoriaRepository;

    @PostConstruct
    public void inicializarDados() {
        // Se tabela está vazia, inserir dados de teste
        if (categoriaRepository.count() == 0) {
            Categoria cat1 = new Categoria();
            cat1.setNome("Eletrônicos");
            cat1.setDescricao("Produtos eletrônicos em geral");
            cat1.setIcone("electronics");
            cat1.setAtiva(true);
            
            Categoria cat2 = new Categoria();
            cat2.setNome("Alimentos");
            cat2.setDescricao("Produtos alimentícios");
            cat2.setIcone("food");
            cat2.setAtiva(true);
            
            Categoria cat3 = new Categoria();
            cat3.setNome("Vestuário");
            cat3.setDescricao("Roupas e acessórios");
            cat3.setIcone("clothing");
            cat3.setAtiva(true);
            
            Categoria cat4 = new Categoria();
            cat4.setNome("Automóvel");
            cat4.setDescricao("Veículos automotores");
            cat4.setIcone("car");
            cat4.setAtiva(true);
            
            Categoria cat5 = new Categoria();
            cat5.setNome("Motocicletas");
            cat5.setDescricao("Motos e similares");
            cat5.setIcone("motorcycle");
            cat5.setAtiva(true);
            
            Categoria cat6 = new Categoria();
            cat6.setNome("Moda e Têxtil");
            cat6.setDescricao("Moda, tecidos e confecções");
            cat6.setIcone("fashion");
            cat6.setAtiva(true);
            
            Categoria cat7 = new Categoria();
            cat7.setNome("Embalagens");
            cat7.setDescricao("Embalagens e materiais de proteção");
            cat7.setIcone("packaging");
            cat7.setAtiva(true);
            
            Categoria cat8 = new Categoria();
            cat8.setNome("Tecnologia");
            cat8.setDescricao("Softwares e serviços tecnológicos");
            cat8.setIcone("technology");
            cat8.setAtiva(true);
            
            Categoria cat9 = new Categoria();
            cat9.setNome("Construção");
            cat9.setDescricao("Materiais de construção");
            cat9.setIcone("construction");
            cat9.setAtiva(true);
            
            Categoria cat10 = new Categoria();
            cat10.setNome("Móvel e Escritório");
            cat10.setDescricao("Móveis e materiais de escritório");
            cat10.setIcone("furniture");
            cat10.setAtiva(true);
            
            categoriaRepository.saveAll(List.of(cat1, cat2, cat3, cat4, cat5, cat6, cat7, cat8, cat9, cat10));
            System.out.println("[INIT] 10 categorias inseridas com sucesso");
        }
    }

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
