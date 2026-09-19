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
