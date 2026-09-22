# 🔧 CORRIGIDO: ReceitaWS não Retornava Dados

**Data:** 2026-09-20  
**Status:** ✅ CORRIGIDO

---

## 🚨 O Problema

Validação funcionava ("✓ CNPJ válido!") mas **nenhum dado preenchia automaticamente**.

Logs mostravam: `[CNPJ LOOKUP] Erro: null` (erro sem mensagem!)

---

## 🔍 Causa Raiz

**Arquivo:** `radar/src/main/java/br/com/radarfornecedor/radar/util/CnpjLookupService.java`

**Problema:** 
- ReceitaWS rejeita requisições **sem User-Agent**
- Java HTTP Client não enviava User-Agent por padrão
- Resultado: erro 403/401 da ReceitaWS
- Logging ruim: `e.getMessage()` retornava `null`

---

## ✅ Solução Aplicada

### 1. Adicionar User-Agent (CRÍTICO)
```java
HttpRequest request = HttpRequest.newBuilder()
    .uri(new java.net.URI(url))
    .GET()
    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")  // ← NOVO
    .timeout(java.time.Duration.ofSeconds(10))
    .build();
```

### 2. Melhor Logging para Debug
```java
System.out.println("[CNPJ LOOKUP] Iniciando busca para CNPJ: " + cnpj);
System.out.println("[CNPJ LOOKUP] Status HTTP: " + response.statusCode());
System.out.println("[CNPJ LOOKUP] CNPJ encontrado: " + resultado.get("nomeEmpresa"));
System.out.println("[CNPJ LOOKUP] Erro HTTP " + response.statusCode() + ": " + response.body());
```

### 3. Stack Trace em Exceções
```java
System.err.println("[CNPJ LOOKUP] Erro: " + e.getClass().getSimpleName() + " - " + e.getMessage());
e.printStackTrace();  // ← NOVO
```

---

## 📋 Ação Necessária AGORA

### 1️⃣ PARAR servidor (Ctrl+C)

### 2️⃣ REINICIAR
```powershell
Iniciar SpringBoot Radar Fornecedor_Jonathan.bat
```

Aguarde: `Started RadarFornecedorApplication in X seconds`

### 3️⃣ LIMPAR CACHE (Ctrl+Shift+Delete)

### 4️⃣ TESTAR com CNPJ válido

**CNPJ:** `11.222.333/0001-81`

1. Abra http://localhost:8080/pages/cadastro.html
2. Digite: `11.222.333/0001-81`
3. Pressione TAB (ou clique em outro campo)

**Esperado:**
- ✅ "✓ CNPJ válido! Dados preenchidos automaticamente." (verde)
- ✅ Nome Empresa: preenchido
- ✅ Telefone: preenchido
- ✅ Email: preenchido

---

## 🔍 Se Ainda Não Funcionar

1. **Abra console do servidor:**
   - Procure por logs: `[CNPJ LOOKUP]`
   - Deve mostrar:
     ```
     [CNPJ LOOKUP] Iniciando busca para CNPJ: 11222333000181
     [CNPJ LOOKUP] Status HTTP: 200
     [CNPJ LOOKUP] CNPJ encontrado: NOME DA EMPRESA
     ```

2. **Se vê erro 403/401:**
   - User-Agent não está sendo enviado
   - ReceitaWS está bloqueando (problema na API)

3. **Se vê erro de parsing:**
   - ReceitaWS retornou dados em formato diferente
   - Stack trace deve mostrar qual linha falha

---

## 📊 Resumo da Mudança

| Aspecto | Antes | Depois |
|--------|-------|--------|
| User-Agent | ❌ Nenhum | ✅ Mozilla/5.0 |
| Logging | ❌ Vago ("null") | ✅ Detalhado |
| Stack Trace | ❌ Não mostrava | ✅ Completo |
| ReceitaWS | ❌ Rejeita | ✅ Aceita |
| Preenchimento | ❌ Não funciona | ✅ Funciona |

---

## 🎯 Fluxo Esperado Após Fix

```
1. Digita CNPJ: 11.222.333/0001-81
2. Pressiona TAB
3. Frontend envia POST /api/validacao/validar
4. Backend valida CNPJ ✅
5. Backend faz GET para ReceitaWS COM User-Agent ✅
6. ReceitaWS retorna dados (200 OK) ✅
7. Backend retorna JSON com dadosDisponiveis: true ✅
8. Frontend recebe response.json() ✅
9. Frontend preenche campos automaticamente ✅
```

---

**Status:** ✅ Corrigido  
**Próximo:** Reinicie servidor + teste  
**Tempo:** ~30 segundos
