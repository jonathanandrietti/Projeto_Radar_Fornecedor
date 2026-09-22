# Regras de Negócio - Radar Fornecedor

## Fluxo de Cadastro e Aprovação

### 1. Cliente se Cadastra
- Preenche dados no formulário `/pages/cadastro.html`
- Seleciona tipo de perfil: **Fornecedor**, **Comprador**, **Representante**, **Cliente**
- Sistema cria **SolicitacaoCadastro** com `aprovado = false`
- Usuário ainda **NÃO é criado** neste momento

### 2. Cliente Tenta Login (Antes de Aprovação)
- Sistema busca em **SolicitacaoCadastro** pelo username
- Se encontrou solicitação pendente → Retorna 403: "Sua solicitação aguarda aprovação"
- Cliente **não consegue fazer login**

### 3. Admin Aprova Solicitação
- Admin acessa `/pages/admin.html`
- Clica no botão ✓ verde
- Sistema:
  - Cria **Usuario** com:
    - `aguardandoAprovacao = false`
    - `cadastroCompleto = false` ← **Precisa finalizar cadastro**
    - Perfis copiados da solicitação (fornecedor, comprador, etc)
  - Marca **SolicitacaoCadastro** como `aprovado = true`

### 4. Cliente Faz Login (Após Aprovação)
- Sistema valida credenciais
- Detecta `cadastroCompleto = false`
- **Redireciona para página de finalização:**
  - Se `fornecedor = true` → `/pages/fornecedores.html?finalizar=true`
  - Se `comprador = true` → `/pages/compradores.html?finalizar=true`
  - Se `representante = true` → `/pages/representantes.html?finalizar=true`
  - Se `cliente = true` → `/pages/clientes.html?finalizar=true`

### 5. Cliente Finaliza Cadastro
- Preenche dados complementares (endereço, CNPJ detalhado, etc)
- Clica em **SALVAR**
- Sistema:
  - Salva dados na tabela específica (FORNECEDORES, COMPRADORES, etc)
  - Marca `usuario.cadastroCompleto = true`
- Redireciona para dashboard

### 6. Próximos Logins
- Sistema detecta `cadastroCompleto = true`
- Vai direto para dashboard (não passa mais pela finalização)

---

## Controle de Acesso por Perfil

### FORNECEDOR
- ✅ Pode ver e editar **seu próprio cadastro** (tabela FORNECEDORES)
- ✅ Pode ver **todos os Compradores** (lista completa)
- ✅ Pode ver **todos os Clientes** (se vende para CPF)
- ✅ Pode ver e editar **apenas Representantes vinculados à sua empresa**
- ❌ NÃO pode ver outros Fornecedores
- ❌ NÃO pode acessar área administrativa

### COMPRADOR
- ✅ Pode ver e editar **seu próprio cadastro** (tabela COMPRADORES)
- ✅ Pode ver **todos os Fornecedores**
- ✅ Pode ver **todos os Representantes**
- ❌ NÃO pode ver outros Compradores
- ❌ NÃO pode acessar área administrativa

### REPRESENTANTE
- ✅ Pode ver e editar **seu próprio cadastro** (tabela REPRESENTANTES)
- ✅ Pode ver **Fornecedores que representa**
- ✅ Pode ver **Compradores dos fornecedores que representa**
- ❌ NÃO pode ver outros Representantes
- ❌ NÃO pode acessar área administrativa

### CLIENTE
- ✅ Pode ver e editar **seu próprio cadastro** (tabela CLIENTES)
- ✅ Pode ver **Fornecedores** disponíveis
- ❌ NÃO pode ver outros Clientes
- ❌ NÃO pode acessar área administrativa

### ADMIN
- ✅ Acesso total a todas as áreas
- ✅ Pode gerenciar usuários
- ✅ Pode aprovar/rejeitar solicitações
- ✅ Pode editar qualquer cadastro

---

## Implementação Técnica

### Backend
- **SessionFilter**: Verifica se usuário está logado (exceto /login, /cadastro, /api/login, /api/cadastro/solicitar)
- **UsuarioService**: Valida login e verifica `aguardandoAprovacao`
- **SolicitacaoCadastroService**: Gerencia fluxo de aprovação
- **Controllers**: Filtram dados por perfil (exemplo: FornecedorController só retorna representantes vinculados)

### Frontend
- **login.html**: Redireciona baseado em `cadastroCompleto`
- **script.js**: `aplicarControlesDeAcesso()` esconde/mostra elementos baseado no perfil
- **Páginas de cadastro**: Detectam `?finalizar=true` e mostram formulário de finalização

---

## Próximos Passos

1. ✅ Corrigir `cadastroCompleto = false` na aprovação
2. ⏳ Criar páginas de finalização de cadastro
3. ⏳ Implementar filtros de acesso nos Controllers
4. ⏳ Criar vínculo Representante ↔ Fornecedor
5. ⏳ Testar fluxo completo end-to-end
