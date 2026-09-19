package br.com.radarfornecedor.radar.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AvaliacaoRequest {
    @NotNull
    private Long produtoId;
    
    @NotNull
    @Min(1)
    @Max(5)
    private Integer nota;
    
    @NotBlank
    @Size(max = 1000)
    private String comentario;
}
