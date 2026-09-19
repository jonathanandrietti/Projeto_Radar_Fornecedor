# 🧹 LIMPEZA DE CACHE - Guia Completo

**Status:** ✅ CONCLUÍDO  
**Data:** 2026-09-19  
**Objetivo:** Remover cache que impede ver as melhorias

---

## 🎯 O Problema

Após compilação e deploy, você pode estar vendo:
- ❌ Erro "Não foi possível carregar os dados"
- ❌ Dados antigos sendo exibidos
- ❌ Páginas não refletindo mudanças
- ❌ Categorias mostrando `[object Object]`

**Causa:** Cache em 2 níveis:
1. **Spring Boot:** Cache Java em `/target/classes/`
2. **Browser:** Cache de arquivos HTML/CSS/JS

---

## ✅ SOLUÇÃO - 4 Passos

### 1. Parar Spring Boot
```powershell
Stop-Process -Name java -Force
```

### 2. Limpar Cache do Spring Boot
```powershell
Remove-Item -Recurse -Force "C:\Visão_Futura\Projeto_Radar_Fornecedor\radar\target\classes"
```

### 3. Limpar Cache do Browser

**Chrome/Chromium:**
- Pressione `Ctrl + Shift + Delete`
- Selecione "Todos os tempos"
- Clique "Limpar dados"

**Firefox:**
- Pressione `Ctrl + Shift + Delete`
- Clique "Limpar tudo"

**Edge:**
- Pressione `Ctrl + Shift + Delete`
- Clique "Limpar agora"

### 4. Reiniciar Spring Boot
```powershell
"C:\Visão_Futura\Projeto_Radar_Fornecedor\Iniciar SpringBoot Radar Fornecedor.bat"
```

---

## 🚀 Hard Refresh no Browser

Após reiniciar, faça **hard refresh** para forçar recarregar tudo:

| OS | Atalho |
|----|--------|
| Windows/Linux | `Ctrl + Shift + R` |
| Mac | `Cmd + Shift + R` |

Ou abra em **modo anônimo/privado** do browser (não usa cache).

---

## 🧪 Verificação

Após limpeza, teste:

```powershell
# Teste 1: API Fornecedores
Invoke-WebRequest -Uri "http://localhost:8080/api/fornecedores"

# Teste 2: API Clientes
Invoke-WebRequest -Uri "http://localhost:8080/api/clientes"

# Teste 3: API Categorias
Invoke-WebRequest -Uri "http://localhost:8080/api/produtos/categorias"
```

---

## ⚠️ Se Ainda Receber Erro 500

Se após limpeza ainda receber erro 500 em `/api/clientes` ou `/api/fornecedores`:

**Significa:** O banco de dados não tem as colunas novas!

**Solução:** Execute o script DDL

```sql
ALTER TABLE Clientes ADD COLUMN Email VARCHAR(100);
ALTER TABLE Clientes ADD COLUMN Telefone VARCHAR(20);
ALTER TABLE Clientes ADD COLUMN Endereco VARCHAR(500);
ALTER TABLE Clientes ADD COLUMN Foto BLOB;
ALTER TABLE Clientes ADD COLUMN FotoNome VARCHAR(255);

ALTER TABLE Fornecedores ADD COLUMN Email VARCHAR(100);
ALTER TABLE Fornecedores ADD COLUMN Telefone VARCHAR(20);
ALTER TABLE Fornecedores ADD COLUMN Foto BLOB;
ALTER TABLE Fornecedores ADD COLUMN FotoNome VARCHAR(255);
```

**Como executar:**
1. Baixe SQLite Browser: https://sqlitebrowser.org/
2. Abra `DBLRadar.db`
3. Vá em "Execute SQL"
4. Cole o script acima
5. Clique "Execute"

---

## 🔍 Checklist Final

- [ ] Spring Boot parado
- [ ] Cache de Spring removido
- [ ] Cache de browser limpo
- [ ] Spring Boot reiniciado
- [ ] Hard refresh (Ctrl+Shift+R) feito no browser
- [ ] Página carregando sem erros
- [ ] Dados sendo exibidos corretamente
- [ ] Se erro 500: DDL executado no banco

---

## 📝 Problemas Comuns

| Problema | Solução |
|----------|---------|
| Erro "Não foi possível carregar..." | Limpeza de cache não funcionou, tente modo privado |
| Erro 500 em /api/clientes | Executar script DDL |
| Categorias mostram [object Object] | Hard refresh (Ctrl+Shift+R) |
| Página branca | Verificar console (F12) para erros JavaScript |

---

**Última atualização:** 2026-09-19 02:45 BRT  
**Status:** ✅ CACHE LIMPO - PRONTO PARA USAR
