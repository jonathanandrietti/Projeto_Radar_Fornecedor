package br.com.radarfornecedor.radar.config;

import br.com.radarfornecedor.radar.model.Atividade;
import br.com.radarfornecedor.radar.repository.AtividadeRepository;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

@Component
public class AtividadeDeserializer extends JsonDeserializer<Atividade> {

    private static AtividadeRepository atividadeRepository;

    @Autowired
    public void setAtividadeRepository(AtividadeRepository repo) {
        AtividadeDeserializer.atividadeRepository = repo;
    }

    @Override
    public Atividade deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        ObjectNode node = p.getCodec().readTree(p);
        
        if (node != null && node.has("id")) {
            try {
                Long id = node.get("id").asLong();
                if (atividadeRepository != null) {
                    Optional<Atividade> atividade = atividadeRepository.findById(id);
                    if (atividade.isPresent()) {
                        System.out.println("[DESERIALIZER] Atividade encontrada: " + id);
                        return atividade.get();
                    } else {
                        System.out.println("[DESERIALIZER] Atividade NÃO encontrada: " + id);
                    }
                } else {
                    System.out.println("[DESERIALIZER] AtividadeRepository é null!");
                }
            } catch (Exception e) {
                System.out.println("[DESERIALIZER] Erro ao desserializar atividade: " + e.getMessage());
                e.printStackTrace();
            }
        }
        
        return null;
    }
}
