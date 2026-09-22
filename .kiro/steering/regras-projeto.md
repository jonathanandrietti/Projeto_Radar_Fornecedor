# Regras do Projeto Radar Fornecedor

Estas regras devem ser seguidas **antes de qualquer alteração** no projeto.
Nenhuma exceção é permitida sem aprovação explícita do usuário.

---

## Regra 01 — Banco de Dados (DBLRadar.db)

O arquivo de banco de dados SQLite segue **sempre o mesmo padrão de caminho relativo**, independente do computador ou pasta raiz utilizada:

```
<qualquer_caminho_raiz>\Projeto_Radar_Fornecedor\radar\DBLRadar.db
```

### Exemplos válidos em máquinas diferentes:
```
C:\Visão_Futura\Projeto_Radar_Fornecedor\radar\DBLRadar.db   ← este workspace
C:\Projeto_Radar_Fornecedor\radar\DBLRadar.db
D:\Dev\Projeto_Radar_Fornecedor\radar\DBLRadar.db
```

### Como identificar o caminho correto em qualquer máquina:
O caminho do banco é **sempre relativo à raiz do módulo `radar/`**, ou seja:
- Localize a pasta que contém `pom.xml` e `src/` — essa é a raiz do módulo `radar/`
- O banco está **na mesma pasta**, com o nome `DBLRadar.db`
- O padrão invariável é: `...\Projeto_Radar_Fornecedor\radar\DBLRadar.db`

### O que NÃO pode ser feito:
- Mover o arquivo `DBLRadar.db` para fora da pasta `radar/`
- Renomear o arquivo
- Criar um banco de dados em outro local
- Alterar o caminho no `application.properties` para qualquer valor que não seja o relativo padrão

### O que DEVE ser mantido no `application.properties`:
```properties
spring.datasource.url=jdbc:sqlite:DBLRadar.db
```
Esse caminho **relativo** funciona em qualquer máquina, pois o Spring Boot resolve a partir da pasta de trabalho do processo, que é sempre a raiz do módulo `radar/`.

### Ao criar ou alterar tabelas (DDL):
- Toda criação, alteração ou migração de tabela deve ser aplicada sobre o `DBLRadar.db` localizado na raiz do módulo `radar/`
- Confirmar com o usuário antes de executar qualquer DDL destrutivo (`DROP`, `TRUNCATE`, alteração de tipo de coluna)
- Nunca criar um banco de dados novo no lugar do existente

---

## Regra 02 — Estrutura de Arquivos Estáticos

Os arquivos front-end (HTML, CSS, JS) residem em:

```
C:\Visão_Futura\Projeto_Radar_Fornecedor\radar\src\main\resources\static\
```

Após qualquer edição nesses arquivos, **copiar também** para o diretório espelho de runtime:

```
C:\Visão_Futura\Projeto_Radar_Fornecedor\radar\target\classes\static\
```

Isso garante que o servidor em execução sirva a versão atualizada sem precisar recompilar.

---

## Regra 03 — Padrão de Páginas HTML

Toda página HTML dentro de `pages/` deve seguir este padrão:

- Incluir `data-standalone="true"` no `<body>` quando a página tiver script inline próprio que gerencie o ciclo de vida (DOMContentLoaded)
- Usar o placeholder `<aside id="menu-lateral" class="w-64 hidden md:block"></aside>` para o menu lateral
- Chamar `carregarMenuLateral()`, `exibirInfoUsuario()` e `aplicarControlesDeAcesso()` no DOMContentLoaded do script inline
- Usar `md:ml-64` no container principal para compensar a largura do menu lateral fixo

---

## Regra 04 — Servidor Spring Boot

O servidor roda na porta `8080`. URL base: `http://localhost:8080`

- Não alterar a porta sem aprovação do usuário
- O arquivo de inicialização é: `C:\Visão_Futura\Projeto_Radar_Fornecedor\Iniciar SpringBoot Radar Fornecedor.bat`
- Após alterações em código Java (`.java`), o servidor precisa ser reiniciado para refletir as mudanças
- Alterações apenas em arquivos estáticos (HTML, CSS, JS) só precisam do **copy para `/target`** e um hard refresh no browser (`Ctrl+Shift+R`)

---

## Regra 05 — Identidade Visual

- Cor dourada do projeto: `#d4af37` (usado em ícones e destaques do menu lateral)
- Todos os ícones de navegação no `menu-lateral.html` devem usar `text-[#d4af37]`
- Não substituir a paleta de cores por outras sem aprovação do usuário

---

## Regra 06 — Banco de Dados: NUNCA Deletar, Sempre Preservar Dados

### CRÍTICO — Política de Dados em Produção

**PROIBIDO:**
- ❌ `spring.jpa.hibernate.ddl-auto=create-drop` (deleta banco a cada execução)
- ❌ `spring.jpa.hibernate.ddl-auto=create` (deleta dados ao reiniciar)
- ❌ Criar novo banco de dados no lugar do existente
- ❌ `DROP TABLE`, `TRUNCATE TABLE` sem backup prévio

**OBRIGATÓRIO:**
- ✅ `spring.jpa.hibernate.ddl-auto=update` em **TODAS** as máquinas (produção, staging, dev)
- ✅ Usar `ALTER TABLE` para modificações de schema — Hibernate sincroniza automaticamente
- ✅ Se precisar excluir uma coluna/tabela destrutiva: fazer BACKUP → copiar dados → recriar estrutura → restaurar dados
- ✅ Confirmar com o usuário antes de executar qualquer operação destrutiva

### Por que?
Milhares de dados produtivos não podem ser perdidos por erro de programação. Isso paralisa a operação até restauração.

---

## Regra 07 — Cache de Arquivos Estáticos e Spring Boot

Após editar arquivos estáticos (`.html`, `.css`, `.js`):

1. **Copiar para `/target/classes/static/`** (regra 02)
2. **Hard refresh no navegador:** `Ctrl+Shift+R` (Chrome/Edge) ou `Cmd+Shift+R` (Mac)
3. **Spring Boot cache:** Desabilitar globalmente em `application.properties`:
   ```properties
   spring.web.resources.cache.period=0
   spring.web.resources.cache.cachecontrol.no-cache=true
   spring.web.resources.cache.cachecontrol.no-store=true
   spring.web.resources.cache.cachecontrol.must-revalidate=true
   ```

Se mudanças ainda não aparecem após copy + hard refresh:
- Fechar abas abertas do projeto no navegador
- Fechar todo o navegador (não apenas a aba)
- Reabrir `http://localhost:8080`

---

## Regra 08 — Fluxo de Edição: Qual Arquivo Editar?

| Edição | Arquivo Principal | Copiar Para | Servidor | Refresh |
|--------|-------------------|------------|----------|---------|
| **Java** (`.java`) | `src/main/java/` | — | **REINICIAR** | Normal |
| **HTML/CSS/JS** | `src/main/resources/static/` | `target/classes/static/` | Sem reiniciar | **Hard** `Ctrl+Shift+R` |
| **application.properties** | `src/main/resources/` | — | **REINICIAR** | — |
| **Entidades JPA** | `src/main/java/model/` | — | **REINICIAR** | — |

---

## Regra 09 — Sistema de Cadastro com Aprovação Admin

### Fluxo Garantido:
1. Cliente clica **CADASTRO** em `login.html` → abre `pages/cadastro.html`
2. Preenche CNPJ/CPF, dados contato, usuário, senha
3. Envia POST `/api/cadastro/solicitar`
4. Status: `aguardandoAprovacao=true` em banco
5. Admin vê notificação de pendências em dashboard
6. Admin aprova: POST `/api/cadastro/aprovar/{id}` → `aguardandoAprovacao=false`
7. Email enviado ao cliente confirmando aprovação
8. Cliente loga com usuário/senha de pré-cadastro
9. Sistema redireciona para finalizar cadastro (completar dados adicionais)

### Validações:
- CNPJ: algoritmo + busca ReceitaWS
- CPF: algoritmo + preenchimento manual de dados
- Senha: criptografada com BCrypt em banco
- Login: valida senha hasheada com BCryptPasswordEncoder

### Banco:
- Tabela `SolicitacaoCadastro` armazena requisições pendentes
- Tabela `Usuario` com flag `aguardandoAprovacao` (boolean)
- Nenhum dado é deletado — apenas status alterado
