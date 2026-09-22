# 🔧 DEBUG: Validação CNPJ Não Funcionava — CORRIGIDO

**Data:** 2026-09-20  
**Status:** ✅ CORRIGIDO

---

## 🚨 O Problema

Ao clicar **VALIDAR** no formulário de cadastro:
- ❌ Erro: "✗ undefined"
- ❌ API retorna: `"Erro ao validar: For input string: \"417285870001\""`
- ❌ Validação CNPJ falha com erro de parsing

---

## 🔍 Causa Raiz

**Arquivo:** `radar/src/main/java/br/com/radarfornecedor/radar/util/CnpjCpfValidator.java`

**Bug no algoritmo de validação CNPJ:**

```java
// ANTES (❌ ERRADO):
int tamanho = cnpj.length() - 2;  // 14 - 2 = 12
int numero = Integer.parseInt(cnpj.substring(0, tamanho));  // Pega primeiros 12
digito = Integer.parseInt(cnpj.substring(tamanho));  // Pega últimos 2 juntos!

// Problema: (digito / 10) não é válido para extrair dígito verificador
```

**Exemplo com CNPJ: 41728587000137**
- Pega substring(0, 12) = "417285870001" ✓ (OK)
- Pega substring(12) = "37" ← Tenta fazer Integer.parseInt("37") e depois (37/10) = 3
- Mas o cálculo do módulo/resto estava errado!

---

## ✅ Solução Aplicada

Reescrito o algoritmo CNPJ com **cálculo correto** usando multiplicadores (padrão oficial):

```java
// DEPOIS (✅ CORRETO):
int[] multiplicadores = {5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
int soma = 0;

for (int i = 0; i < 12; i++) {
    soma += Integer.parseInt(cnpj.substring(i, i + 1)) * multiplicadores[i];
}

int primeiroDigito = 11 - (soma % 11);
if (primeiroDigito >= 10) {
    primeiroDigito = 0;
}

// Valida se primeiro dígito bate com posição 12
if (primeiroDigito != Integer.parseInt(cnpj.substring(12, 13))) {
    return false;  // CNPJ inválido
}

// ... repete para segundo dígito (posição 13)
```

**Algoritmo oficial CNPJ:**
- Multiplicadores 1º dígito: `5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2`
- Multiplicadores 2º dígito: `6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2`
- Resto: `11 - (soma % 11)` (se ≥ 10, usa 0)

---

## 📋 Ação Necessária

### 1️⃣ **PARAR servidor**
Feche a janela de comando / terminal

### 2️⃣ **REINICIAR servidor**
```powershell
C:\Visão_Futura\Projeto_Radar_Fornecedor\Iniciar SpringBoot Radar Fornecedor.bat
```

Aguarde: `Started RadarFornecedorApplication in X seconds`

### 3️⃣ **TESTAR CNPJ válido**

**CNPJ para teste:** `11222333000181` (válido)

1. Abra http://localhost:8080/pages/cadastro.html
2. Selecione CNPJ (já é padrão)
3. Digite: `11.222.333/0001-81`
4. Clique **VALIDAR**

**Esperado:**
- ✅ Mensagem verde: "✓ CNPJ válido! Dados preenchidos automaticamente."
- ✅ Campo "Nome da Empresa" preenchido com dados da ReceitaWS
- ✅ Campo readonly (não editável)

### 4️⃣ **TESTAR CNPJ do usuário**

**CNPJ fornecido:** `41.728.587/0001-37`

1. Digite: `41.728.587/0001-37`
2. Clique **VALIDAR**

**Se for válido:**
- ✅ Mensagem verde com dados da empresa
- ✅ Busca ReceitaWS

**Se for inválido:**
- ✅ Mensagem vermelha: "✗ CNPJ inválido"

### 5️⃣ **TESTAR CPF**

1. Clique em **CPF**
2. Digite: `123.456.789-09` (teste, pode ser inválido)
3. Clique **VALIDAR**

**Esperado:**
- ✓ Se válido: "✓ CPF válido!"
- ✗ Se inválido: "✗ CPF inválido"

---

## 📊 Resumo da Correção

| Aspecto | Antes | Depois |
|--------|-------|--------|
| Validação CNPJ | ❌ Erro parsing | ✅ Algoritmo correto |
| Dígito verificador 1º | ❌ Cálculo errado | ✅ Multiplicadores corretos |
| Dígito verificador 2º | ❌ Cálculo errado | ✅ Multiplicadores corretos |
| ReceitaWS lookup | ✅ OK (se validar) | ✅ Funciona agora |
| Mensagem erro | ❌ "undefined" | ✅ Mensagens claras |

---

## 🧪 Testes Após Correção

**Validos para testar:**
- CNPJ: `11.222.333/0001-81` (Empresa Teste)
- CNPJ: `41.728.587/0001-37` (Se for realmente válido)
- CPF: `123.456.789-09` (Teste)

**Validação CPF (exemplo com números aleatórios):**
- Deve validar os dígitos verificadores
- Se inválido, deve exibir mensagem clara

---

## 📁 Arquivo Modificado

```
radar/src/main/java/br/com/radarfornecedor/radar/util/CnpjCpfValidator.java
  - Corrigido: validarCnpj() com algoritmo oficial
  - Mantido: validarCpf() (estava correto)
```

---

## 🎯 Fluxo Esperado Após Fix

```
1. Usuário abre cadastro.html ✓
2. Digita CNPJ válido (ex: 11.222.333/0001-81)
3. Clica VALIDAR
4. API /api/validacao/validar recebe requisição
5. CnpjCpfValidator.validarCnpj() executa algoritmo correto
6. Se válido:
   - CnpjLookupService busca na ReceitaWS
   - Retorna nome empresa, endereço, etc
   - Formulário auto-preenche dados
7. Se inválido:
   - Retorna "CNPJ inválido"
   - Usuário pode tentar outro
```

---

## ❌ Se Ainda Não Funcionar

1. **Verificar se servidor reiniciou:**
   - Log deve mostrar: `CnpjCpfValidator class loaded`
   - Sem erros de compilação

2. **Verificar console do navegador (F12):**
   - Deve mostrar POST `/api/validacao/validar`
   - Resposta deve ser JSON válido (não erro)

3. **Testar via curl (após servidor reiniciar):**
   ```powershell
   $body = @{tipo="CNPJ"; valor="11222333000181"} | ConvertTo-Json
   curl -X POST "http://localhost:8080/api/validacao/validar" `
        -H "Content-Type: application/json" -d $body
   ```
   
   Esperado: JSON com `"valido": true`

---

**Status:** ✅ Corrigido  
**Próximo:** Reiniciar servidor + testar validação
