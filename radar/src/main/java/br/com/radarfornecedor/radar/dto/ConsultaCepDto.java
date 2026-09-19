package br.com.radarfornecedor.radar.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para resposta de consulta de CEP e geolocalização
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConsultaCepDto {
    private String cep;
    private String logradouro;
    private String complemento;
    private String bairro;
    private String cidade;
    private String estado;
    private Double latitude;
    private Double longitude;
    private String ibge;
    private String ddd;
}
