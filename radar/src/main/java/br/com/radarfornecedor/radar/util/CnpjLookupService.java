package br.com.radarfornecedor.radar.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * Serviço para buscar dados de CNPJ via API ReceitaWS
 */
@Service
public class CnpjLookupService {

    private static final String RECEITA_WS_URL = "https://www.receitaws.com.br/v1/cnpj/";
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final HttpClient httpClient = HttpClient.newHttpClient();

    /**
     * Buscar dados de CNPJ (retorna nome da empresa, etc)
     */
    public Map<String, String> buscarDadosCnpj(String cnpj) {
        Map<String, String> resultado = new HashMap<>();

        try {
            // Remover formatação
            cnpj = cnpj.replaceAll("\\D", "");

            if (cnpj.length() != 14) {
                resultado.put("erro", "CNPJ inválido");
                return resultado;
            }

            // Fazer requisição à API
            String url = RECEITA_WS_URL + cnpj;
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new java.net.URI(url))
                    .GET()
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                    .timeout(java.time.Duration.ofSeconds(10))
                    .build();

            System.out.println("[CNPJ LOOKUP] Iniciando busca para CNPJ: " + cnpj);
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("[CNPJ LOOKUP] Status HTTP: " + response.statusCode());

            if (response.statusCode() == 200) {
                JsonNode jsonNode = objectMapper.readTree(response.body());

                // Verifica se há erro na resposta
                if (jsonNode.has("status") && jsonNode.get("status").asInt() == 400) {
                    resultado.put("erro", "CNPJ não encontrado");
                    System.out.println("[CNPJ LOOKUP] CNPJ não encontrado (status 400)");
                    return resultado;
                }

                // Extrair dados
                resultado.put("nomeEmpresa", jsonNode.get("nome") != null ? jsonNode.get("nome").asText("") : "");
                resultado.put("fantasia", jsonNode.get("fantasia") != null ? jsonNode.get("fantasia").asText("") : "");
                resultado.put("logradouro", jsonNode.get("logradouro") != null ? jsonNode.get("logradouro").asText("") : "");
                resultado.put("numero", jsonNode.get("numero") != null ? jsonNode.get("numero").asText("") : "");
                resultado.put("complemento", jsonNode.get("complemento") != null ? jsonNode.get("complemento").asText("") : "");
                resultado.put("bairro", jsonNode.get("bairro") != null ? jsonNode.get("bairro").asText("") : "");
                resultado.put("cidade", jsonNode.get("cidade") != null ? jsonNode.get("cidade").asText("") : "");
                resultado.put("uf", jsonNode.get("uf") != null ? jsonNode.get("uf").asText("") : "");
                resultado.put("cep", jsonNode.get("cep") != null ? jsonNode.get("cep").asText("") : "");
                resultado.put("telefone", jsonNode.get("telefone") != null ? jsonNode.get("telefone").asText("") : "");
                resultado.put("email", jsonNode.get("email") != null ? jsonNode.get("email").asText("") : "");
                
                // Atividade principal com null-safe navigation
                String atividade = "";
                if (jsonNode.has("atividade_principal") && jsonNode.get("atividade_principal").isArray() && 
                    jsonNode.get("atividade_principal").size() > 0) {
                    JsonNode primeiraAtividade = jsonNode.get("atividade_principal").get(0);
                    if (primeiraAtividade.has("text") && primeiraAtividade.get("text") != null) {
                        atividade = primeiraAtividade.get("text").asText("");
                    }
                }
                resultado.put("atividade_principal", atividade);

                System.out.println("[CNPJ LOOKUP] CNPJ encontrado: " + resultado.get("nomeEmpresa"));
                return resultado;
            } else if (response.statusCode() == 429) {
                resultado.put("erro", "Limite de requisições atingido. Tente novamente mais tarde.");
                System.out.println("[CNPJ LOOKUP] Erro 429: Limite de requisições");
                return resultado;
            } else {
                resultado.put("erro", "Erro ao consultar CNPJ: " + response.statusCode());
                System.out.println("[CNPJ LOOKUP] Erro HTTP " + response.statusCode() + ": " + response.body());
                return resultado;
            }
        } catch (Exception e) {
            resultado.put("erro", "Erro ao processar resposta: " + e.getMessage());
            System.err.println("[CNPJ LOOKUP] Erro: " + e.getClass().getSimpleName() + " - " + e.getMessage());
            e.printStackTrace();
            return resultado;
        }
    }
}
