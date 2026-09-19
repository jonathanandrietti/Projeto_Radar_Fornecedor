package br.com.radarfornecedor.radar.controller;

import br.com.radarfornecedor.radar.service.GeolocalizacaoCepService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller para consulta de CEP e geolocalização
 * Endpoints públicos que retornam endereço completo com latitude/longitude
 */
@RestController
@RequestMapping("/api/cep")
public class GeolocalizacaoCepController {

    @Autowired
    private GeolocalizacaoCepService geolocalizacaoCepService;

    /**
     * Consulta CEP e retorna endereço + geolocalização
     * GET /api/cep/{cep}
     * 
     * @param cep CEP com ou sem formatação (ex: 01310100 ou 01310-100)
     * @return Endereço completo com latitude/longitude
     */
    @GetMapping("/{cep}")
    public ResponseEntity<?> consultarCep(@PathVariable String cep) {
        try {
            // Validação básica
            if (cep == null || cep.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(criarErro("CEP não pode ser vazio"));
            }

            if (!geolocalizacaoCepService.validarFormatoCep(cep)) {
                return ResponseEntity.badRequest().body(criarErro("CEP inválido. Deve conter 8 dígitos"));
            }

            // Consulta dados
            Map<String, Object> dados = geolocalizacaoCepService.consultarCep(cep);

            if (dados == null || dados.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(criarErro("CEP não encontrado"));
            }

            return ResponseEntity.ok(dados);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(criarErro("Erro ao consultar CEP: " + e.getMessage()));
        }
    }

    /**
     * Consulta CEP via POST
     * POST /api/cep/buscar
     * Body: {"cep": "01310100"}
     */
    @PostMapping("/buscar")
    public ResponseEntity<?> consultarCepPost(@RequestBody Map<String, String> request) {
        String cep = request.get("cep");
        return consultarCep(cep);
    }

    /**
     * Obtém apenas latitude e longitude para um endereço completo
     * GET /api/cep/localizacao?logradouro=Rua+X&numero=123&bairro=Centro&cidade=São+Paulo&estado=SP
     * 
     * @param logradouro Logradouro
     * @param numero Número
     * @param bairro Bairro
     * @param cidade Cidade
     * @param estado Estado (UF)
     * @return {latitude: X, longitude: Y}
     */
    @GetMapping("/localizacao")
    public ResponseEntity<?> obterLocalizacao(
            @RequestParam(required = false) String logradouro,
            @RequestParam(required = false) String numero,
            @RequestParam(required = false) String bairro,
            @RequestParam(required = false) String cidade,
            @RequestParam(required = false) String estado) {
        
        try {
            // Validação básica
            if ((cidade == null || cidade.trim().isEmpty()) && (estado == null || estado.trim().isEmpty())) {
                return ResponseEntity.badRequest().body(criarErro("Informe pelo menos cidade e estado"));
            }

            // Consulta localização
            Map<String, Object> localizacao = geolocalizacaoCepService.obterLocalizacao(
                    logradouro, numero, bairro, cidade, estado);

            if (localizacao == null || localizacao.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(criarErro("Localização não encontrada"));
            }

            return ResponseEntity.ok(localizacao);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(criarErro("Erro ao obter localização: " + e.getMessage()));
        }
    }

    /**
     * POST alternativo para obter localização
     * POST /api/cep/localizacao
     * Body: {"logradouro": "Rua X", "numero": "123", "bairro": "Centro", "cidade": "São Paulo", "estado": "SP"}
     */
    @PostMapping("/localizacao")
    public ResponseEntity<?> obterLocalizacaoPost(@RequestBody Map<String, String> request) {
        return obterLocalizacao(
                request.get("logradouro"),
                request.get("numero"),
                request.get("bairro"),
                request.get("cidade"),
                request.get("estado"));
    }

    /**
     * Valida formato de CEP
     * GET /api/cep/validar/{cep}
     */
    @GetMapping("/validar/{cep}")
    public ResponseEntity<?> validarCep(@PathVariable String cep) {
        Map<String, Object> resposta = new HashMap<>();
        resposta.put("valido", geolocalizacaoCepService.validarFormatoCep(cep));
        return ResponseEntity.ok(resposta);
    }

    /**
     * Cria estrutura de erro padronizada
     */
    private Map<String, Object> criarErro(String mensagem) {
        Map<String, Object> erro = new HashMap<>();
        erro.put("erro", true);
        erro.put("mensagem", mensagem);
        erro.put("timestamp", System.currentTimeMillis());
        return erro;
    }
}
