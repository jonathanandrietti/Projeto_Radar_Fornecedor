package br.com.radarfornecedor.radar.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Serviço para consultar dados públicos de CNPJ
 * Utiliza a API gratuita Serenata (serenatadeamor.org) que agrega dados da Receita Federal
 */
@Service
public class ConsultaCnpjService {
    
    private static final Logger logger = LoggerFactory.getLogger(ConsultaCnpjService.class);
    private static final String API_SERENATA_URL = "https://api.serenata.ai/companies/";
    
    private final RestTemplate restTemplate;

    public ConsultaCnpjService() {
        this.restTemplate = new RestTemplate();
    }

    /**
     * Consulta dados públicos de empresa via CNPJ
     * API Serenata retorna dados da Receita Federal e outras fontes públicas
     * 
     * @param cnpj CNPJ sem formatação (14 dígitos)
     * @return Mapa contendo dados da empresa ou null se não encontrado
     */
    public Map<String, Object> consultarCnpj(String cnpj) {
        try {
            // Remove formatação do CNPJ
            String cnpjLimpo = cnpj.replaceAll("[^0-9]", "");
            
            if (cnpjLimpo.length() != 14) {
                logger.warn("CNPJ inválido: " + cnpj);
                return null;
            }
            
            // Consulta API Serenata
            String url = API_SERENATA_URL + cnpjLimpo;
            logger.info("Consultando CNPJ via API: " + url);
            
            try {
                Map<String, Object> response = restTemplate.getForObject(url, Map.class);
                logger.info("Consulta CNPJ realizada com sucesso para: " + cnpjLimpo);
                return transformarResposta(response);
            } catch (RestClientException e) {
                logger.error("Erro ao consultar API Serenata para CNPJ: " + cnpjLimpo, e);
                // Tenta fallback com API alternativa
                return consultarCnpjAlternativo(cnpjLimpo);
            }
            
        } catch (Exception e) {
            logger.error("Erro ao consultar CNPJ: " + cnpj, e);
            return null;
        }
    }

    /**
     * Fallback: Consulta alternativa usando API Receitaws (sem garantia de disponibilidade)
     */
    private Map<String, Object> consultarCnpjAlternativo(String cnpj) {
        try {
            String url = "https://www.receitaws.com.br/v1/cnpj/" + cnpj;
            logger.info("Tentando fallback ReceitaWS para: " + cnpj);
            
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            
            // Verifica se tem status "OK"
            if ("OK".equals(response.get("status"))) {
                logger.info("Dados obtidos via ReceitaWS para: " + cnpj);
                return transformarRespostaReceitaWS(response);
            }
        } catch (Exception e) {
            logger.error("Fallback ReceitaWS também falhou para CNPJ: " + cnpj, e);
        }
        return null;
    }

    /**
     * Transforma resposta da API Serenata em um formato padronizado
     */
    private Map<String, Object> transformarResposta(Map<String, Object> response) {
        Map<String, Object> resultado = new HashMap<>();
        
        if (response != null) {
            // Dados básicos
            resultado.put("cnpj", response.get("cnpj"));
            resultado.put("nome", response.get("name") != null ? response.get("name") : response.get("company_name"));
            resultado.put("razaoSocial", response.get("company_name"));
            resultado.put("nomeFantasia", response.get("name"));
            
            // Endereço
            Map<String, Object> endereco = extrairEndereco(response);
            resultado.putAll(endereco);
            
            // Atividade
            resultado.put("atividade", response.get("activity"));
            resultado.put("naturezaJuridica", response.get("legal_nature"));
            
            // Status
            resultado.put("status", response.get("status") != null ? "ATIVA" : "INATIVA");
            resultado.put("dataAbertura", response.get("opening_date"));
            resultado.put("dataUltimaAtualizacao", response.get("last_update"));
            
            // Contato (se disponível)
            resultado.put("telefone", response.get("phone"));
            resultado.put("email", response.get("email"));
        }
        
        return resultado;
    }

    /**
     * Transforma resposta da API ReceitaWS
     */
    private Map<String, Object> transformarRespostaReceitaWS(Map<String, Object> response) {
        Map<String, Object> resultado = new HashMap<>();
        
        if (response != null) {
            resultado.put("cnpj", response.get("cnpj"));
            resultado.put("nome", response.get("nome"));
            resultado.put("razaoSocial", response.get("nome"));
            resultado.put("nomeFantasia", response.get("fantasia"));
            
            // Endereço
            if (response.get("logradouro") != null) {
                resultado.put("logradouro", response.get("logradouro"));
                resultado.put("numero", response.get("numero"));
                resultado.put("complemento", response.get("complemento"));
                resultado.put("bairro", response.get("bairro"));
                resultado.put("cidade", response.get("municipio"));
                resultado.put("estado", response.get("uf"));
                resultado.put("cep", response.get("cep"));
            }
            
            // Atividade
            if (response.get("atividade") != null && response.get("atividade") instanceof java.util.List) {
                java.util.List<?> atividades = (java.util.List<?>) response.get("atividade");
                if (!atividades.isEmpty() && atividades.get(0) instanceof Map) {
                    Map<String, Object> ativ = (Map<String, Object>) atividades.get(0);
                    resultado.put("atividade", ativ.get("text"));
                    resultado.put("cnae", ativ.get("code"));
                }
            }
            
            resultado.put("status", response.get("status"));
            resultado.put("dataAbertura", response.get("data_abertura"));
        }
        
        return resultado;
    }

    /**
     * Extrai dados de endereço da resposta da API
     */
    private Map<String, Object> extrairEndereco(Map<String, Object> response) {
        Map<String, Object> endereco = new HashMap<>();
        
        try {
            if (response.get("address") != null) {
                Map<String, Object> addr = (Map<String, Object>) response.get("address");
                endereco.put("logradouro", addr.get("street"));
                endereco.put("numero", addr.get("number"));
                endereco.put("complemento", addr.get("complement"));
                endereco.put("bairro", addr.get("neighborhood"));
                endereco.put("cidade", addr.get("city"));
                endereco.put("estado", addr.get("state"));
                endereco.put("cep", addr.get("zip_code"));
            }
        } catch (Exception e) {
            logger.warn("Erro ao extrair endereço da resposta", e);
        }
        
        return endereco;
    }

    /**
     * Valida formato de CNPJ
     */
    public boolean validarFormatoCnpj(String cnpj) {
        if (cnpj == null) return false;
        String cnpjLimpo = cnpj.replaceAll("[^0-9]", "");
        return cnpjLimpo.length() == 14;
    }
}
