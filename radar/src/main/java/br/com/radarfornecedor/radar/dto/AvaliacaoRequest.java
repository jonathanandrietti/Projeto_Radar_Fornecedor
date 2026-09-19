package br.com.radarfornecedor.radar.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AvaliacaoRequest(
        @NotNull Long produtoId,
        @NotNull @Min(1) @Max(5) Integer nota,
        @NotBlank @jakarta.validation.constraints.Size(max = 1000) String comentario
) {}
