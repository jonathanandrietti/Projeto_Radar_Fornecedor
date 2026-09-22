package br.com.radarfornecedor.radar.util;

/**
 * Utilitário para validar CNPJ e CPF
 */
public class CnpjCpfValidator {

    /**
     * Validar CNPJ (sem formatação)
     */
    public static boolean validarCnpj(String cnpj) {
        // Remove caracteres não numéricos
        cnpj = cnpj.replaceAll("\\D", "");

        // Verifica tamanho
        if (cnpj.length() != 14) {
            return false;
        }

        // Verifica se todos os dígitos são iguais (inválido)
        if (cnpj.matches("(\\d)\\1{13}")) {
            return false;
        }

        // Valida usando algoritmo correto CNPJ
        // Calcula o primeiro dígito verificador
        int[] multiplicadores = {5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
        int soma = 0;
        
        for (int i = 0; i < 12; i++) {
            soma += Integer.parseInt(cnpj.substring(i, i + 1)) * multiplicadores[i];
        }
        
        int primeiroDigito = 11 - (soma % 11);
        if (primeiroDigito >= 10) {
            primeiroDigito = 0;
        }
        
        if (primeiroDigito != Integer.parseInt(cnpj.substring(12, 13))) {
            return false;
        }
        
        // Calcula o segundo dígito verificador
        multiplicadores = new int[]{6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
        soma = 0;
        
        for (int i = 0; i < 13; i++) {
            soma += Integer.parseInt(cnpj.substring(i, i + 1)) * multiplicadores[i];
        }
        
        int segundoDigito = 11 - (soma % 11);
        if (segundoDigito >= 10) {
            segundoDigito = 0;
        }
        
        if (segundoDigito != Integer.parseInt(cnpj.substring(13, 14))) {
            return false;
        }

        return true;
    }

    /**
     * Validar CPF (sem formatação)
     */
    public static boolean validarCpf(String cpf) {
        // Remove caracteres não numéricos
        cpf = cpf.replaceAll("\\D", "");

        // Verifica tamanho
        if (cpf.length() != 11) {
            return false;
        }

        // Verifica se todos os dígitos são iguais (inválido)
        if (cpf.matches("(\\d)\\1{10}")) {
            return false;
        }

        // Calcula primeiro dígito verificador
        int soma = 0;
        int resto;

        for (int i = 1; i <= 9; i++) {
            soma += Integer.parseInt(cpf.substring(i - 1, i)) * (11 - i);
        }

        resto = (soma * 10) % 11;

        if (resto == 10 || resto == 11) {
            resto = 0;
        }

        if (resto != Integer.parseInt(cpf.substring(9, 10))) {
            return false;
        }

        // Calcula segundo dígito verificador
        soma = 0;

        for (int i = 1; i <= 10; i++) {
            soma += Integer.parseInt(cpf.substring(i - 1, i)) * (12 - i);
        }

        resto = (soma * 10) % 11;

        if (resto == 10 || resto == 11) {
            resto = 0;
        }

        if (resto != Integer.parseInt(cpf.substring(10, 11))) {
            return false;
        }

        return true;
    }

    /**
     * Validar CNPJ ou CPF
     */
    public static boolean validar(String tipo, String valor) {
        if ("CNPJ".equals(tipo)) {
            return validarCnpj(valor);
        } else if ("CPF".equals(tipo)) {
            return validarCpf(valor);
        }
        return false;
    }

    /**
     * Formatar CNPJ (14 dígitos -> XX.XXX.XXX/XXXX-XX)
     */
    public static String formatarCnpj(String cnpj) {
        cnpj = cnpj.replaceAll("\\D", "");
        if (cnpj.length() != 14) {
            return cnpj;
        }
        return cnpj.substring(0, 2) + "." + 
               cnpj.substring(2, 5) + "." + 
               cnpj.substring(5, 8) + "/" + 
               cnpj.substring(8, 12) + "-" + 
               cnpj.substring(12, 14);
    }

    /**
     * Formatar CPF (11 dígitos -> XXX.XXX.XXX-XX)
     */
    public static String formatarCpf(String cpf) {
        cpf = cpf.replaceAll("\\D", "");
        if (cpf.length() != 11) {
            return cpf;
        }
        return cpf.substring(0, 3) + "." + 
               cpf.substring(3, 6) + "." + 
               cpf.substring(6, 9) + "-" + 
               cpf.substring(9, 11);
    }

    /**
     * Remover formatação
     */
    public static String removerFormatacao(String valor) {
        return valor.replaceAll("\\D", "");
    }
}
