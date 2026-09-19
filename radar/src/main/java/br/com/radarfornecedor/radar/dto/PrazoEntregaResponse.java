package br.com.radarfornecedor.radar.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PrazoEntregaResponse {
    private String empresa;
    private double distanciaKm;
    private int diasPreparacao;
    private int diasTransporte;
    private int totalDiasUteis;
    private LocalDate previsaoEntrega;
}
