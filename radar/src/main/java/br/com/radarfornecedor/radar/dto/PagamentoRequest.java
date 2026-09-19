package br.com.radarfornecedor.radar.dto;

import br.com.radarfornecedor.radar.model.FormaPagamento;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PagamentoRequest(@NotNull Long produtoId, @NotNull FormaPagamento forma,
                               @NotBlank String cepEntrega) {}
