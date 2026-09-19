# Quick Start - API de Consulta CNPJ/CEP

## 🚀 Comece Agora em 5 Minutos

### 1️⃣ Inicie o Servidor
```bash
cd C:\Visão_Futura\Projeto_Radar_Fornecedor\radar
mvnw.cmd spring-boot:run
```

Aguarde até ver: `Started RadarApplication in X seconds`

### 2️⃣ Abra a Aplicação
```
http://localhost:8080/pages/fornecedores.html
```

### 3️⃣ Teste o Preenchimento Automático
1. Clique em **"NOVO FORNECEDOR"**
2. No campo **CNPJ**, digite: `11222333000181`
3. Pressione **TAB** (ou clique fora do campo)
4. ✨ Observe os campos serem preenchidos automaticamente!

### 4️⃣ Verifique Categoria e Atividade
- Dropdown **"Categoria de Produtos"** deve estar preenchido
- Dropdown **"Atividade (CNAE)"** deve estar preenchido
- Campos **Latitude/Longitude** devem ter valores

### 5️⃣ Execute os Testes Completos
```javascript
// Abrir DevTools (F12) → Console → Copiar e executar:
runAllTests()
```

---

## 📱 Testando via cURL

```bash
# Consultar CNPJ
curl "http://localhost:8080/api/consulta-cnpj/11222333000181"

# Consultar CEP (Avenida Paulista)
curl "http://localhost:8080/api/cep/01310100"

# Listar categorias
curl "http://localhost:8080/api/categorias/ativas"

# Listar atividades
curl "http://localhost:8080/api/atividades/ativas"
```

---

## 🔍 O Que Funciona

### ✅ Preenchimento Automático
- [x] Digitar CNPJ → Preenche nome, CEP, endereço
- [x] Digitar CEP → Preenche endereço + latitude/longitude
- [x] Latitude/Longitude calculadas automaticamente
- [x] Dropdowns de Categoria e Atividade carregados

### ✅ Formulários Atualizados
- [x] Fornecedores
- [x] Compradores  
- [x] Representantes

### ✅ APIs Integradas
- [x] **Serenata** - Consulta CNPJ (público)
- [x] **ViaCEP** - Consulta CEP (público)
- [x] **Nominatim** - Geolocalização (público)
- [x] **Banco de Dados** - Categorias e Atividades

---

## 🎯 Próximos Passos

### 1. Criar Categorias (Opcional)
```bash
curl -X POST "http://localhost:8080/api/categorias" \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "Eletrônicos",
    "descricao": "Produtos eletrônicos",
    "icone": "fa-laptop",
    "ativa": true
  }'
```

### 2. Criar Atividades (Opcional)
```bash
curl -X POST "http://localhost:8080/api/atividades" \
  -H "Content-Type: application/json" \
  -d '{
    "cnae": "4752",
    "descricao": "Comércio varejista de computadores",
    "secao": "G",
    "divisao": "47",
    "ativa": true
  }'
```

### 3. Salvar Registro Completo
1. Preencher todos os campos no formulário
2. Selecionar Categoria e Atividade
3. Clicar "SALVAR REGISTRO"
4. ✓ Registro salvo com sucesso!

---

## 🐛 Troubleshooting Rápido

### Problema: "CNPJ não encontrado"
**Solução**: Use CNPJ válido: `11222333000181` (Serenata)

### Problema: "Latitude/Longitude vazios"
**Solução**: Verifique se CEP é válido (ex: `01310100`)

### Problema: "Dropdowns de Categoria vazios"
**Solução**: 
```javascript
// No console:
fetch('http://localhost:8080/api/categorias', {
  method: 'POST',
  headers: {'Content-Type': 'application/json'},
  body: JSON.stringify({nome: 'Teste', ativa: true})
})
```

### Problema: "Servidor não responde"
**Solução**: Verifique se Spring Boot está rodando
```bash
# Terminal novo:
cd C:\Visão_Futura\Projeto_Radar_Fornecedor\radar
mvnw.cmd spring-boot:run
```

---

## 📊 Dados de Teste

### CNPJs para Testar
| CNPJ | Empresa |
|------|---------|
| 11222333000181 | Serenata de Amigos |
| 47123456000195 | (Exemplo aleatório) |

### CEPs para Testar
| CEP | Local |
|-----|-------|
| 01310100 | Avenida Paulista, SP |
| 20040020 | Centro, RJ |
| 30140071 | Savassi, MG |

---

## 💡 Dicas de Desenvolvimento

### Ativar Modo Debug
```javascript
// Console:
localStorage.setItem('debug', 'true')
// Recarregar página
location.reload()
```

### Verificar Requests
```javascript
// DevTools → Network → Filtrar por XHR
// Observar requisições para /api/consulta-cnpj, /api/cep, etc
```

### Monitorar Respostas
```javascript
// Console:
fetch('http://localhost:8080/api/consulta-cnpj/11222333000181')
  .then(r => r.json())
  .then(d => console.table(d))
```

### Limpar LocalStorage
```javascript
// Console:
localStorage.clear()
location.reload()
```

---

## 📚 Documentação Completa

Para guia detalhado, consulte:
- **GUIA_INTEGRACAO_API_CONSULTA.md** - Documentação técnica
- **RESUMO_IMPLEMENTACAO.md** - Estatísticas e checklist
- **testes-integracao.js** - Testes automatizados

---

## ✅ Validar Instalação

Execute no console do navegador:
```javascript
// Deve retornar 4 (todas as funções disponíveis)
[
  typeof consultarCnpj === 'function',
  typeof consultarCep === 'function', 
  typeof carregarCategorias === 'function',
  typeof carregarAtividades === 'function'
].filter(x => x).length
```

**Resultado esperado**: `4` ✓

---

## 🎉 Pronto!

Você está pronto para usar a integração completa de APIs de consulta CNPJ, CEP e Categorias!

**Próximo passo**: [Vá para o formulário de Fornecedores](http://localhost:8080/pages/fornecedores.html)

---

**Versão**: 1.0  
**Status**: ✅ Pronto para Produção  
**Suporte**: Consulte GUIA_INTEGRACAO_API_CONSULTA.md
