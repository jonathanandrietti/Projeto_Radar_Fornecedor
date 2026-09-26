# 📋 Dados Retornados da API de Consulta CNPJ

## 🔍 Endpoint de Consulta

**URL Base:** `http://localhost:8080/api/consulta-cnpj/{cnpj}`

**Endpoint de Teste:** `http://localhost:8080/api/consulta-cnpj/teste/{cnpj}`

---

## 📊 Lista Completa de Campos Retornados

A API de consulta CNPJ retorna os seguintes **13 campos**:

| # | Campo | Tipo | Descrição | Exemplo |
|---|-------|------|-----------|---------|
| 1 | **cnpj** | String | CNPJ formatado | `"00.000.000/0001-91"` |
| 2 | **nome** | String | Nome fantasia da empresa | `"BANCO DO BRASIL SA"` |
| 3 | **razaoSocial** | String | Razão social completa | `"BANCO DO BRASIL SA"` |
| 4 | **nomeFantasia** | String | Nome fantasia/filial | `"DIRECAO GERAL"` |
| 5 | **status** | String | **Status operacional da Receita** | `"OK"` / `"ATIVA"` / `"INATIVA"` |
| 6 | **dataAbertura** | String | Data de abertura da empresa | `"01/01/1966"` ou `null` |
| 7 | **logradouro** | String | Endereço (rua, avenida, etc.) | `"Q SAUN QUADRA 5 BLOCO B..."` |
| 8 | **numero** | String | Número do endereço | `"SN"` ou `"123"` |
| 9 | **complemento** | String | Complemento do endereço | `"ANDAR T I SL S101..."` |
| 10 | **bairro** | String | Bairro | `"ASA NORTE"` |
| 11 | **cidade** | String | Município | `"BRASILIA"` |
| 12 | **estado** | String | UF (sigla do estado) | `"DF"` |
| 13 | **cep** | String | CEP formatado | `"70.040-912"` |

---

## ⚠️ **Campo STATUS - ATENÇÃO!**

### 🔒 **Regra Importante:**

O campo **`status`** (Status Operacional) **NÃO PODE SER EDITADO MANUALMENTE** pelos usuários.

- ✅ **Deve vir automaticamente da consulta à Receita Federal**
- ❌ **Usuários não podem selecionar/alterar este campo**
- 🔄 **É atualizado automaticamente ao consultar o CNPJ**

### Valores Possíveis:
- `"OK"` → Empresa ativa
- `"ATIVA"` → Empresa em situação regular
- `"INATIVA"` → Empresa baixada/cancelada
- `"SUSPENSA"` → Empresa com cadastro suspenso
- `null` → Status não disponível

---

## 🧪 Exemplo de Uso - Teste Completo

### 1️⃣ **Via cURL:**
```bash
curl http://localhost:8080/api/consulta-cnpj/teste/00000000000191
```

### 2️⃣ **Via PowerShell:**
```powershell
Invoke-RestMethod -Uri "http://localhost:8080/api/consulta-cnpj/teste/00000000000191" | ConvertTo-Json -Depth 10
```

### 3️⃣ **Via JavaScript (Frontend):**
```javascript
fetch('http://localhost:8080/api/consulta-cnpj/00000000000191')
    .then(response => response.json())
    .then(dados => {
        console.log('Status da Receita:', dados.status);
        console.log('Razão Social:', dados.razaoSocial);
        console.log('Cidade:', dados.cidade);
    });
```

---

## 📦 Resposta Completa (Exemplo Real)

```json
{
  "totalCampos": 13,
  "mensagem": "Dados retornados da consulta CNPJ",
  "campos": [
    "cidade",
    "estado",
    "numero",
    "bairro",
    "nome",
    "cnpj",
    "cep",
    "dataAbertura",
    "nomeFantasia",
    "complemento",
    "logradouro",
    "razaoSocial",
    "status"
  ],
  "dados": {
    "cnpj": "00.000.000/0001-91",
    "nome": "BANCO DO BRASIL SA",
    "razaoSocial": "BANCO DO BRASIL SA",
    "nomeFantasia": "DIRECAO GERAL",
    "status": "OK",
    "dataAbertura": null,
    "logradouro": "Q SAUN QUADRA 5 BLOCO B TORRE I, II, III",
    "numero": "SN",
    "complemento": "ANDAR T I SL S101 A S1602 T II SL C101 A C1602 TIII SL N101 A N1602",
    "bairro": "ASA NORTE",
    "cidade": "BRASILIA",
    "estado": "DF",
    "cep": "70.040-912"
  }
}
```

---

## 🔄 APIs Utilizadas (Fallback)

O sistema tenta consultar dados em ordem de prioridade:

1. **API Serenata** (Principal): `https://api.serenata.ai/companies/{cnpj}`
2. **API ReceitaWS** (Fallback): `https://www.receitaws.com.br/v1/cnpj/{cnpj}`

---

## ✅ Implementação nos Formulários

### **Antes (❌ Incorreto):**
```html
<label>Status Operacional
    <select id="status">
        <option value="EM_ANALISE">Em análise</option>
        <option value="APROVADO">Aprovado</option>
        <option value="REJEITADO">Rejeitado</option>
        <option value="SUSPENSO">Suspenso</option>
    </select>
</label>
```

### **Depois (✅ Correto):**
```html
<label>Status Operacional (Receita Federal)
    <input id="status" readonly 
           class="bg-slate-100 font-semibold text-slate-600 cursor-not-allowed" 
           placeholder="Será preenchido automaticamente via CNPJ"
           title="Este campo é preenchido automaticamente pela consulta à Receita Federal">
</label>
```

---

## 📝 Arquivos Alterados

✅ **Frontend:**
- `fornecedores.html` → Campo status agora é `readonly`
- `compradores.html` → Campo status agora é `readonly`

✅ **Backend:**
- `ConsultaCnpjController.java` → Adicionado endpoint `/teste/{cnpj}` para debug

---

## 🎯 Conclusão

O campo **Status Operacional** é **obrigatoriamente preenchido pela Receita Federal** via consulta CNPJ.

Nenhum usuário pode editar este campo manualmente, garantindo a **integridade dos dados oficiais**.

---

**Data da Documentação:** 25/09/2026  
**Versão do Sistema:** 0.0.1-SNAPSHOT  
**Servidor:** http://localhost:8080
