package br.com.radarfornecedor.radar.dto;

import java.time.LocalDate;

public record PrazoEntregaResponse(String empresa, double distanciaKm, int diasPreparacao,
                                   int diasTransporte, int totalDiasUteis, LocalDate previsaoEntrega) {}
