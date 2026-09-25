package br.com.radarfornecedor.radar.controller;

import br.com.radarfornecedor.radar.model.ConfiguracaoEmail;
import br.com.radarfornecedor.radar.repository.ConfiguracaoEmailRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/configuracoes-email")
public class ConfiguracaoEmailController {

    private final ConfiguracaoEmailRepository configuracaoRepository;

    public ConfiguracaoEmailController(ConfiguracaoEmailRepository configuracaoRepository) {
        this.configuracaoRepository = configuracaoRepository;
    }

    /**
     * Obter configuração atual (sem retornar a senha por segurança)
     */
    @GetMapping
    public ResponseEntity<?> obterConfiguracao() {
        ConfiguracaoEmail config = configuracaoRepository.findFirstByOrderByIdAsc()
                .orElseGet(() -> {
                    // Criar configuração padrão se não existir
                    ConfiguracaoEmail novaConfig = new ConfiguracaoEmail();
                    novaConfig.setCumprimento("Prezado(a)");
                    novaConfig.setAssinatura("A equipe HAIE agradece seu contato!");
                    novaConfig.setMensagemAprovacao("Através deste canal informamos que sua solicitação foi <strong>APROVADA</strong>.");
                    novaConfig.setMensagemRejeicao("Através deste canal informamos que sua solicitação <strong>NÃO FOI APROVADA</strong>.");
                    novaConfig.setMensagemCadastroCompleto("Seu cadastro foi finalizado com sucesso!");
                    novaConfig.setEmailAtivo(false);
                    return configuracaoRepository.save(novaConfig);
                });

        // ⚠️ IMPORTANTE: Não retornar a senha por segurança
        // Mas indicar se existe uma senha salva
        boolean temSenha = config.getSmtpSenha() != null && !config.getSmtpSenha().isEmpty();
        config.setSmtpSenha(temSenha ? "HAS_PASSWORD" : null);

        return ResponseEntity.ok(config);
    }

    /**
     * Buscar senha salva APENAS para uso em testes (nunca retorna para frontend exibir)
     */
    @PostMapping("/buscar-senha-teste")
    public ResponseEntity<?> buscarSenhaTeste() {
        try {
            ConfiguracaoEmail config = configuracaoRepository.findFirstByOrderByIdAsc().orElse(null);
            
            if (config == null || config.getSmtpSenha() == null || config.getSmtpSenha().isEmpty()) {
                return ResponseEntity.status(404).body(Map.of(
                    "erro", "Nenhuma senha salva no banco",
                    "temSenha", false
                ));
            }

            // Retorna a senha APENAS para uso interno (testes)
            return ResponseEntity.ok(Map.of(
                "senha", config.getSmtpSenha(),
                "temSenha", true
            ));

        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                "erro", "Erro ao buscar senha",
                "temSenha", false
            ));
        }
    }

    /**
     * Testar apenas a conexão SMTP (sem enviar email)
     */
    @PostMapping("/testar-conexao")
    public ResponseEntity<?> testarConexaoSmtp(@RequestBody Map<String, Object> dados) {
        try {
            String smtpHost = (String) dados.get("smtpHost");
            Integer smtpPort = (Integer) dados.get("smtpPort");
            String smtpUsuario = (String) dados.get("smtpUsuario");
            String smtpSenha = (String) dados.get("smtpSenha");
            Boolean smtpTls = (Boolean) dados.get("smtpTls");

            if (smtpHost == null || smtpHost.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("erro", "Host SMTP é obrigatório", "sucesso", false));
            }
            if (smtpPort == null) {
                return ResponseEntity.badRequest().body(Map.of("erro", "Porta SMTP é obrigatória", "sucesso", false));
            }
            if (smtpUsuario == null || smtpUsuario.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("erro", "Usuário SMTP é obrigatório", "sucesso", false));
            }
            if (smtpSenha == null || smtpSenha.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("erro", "Senha SMTP é obrigatória", "sucesso", false));
            }

            // ========== DEBUG DETALHADO DA SENHA ==========
            System.out.println("\n[DEBUG SENHA] ========================================");
            System.out.println("[DEBUG SENHA] Senha ORIGINAL (antes do trim):");
            System.out.println("[DEBUG SENHA]   - Length: " + smtpSenha.length());
            System.out.println("[DEBUG SENHA]   - Primeiros 3 chars: '" + (smtpSenha.length() >= 3 ? smtpSenha.substring(0, 3) : smtpSenha) + "'");
            System.out.println("[DEBUG SENHA]   - Últimos 3 chars: '" + (smtpSenha.length() >= 3 ? smtpSenha.substring(smtpSenha.length() - 3) : smtpSenha) + "'");
            System.out.println("[DEBUG SENHA]   - Tem espaços? " + smtpSenha.contains(" "));
            System.out.println("[DEBUG SENHA]   - Tem tabs? " + smtpSenha.contains("\t"));
            System.out.println("[DEBUG SENHA]   - Tem quebras? " + (smtpSenha.contains("\n") || smtpSenha.contains("\r")));
            
            // Bytes da senha para verificar encoding
            byte[] senhaBytes = smtpSenha.getBytes(java.nio.charset.StandardCharsets.UTF_8);
            System.out.println("[DEBUG SENHA]   - Bytes (UTF-8): " + senhaBytes.length);
            
            // Criar variável final para uso na classe anônima
            final String senhaTrimmed = smtpSenha.trim();
            boolean mudouAposTrim = !smtpSenha.equals(senhaTrimmed);
            
            System.out.println("[DEBUG SENHA] Senha APÓS TRIM:");
            System.out.println("[DEBUG SENHA]   - Length: " + senhaTrimmed.length());
            System.out.println("[DEBUG SENHA]   - Mudou após trim? " + mudouAposTrim);
            System.out.println("[DEBUG SENHA]   - Primeiros 3 chars: '" + (senhaTrimmed.length() >= 3 ? senhaTrimmed.substring(0, 3) : senhaTrimmed) + "'");
            System.out.println("[DEBUG SENHA]   - Últimos 3 chars: '" + (senhaTrimmed.length() >= 3 ? senhaTrimmed.substring(senhaTrimmed.length() - 3) : senhaTrimmed) + "'");
            System.out.println("[DEBUG SENHA] ========================================\n");

            System.out.println("[CONFIG EMAIL TESTE CONEXAO] ========================================");
            System.out.println("[CONFIG EMAIL TESTE CONEXAO] Testando APENAS conexão SMTP...");
            System.out.println("[CONFIG EMAIL TESTE CONEXAO] Host: " + smtpHost);
            System.out.println("[CONFIG EMAIL TESTE CONEXAO] Porta: " + smtpPort);
            System.out.println("[CONFIG EMAIL TESTE CONEXAO] Usuário: " + smtpUsuario);
            System.out.println("[CONFIG EMAIL TESTE CONEXAO] TLS: " + (smtpTls != null ? smtpTls : true));

            // Configurar propriedades SMTP
            java.util.Properties props = new java.util.Properties();
            props.put("mail.smtp.host", smtpHost);
            props.put("mail.smtp.port", smtpPort);
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", smtpTls != null ? smtpTls : true);
            props.put("mail.smtp.ssl.trust", smtpHost);
            props.put("mail.smtp.connectiontimeout", "10000");
            props.put("mail.smtp.timeout", "10000");

            // Criar sessão
            javax.mail.Session session = javax.mail.Session.getInstance(props, new javax.mail.Authenticator() {
                @Override
                protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
                    return new javax.mail.PasswordAuthentication(smtpUsuario, senhaTrimmed);
                }
            });

            // Testar conexão
            javax.mail.Transport transport = session.getTransport("smtp");
            transport.connect(smtpHost, smtpPort, smtpUsuario, senhaTrimmed);
            transport.close();

            System.out.println("[CONFIG EMAIL TESTE CONEXAO] ✓ Conexão SMTP bem-sucedida!");
            System.out.println("[CONFIG EMAIL TESTE CONEXAO] ========================================");

            return ResponseEntity.ok(Map.of(
                "mensagem", "Conexão SMTP estabelecida com sucesso!",
                "sucesso", true,
                "detalhes", Map.of(
                    "host", smtpHost,
                    "porta", smtpPort,
                    "usuario", smtpUsuario,
                    "tls", smtpTls != null ? smtpTls : true
                )
            ));

        } catch (javax.mail.AuthenticationFailedException e) {
            System.err.println("[CONFIG EMAIL TESTE CONEXAO] ✗ Falha de autenticação: " + e.getMessage());
            return ResponseEntity.status(401).body(Map.of(
                "erro", "Autenticação rejeitada: " + e.getMessage(),
                "sucesso", false
            ));
        } catch (javax.mail.MessagingException e) {
            System.err.println("[CONFIG EMAIL TESTE CONEXAO] ✗ Erro de conexão: " + e.getMessage());
            return ResponseEntity.status(500).body(Map.of(
                "erro", "Erro ao conectar: " + e.getMessage(),
                "sucesso", false
            ));
        } catch (Exception e) {
            System.err.println("[CONFIG EMAIL TESTE CONEXAO] ✗ Erro inesperado: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of(
                "erro", "Erro inesperado: " + e.getMessage(),
                "sucesso", false
            ));
        }
    }

    /**
     * Testar conexão SMTP e enviar email de teste
     */
    @PostMapping("/testar")
    public ResponseEntity<?> testarEmailCompleto(@RequestBody Map<String, Object> dados) {
        try {
            // Extrair dados do request
            String smtpHost = (String) dados.get("smtpHost");
            Integer smtpPort = (Integer) dados.get("smtpPort");
            String smtpUsuario = (String) dados.get("smtpUsuario");
            String smtpSenha = (String) dados.get("smtpSenha");
            Boolean smtpTls = (Boolean) dados.get("smtpTls");
            String emailRemetente = (String) dados.get("emailRemetente");
            String nomeRemetente = (String) dados.get("nomeRemetente");
            String emailDestino = (String) dados.get("emailDestino");

            // Validações básicas
            if (smtpHost == null || smtpHost.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("erro", "Host SMTP é obrigatório"));
            }
            if (smtpPort == null) {
                return ResponseEntity.badRequest().body(Map.of("erro", "Porta SMTP é obrigatória"));
            }
            if (emailDestino == null || emailDestino.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("erro", "Email de destino é obrigatório"));
            }

            // Criar configuração temporária
            ConfiguracaoEmail configTeste = new ConfiguracaoEmail();
            configTeste.setSmtpHost(smtpHost);
            configTeste.setSmtpPort(smtpPort);
            configTeste.setSmtpUsuario(smtpUsuario);
            configTeste.setSmtpSenha(smtpSenha);
            configTeste.setSmtpTls(smtpTls != null ? smtpTls : true);
            configTeste.setEmailRemetente(emailRemetente);
            configTeste.setNomeRemetente(nomeRemetente != null ? nomeRemetente : "Radar Fornecedor");
            configTeste.setLogoUrl((String) dados.get("logoUrl"));
            configTeste.setRodapeImagemUrl((String) dados.get("rodapeImagemUrl"));

            // Tentar enviar email de teste
            enviarEmailTeste(configTeste, emailDestino);

            return ResponseEntity.ok(Map.of(
                "mensagem", "✓ Email de teste enviado com sucesso para " + emailDestino,
                "sucesso", true
            ));

        } catch (Exception e) {
            System.err.println("[CONFIG EMAIL] Erro ao testar conexão: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of(
                "erro", "Falha ao enviar email de teste: " + e.getMessage(),
                "sucesso", false
            ));
        }
    }

    /**
     * Enviar email de teste
     */
    private void enviarEmailTeste(ConfiguracaoEmail config, String destinatario) throws Exception {
        System.out.println("[CONFIG EMAIL TESTE] ========================================");
        System.out.println("[CONFIG EMAIL TESTE] Iniciando teste de conexão SMTP...");
        System.out.println("[CONFIG EMAIL TESTE] Host: " + config.getSmtpHost());
        System.out.println("[CONFIG EMAIL TESTE] Porta: " + config.getSmtpPort());
        System.out.println("[CONFIG EMAIL TESTE] Usuário: " + config.getSmtpUsuario());
        System.out.println("[CONFIG EMAIL TESTE] Senha: " + (config.getSmtpSenha() != null && !config.getSmtpSenha().isEmpty() ? 
            "****** (" + config.getSmtpSenha().length() + " caracteres)" : "VAZIO!"));
        System.out.println("[CONFIG EMAIL TESTE] TLS: " + config.getSmtpTls());
        System.out.println("[CONFIG EMAIL TESTE] Remetente: " + config.getEmailRemetente());
        System.out.println("[CONFIG EMAIL TESTE] Destino: " + destinatario);
        System.out.println("[CONFIG EMAIL TESTE] ========================================");
        
        java.util.Properties props = new java.util.Properties();
        props.put("mail.smtp.host", config.getSmtpHost());
        props.put("mail.smtp.port", config.getSmtpPort());
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", config.getSmtpTls());
        props.put("mail.smtp.ssl.trust", config.getSmtpHost());
        props.put("mail.smtp.connectiontimeout", "10000");
        props.put("mail.smtp.timeout", "10000");
        props.put("mail.debug", "true"); // Debug detalhado

        javax.mail.Session session = javax.mail.Session.getInstance(props, new javax.mail.Authenticator() {
            @Override
            protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
                System.out.println("[CONFIG EMAIL TESTE] Autenticando com usuário: " + config.getSmtpUsuario());
                return new javax.mail.PasswordAuthentication(
                    config.getSmtpUsuario(), 
                    config.getSmtpSenha()
                );
            }
        });

        session.setDebug(true);

        javax.mail.Message message = new javax.mail.internet.MimeMessage(session);
        message.setFrom(new javax.mail.internet.InternetAddress(
            config.getEmailRemetente(), 
            config.getNomeRemetente()
        ));
        message.setRecipients(
            javax.mail.Message.RecipientType.TO, 
            javax.mail.internet.InternetAddress.parse(destinatario)
        );
        message.setSubject("✓ Teste de Configuração SMTP - Radar Fornecedor");

        // Corpo HTML do email de teste
        StringBuilder html = new StringBuilder();
        html.append("<html><body style='font-family: Arial, sans-serif; padding: 20px;'>");
        
        if (config.getLogoUrl() != null && !config.getLogoUrl().isEmpty()) {
            html.append("<div style='text-align: center; margin-bottom: 20px;'>");
            html.append("<img src='").append(config.getLogoUrl()).append("' alt='Logo' style='max-width: 200px;'>");
            html.append("</div>");
        }
        
        html.append("<div style='background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 30px; border-radius: 10px; text-align: center;'>");
        html.append("<h1 style='margin: 0; font-size: 28px;'>✓ Teste de Conexão SMTP</h1>");
        html.append("<p style='margin: 10px 0 0 0; font-size: 14px; opacity: 0.9;'>Radar Fornecedor</p>");
        html.append("</div>");
        
        html.append("<div style='background: #f8f9fa; padding: 30px; margin-top: 20px; border-radius: 10px;'>");
        html.append("<h2 style='color: #2d3748; margin-top: 0;'>Parabéns! 🎉</h2>");
        html.append("<p style='color: #4a5568; line-height: 1.6;'>Sua configuração SMTP está funcionando perfeitamente. Este é um email de teste enviado automaticamente pelo sistema.</p>");
        
        html.append("<div style='background: white; padding: 20px; margin: 20px 0; border-left: 4px solid #48bb78; border-radius: 5px;'>");
        html.append("<h3 style='color: #2d3748; margin-top: 0; font-size: 16px;'>Detalhes da Configuração:</h3>");
        html.append("<ul style='color: #4a5568; line-height: 1.8;'>");
        html.append("<li><strong>Servidor SMTP:</strong> ").append(config.getSmtpHost()).append(":").append(config.getSmtpPort()).append("</li>");
        html.append("<li><strong>Remetente:</strong> ").append(config.getNomeRemetente()).append(" &lt;").append(config.getEmailRemetente()).append("&gt;</li>");
        html.append("<li><strong>TLS/STARTTLS:</strong> ").append(config.getSmtpTls() ? "Ativado" : "Desativado").append("</li>");
        html.append("<li><strong>Data/Hora:</strong> ").append(java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))).append("</li>");
        html.append("</ul>");
        html.append("</div>");
        
        html.append("<p style='color: #718096; font-size: 14px; margin-top: 20px;'>Agora você pode salvar as configurações e começar a enviar emails automáticos através do sistema.</p>");
        html.append("</div>");
        
        if (config.getRodapeImagemUrl() != null && !config.getRodapeImagemUrl().isEmpty()) {
            html.append("<div style='text-align: center; margin-top: 30px; padding-top: 20px; border-top: 1px solid #e2e8f0;'>");
            html.append("<img src='").append(config.getRodapeImagemUrl()).append("' alt='Rodapé' style='max-width: 100%;'>");
            html.append("</div>");
        }
        
        html.append("<div style='text-align: center; margin-top: 30px; padding-top: 20px; border-top: 1px solid #e2e8f0; color: #a0aec0; font-size: 12px;'>");
        html.append("<p>© 2026 Radar Fornecedor - Sistema de Inteligência de Fornecedores</p>");
        html.append("</div>");
        
        html.append("</body></html>");

        message.setContent(html.toString(), "text/html; charset=utf-8");

        javax.mail.Transport.send(message);
        System.out.println("[CONFIG EMAIL TESTE] ========================================");
        System.out.println("[CONFIG EMAIL TESTE] ✓ Email de teste enviado com SUCESSO!");
        System.out.println("[CONFIG EMAIL TESTE] Destinatário: " + destinatario);
        System.out.println("[CONFIG EMAIL TESTE] ========================================");
    }

    /**
     * Salvar configuração
     */
    @PostMapping
    public ResponseEntity<?> salvarConfiguracao(@RequestBody ConfiguracaoEmail configuracao) {
        try {
            ConfiguracaoEmail configExistente = configuracaoRepository.findFirstByOrderByIdAsc().orElse(new ConfiguracaoEmail());
            
            // Atualizar campos
            configExistente.setSmtpHost(configuracao.getSmtpHost());
            configExistente.setSmtpPort(configuracao.getSmtpPort());
            configExistente.setSmtpUsuario(configuracao.getSmtpUsuario());
            
            // Só atualiza senha se foi fornecida
            if (configuracao.getSmtpSenha() != null && !configuracao.getSmtpSenha().isEmpty()) {
                configExistente.setSmtpSenha(configuracao.getSmtpSenha());
            }
            
            configExistente.setSmtpTls(configuracao.getSmtpTls());
            configExistente.setEmailRemetente(configuracao.getEmailRemetente());
            configExistente.setNomeRemetente(configuracao.getNomeRemetente());
            configExistente.setMensagemAprovacao(configuracao.getMensagemAprovacao());
            configExistente.setMensagemRejeicao(configuracao.getMensagemRejeicao());
            configExistente.setMensagemPreCadastro(configuracao.getMensagemPreCadastro());
            configExistente.setMensagemCadastroCompleto(configuracao.getMensagemCadastroCompleto());
            configExistente.setAssinatura(configuracao.getAssinatura());
            configExistente.setCumprimento(configuracao.getCumprimento());
            configExistente.setLogoUrl(configuracao.getLogoUrl());
            configExistente.setRodapeImagemUrl(configuracao.getRodapeImagemUrl());
            configExistente.setEmailAtivo(configuracao.getEmailAtivo());

            ConfiguracaoEmail salva = configuracaoRepository.save(configExistente);
            
            return ResponseEntity.ok(Map.of("mensagem", "Configurações salvas com sucesso", "config", salva));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("erro", "Erro ao salvar configurações: " + e.getMessage()));
        }
    }
}
