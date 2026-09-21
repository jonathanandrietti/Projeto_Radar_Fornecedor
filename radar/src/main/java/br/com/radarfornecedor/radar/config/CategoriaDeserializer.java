package br.com.radarfornecedor.radar.config;

import br.com.radarfornecedor.radar.model.Categoria;
import br.com.radarfornecedor.radar.repository.CategoriaRepository;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.Optional;

@Component
public class CategoriaDeserializer extends JsonDeserializer<Categoria> {

    private static CategoriaRepository categoriaRepository;

    @Autowired
    public void setCategoriaRepository(CategoriaRepository repo) {
        CategoriaDeserializer.categoriaRepository = repo;
    }

    @Override
    public Categoria deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        ObjectNode node = p.getCodec().readTree(p);
        
        if (node != null && node.has("id")) {
            try {
                Long id = node.get("id").asLong();
                if (categoriaRepository != null) {
                    Optional<Categoria> categoria = categoriaRepository.findById(id);
                    if (categoria.isPresent()) {
                        System.out.println("[DESERIALIZER] Categoria encontrada: " + id);
                        return categoria.get();
                    } else {
                        System.out.println("[DESERIALIZER] Categoria NÃO encontrada: " + id);
                    }
                } else {
                    System.out.println("[DESERIALIZER] CategoriaRepository é null!");
                }
            } catch (Exception e) {
                System.out.println("[DESERIALIZER] Erro ao desserializar categoria: " + e.getMessage());
                e.printStackTrace();
            }
        }
        
        return null;
    }
}
