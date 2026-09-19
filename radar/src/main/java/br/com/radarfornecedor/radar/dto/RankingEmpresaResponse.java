package br.com.radarfornecedor.radar.dto;

public record RankingEmpresaResponse(Long fornecedorId, String empresa, double media, long totalAvaliacoes) {}
