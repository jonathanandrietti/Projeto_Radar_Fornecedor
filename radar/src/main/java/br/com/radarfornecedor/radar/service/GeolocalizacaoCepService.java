package br.com.radarfornecedor.radar.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Serviço para consultar CEP e obter geolocalização (latitude/longitude)
 * Utiliza API ViaCEP (gratuita) para resolução de endereço
 * Utiliza Nominatim (OpenStreetMap) para geolocalização
 */
@Service
public class GeolocalizacaoCepService {
    
    private static final Logger logger = LoggerFactory.getLogger(GeolocalizacaoCepService.class);
    private static final String API_VIACEP_URL = "https://viacep.com.br/ws/";
    private static final String API_NOMINATIM_URL = "https://nominatim.openstreetmap.org/search";
    
    private final RestTemplate restTemplate;

    public GeolocalizacaoCepService() {
        this.restTemplate = new RestTemplate();
    }

    /**
     * Consulta CEP e retorna dados de endereço + geolocalização
     * 
     * @param cep CEP sem formatação (8 dígitos)
     * @return Mapa contendo endereço completo + latitude/longitude
     */
    public Map<String, Object> consultarCep(String cep) {
        try {
            // Remove formatação do CEP
            String cepLimpo = cep.replaceAll("[^0-9]", "");
            
            if (cepLimpo.length() != 8) {
                logger.warn("CEP inválido: " + cep);
                return null;
            }
            
            // Consulta ViaCEP
            Map<String, Object> enderecoData = consultarViaCep(cepLimpo);
            
            if (enderecoData == null || enderecoData.containsKey("erro")) {
                logger.warn("CEP não encontrado ou inválido: " + cepLimpo);
                return null;
            }
            
            // Obtém geolocalização
            Map<String, Object> localizacao = obterGeolocalizar(enderecoData);
            
            if (localizacao != null) {
                enderecoData.putAll(localizacao);
            }
            
            return enderecoData;
            
        } catch (Exception e) {
            logger.error("Erro ao consultar CEP: " + cep, e);
            return null;
        }
    }

    /**
     * Consulta dados de endereço via ViaCEP
     */
    private Map<String, Object> consultarViaCep(String cep) {
        try {
            String url = API_VIACEP_URL + cep + "/json";
            logger.info("Consultando CEP via ViaCEP: " + url);
            
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            
            if (response != null && !response.containsKey("erro")) {
                logger.info("CEP consultado com sucesso: " + cep);
                
                Map<String, Object> resultado = new HashMap<>();
                resultado.put("cep", response.get("cep"));
                resultado.put("logradouro", response.get("logradouro"));
                resultado.put("complemento", response.get("complemento"));
                resultado.put("bairro", response.get("bairro"));
                resultado.put("cidade", response.get("localidade"));
                resultado.put("estado", response.get("uf"));
                resultado.put("ibge", response.get("ibge"));
                resultado.put("gia", response.get("gia"));
                resultado.put("ddd", response.get("ddd"));
                resultado.put("siafi", response.get("siafi"));
                
                return resultado;
            }
            
        } catch (RestClientException e) {
            logger.error("Erro ao consultar ViaCEP para CEP: " + cep, e);
        }
        
        return null;
    }

    /**
     * Obtém latitude e longitude do endereço completo usando Nominatim
     */
    private Map<String, Object> obterGeolocalizar(Map<String, Object> enderecoData) {
        try {
            // Monta string de busca com endereço completo
            String logradouro = (String) enderecoData.get("logradouro");
            String bairro = (String) enderecoData.get("bairro");
            String cidade = (String) enderecoData.get("cidade");
            String estado = (String) enderecoData.get("estado");
            
            if (logradouro == null || logradouro.isEmpty()) {
                // Se não tem logradouro, usa apenas cidade e estado
                logradouro = "";
            }
            
            // Nominatim usa URL encoding, então montamos a query
            String query = logradouro + ", " + cidade + ", " + estado;
            query = query.replaceAll(" ", "+");
            
            String url = API_NOMINATIM_URL + "?q=" + query + "&format=json&limit=1";
            logger.info("Consultando geolocalização via Nominatim para: " + query);
            
            Object[] responses = restTemplate.getForObject(url, Object[].class);
            
            if (responses != null && responses.length > 0) {
                Map<String, Object> primeiro = (Map<String, Object>) responses[0];
                
                Map<String, Object> geoloc = new HashMap<>();
                geoloc.put("latitude", Double.parseDouble(primeiro.get("lat").toString()));
                geoloc.put("longitude", Double.parseDouble(primeiro.get("lon").toString()));
                
                logger.info("Geolocalização obtida: " + geoloc.get("latitude") + ", " + geoloc.get("longitude"));
                return geoloc;
            }
            
        } catch (Exception e) {
            logger.warn("Erro ao obter geolocalização via Nominatim", e);
            // Retorna null e deixa latitude/longitude em branco
        }
        
        return null;
    }

    /**
     * Obtém apenas latitude/longitude para um endereço completo
     * 
     * @param logradouro Logradouro completo
     * @param numero Número
     * @param bairro Bairro
     * @param cidade Cidade
     * @param estado Estado (UF)
     * @return Mapa com latitude e longitude
     */
    public Map<String, Object> obterLocalizacao(String logradouro, String numero, String bairro, String cidade, String estado) {
        try {
            // Monta string de busca
            String query = (logradouro != null ? logradouro : "") + " " 
                          + (numero != null ? numero : "") + " " 
                          + (bairro != null ? bairro : "") + ", " 
                          + (cidade != null ? cidade : "") + ", " 
                          + (estado != null ? estado : "");
            
            query = query.replaceAll("\\s+", "+").replaceAll("\\++", "+");
            
            String url = API_NOMINATIM_URL + "?q=" + query + "&format=json&limit=1";
            logger.info("Consultando localização Nominatim para: " + query);
            
            Object[] responses = restTemplate.getForObject(url, Object[].class);
            
            if (responses != null && responses.length > 0) {
                Map<String, Object> primeiro = (Map<String, Object>) responses[0];
                
                Map<String, Object> resultado = new HashMap<>();
                resultado.put("latitude", Double.parseDouble(primeiro.get("lat").toString()));
                resultado.put("longitude", Double.parseDouble(primeiro.get("lon").toString()));
                
                logger.info("Localização obtida: " + resultado.get("latitude") + ", " + resultado.get("longitude"));
                return resultado;
            }
            
        } catch (Exception e) {
            logger.warn("Erro ao obter localização", e);
        }
        
        return null;
    }

    /**
     * Valida formato de CEP
     */
    public boolean validarFormatoCep(String cep) {
        if (cep == null) return false;
        String cepLimpo = cep.replaceAll("[^0-9]", "");
        return cepLimpo.length() == 8;
    }
}
