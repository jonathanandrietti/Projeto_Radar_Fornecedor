package br.com.radarfornecedor.radar.service;

import br.com.radarfornecedor.radar.dto.ConsultaCepDto;
import br.com.radarfornecedor.radar.dto.ConsultaCnpjDto;
import br.com.radarfornecedor.radar.dto.PreenchimentoAutomaticoDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Serviço que integra consultas de CNPJ e CEP para preenchimento automático
 * Orquestra as consultas e retorna dados consolidados para o frontend
 */
@Service
public class PreenchimentoAutomaticoService {
    
    private static final Logger logger = LoggerFactory.getLogger(PreenchimentoAutomaticoService.class);
    
    @Autowired
    private ConsultaCnpjService consultaCnpjService;
    
    @Autowired
    private GeolocalizacaoCepService geolocalizacaoCepService;

    /**
     * Processa preenchimento automático via CNPJ
     * Retorna dados da empresa consultada
     */
    public PreenchimentoAutomaticoDto preencherPorCnpj(String cnpj) {
        PreenchimentoAutomaticoDto resultado = new PreenchimentoAutomaticoDto();
        resultado.setCnpj(cnpj);
        
        try {
            if (!consultaCnpjService.validarFormatoCnpj(cnpj)) {
                resultado.setSucesso(false);
                resultado.setMensagem("CNPJ inválido. Deve conter 14 dígitos");
                return resultado;
            }
            
            // Consulta dados do CNPJ
            Map<String, Object> dados = consultaCnpjService.consultarCnpj(cnpj);
            
            if (dados == null || dados.isEmpty()) {
                resultado.setSucesso(false);
                resultado.setMensagem("Empresa não encontrada para o CNPJ informado");
                return resultado;
            }
            
            // Converte para DTO
            ConsultaCnpjDto dadosCnpj = converterMapParaConsultaCnpjDto(dados);
            resultado.setDadosCnpj(dadosCnpj);
            
            // Se temos CEP, consulta localização
            if (dadosCnpj.getCep() != null && !dadosCnpj.getCep().isEmpty()) {
                Map<String, Object> dadosCep = geolocalizacaoCepService.consultarCep(dadosCnpj.getCep());
                if (dadosCep != null && !dadosCep.isEmpty()) {
                    ConsultaCepDto cepDto = converterMapParaConsultaCepDto(dadosCep);
                    resultado.setDadosCep(cepDto);
                    // Atualiza latitude/longitude nos dados do CNPJ
                    dadosCnpj.setLatitude((Double) dadosCep.get("latitude"));
                    dadosCnpj.setLongitude((Double) dadosCep.get("longitude"));
                }
            }
            
            resultado.setSucesso(true);
            resultado.setMensagem("Dados consultados com sucesso");
            logger.info("Preenchimento automático via CNPJ realizado com sucesso: " + cnpj);
            
        } catch (Exception e) {
            resultado.setSucesso(false);
            resultado.setMensagem("Erro ao consultar CNPJ: " + e.getMessage());
            logger.error("Erro ao processar CNPJ: " + cnpj, e);
        }
        
        return resultado;
    }

    /**
     * Processa preenchimento automático via CEP
     * Retorna endereço e geolocalização
     */
    public PreenchimentoAutomaticoDto preencherPorCep(String cep) {
        PreenchimentoAutomaticoDto resultado = new PreenchimentoAutomaticoDto();
        resultado.setCep(cep);
        
        try {
            if (!geolocalizacaoCepService.validarFormatoCep(cep)) {
                resultado.setSucesso(false);
                resultado.setMensagem("CEP inválido. Deve conter 8 dígitos");
                return resultado;
            }
            
            // Consulta dados do CEP
            Map<String, Object> dados = geolocalizacaoCepService.consultarCep(cep);
            
            if (dados == null || dados.isEmpty()) {
                resultado.setSucesso(false);
                resultado.setMensagem("CEP não encontrado");
                return resultado;
            }
            
            // Converte para DTO
            ConsultaCepDto dadosCep = converterMapParaConsultaCepDto(dados);
            resultado.setDadosCep(dadosCep);
            
            resultado.setSucesso(true);
            resultado.setMensagem("CEP consultado com sucesso");
            logger.info("Preenchimento automático via CEP realizado com sucesso: " + cep);
            
        } catch (Exception e) {
            resultado.setSucesso(false);
            resultado.setMensagem("Erro ao consultar CEP: " + e.getMessage());
            logger.error("Erro ao processar CEP: " + cep, e);
        }
        
        return resultado;
    }

    /**
     * Converte Map para ConsultaCnpjDto
     */
    private ConsultaCnpjDto converterMapParaConsultaCnpjDto(Map<String, Object> dados) {
        ConsultaCnpjDto dto = new ConsultaCnpjDto();
        
        dto.setCnpj(toString(dados.get("cnpj")));
        dto.setNome(toString(dados.get("nome")));
        dto.setRazaoSocial(toString(dados.get("razaoSocial")));
        dto.setNomeFantasia(toString(dados.get("nomeFantasia")));
        dto.setLogradouro(toString(dados.get("logradouro")));
        dto.setNumero(toString(dados.get("numero")));
        dto.setComplemento(toString(dados.get("complemento")));
        dto.setBairro(toString(dados.get("bairro")));
        dto.setCidade(toString(dados.get("cidade")));
        dto.setEstado(toString(dados.get("estado")));
        dto.setCep(toString(dados.get("cep")));
        dto.setAtividade(toString(dados.get("atividade")));
        dto.setCnae(toString(dados.get("cnae")));
        dto.setNaturezaJuridica(toString(dados.get("naturezaJuridica")));
        dto.setStatus(toString(dados.get("status")));
        dto.setDataAbertura(toString(dados.get("dataAbertura")));
        dto.setDataUltimaAtualizacao(toString(dados.get("dataUltimaAtualizacao")));
        dto.setTelefone(toString(dados.get("telefone")));
        dto.setEmail(toString(dados.get("email")));
        
        return dto;
    }

    /**
     * Converte Map para ConsultaCepDto
     */
    private ConsultaCepDto converterMapParaConsultaCepDto(Map<String, Object> dados) {
        ConsultaCepDto dto = new ConsultaCepDto();
        
        dto.setCep(toString(dados.get("cep")));
        dto.setLogradouro(toString(dados.get("logradouro")));
        dto.setComplemento(toString(dados.get("complemento")));
        dto.setBairro(toString(dados.get("bairro")));
        dto.setCidade(toString(dados.get("cidade")));
        dto.setEstado(toString(dados.get("estado")));
        dto.setLatitude(toDouble(dados.get("latitude")));
        dto.setLongitude(toDouble(dados.get("longitude")));
        dto.setIbge(toString(dados.get("ibge")));
        dto.setDdd(toString(dados.get("ddd")));
        
        return dto;
    }

    /**
     * Utilitários de conversão
     */
    private String toString(Object obj) {
        return obj != null ? obj.toString() : null;
    }

    private Double toDouble(Object obj) {
        if (obj == null) return null;
        if (obj instanceof Double) return (Double) obj;
        try {
            return Double.parseDouble(obj.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
