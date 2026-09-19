# Guia de Integração - API de Consulta CNPJ, CEP e Categorias

## Visão Geral

Este documento descreve a integração completa de APIs públicas de consulta de CNPJ, CEP com geolocalização e gestão de categorias/atividades de produtos no sistema Radar Fornecedor.

## Arquitetura Implementada

### 1. Backend (Java Spring Boot)

#### Entidades de Dados
- **Categoria** (`model/Categoria.java`): Tabela para categorias de produtos
- **Atividade** (`model/Atividade.java`): Tabela para atividades econômicas (CNAE)
- **Comprador, Fornecedor, Representante**: Atualizados com relacionamentos ManyToOne

#### Serviços de Integração
1. **ConsultaCnpjService**
   - Integra com API Serenata (público)
   - Fallback para ReceitaWS
   - Valida e transforma dados de resposta

2. **GeolocalizacaoCepService**
   - Consulta ViaCEP para dados de endereço
   - Integra com Nominatim (OpenStreetMap) para latitude/longitude
   - Retorna coordenadas GPS

3. **PreenchimentoAutomaticoService**
   - Orquestra consultas de CNPJ e CEP
   - Retorna dados consolidados em DTO

4. **CategoriaService** e **AtividadeService**
   - CRUD completo para gestão de categorias/atividades

#### Endpoints Disponíveis

**Consulta de CNPJ** (Público)
```
GET  /api/consulta-cnpj/{cnpj}           - Consulta via CNPJ formatado ou não
GET  /api/consulta-cnpj/validar/{cnpj}   - Apenas validação
POST /api/consulta-cnpj/buscar            - Alternativo POST
```

**Consulta de CEP** (Público)
```
GET  /api/cep/{cep}                      - Consulta via CEP
GET  /api/cep/localizacao?params         - Obter latitude/longitude por endereço
GET  /api/cep/validar/{cep}              - Apenas validação
POST /api/cep/buscar                     - Alternativo POST
POST /api/cep/localizacao                - Alternativo POST
```

**Preenchimento Automático** (Público)
```
GET  /api/preenchimento-automatico/cnpj/{cnpj}  - Preenche por CNPJ
GET  /api/preenchimento-automatico/cep/{cep}    - Preenche por CEP
POST /api/preenchimento-automatico              - Alternativo POST
```

**Categorias** (Autenticado)
```
GET    /api/categorias                    - Lista todas
GET    /api/categorias/ativas             - Apenas ativas
GET    /api/categorias/{id}               - Detalhes
GET    /api/categorias/nome/{nome}        - Busca por nome
POST   /api/categorias                    - Criar
PUT    /api/categorias/{id}               - Atualizar
DELETE /api/categorias/{id}               - Excluir
PATCH  /api/categorias/{id}/desativar     - Desativar
```

**Atividades** (Autenticado)
```
GET    /api/atividades                    - Lista todas
GET    /api/atividades/ativas             - Apenas ativas
GET    /api/atividades/{id}               - Detalhes
GET    /api/atividades/cnae/{cnae}        - Busca por CNAE
GET    /api/atividades/descricao/{desc}   - Busca por descrição
GET    /api/atividades/secao/{secao}      - Atividades por seção
POST   /api/atividades                    - Criar
PUT    /api/atividades/{id}               - Atualizar
DELETE /api/atividades/{id}               - Excluir
PATCH  /api/atividades/{id}/desativar     - Desativar
```

### 2. Frontend (JavaScript/HTML)

#### Script Principal: `preenchimento-automatico.js`

**Funções Principais:**
- `consultarCnpj(cnpj)`: Consulta API de CNPJ
- `consultarCep(cep)`: Consulta API de CEP
- `carregarCategorias(elementId)`: Popula dropdown de categorias
- `carregarAtividades(elementId)`: Popula dropdown de atividades
- `integrarConsultaCnpj(element)`: Ativa consulta ao perder foco
- `integrarConsultaCep(element)`: Ativa consulta ao perder foco

**Alertas Visuais:**
- `mostrarSucesso(mensagem)`: Alerta verde
- `mostrarErro(mensagem)`: Alerta vermelho
- `mostrarCarregamento(mensagem)`: Spinner de carregamento

#### Formulários Atualizados

1. **fornecedores.html**
   - Campo CNPJ com busca automática
   - Campo CEP com preenchimento de endereço e geolocalização
   - Dropdown de Categoria de Produtos
   - Dropdown de Atividade (CNAE)
   - Campos de latitude/longitude (somente leitura)

2. **compradores.html**
   - Mesmos campos do fornecedor
   - Adaptação para layout de comprador

3. **representantes.html**
   - Mesmo CNPJ do representante + CNPJ do fornecedor
   - Categoria e atividade opcionais
   - Endereço completo com geolocalização

## Fluxo de Teste Passo a Passo

### Pré-requisitos
- Servidor Spring Boot rodando na porta 8080
- Banco de dados SQLite sincronizado
- Browser com conexão à internet (para APIs públicas)

### Teste 1: Consulta de CNPJ

**Via cURL:**
```bash
# Consultar dados de empresa (CNPJ da Serenata)
curl -X GET "http://localhost:8080/api/consulta-cnpj/11222333000181"

# Validar CNPJ
curl -X GET "http://localhost:8080/api/consulta-cnpj/validar/11222333000181"
```

**Via Browser:**
1. Abrir DevTools (F12)
2. Ir para aba Console
3. Executar: `consultarCnpj('11222333000181')`
4. Verificar se campos são preenchidos

**Resposta Esperada:**
```json
{
  "cnpj": "11222333000181",
  "nome": "Serenata de Amigos",
  "razaoSocial": "Serenata de Amigos",
  "logradouro": "Rua X",
  "cidade": "São Paulo",
  "estado": "SP",
  "cep": "01234567",
  "atividade": "...",
  "status": "ATIVA",
  "sucesso": true
}
```

### Teste 2: Consulta de CEP

**Via cURL:**
```bash
# Consultar CEP
curl -X GET "http://localhost:8080/api/cep/01310100"

# Obter localização para endereço
curl -X GET "http://localhost:8080/api/cep/localizacao?cidade=Sao%20Paulo&estado=SP"
```

**Via Browser:**
```javascript
consultarCep('01310100')
```

**Resposta Esperada:**
```json
{
  "cep": "01310-100",
  "logradouro": "Avenida Paulista",
  "cidade": "São Paulo",
  "estado": "SP",
  "latitude": -23.561414,
  "longitude": -46.656139,
  "sucesso": true
}
```

### Teste 3: Preenchimento Automático em Formulário

1. Abrir página de Cadastro de Fornecedor (`/pages/fornecedores.html`)
2. Clicar em "NOVO FORNECEDOR"
3. No campo CNPJ, digitar: `11222333000181`
4. Pressionar TAB (ativa blur event)
5. **Resultado esperado:**
   - Campo Nome preenchido automaticamente
   - Campo CEP preenchido automaticamente
   - Campos de endereço preenchidos
   - Latitude/Longitude calculadas
   - Dropdown de Categorias carregado

### Teste 4: Dropdown de Categorias

```javascript
// Carregar categorias
carregarCategorias('categoria')

// Verificar se opções aparecem
document.getElementById('categoria').options.length // Deve ser > 1
```

### Teste 5: Dropdown de Atividades

```javascript
// Carregar atividades agrupadas por seção
carregarAtividades('atividade')

// Verificar quantas seções foram criadas
document.querySelectorAll('#atividade optgroup').length
```

### Teste 6: Salvamento de Registro com Categorias

1. Preencher formulário de Fornecedor completo:
   - Nome: "Empresa Teste Ltda"
   - CNPJ: "11222333000181"
   - Status: "Aprovado"
   - Categoria: Selecionar uma opção
   - Atividade: Selecionar uma opção

2. Clicar em "SALVAR REGISTRO"

3. Verificar no Network (DevTools > Network) que:
   - POST `/api/fornecedores` foi enviado
   - Payload contém `categoria: {id: X}`
   - Payload contém `atividade: {id: Y}`
   - Resposta HTTP 201 (Created)

## Estrutura de Dados

### Tabela Categorias
```sql
CREATE TABLE Categorias (
  id INTEGER PRIMARY KEY AUTO_INCREMENT,
  nome VARCHAR(255) UNIQUE NOT NULL,
  descricao VARCHAR(500),
  icone VARCHAR(50),
  ativa BOOLEAN DEFAULT TRUE,
  criadaEm TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  atualizadaEm TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### Tabela Atividades
```sql
CREATE TABLE Atividades (
  id INTEGER PRIMARY KEY AUTO_INCREMENT,
  cnae VARCHAR(10) UNIQUE,
  descricao VARCHAR(255) UNIQUE NOT NULL,
  secao VARCHAR(5),
  divisao VARCHAR(5),
  ativa BOOLEAN DEFAULT TRUE,
  criadaEm TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  atualizadaEm TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### Relacionamentos
- `Fornecedores.categoria_id` → `Categorias.id` (ManyToOne)
- `Fornecedores.atividade_id` → `Atividades.id` (ManyToOne)
- Mesmo para `Compradores` e `Representantes`

## Tratamento de Erros

### CNPJ Não Encontrado
```json
{
  "erro": true,
  "mensagem": "Empresa não encontrada para o CNPJ informado",
  "sucesso": false
}
```

### CEP Inválido
```json
{
  "erro": true,
  "mensagem": "CEP inválido. Deve conter 8 dígitos",
  "sucesso": false
}
```

### API Indisponível
```json
{
  "erro": true,
  "mensagem": "Erro ao consultar CNPJ: Connection refused",
  "sucesso": false
}
```

## Performance e Limitações

### Limites de API
- **ViaCEP**: Sem limite documentado, gratuita
- **Nominatim**: 1 req/s por IP (incluído no delay)
- **Serenata**: Sem limite documentado, gratuita
- **ReceitaWS**: 3 req/s sem autenticação

### Timeouts
- Consulta CNPJ: ~2-3s (depende da API)
- Consulta CEP: ~1s (ViaCEP) + ~1s (Nominatim)
- Carregamento de categorias: ~500ms

### Cache Recomendado
- Armazenar resultados de CNPJ por 24 horas
- Armazenar resultados de CEP por 1 semana (dados públicos não mudam)
- Cachear lista de Categorias/Atividades no localStorage

## Integração com Ferramentas Externas

### Google Maps
Para exibir mapa com as coordenadas obtidas:
```html
<iframe src="https://maps.google.com/maps?q={latitude},{longitude}&z=16&output=embed" 
        width="400" height="300"></iframe>
```

### OpenStreetMap
Alternativa gratuita:
```html
<iframe src="https://www.openstreetmap.org/export/embed.html?bbox=...&layer=mapnik" 
        width="400" height="300"></iframe>
```

## Próximas Melhorias

1. **Cache em Memória**: Redis para cache distribuído
2. **Fila de Requisições**: Para lidar com múltiplas consultas simultâneas
3. **Webhook de Sincronização**: Para manter Categorias/Atividades atualizadas
4. **Dashboard de Analytics**: Monitorar uso de APIs
5. **OCR para Documentos**: Extrair CNPJ de documentos scaneados
6. **Validação de Certificado**: Verificar validade de certificações (ISO, etc)

## Troubleshooting

### Problema: CNPJ não retorna resultados
**Solução:** 
- Verificar se CNPJ tem 14 dígitos
- Tentar com outro CNPJ (ex: 11222333000181 - Serenata)
- Verificar conexão com internet
- Checar firewall/proxy

### Problema: Latitude/Longitude zeradas
**Solução:**
- Nominatim requer nome completo da cidade
- Tentar CEP diferente
- Verificar se estado está em sigla (SP, RJ, etc)

### Problema: Dropdown de categorias vazio
**Solução:**
- Verificar se endpoint `/api/categorias/ativas` retorna dados
- Inserir categorias de teste via POST `/api/categorias`
- Verificar se `marcar.js` foi carregado

### Problema: Erro CORS
**Solução:**
- Frontend e Backend devem estar no mesmo domínio ou CORS habilitado
- Verificar se `@CrossOrigin` está nos Controllers (já adicionado)

## Conclusão

Este guia fornece uma implementação completa e robusta de integração de APIs públicas para consulta de CNPJ, CEP e gestão de categorias. O sistema é modular, permitindo fácil manutenção e adição de novas funcionalidades.

Para suporte adicional ou dúvidas, consulte a documentação das APIs:
- [ViaCEP](https://viacep.com.br/)
- [Nominatim](https://nominatim.org/)
- [Serenata](https://serenata.ai/)
- [ReceitaWS](https://www.receitaws.com.br/)
