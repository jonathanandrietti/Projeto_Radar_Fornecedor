package br.com.radarfornecedor.radar.service;

import br.com.radarfornecedor.radar.model.ConfiguracaoEmail;
import br.com.radarfornecedor.radar.repository.ConfiguracaoEmailRepository;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

@Service
public class EmailService {

    private final ConfiguracaoEmailRepository configuracaoRepository;

    public EmailService(ConfiguracaoEmailRepository configuracaoRepository) {
        this.configuracaoRepository = configuracaoRepository;
    }

    /**
     * Enviar email de pré-cadastro (solicitação recebida)
     */
    public void enviarEmailPreCadastro(String destinatario, String nomeUsuario) {
        ConfiguracaoEmail config = obterConfiguracao();
        if (config == null || !config.getEmailAtivo()) {
            System.out.println("[EMAIL] Sistema de email desativado. Email não enviado.");
            return;
        }

        String assunto = "Solicitação de Cadastro Recebida - Radar Fornecedor";
        String corpo = construirEmailPreCadastro(config, nomeUsuario);
        
        enviarEmail(config, destinatario, assunto, corpo);
    }

    /**
     * Enviar email de aprovação
     */
    public void enviarEmailAprovacao(String destinatario, String nomeUsuario) {
        ConfiguracaoEmail config = obterConfiguracao();
        if (config == null || !config.getEmailAtivo()) {
            System.out.println("[EMAIL] Sistema de email desativado. Email não enviado.");
            return;
        }

        String assunto = "Cadastro Aprovado - Radar Fornecedor";
        String corpo = construirEmailAprovacao(config, nomeUsuario);
        
        enviarEmail(config, destinatario, assunto, corpo);
    }

    /**
     * Enviar email de rejeição
     */
    public void enviarEmailRejeicao(String destinatario, String nomeUsuario, String motivo) {
        ConfiguracaoEmail config = obterConfiguracao();
        if (config == null || !config.getEmailAtivo()) {
            System.out.println("[EMAIL] Sistema de email desativado. Email não enviado.");
            return;
        }

        String assunto = "Solicitação de Cadastro - Radar Fornecedor";
        String corpo = construirEmailRejeicao(config, nomeUsuario, motivo);
        
        enviarEmail(config, destinatario, assunto, corpo);
    }

    /**
     * Enviar email de cadastro completo
     */
    public void enviarEmailCadastroCompleto(String destinatario, String nomeUsuario) {
        ConfiguracaoEmail config = obterConfiguracao();
        if (config == null || !config.getEmailAtivo()) {
            System.out.println("[EMAIL] Sistema de email desativado. Email não enviado.");
            return;
        }

        String assunto = "Cadastro Finalizado - Radar Fornecedor";
        String corpo = construirEmailCadastroCompleto(config, nomeUsuario);
        
        enviarEmail(config, destinatario, assunto, corpo);
    }

    /**
     * Construir corpo do email de pré-cadastro
     */
    private String construirEmailPreCadastro(ConfiguracaoEmail config, String nomeUsuario) {
        StringBuilder html = new StringBuilder();
        html.append("<html><body style='font-family: Arial, sans-serif;'>");
        
        // Logo
        if (config.getLogoUrl() != null && !config.getLogoUrl().isEmpty()) {
            html.append("<div style='text-align: center; margin-bottom: 20px;'>");
            html.append("<img src='").append(config.getLogoUrl()).append("' alt='Logo' style='max-width: 200px;'>");
            html.append("</div>");
        }
        
        // Cumprimento
        html.append("<p>").append(config.getCumprimento()).append(" <strong>").append(nomeUsuario).append("</strong>,</p>");
        
        // Mensagem
        String mensagem = config.getMensagemPreCadastro();
        if (mensagem == null || mensagem.isEmpty()) {
            mensagem = "Sua solicitação de cadastro foi <strong>RECEBIDA COM SUCESSO</strong>!<br><br>" +
                      "Aguarde a análise da nossa equipe. Você receberá um email assim que sua solicitação for aprovada.<br><br>" +
                      "Se aprovado, você poderá acessar o sistema com o usuário e senha que cadastrou.";
        }
        html.append("<p>").append(mensagem).append("</p>");
        
        // Assinatura
        if (config.getAssinatura() != null && !config.getAssinatura().isEmpty()) {
            html.append("<p>").append(config.getAssinatura()).append("</p>");
        }
        
        // Rodapé
        if (config.getRodapeImagemUrl() != null && !config.getRodapeImagemUrl().isEmpty()) {
            html.append("<div style='text-align: center; margin-top: 30px; border-top: 1px solid #ccc; padding-top: 20px;'>");
            html.append("<img src='").append(config.getRodapeImagemUrl()).append("' alt='Rodapé' style='max-width: 100%;'>");
            html.append("</div>");
        }
        
        html.append("</body></html>");
        return html.toString();
    }

    /**
     * Construir corpo do email de aprovação
     */
    private String construirEmailAprovacao(ConfiguracaoEmail config, String nomeUsuario) {
        StringBuilder html = new StringBuilder();
        html.append("<html><body style='font-family: Arial, sans-serif;'>");
        
        // Logo
        if (config.getLogoUrl() != null && !config.getLogoUrl().isEmpty()) {
            html.append("<div style='text-align: center; margin-bottom: 20px;'>");
            html.append("<img src='").append(config.getLogoUrl()).append("' alt='Logo' style='max-width: 200px;'>");
            html.append("</div>");
        }
        
        // Cumprimento
        html.append("<p>").append(config.getCumprimento()).append(" <strong>").append(nomeUsuario).append("</strong>,</p>");
        
        // Mensagem
        String mensagem = config.getMensagemAprovacao();
        if (mensagem == null || mensagem.isEmpty()) {
            mensagem = "Através deste canal informamos que sua solicitação foi <strong>APROVADA</strong>. Você já pode fazer login no sistema.";
        }
        html.append("<p>").append(mensagem).append("</p>");
        
        // Assinatura
        if (config.getAssinatura() != null && !config.getAssinatura().isEmpty()) {
            html.append("<p>").append(config.getAssinatura()).append("</p>");
        }
        
        // Rodapé
        if (config.getRodapeImagemUrl() != null && !config.getRodapeImagemUrl().isEmpty()) {
            html.append("<div style='text-align: center; margin-top: 30px; border-top: 1px solid #ccc; padding-top: 20px;'>");
            html.append("<img src='").append(config.getRodapeImagemUrl()).append("' alt='Rodapé' style='max-width: 100%;'>");
            html.append("</div>");
        }
        
        html.append("</body></html>");
        return html.toString();
    }

    /**
     * Construir corpo do email de rejeição
     */
    private String construirEmailRejeicao(ConfiguracaoEmail config, String nomeUsuario, String motivo) {
        StringBuilder html = new StringBuilder();
        html.append("<html><body style='font-family: Arial, sans-serif;'>");
        
        // Logo
        if (config.getLogoUrl() != null && !config.getLogoUrl().isEmpty()) {
            html.append("<div style='text-align: center; margin-bottom: 20px;'>");
            html.append("<img src='").append(config.getLogoUrl()).append("' alt='Logo' style='max-width: 200px;'>");
            html.append("</div>");
        }
        
        // Cumprimento
        html.append("<p>").append(config.getCumprimento()).append(" <strong>").append(nomeUsuario).append("</strong>,</p>");
        
        // Mensagem
        String mensagem = config.getMensagemRejeicao();
        if (mensagem == null || mensagem.isEmpty()) {
            mensagem = "Através deste canal informamos que sua solicitação <strong>NÃO FOI APROVADA</strong>.";
        }
        html.append("<p>").append(mensagem).append("</p>");
        
        if (motivo != null && !motivo.isEmpty()) {
            html.append("<p><strong>Motivo:</strong> ").append(motivo).append("</p>");
        }
        
        // Assinatura
        if (config.getAssinatura() != null && !config.getAssinatura().isEmpty()) {
            html.append("<p>").append(config.getAssinatura()).append("</p>");
        }
        
        // Rodapé
        if (config.getRodapeImagemUrl() != null && !config.getRodapeImagemUrl().isEmpty()) {
            html.append("<div style='text-align: center; margin-top: 30px; border-top: 1px solid #ccc; padding-top: 20px;'>");
            html.append("<img src='").append(config.getRodapeImagemUrl()).append("' alt='Rodapé' style='max-width: 100%;'>");
            html.append("</div>");
        }
        
        html.append("</body></html>");
        return html.toString();
    }

    /**
     * Construir corpo do email de cadastro completo
     */
    private String construirEmailCadastroCompleto(ConfiguracaoEmail config, String nomeUsuario) {
        StringBuilder html = new StringBuilder();
        html.append("<html><body style='font-family: Arial, sans-serif;'>");
        
        // Logo
        if (config.getLogoUrl() != null && !config.getLogoUrl().isEmpty()) {
            html.append("<div style='text-align: center; margin-bottom: 20px;'>");
            html.append("<img src='").append(config.getLogoUrl()).append("' alt='Logo' style='max-width: 200px;'>");
            html.append("</div>");
        }
        
        // Cumprimento
        html.append("<p>").append(config.getCumprimento()).append(" <strong>").append(nomeUsuario).append("</strong>,</p>");
        
        // Mensagem
        String mensagem = config.getMensagemCadastroCompleto();
        if (mensagem == null || mensagem.isEmpty()) {
            mensagem = "Seu cadastro foi finalizado com sucesso! Agora você tem acesso completo ao sistema.";
        }
        html.append("<p>").append(mensagem).append("</p>");
        
        // Assinatura
        if (config.getAssinatura() != null && !config.getAssinatura().isEmpty()) {
            html.append("<p>").append(config.getAssinatura()).append("</p>");
        }
        
        // Rodapé
        if (config.getRodapeImagemUrl() != null && !config.getRodapeImagemUrl().isEmpty()) {
            html.append("<div style='text-align: center; margin-top: 30px; border-top: 1px solid #ccc; padding-top: 20px;'>");
            html.append("<img src='").append(config.getRodapeImagemUrl()).append("' alt='Rodapé' style='max-width: 100%;'>");
            html.append("</div>");
        }
        
        html.append("</body></html>");
        return html.toString();
    }

    /**
     * Enviar email via SMTP
     */
    private void enviarEmail(ConfiguracaoEmail config, String destinatario, String assunto, String corpoHtml) {
        try {
            Properties props = new Properties();
            props.put("mail.smtp.host", config.getSmtpHost());
            props.put("mail.smtp.port", config.getSmtpPort());
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", config.getSmtpTls());

            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(config.getSmtpUsuario(), config.getSmtpSenha());
                }
            });

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(config.getEmailRemetente(), config.getNomeRemetente()));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario));
            message.setSubject(assunto);
            message.setContent(corpoHtml, "text/html; charset=utf-8");

            Transport.send(message);
            System.out.println("[EMAIL] Email enviado com sucesso para: " + destinatario);

        } catch (Exception e) {
            System.err.println("[EMAIL] Erro ao enviar email: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Obter configuração de email (singleton)
     */
    private ConfiguracaoEmail obterConfiguracao() {
        return configuracaoRepository.findFirstByOrderByIdAsc().orElse(null);
    }
}
