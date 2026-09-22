package br.com.radarfornecedor.radar.controller;

import br.com.radarfornecedor.radar.util.CnpjCpfValidator;
import br.com.radarfornecedor.radar.util.CnpjLookupService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/validacao")
public class ValidacaoController {

    private final CnpjLookupService cnpjLookupService;

    public ValidacaoController(CnpjLookupService cnpjLookupService) {
        this.cnpjLookupService = cnpjLookupService;
    }

    /**
     * Validar CNPJ ou CPF
     */
    @PostMapping("/validar")
    public ResponseEntity<?> validar(@RequestBody Map<String, String> body) {
        try {
            String tipo = body.get("tipo");
            String valor = body.get("valor");

            if (tipo == null || valor == null) {
                return ResponseEntity.status(400).body(Map.of("erro", "tipo e valor são obrigatórios"));
            }

            boolean valido = CnpjCpfValidator.validar(tipo, valor);

            if (!valido) {
                return ResponseEntity.ok(Map.of(
                        "valido", false,
                        "mensagem", tipo + " inválido"
                ));
            }

            // Se CNPJ, buscar dados da empresa
            if ("CNPJ".equals(tipo)) {
                Map<String, String> dadosEmpresa = cnpjLookupService.buscarDadosCnpj(valor);

                if (dadosEmpresa.containsKey("erro")) {
                    // Se erro na busca, retorna como válido mas sem dados
                    Map<String, Object> resposta = new java.util.HashMap<>();
                    resposta.put("valido", true);
                    resposta.put("tipo", tipo);
                    resposta.put("aviso", dadosEmpresa.get("erro"));
                    resposta.put("dadosDisponiveis", false);
                    return ResponseEntity.ok(resposta);
                }

                Map<String, Object> resposta = new java.util.HashMap<>();
                resposta.put("valido", true);
                resposta.put("tipo", tipo);
                resposta.put("nomeEmpresa", dadosEmpresa.get("nomeEmpresa"));
                resposta.put("fantasia", dadosEmpresa.get("fantasia"));
                resposta.put("logradouro", dadosEmpresa.get("logradouro"));
                resposta.put("numero", dadosEmpresa.get("numero"));
                resposta.put("complemento", dadosEmpresa.get("complemento"));
                resposta.put("bairro", dadosEmpresa.get("bairro"));
                resposta.put("cidade", dadosEmpresa.get("cidade"));
                resposta.put("uf", dadosEmpresa.get("uf"));
                resposta.put("cep", dadosEmpresa.get("cep"));
                resposta.put("telefone", dadosEmpresa.get("telefone"));
                resposta.put("email", dadosEmpresa.get("email"));
                resposta.put("atividade", dadosEmpresa.get("atividade_principal"));
                resposta.put("dadosDisponiveis", true);
                return ResponseEntity.ok(resposta);
            } else {
                // CPF
                return ResponseEntity.ok(Map.of(
                        "valido", true,
                        "tipo", tipo,
                        "dadosDisponiveis", false
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("erro", "Erro ao validar: " + e.getMessage()));
        }
    }

    /**
     * Formatar CNPJ ou CPF
     */
    @PostMapping("/formatar")
    public ResponseEntity<?> formatar(@RequestBody Map<String, String> body) {
        try {
            String tipo = body.get("tipo");
            String valor = body.get("valor");

            if (tipo == null || valor == null) {
                return ResponseEntity.status(400).body(Map.of("erro", "tipo e valor são obrigatórios"));
            }

            String formatado;
            if ("CNPJ".equals(tipo)) {
                formatado = CnpjCpfValidator.formatarCnpj(valor);
            } else if ("CPF".equals(tipo)) {
                formatado = CnpjCpfValidator.formatarCpf(valor);
            } else {
                return ResponseEntity.status(400).body(Map.of("erro", "tipo deve ser CNPJ ou CPF"));
            }

            return ResponseEntity.ok(Map.of("valor", formatado));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("erro", "Erro ao formatar: " + e.getMessage()));
        }
    }
}
