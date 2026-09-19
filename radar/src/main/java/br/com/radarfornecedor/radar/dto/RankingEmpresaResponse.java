package br.com.radarfornecedor.radar.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RankingEmpresaResponse {
    private Long fornecedorId;
    private String empresa;
    private double media;
    private long totalAvaliacoes;
}
