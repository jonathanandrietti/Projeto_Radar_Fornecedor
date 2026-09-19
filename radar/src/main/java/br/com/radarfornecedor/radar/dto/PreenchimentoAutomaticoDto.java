package br.com.radarfornecedor.radar.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para requisição de preenchimento automático
 * Recebe CNPJ ou CEP e retorna dados para preenchimento
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PreenchimentoAutomaticoDto {
    private String cnpj;
    private String cep;
    private ConsultaCnpjDto dadosCnpj;
    private ConsultaCepDto dadosCep;
    private Boolean sucesso;
    private String mensagem;
}
