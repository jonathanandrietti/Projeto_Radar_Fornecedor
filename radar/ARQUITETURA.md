# Arquitetura da Solução - API Consulta CNPJ/CEP

## 🏗️ Visão Geral da Arquitetura

```
┌──────────────────────────────────────────────────────────────────────┐
│                         CAMADA APRESENTAÇÃO                           │
│  Browser (HTML + CSS + JavaScript - Tailwind + Font Awesome)         │
│                                                                       │
│  ┌─────────────────┐  ┌─────────────────┐  ┌──────────────────┐    │
│  │ fornecedores.   │  │ compradores.    │  │ representantes.  │    │
│  │      html       │  │      html       │  │       html       │    │
│  └────────┬────────┘  └────────┬────────┘  └────────┬─────────┘    │
│           │                    │                    │               │
│           └────────────────────┼────────────────────┘               │
│                                │                                    │
│         ┌───────────────────────▼────────────────────────┐          │
│         │   preenchimento-automatico.js (600 linhas)     │          │
│         │   - consultarCnpj(cnpj)                        │          │
│         │   - consultarCep(cep)                          │          │
│         │   - carregarCategorias()                       │          │
│         │   - carregarAtividades()                       │          │
│         │   - mostrarSucesso/Erro/Carregamento()         │          │
│         └───────────────────────┬────────────────────────┘          │
│                                 │                                    │
└─────────────────────────────────┼────────────────────────────────────┘
                                  │ HTTP/REST
                                  │ JSON
                                  │
                    ┌─────────────▼──────────────┐
                    │  Spring Boot 4.1.0         │
                    │  Port: 8080                │
                    └─────────────┬──────────────┘
                                  │
┌─────────────────────────────────┴────────────────────────────────────┐
│                    CAMADA DE CONTROLADORES REST                       │
│                                                                       │
│  ┌────────────────────────────┐  ┌──────────────────────────────┐   │
│  │ ConsultaCnpjController     │  │ GeolocalizacaoCepController  │   │
│  │ - GET  /consulta-cnpj/{..} │  │ - GET  /cep/{cep}           │   │
│  │ - GET  /consulta-cnpj/val..│  │ - GET  /cep/localizacao     │   │
│  │ - POST /consulta-cnpj/bu...│  │ - POST /cep/buscar          │   │
│  └────────────────────────────┘  └──────────────────────────────┘   │
│                                                                       │
│  ┌──────────────────────────┐  ┌─────────────────────────────────┐  │
│  │PreenchimentoAutomaticoCtrl│  │ CategoriaController + Atividade│  │
│  │ - GET  /cep/{cep}        │  │ - GET    /categorias           │  │
│  │ - GET  /cnpj/{cnpj}      │  │ - GET    /atividades           │  │
│  │ - POST /                 │  │ - POST   /categorias           │  │
│  └──────────────────────────┘  │ - DELETE /atividades/{id}      │  │
│                                │ - PATCH  /desativar            │  │
│                                └─────────────────────────────────┘  │
│                                                                       │
└───────────────────────────────┬───────────────────────────────────────┘
                                │
┌───────────────────────────────▼───────────────────────────────────────┐
│                    CAMADA DE SERVIÇOS (Business Logic)                │
│                                                                       │
│  ┌──────────────────────────┐  ┌──────────────────────────┐          │
│  │ConsultaCnpjService       │  │GeolocalizacaoCepService  │          │
│  ├──────────────────────────┤  ├──────────────────────────┤          │
│  │+ consultarCnpj(cnpj)     │  │+ consultarCep(cep)       │          │
│  │+ validarFormatoCnpj()    │  │+ obterLocalizacao()      │          │
│  │+ transformarResposta()   │  │+ validarFormatoCep()     │          │
│  │                          │  │+ extrairEndereco()       │          │
│  │Integração:               │  │                          │          │
│  │• Serenata API (Public)   │  │Integração:               │          │
│  │• ReceitaWS Fallback      │  │• ViaCEP (Public)         │          │
│  │                          │  │• Nominatim (Public)      │          │
│  └──────────────────────────┘  └──────────────────────────┘          │
│                                                                       │
│  ┌──────────────────────────┐  ┌──────────────────────────┐          │
│  │PreenchimentoAutomaticoSrv│  │CategoriaService          │          │
│  ├──────────────────────────┤  ├──────────────────────────┤          │
│  │+ preencherPorCnpj()      │  │+ cadastrar()             │          │
│  │+ preencherPorCep()       │  │+ listarAtivas()          │          │
│  │+ converterMapParaDto()   │  │+ buscarPorNome()         │          │
│  │                          │  │+ atualizar()             │          │
│  │Orquestra:                │  │+ desativar()             │          │
│  │• CNPJ + CEP             │  │                          │          │
│  │• Consolidação           │  │AtividadeService (id)     │          │
│  └──────────────────────────┘  └──────────────────────────┘          │
│                                                                       │
│  ┌──────────────────────────┐  ┌──────────────────────────┐          │
│  │CompradorService (upd)    │  │FornecedorService (upd)   │          │
│  ├──────────────────────────┤  ├──────────────────────────┤          │
│  │+ cadastrar()             │  │+ cadastrar()             │          │
│  │+ atualizar(categoria_id)│  │+ atualizar(categoria_id)│  │
│  │+ atualizar(atividade_id)│  │+ atualizar(atividade_id)│  │
│  │+ excluir()               │  │+ excluir()               │          │
│  │                          │  │                          │          │
│  │RepresentanteService (upd)  │                          │          │
│  └──────────────────────────┘  └──────────────────────────┘          │
│                                                                       │
└───────────────────────────────┬───────────────────────────────────────┘
                                │
┌───────────────────────────────▼───────────────────────────────────────┐
│              CAMADA DE PERSISTÊNCIA (Data Layer)                      │
│                                                                       │
│  ┌────────────────────────────────────────────────────────────────┐  │
│  │             JPA Repository Interfaces                         │  │
│  │                                                                │  │
│  │ ┌──────────────────┐  ┌──────────────────┐                   │  │
│  │ │CategoriaRepository  │  │AtividadeRepository  │                │  │
│  │ ├──────────────────┤  ├──────────────────┤                   │  │
│  │ │findByNome()      │  │findByCnae()      │                   │  │
│  │ │findByAtivaTrue() │  │findByDescricao() │                   │  │
│  │ │save/update/delete│  │findBySecao()     │                   │  │
│  │ └──────────────────┘  └──────────────────┘                   │  │
│  │                                                                │  │
│  │ ┌──────────────────┐  ┌──────────────────┐                   │  │
│  │ │CompradorRepo (upd)  │  │FornecedorRepo (upd)  │                │  │
│  │ ├──────────────────┤  ├──────────────────┤                   │  │
│  │ │findByCnpj()      │  │findByCnpj()      │                   │  │
│  │ │save/update/delete│  │save/update/delete│                   │  │
│  │ └──────────────────┘  └──────────────────┘                   │  │
│  │                                                                │  │
│  │ ┌────────────────────────────────────────────┐                │  │
│  │ │RepresentanteRepository (upd)              │                │  │
│  │ ├────────────────────────────────────────────┤                │  │
│  │ │findByCodEmpresaAndCnpjFornecedor()        │                │  │
│  │ │save/update/delete                        │                │  │
│  │ └────────────────────────────────────────────┘                │  │
│  │                                                                │  │
│  └────────────────────────────────────────────────────────────────┘  │
│                                │                                    │
└────────────────────────────────┼────────────────────────────────────┘
                                 │ SQL/JDBC
                                 │
                    ┌────────────▼──────────┐
                    │   SQLite Database     │
                    │   DBLRadar.db         │
                    └──────────────────────┘
                          │
        ┌─────────────────┼─────────────────┐
        │                 │                 │
    ┌───▼────┐     ┌──────▼─────┐    ┌──────▼──────┐
    │Categorias    │Atividades   │    │Fornecedores│
    │─────────│    │──────────────│    │─────────────│
    │id (PK)  │    │id (PK)      │    │id (PK)     │
    │nome (U) │    │cnae (U)     │    │cnpj (U)    │
    │descr    │    │descricao (U)│    │categoria_id│
    │icone    │    │secao        │    │atividade_id│
    │ativa    │    │divisao      │    │nome        │
    │...      │    │ativa        │    │cep         │
    └────┬────┘    └──────┬──────┘    │lat/lon     │
         │                │            │...         │
    ┌────▼────┬──────────┬┴───────┬───▼────┬───────────────┐
    │Compradores│Representantes│           │
    │─────────────────────────────           │
    │id (PK)    │id (PK)    │               │
    │cnpj (U)   │cnpj       │               │
    │categoria_id│categoria_id│               │
    │atividade_id│atividade_id│               │
    │nome        │cnpjFornecedor│             │
    │...         │...        │               │
    └────────────┴───────────┘               │
                                             └──────────────┘
```

---

## 📊 Fluxo de Dados: Consulta CNPJ

```
┌─────────────────────────────────────────────────────────────────────┐
│ 1. Usuário digita CNPJ no formulário                               │
│    CNPJ: 11222333000181                                            │
└────────────────────────┬────────────────────────────────────────────┘
                         │
                         ▼
┌─────────────────────────────────────────────────────────────────────┐
│ 2. Event Listener (onblur) dispara consultarCnpj()                 │
│    JavaScript: preenchimento-automatico.js                         │
└────────────────────────┬────────────────────────────────────────────┘
                         │
                         ▼
┌─────────────────────────────────────────────────────────────────────┐
│ 3. GET /api/preenchimento-automatico/cnpj/11222333000181           │
│    HTTP Request para Backend                                        │
└────────────────────────┬────────────────────────────────────────────┘
                         │
                         ▼
┌─────────────────────────────────────────────────────────────────────┐
│ 4. PreenchimentoAutomaticoController recebe requisição             │
│    Route: GET /preenchimento-automatico/cnpj/{cnpj}               │
└────────────────────────┬────────────────────────────────────────────┘
                         │
                         ▼
┌─────────────────────────────────────────────────────────────────────┐
│ 5. PreenchimentoAutomaticoService.preencherPorCnpj(cnpj)          │
│    Orquestra a chamada de ConsultaCnpjService                      │
└────────────────────────┬────────────────────────────────────────────┘
                         │
                         ▼
┌─────────────────────────────────────────────────────────────────────┐
│ 6. ConsultaCnpjService.consultarCnpj(cnpj)                        │
│    Valida formato, remove máscara                                   │
└────────────────────────┬────────────────────────────────────────────┘
                         │
        ┌────────────────┼────────────────┐
        │                │                │
        ▼                ▼                ▼
    ┌────────────┐  ┌────────────┐  ┌──────────────┐
    │ Tenta API  │  │ Se Falhar  │  │ Se Falhar 2x │
    │ Serenata   │  │ ReceitaWS  │  │ Retorna erro │
    │            │  │            │  │              │
    └────┬───────┘  └────┬───────┘  └──────────────┘
         │                │
         ▼                ▼
    ┌────────────────────────────────────┐
    │ Resposta JSON com dados da empresa │
    │ {                                  │
    │   nome: "Serenata",                │
    │   razaoSocial: "...",              │
    │   cep: "01234567",                 │
    │   ...                              │
    │ }                                  │
    └────┬───────────────────────────────┘
         │
         ▼
    ┌──────────────────────────────────────┐
    │ Se tem CEP, consulta GeoService      │
    │ + Latitude/Longitude via Nominatim   │
    └────┬───────────────────────────────────┘
         │
         ▼
    ┌──────────────────────────────────┐
    │ Retorna PreenchimentoAutomaticoDto
    │ {                                │
    │   sucesso: true,                 │
    │   dadosCnpj: {...},              │
    │   dadosCep: {...},               │
    │   mensagem: "Sucesso"            │
    │ }                                │
    └────┬──────────────────────────────┘
         │ JSON Response
         ▼
    ┌──────────────────────────────────────┐
    │ Browser recebe resposta               │
    │ preencherCamposComCnpj() preenche    │
    │ - nome                               │
    │ - cep                                │
    │ - logradouro, bairro, cidade, etc   │
    │ - latitude, longitude                │
    │                                      │
    │ mostrarSucesso() exibe alerta        │
    └──────────────────────────────────────┘
```

---

## 🔄 Fluxo de Dados: Consulta CEP

```
Usuário digita CEP
         │
         ▼
consultarCep() JavaScript
         │
         ▼
GET /api/cep/{cep}
         │
         ▼
GeolocalizacaoCepController
         │
         ▼
GeolocalizacaoCepService
         │
    ┌────┴─────┐
    │           │
    ▼           ▼
ViaCEP    Nominatim
(Endereço) (Lat/Long)
    │           │
    └────┬──────┘
         │
         ▼
ConsultaCepDto
{
  cep: "01310-100",
  logradouro: "Avenida Paulista",
  latitude: -23.561414,
  longitude: -46.656139
}
         │
         ▼
Browser preenche campos
- logradouro
- bairro
- latitude
- longitude
```

---

## 🗄️ Modelo de Dados

```
Categorias (1 ← → N) Fornecedores
┌──────────────┐         ┌──────────────┐
│ id (PK)      │◄────────│ categoria_id │
│ nome         │         │ nome         │
│ descricao    │         │ cnpj         │
│ icone        │         │ status       │
│ ativa        │         │ ...          │
│ criadaEm     │         └──────────────┘
│ atualizadaEm │
└──────────────┘

Atividades (1 ← → N) Fornecedores
┌──────────────┐         ┌──────────────┐
│ id (PK)      │◄────────│ atividade_id │
│ cnae         │         │ nome         │
│ descricao    │         │ cnpj         │
│ secao        │         │ status       │
│ divisao      │         │ ...          │
│ ativa        │         └──────────────┘
│ criadaEm     │
│ atualizadaEm │
└──────────────┘

(Mesmo para Compradores e Representantes)
```

---

## 🚀 Fluxo de Startup

```
1. Spring Boot Initialization
   ↓
2. DatabaseConfig.java executa
   ↓
3. Tabelas criadas via Hibernate
   - Categorias
   - Atividades
   - Colunas FK em Fornecedor/Comprador/Representante
   ↓
4. Controllers registrados
   - ConsultaCnpjController
   - GeolocalizacaoCepController
   - PreenchimentoAutomaticoController
   - CategoriaController
   - AtividadeController
   ↓
5. Services inicializados
   - Todos os @Service beans
   ↓
6. Static resources loaded
   - HTML, CSS, JS
   ↓
7. Servidor disponível em http://localhost:8080
   ↓
8. Frontend carrega preenchimento-automatico.js
   ↓
9. inicializarPreenchimentoAutomatico() executa
   - integrarConsultaCnpj()
   - integrarConsultaCep()
   - carregarCategorias()
   - carregarAtividades()
```

---

## 📡 Integração com APIs Externas

```
┌────────────────────────────────────────┐
│         Radar Fornecedor               │
└────────────┬─────────────────────┬─────┘
             │                     │
             │ 14 dígitos          │ 8 dígitos
             │                     │
    ┌────────▼──────────┐  ┌──────▼──────────────┐
    │  Serenata API     │  │  ViaCEP API         │
    │  (CNPJ)           │  │  (CEP)              │
    │                   │  │                     │
    │ https://api.      │  │ https://viacep.     │
    │ serenata.ai/...   │  │ com.br/ws/.../json  │
    │                   │  │                     │
    │ Fallback:         │  │ Seguido por:        │
    │ ReceitaWS         │  │ Nominatim (Maps)    │
    │                   │  │                     │
    │ Retorna:          │  │ Retorna:            │
    │ • Nome            │  │ • Endereço          │
    │ • Atividade       │  │ • Latitude          │
    │ • CEP             │  │ • Longitude         │
    │ • Status          │  │ • DDD               │
    │ • Endereço        │  │                     │
    │                   │  │                     │
    └───────────────────┘  └─────────────────────┘
```

---

## 💾 Ciclo de Vida de um Registro

```
┌─────────────────────────────────────┐
│ 1. Formulário vazio                  │
└────────────────┬────────────────────┘
                 │
                 ▼
┌─────────────────────────────────────┐
│ 2. Usuário digita CNPJ              │
│    API Serenata preenche campos     │
│    API ViaCEP + Nominatim preenche  │
│    Dropdowns carregam Categorias    │
└────────────────┬────────────────────┘
                 │
                 ▼
┌─────────────────────────────────────┐
│ 3. Usuário seleciona:               │
│    • Categoria (FK)                 │
│    • Atividade (FK)                 │
└────────────────┬────────────────────┘
                 │
                 ▼
┌─────────────────────────────────────┐
│ 4. Usuário clica "SALVAR REGISTRO"  │
│    Validações client-side           │
│    POST /api/fornecedores/registrar │
└────────────────┬────────────────────┘
                 │
                 ▼
┌─────────────────────────────────────┐
│ 5. Backend valida:                  │
│    • CNPJ único                     │
│    • Categoria existe               │
│    • Atividade existe               │
└────────────────┬────────────────────┘
                 │
                 ▼
┌─────────────────────────────────────┐
│ 6. Registro inserido no SQLite      │
│    • id gerado                      │
│    • categoria_id preenchido        │
│    • atividade_id preenchido        │
│    • latitude/longitude salvos      │
└────────────────┬────────────────────┘
                 │
                 ▼
┌─────────────────────────────────────┐
│ 7. HTTP 201 Created retornado       │
│    Alerta de sucesso exibido        │
│    Tabela atualizada                │
│    Modal fechado                    │
└─────────────────────────────────────┘
```

---

## 📋 Matriz de Relacionamentos

| Entidade | Categoria | Atividade | Status |
|----------|-----------|-----------|--------|
| Fornecedor | ManyToOne | ManyToOne | ✅ Implementado |
| Comprador | ManyToOne | ManyToOne | ✅ Implementado |
| Representante | ManyToOne | ManyToOne | ✅ Implementado |

---

## 🔐 Fluxo de Autenticação e Autorização

```
Public APIs (Sem autenticação):
├── GET  /api/consulta-cnpj/*
├── GET  /api/cep/*
└── GET  /api/preenchimento-automatico/*

Authenticated APIs (Com HttpSession):
├── GET    /api/categorias
├── GET    /api/atividades
├── POST   /api/fornecedores
├── PUT    /api/fornecedores/{id}
└── DELETE /api/fornecedores/{id}

Controle de Acesso:
├── ADMIN       → Acesso total
├── FORNECEDOR  → Apenas sua empresa
├── REPRESENTANTE → Apenas representados
└── CLIENTE     → Apenas públicas
```

---

**Documentação**: Versão 1.0  
**Última Atualização**: Setembro 2026  
**Status**: ✅ Completa
