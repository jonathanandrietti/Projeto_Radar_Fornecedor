# ⚡ Acesso Rápido ao Banco H2

## 🎯 3 Maneiras de Abrir

### 1️⃣ **H2 Console (Recomendado)** ⭐

Acesso web direto, sem instalar nada.

**PASSO 1:** Já está ativado! ✅

**PASSO 2:** Reiniciar servidor
```powershell
Iniciar SpringBoot Radar Fornecedor_Jonathan.bat
```

**PASSO 3:** Abrir no navegador
```
http://localhost:8080/h2-console
```

**PASSO 4:** Conectar
```
JDBC URL:    jdbc:h2:./DBLRadar;MODE=MySQL
User Name:   sa
Password:    (deixe vazio)
```

Clique **CONNECT**

**PASSO 5:** Consultar dados
```sql
SELECT * FROM USUARIO;
SELECT * FROM SOLICITACAO_CADASTRO;
```

---

### 2️⃣ **DBeaver** (Profissional)

Ferramenta desktop mais completa.

1. Baixar: https://dbeaver.io/download/
2. Instalar
3. New Connection → H2
4. Database: `C:\...\DBLRadar.mv.db`
5. User: `sa`
6. Connect

---

### 3️⃣ **Linha de Comando** (Sem GUI)

```powershell
cd C:\Visão_Futura\Projeto_Radar_Fornecedor\radar
java -jar target/radar-0.0.1-SNAPSHOT.jar
# Depois acessa H2 Console conforme opção 1
```

---

## 📊 Queries Úteis

```sql
-- Ver todas as tabelas
SHOW TABLES;

-- Usuários
SELECT * FROM USUARIO;

-- Solicitações de cadastro
SELECT * FROM SOLICITACAO_CADASTRO ORDER BY data_criacao DESC;

-- Pendentes de aprovação
SELECT * FROM SOLICITACAO_CADASTRO WHERE status = 'PENDENTE';

-- Aprovadas
SELECT * FROM SOLICITACAO_CADASTRO WHERE status = 'APROVADO';
```

---

## 🔗 Links Diretos

| Recurso | URL |
|---------|-----|
| Sistema | http://localhost:8080 |
| Login | http://localhost:8080/login.html |
| Cadastro | http://localhost:8080/pages/cadastro.html |
| **H2 Console** | **http://localhost:8080/h2-console** ⭐ |

---

## ✅ Pronto!

H2 Console já está ativado. Basta:

1. Reiniciar servidor
2. Acessar http://localhost:8080/h2-console
3. Conectar com credenciais acima
4. Consultar dados

---

**Documentação Completa:** `.kiro/COMO-ABRIR-BANCO-H2.md`
