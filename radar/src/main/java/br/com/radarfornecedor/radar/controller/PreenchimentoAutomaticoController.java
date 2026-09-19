package br.com.radarfornecedor.radar.controller;

import br.com.radarfornecedor.radar.dto.PreenchimentoAutomaticoDto;
import br.com.radarfornecedor.radar.service.PreenchimentoAutomaticoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller para preenchimento automático de formulários
 * Integra consultas de CNPJ, CEP e retorna dados consolidados
 */
@RestController
@RequestMapping("/api/preenchimento-automatico")
public class PreenchimentoAutomaticoController {

    @Autowired
    private PreenchimentoAutomaticoService preenchimentoAutomaticoService;

    /**
     * Preenche formulário via CNPJ
     * GET /api/preenchimento-automatico/cnpj/{cnpj}
     * 
     * @param cnpj CNPJ para consulta
     * @return Dados da empresa com endereço e geolocalização
     */
    @GetMapping("/cnpj/{cnpj}")
    public ResponseEntity<PreenchimentoAutomaticoDto> preencherPorCnpj(@PathVariable String cnpj) {
        PreenchimentoAutomaticoDto resultado = preenchimentoAutomaticoService.preencherPorCnpj(cnpj);
        return ResponseEntity.ok(resultado);
    }

    /**
     * Preenche formulário via CEP
     * GET /api/preenchimento-automatico/cep/{cep}
     * 
     * @param cep CEP para consulta
     * @return Endereço completo com latitude/longitude
     */
    @GetMapping("/cep/{cep}")
    public ResponseEntity<PreenchimentoAutomaticoDto> preencherPorCep(@PathVariable String cep) {
        PreenchimentoAutomaticoDto resultado = preenchimentoAutomaticoService.preencherPorCep(cep);
        return ResponseEntity.ok(resultado);
    }

    /**
     * Preenche via POST - mais flexível
     * POST /api/preenchimento-automatico
     * Body: {"cnpj": "00000000000191"} ou {"cep": "01310100"}
     */
    @PostMapping
    public ResponseEntity<PreenchimentoAutomaticoDto> preencher(@RequestBody Map<String, String> request) {
        String cnpj = request.get("cnpj");
        String cep = request.get("cep");
        
        if (cnpj != null && !cnpj.isEmpty()) {
            PreenchimentoAutomaticoDto resultado = preenchimentoAutomaticoService.preencherPorCnpj(cnpj);
            return ResponseEntity.ok(resultado);
        } else if (cep != null && !cep.isEmpty()) {
            PreenchimentoAutomaticoDto resultado = preenchimentoAutomaticoService.preencherPorCep(cep);
            return ResponseEntity.ok(resultado);
        } else {
            PreenchimentoAutomaticoDto erro = new PreenchimentoAutomaticoDto();
            erro.setSucesso(false);
            erro.setMensagem("Informe CNPJ ou CEP");
            return ResponseEntity.badRequest().body(erro);
        }
    }
}
