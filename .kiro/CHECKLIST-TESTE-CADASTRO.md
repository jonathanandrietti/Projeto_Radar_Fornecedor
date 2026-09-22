# ✅ Checklist de Teste — Sistema de Cadastro com Aprovação Admin

**Data:** 2026-09-20  
**Status:** Pronto para Testar

---

## 📋 PRÉ-TESTE

### 1. Servidor Spring Boot
- [ ] Spring Boot está rodando em http://localhost:8080
- [ ] Banco H2 em `C:\Visão_Futura\Projeto_Radar_Fornecedor\radar\DBLRadar.mv.db`
- [ ] Log do servidor não mostra erros críticos

**Comando para iniciar (se não estiver rodando):**
```powershell
C:\Visão_Futura\Projeto_Radar_Fornecedor\Iniciar SpringBoot Radar Fornecedor.bat
```

### 2. Cache do Browser
- [ ] Hard refresh executado: `Ctrl+Shift+R`
- [ ] Abas anteriores do projeto fechadas
- [ ] Browser fechado e reaberto completamente

---

## 🔐 TESTE 1: Login Padrão (Usuario Existente)

1. Abra http://localhost:8080
   - [ ] Página de login carrega
   - [ ] Logo RADAR visível
   - [ ] 4 módulos exibidos (Fornecedor, Comprador, Representante, Cliente)

2. Faça login com credenciais padrão:
   - **Usuário:** `admin`
   - **Senha:** `admin`
   - [ ] Login bem-sucedido
   - [ ] Redirecionado para dashboard admin (`/pages/admin.html`)

**Se falhar:**
- Verificar: `application.properties` → `spring.datasource.url`
- Verificar: BCryptPasswordEncoder em UsuarioService

---

## 🆕 TESTE 2: Botão CADASTRO (CRÍTICO)

1. Abra http://localhost:8080
2. Localize botão **CADASTRO** (dourado, com ícone 👤+)
3. **Clique no botão CADASTRO**
   - [ ] Navegação acontece **SEM** tentativa de submit de login
   - [ ] URL muda para http://localhost:8080/pages/cadastro.html
   - [ ] Página de cadastro carrega corretamente

**Se não funcionar:**
- [ ] Verificar console do navegador (F12 → Console)
- [ ] Executar: `Ctrl+Shift+R` (hard refresh)
- [ ] Fechar browser completamente e reabrir
- [ ] Confirmar que arquivo foi copiado para `/target/classes/static/login.html`

---

## 📝 TESTE 3: Formulário de Cadastro

### 3a. Validação de CNPJ

1. Clique em **CNPJ** (já é padrão)
2. Digite CNPJ válido: `11222333000181` (empresa TESTE)
3. Clique **VALIDAR**
   - [ ] Campo muda de cor (sucesso verde)
   - [ ] Nome da Empresa auto-preenchido (busca ReceitaWS)
   - [ ] Mensagem de sucesso exibida

**Se falhar:**
- Validador: `CnpjCpfValidator.java` → algoritmo CNPJ
- ReceitaWS: `CnpjLookupService.java` → busca de dados

### 3b. Validação de CPF

1. Mude para **CPF**
2. Digite CPF válido: `12345678901` (teste)
3. Clique **VALIDAR**
   - [ ] Campo muda de cor (sucesso)
   - [ ] Campo "Nome da Empresa" muda para label "Nome Livre"
   - [ ] Nome NÃO é auto-preenchido (usuário preenche manualmente)

---

## 📧 TESTE 4: Envio de Solicitação de Cadastro

1. Preencha formulário com dados:
   - **CNPJ/CPF:** `11222333000181` (válido)
   - **Nome de Contato:** `João Silva`
   - **E-mail:** `joao@empresa.com`
   - **Telefone Fixo:** `(11) 3000-0000`
   - **Celular:** `(11) 98765-4321` (obrigatório)
   - **WhatsApp?:** ☑️ Sim
   - **Usuário:** `joao.silva`
   - **Senha:** `Senha@123` (deve validar força)

2. Clique **SOLICITAR CADASTRO**
   - [ ] POST `/api/cadastro/solicitar` enviado
   - [ ] Resposta 200 OK
   - [ ] Mensagem de sucesso: "Solicitação recebida! Aguardando aprovação do administrador"
   - [ ] Dados salvos em tabela `SolicitacaoCadastro`

**Verificar no banco:**
```sql
SELECT * FROM SOLICITACAO_CADASTRO ORDER BY data_criacao DESC LIMIT 1;
```

Esperado:
- `status = 'PENDENTE'`
- `usuario NOT NULL`
- `senha_hash` (criptografada com BCrypt)
- `data_criacao = TODAY()`

---

## 👤 TESTE 5: Dashboard Admin — Aprovação de Solicitações

1. Faça login como admin: `admin` / `admin`
2. Acesse Dashboard Admin (`/pages/admin.html`)
   - [ ] Notificação visual de "X solicitações aguardando aprovação"
   - [ ] Link/botão para "Solicitações de Cadastro"

3. Abra lista de solicitações pendentes
   - [ ] Exibe solicitação criada no TESTE 4
   - [ ] Dados estão corretos (CNPJ, contato, email, etc.)

4. Clique **APROVAR**
   - [ ] POST `/api/cadastro/aprovar/{id}` enviado
   - [ ] Status muda de PENDENTE → APROVADO
   - [ ] Flag `aguardandoAprovacao` no Usuario muda de TRUE → FALSE
   - [ ] **E-mail enviado** ao cliente (verificar logs ou email real)

**Verificar no banco:**
```sql
SELECT * FROM USUARIO WHERE username = 'joao.silva';
```

Esperado:
- `aguardandoAprovacao = FALSE`
- `data_aprovacao = HOJE()`

---

## 🔑 TESTE 6: Login com Usuário Aprovado

1. Faça logout (se ainda logado como admin)
2. Volte para http://localhost:8080
3. Faça login com credenciais do TESTE 4:
   - **Usuário:** `joao.silva`
   - **Senha:** `Senha@123` (MESMA do formulário)
   - [ ] Login bem-sucedido
   - [ ] Senha criptografada foi comparada corretamente com BCrypt
   - [ ] Redirecionado para dashboard baseado no `tipo` do usuário

**Verificar no banco:**
- Senha em banco NÃO é legível (hasheada)
- Exato do formulário comparado com hash

---

## 🗂️ TESTE 7: Persistência de Dados

1. Reinicie o Spring Boot (parar e iniciar novamente)
   ```powershell
   C:\Visão_Futura\Projeto_Radar_Fornecedor\Iniciar SpringBoot Radar Fornecedor.bat
   ```

2. Verifique:
   - [ ] Banco H2 mantém todos os dados (nenhum foi deletado)
   - [ ] Solicitação de cadastro do TESTE 4 ainda existe
   - [ ] Usuário `joao.silva` ainda existe com status APROVADO
   - [ ] Histórico completo preservado

**Se dados foram deletados:**
- ❌ CRÍTICO: Verifique `application.properties`
- Deve ser: `spring.jpa.hibernate.ddl-auto=update`
- NÃO deve ser: `create-drop` ou `create`

---

## ✅ TESTE 8: Rejeição de Solicitação (Alternativo)

1. Envie nova solicitação com dados diferentes (TESTE 4 repetido)
2. Admin escolhe **REJEITAR** em vez de aprovar
   - [ ] POST `/api/cadastro/rejeitar/{id}` enviado
   - [ ] Status muda de PENDENTE → REJEITADO
   - [ ] Usuário NÃO é criado
   - [ ] E-mail enviado ao cliente com motivo (se configurado)

---

## 🔍 TESTE 9: Validações de Formulário

| Campo | Teste | Esperado |
|-------|-------|----------|
| CNPJ/CPF inválido | Digite `1234567890` | Botão VALIDAR desabilitado / erro |
| Email inválido | Digite `email_ruim` | Validação HTML5 bloqueia submit |
| Celular vazio | Deixe em branco | Mensagem obrigatório |
| Usuário duplicado | Use `admin` | Erro: usuário já existe |
| Senha fraca | Digite `123` | Validação de força de senha |

---

## 📊 TESTE 10: Backup Automático

1. Verifique pasta: `C:\Visão_Futura\Projeto_Radar_Fornecedor\radar\BackUpBanco\`
   - [ ] Pasta existe
   - [ ] Contém arquivo backup (ex: `DBLRadar.mv.db.backup.TIMESTAMP`)
   - [ ] Apenas **último** backup é mantido (outros apagados)

2. Reinicie servidor
   - [ ] Novo backup criado automaticamente
   - [ ] Backup anterior removido (apenas 1 mantido)

---

## 🎯 RESULTADO FINAL

Marque como **✅ PASSOU** apenas se TODOS os testes passarem:

- [ ] TESTE 1: Login padrão funciona
- [ ] TESTE 2: Botão CADASTRO abre página de cadastro
- [ ] TESTE 3: Validação CNPJ/CPF funciona
- [ ] TESTE 4: Solicitação é enviada e salva
- [ ] TESTE 5: Admin aprova solicitação
- [ ] TESTE 6: Usuário faz login com credenciais do pré-cadastro
- [ ] TESTE 7: Dados persistem após reinicialização
- [ ] TESTE 8: Rejeição funciona
- [ ] TESTE 9: Validações funcionam
- [ ] TESTE 10: Backup automático funciona

**Se PASSOU TODOS:** Sistema está pronto para produção ✅

---

## 🐛 Se Algum Teste Falhar

1. **Abra o Console do Browser (F12 → Console)**
   - Copie erros de JavaScript
   - Procure por erros de rede (red requests)

2. **Verifique logs do Spring Boot:**
   - Abra arquivo: `C:\Visão_Futura\Projeto_Radar_Fornecedor\radar\spring_boot.log`
   - Procure por exceções (Exception, Error)

3. **Reinicie TUDO:**
   - Feche browser
   - Para servidor Spring Boot
   - Hard delete cache (Ctrl+Shift+Delete)
   - Reinicie servidor
   - Reabra browser

4. **Contate desenvolvedor com:**
   - Screenshot do erro
   - Logs do browser (F12)
   - Logs do Spring Boot (spring_boot.log)
