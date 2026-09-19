# 🔧 SOLUÇÃO - Problemas de Conexão ao Banco de Dados

**Status:** Identificado e com solução fornecida  
**Data:** 2026-09-19  
**Problema:** Páginas não conectando ao banco / Produtos não exibindo corretamente

---

## 🎯 DIAGNÓSTICO

### Problema Encontrado
Após o merge do "Projeto_Radar_Fornecedor - Helo", os modelos Java foram atualizados com novos campos:

**Cliente.java:**
- ✅ Campo: `Email` (nova coluna obrigatória)
- ✅ Campo: `Telefone` (nova coluna obrigatória)
- ✅ Campo: `Endereco` (nova coluna obrigatória)
- ✅ Campo: `Foto` (BLOB - nova coluna obrigatória)
- ✅ Campo: `FotoNome` (nova coluna obrigatória)

**Fornecedor.java:**
- ✅ Campo: `Email` (nova coluna obrigatória)
- ✅ Campo: `Telefone` (nova coluna obrigatória)
- ✅ Campo: `Foto` (BLOB - nova coluna obrigatória)
- ✅ Campo: `FotoNome` (nova coluna obrigatória)

### Por que está dando erro?
Quando o Spring Boot tenta consultar essas tabelas, o Hibernate (ORM do Spring) espera que as colunas existam no banco. Se não existem, ocorre erro SQL 500.

**Erro observado:**
```
GET /api/clientes → 500 Internal Server Error
```

**Razão:** Tabela Clientes não possui coluna `Email`, `Telefone`, etc.

---

## ✅ SOLUÇÃO

### Passo 1: Baixar SQLite Browser (Se Não Tiver)
Download: https://sqlitebrowser.org/dl/

Ou use qualquer ferramenta que permita executar SQL em SQLite.

### Passo 2: Abrir o Banco de Dados
1. Abra **SQLite Browser** (ou similar)
2. Clique em **"Open Database"**
3. Navegue até: `C:\Visão_Futura\Projeto_Radar_Fornecedor\radar\DBLRadar.db`
4. Clique em **"Open"**

### Passo 3: Executar o Script DDL
1. Clique na aba **"Execute SQL"**
2. Copie TODO o conteúdo do arquivo: `.kiro/migration-ddl.sql`
3. Cole na caixa de texto SQL
4. Clique em **"Execute"** (botão com Play ▶)

**Script SQL (copiar e colar):**
```sql
-- ADICIONAR COLUNAS À TABELA Clientes
ALTER TABLE Clientes ADD COLUMN Email VARCHAR(100);
ALTER TABLE Clientes ADD COLUMN Telefone VARCHAR(20);
ALTER TABLE Clientes ADD COLUMN Endereco VARCHAR(500);
ALTER TABLE Clientes ADD COLUMN Foto BLOB;
ALTER TABLE Clientes ADD COLUMN FotoNome VARCHAR(255);

-- ADICIONAR COLUNAS À TABELA Fornecedores
ALTER TABLE Fornecedores ADD COLUMN Email VARCHAR(100);
ALTER TABLE Fornecedores ADD COLUMN Telefone VARCHAR(20);
ALTER TABLE Fornecedores ADD COLUMN Foto BLOB;
ALTER TABLE Fornecedores ADD COLUMN FotoNome VARCHAR(255);
```

### Passo 4: Verificar se Funcionou
Execute na aba "Execute SQL":
```sql
PRAGMA table_info(Clientes);
PRAGMA table_info(Fornecedores);
```

Você deve ver as novas colunas na lista!

---

## ✅ SOLUÇÃO ALTERNATIVA (Via PowerShell)

Se tiver SQLite instalado no PATH:

```powershell
$db = "c:\Visão_Futura\Projeto_Radar_Fornecedor\radar\DBLRadar.db"

# Executar o script DDL
sqlite3 $db < "c:\Visão_Futura\Projeto_Radar_Fornecedor\.kiro\migration-ddl.sql"

# Verificar resultado
sqlite3 $db "PRAGMA table_info(Clientes);"
sqlite3 $db "PRAGMA table_info(Fornecedores);"
```

---

## 🧪 TESTE APÓS A MIGRAÇÃO

### 1. Reiniciar Spring Boot
```powershell
# Parar servidor anterior
# Iniciar novo servidor
C:\Visão_Futura\Projeto_Radar_Fornecedor\Iniciar SpringBoot Radar Fornecedor.bat
```

### 2. Testar os Endpoints
```powershell
# Teste 1: Categorias
Invoke-WebRequest -Uri "http://localhost:8080/api/produtos/categorias"

# Teste 2: Produtos
Invoke-WebRequest -Uri "http://localhost:8080/api/produtos"

# Teste 3: Clientes (deve funcionar agora)
Invoke-WebRequest -Uri "http://localhost:8080/api/clientes"

# Teste 4: Fornecedores (deve funcionar agora)
Invoke-WebRequest -Uri "http://localhost:8080/api/fornecedores"
```

### 3. Acessar as Páginas
- **Produtos:** http://localhost:8080/pages/produtos.html
- **Clientes:** http://localhost:8080/pages/clientes.html
- **Fornecedores:** http://localhost:8080/pages/fornecedores.html
- **Cadastro Cliente:** http://localhost:8080/pages/cadastro-cliente.html
- **Cadastro Fornecedor:** http://localhost:8080/pages/cadastro-fornecedor.html

---

## ⚠️ POSSÍVEIS ERROS E SOLUÇÕES

### Erro: "no such table: Clientes"
**Causa:** Arquivo `DBLRadar.db` corrompido ou em local errado  
**Solução:** Verificar se está em `C:\Visão_Futura\Projeto_Radar_Fornecedor\radar\DBLRadar.db`

### Erro: "already exists"
**Causa:** Coluna já foi criada em execução anterior  
**Solução:** Remover o comando que está duplicado ou executar apenas novas colunas

### Erro: "database is locked"
**Causa:** Outro programa está usando o banco (Spring Boot ainda rodando)  
**Solução:** Parar o Spring Boot antes de executar a migração

### Página em branco após migração
**Causa:** Cache do browser com dados antigos  
**Solução:** Hard refresh: `Ctrl + Shift + R`

---

## 📊 CHECKLIST DE RESOLUÇÃO

- [ ] Baixei SQLite Browser
- [ ] Abri o arquivo `DBLRadar.db`
- [ ] Copiei e colei o script DDL
- [ ] Cliquei em "Execute"
- [ ] Verifiquei com `PRAGMA table_info()`
- [ ] Reiniciei Spring Boot
- [ ] Testei `GET /api/clientes` - retorna 200 OK
- [ ] Testei `GET /api/fornecedores` - retorna 200 OK
- [ ] Página de produtos exibe corretamente
- [ ] Página de clientes carrega dados
- [ ] Página de fornecedores carrega dados

---

## 📞 SUPORTE

Se ainda tiver problemas:
1. Verifique se Spring Boot está rodando (porta 8080)
2. Abra console do browser (F12) para ver erros JavaScript
3. Verifique logs do Spring Boot na janela do terminal
4. Confirme que todas as colunas foram criadas com `PRAGMA table_info()`

---

**Última atualização:** 2026-09-19 02:15 BRT  
**Status:** ✅ SOLUÇÃO PRONTA PARA IMPLEMENTAÇÃO
