package br.com.radarfornecedor.radar.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para resposta de consulta de CNPJ
 * Retorna dados da empresa consultada via API pública
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConsultaCnpjDto {
    private String cnpj;
    private String nome;
    private String razaoSocial;
    private String nomeFantasia;
    private String logradouro;
    private String numero;
    private String complemento;
    private String bairro;
    private String cidade;
    private String estado;
    private String cep;
    private String atividade;
    private String cnae;
    private String naturezaJuridica;
    private String status;
    private String dataAbertura;
    private String dataUltimaAtualizacao;
    private String telefone;
    private String email;
    private Double latitude;
    private Double longitude;
}
