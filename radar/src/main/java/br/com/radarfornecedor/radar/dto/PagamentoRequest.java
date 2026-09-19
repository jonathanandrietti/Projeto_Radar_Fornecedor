package br.com.radarfornecedor.radar.dto;

import br.com.radarfornecedor.radar.model.FormaPagamento;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PagamentoRequest {
    @NotNull
    private Long produtoId;
    
    @NotNull
    private FormaPagamento forma;
    
    @NotBlank
    private String cepEntrega;
}
