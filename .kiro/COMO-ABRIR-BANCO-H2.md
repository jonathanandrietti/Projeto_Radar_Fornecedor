# 🗄️ Como Abrir e Consultar o Banco H2 (DBLRadar.mv.db)

**Banco:** H2 Database (embutido no Spring Boot)  
**Arquivo:** `C:\Visão_Futura\Projeto_Radar_Fornecedor\radar\DBLRadar.mv.db`  
**Status:** Persistente (dados não são deletados)

---

## 📍 Localização do Arquivo

```
C:\Visão_Futura\Projeto_Radar_Fornecedor\radar\DBLRadar.mv.db
```

---

## 🔧 Opção 1: Abrir via H2 Console (Melhor!)

H2 tem um console web embutido. Vamos ativar:

### Passo 1: Editar `application.properties`

```
C:\Visão_Futura\Projeto_Radar_Fornecedor\radar\src\main\resources\application.properties
```

Adicione estas linhas NO FINAL:

```properties
# H2 Console
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console
```

### Passo 2: Reiniciar servidor

```powershell
Iniciar SpringBoot Radar Fornecedor_Jonathan.bat
```

### Passo 3: Abrir H2 Console

Acesse no navegador:
```
http://localhost:8080/h2-console
```

### Passo 4: Conectar ao banco

Na tela do H2 Console:

| Campo | Valor |
|-------|-------|
| **JDBC URL** | `jdbc:h2:./DBLRadar;MODE=MySQL` |
| **User Name** | `sa` |
| **Password** | (deixe vazio) |

Clique **CONNECT**

### Passo 5: Consultar tabelas

```sql
-- Ver todas as tabelas
SHOW TABLES;

-- Ver solicitações de cadastro
SELECT * FROM SOLICITACAO_CADASTRO;

-- Ver usuários
SELECT * FROM USUARIO;

-- Ver dados específicos
SELECT id, usuario, status FROM SOLICITACAO_CADASTRO ORDER BY data_criacao DESC;
```

---

## 🔧 Opção 2: Abrir com DBeaver (Ferramenta Desktop)

Mais profissional e completo.

### Passo 1: Baixar DBeaver

Acesse: https://dbeaver.io/download/

Escolha versão Community (gratuita)

### Passo 2: Instalar

Duplo-clique no instalador e siga passos

### Passo 3: Conectar ao banco H2

1. Abra DBeaver
2. File → New → Database Connection
3. Selecione **H2**
4. Clique Next
5. Preencha:
   - **Host:** (deixe vazio)
   - **Database:** `C:\Visão_Futura\Projeto_Radar_Fornecedor\radar\DBLRadar.mv.db`
   - **User:** `sa`
   - **Password:** (vazio)
6. Clique Finish

### Passo 4: Explorar dados

- Expanda conexão H2
- Veja todas as tabelas
- Clique direito em tabela → View Data
- Escreva queries SQL

---

## 🔧 Opção 3: Linha de Comando (Java/Maven)

Se quiser sem ferramentas gráficas:

### Passo 1: Usando Java

```powershell
cd C:\Visão_Futura\Projeto_Radar_Fornecedor\radar

# Conectar ao banco H2 via CLI
java -cp mvnw.cmd org.h2.tools.Shell ^
  -url "jdbc:h2:./DBLRadar;MODE=MySQL" ^
  -user sa -password ""
```

Depois você pode digitar queries:

```sql
SELECT * FROM USUARIO;
SELECT * FROM SOLICITACAO_CADASTRO;
```

---

## 📊 Queries Úteis

### Ver estrutura das tabelas

```sql
-- Listar todas as tabelas
SHOW TABLES;

-- Ver estrutura de uma tabela
SHOW COLUMNS FROM USUARIO;
SHOW COLUMNS FROM SOLICITACAO_CADASTRO;
```

### Consultas de dados

```sql
-- Todos os usuários
SELECT * FROM USUARIO;

-- Usuários com flag aguardandoAprovacao
SELECT usuario, aguardando_aprovacao FROM USUARIO WHERE aguardando_aprovacao = TRUE;

-- Todas as solicitações de cadastro
SELECT * FROM SOLICITACAO_CADASTRO ORDER BY data_criacao DESC;

-- Solicitações pendentes
SELECT id, usuario, status, data_criacao FROM SOLICITACAO_CADASTRO WHERE status = 'PENDENTE';

-- Solicitações aprovadas
SELECT id, usuario, status, data_criacao FROM SOLICITACAO_CADASTRO WHERE status = 'APROVADO';

-- Contar registros
SELECT COUNT(*) as total_usuarios FROM USUARIO;
SELECT COUNT(*) as total_solicitacoes FROM SOLICITACAO_CADASTRO;
```

### Verificar dados de um usuário específico

```sql
-- Buscar usuário
SELECT * FROM USUARIO WHERE username = 'admin';

-- Buscar solicitação por usuário
SELECT * FROM SOLICITACAO_CADASTRO WHERE usuario = 'seu_usuario';
```

---

## 🎯 Recomendação

**Melhor opção:** H2 Console (Opção 1)
- ✅ Já vem com o Spring Boot
- ✅ Acesso via navegador
- ✅ Nenhuma instalação extra
- ✅ Rápido e prático

**Se quiser mais recursos:** DBeaver (Opção 2)
- ✅ Interface profissional
- ✅ Múltiplos bancos
- ✅ Export/Import dados
- ✅ Backups automáticos

---

## ⚙️ Ativar H2 Console Permanentemente

Para que H2 Console sempre esteja disponível:

### Editar `application.properties`

```
C:\Visão_Futura\Projeto_Radar_Fornecedor\radar\src\main\resources\application.properties
```

Adicione ao final:

```properties
# ===== H2 Console =====
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console
spring.h2.console.print-statement=true

# Permitir acesso sem autenticação
spring.h2.console.settings.trace=false
spring.h2.console.settings.web-allow-others=false
```

### Reiniciar servidor

```powershell
Iniciar SpringBoot Radar Fornecedor_Jonathan.bat
```

### Acessar

```
http://localhost:8080/h2-console
```

---

## 🔒 Segurança

⚠️ **NUNCA** abra H2 Console com `web-allow-others=true` em produção!

Mantém `false` para acesso apenas local.

---

## 🆘 Troubleshooting

| Problema | Solução |
|----------|---------|
| Erro "Database "DBLRadar" not found" | Verifique caminho do banco: `C:\....\DBLRadar.mv.db` |
| Erro "User "sa" not found" | User correto é `sa` (sem password) |
| Porta 8080 já em uso | Outro processo Java rodando. Feche e reinicie. |
| H2 Console não aparece | Adicione `spring.h2.console.enabled=true` em application.properties |

---

**Status:** ✅ Pronto para usar  
**Acesso:** http://localhost:8080/h2-console (após ativar)
