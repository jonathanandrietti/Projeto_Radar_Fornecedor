package br.com.radarfornecedor.radar.controller;

import br.com.radarfornecedor.radar.service.ConsultaCnpjService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller para consulta pública de dados de empresas via CNPJ
 * Endpoints públicos (sem autenticação) que consultam API gratuita Serenata/ReceitaWS
 */
@RestController
@RequestMapping("/api/consulta-cnpj")
public class ConsultaCnpjController {

    @Autowired
    private ConsultaCnpjService consultaCnpjService;

    /**
     * Consulta dados públicos de empresa via CNPJ
     * GET /api/consulta-cnpj/{cnpj}
     * 
     * @param cnpj CNPJ com ou sem formatação (ex: 00000000000191 ou 00.000.000/0001-91)
     * @return Dados da empresa ou erro 404 se não encontrado
     */
    @GetMapping("/{cnpj}")
    public ResponseEntity<?> consultarCnpj(@PathVariable String cnpj) {
        try {
            // Validação básica
            if (cnpj == null || cnpj.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(criarErro("CNPJ não pode ser vazio"));
            }

            if (!consultaCnpjService.validarFormatoCnpj(cnpj)) {
                return ResponseEntity.badRequest().body(criarErro("CNPJ inválido. Deve conter 14 dígitos"));
            }

            // Consulta dados
            Map<String, Object> dados = consultaCnpjService.consultarCnpj(cnpj);

            if (dados == null || dados.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(criarErro("Empresa não encontrada para o CNPJ informado"));
            }

            return ResponseEntity.ok(dados);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(criarErro("Erro ao consultar CNPJ: " + e.getMessage()));
        }
    }

    /**
     * Valida se um CNPJ existe (apenas status)
     * GET /api/consulta-cnpj/validar/{cnpj}
     * 
     * @param cnpj CNPJ para validação
     * @return {valido: true/false}
     */
    @GetMapping("/validar/{cnpj}")
    public ResponseEntity<?> validarCnpj(@PathVariable String cnpj) {
        try {
            Map<String, Object> dados = consultaCnpjService.consultarCnpj(cnpj);
            
            Map<String, Object> resposta = new HashMap<>();
            resposta.put("valido", dados != null && !dados.isEmpty());
            
            if (dados != null && !dados.isEmpty()) {
                resposta.put("nome", dados.get("nome"));
                resposta.put("razaoSocial", dados.get("razaoSocial"));
            }
            
            return ResponseEntity.ok(resposta);

        } catch (Exception e) {
            Map<String, Object> resposta = new HashMap<>();
            resposta.put("valido", false);
            resposta.put("erro", e.getMessage());
            return ResponseEntity.ok(resposta);
        }
    }

    /**
     * Consulta com endpoint alternativo para compatibilidade
     * POST /api/consulta-cnpj/buscar
     * Body: {"cnpj": "00000000000191"}
     */
    @PostMapping("/buscar")
    public ResponseEntity<?> consultarCnpjPost(@RequestBody Map<String, String> request) {
        String cnpj = request.get("cnpj");
        return consultarCnpj(cnpj);
    }

    /**
     * Endpoint de teste para visualizar todos os campos retornados pela API
     * GET /api/consulta-cnpj/teste/{cnpj}
     * 
     * @param cnpj CNPJ para teste
     * @return Todos os campos retornados da API da Receita com suas chaves
     */
    @GetMapping("/teste/{cnpj}")
    public ResponseEntity<?> testarConsultaCnpj(@PathVariable String cnpj) {
        try {
            if (!consultaCnpjService.validarFormatoCnpj(cnpj)) {
                return ResponseEntity.badRequest().body(criarErro("CNPJ inválido. Deve conter 14 dígitos"));
            }

            Map<String, Object> dados = consultaCnpjService.consultarCnpj(cnpj);
            
            if (dados == null || dados.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(criarErro("Empresa não encontrada"));
            }

            // Retorna dados formatados para visualização
            Map<String, Object> resposta = new HashMap<>();
            resposta.put("mensagem", "Dados retornados da consulta CNPJ");
            resposta.put("totalCampos", dados.size());
            resposta.put("campos", dados.keySet());
            resposta.put("dados", dados);
            
            return ResponseEntity.ok(resposta);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(criarErro("Erro: " + e.getMessage()));
        }
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
