---
inclusion: auto
fileMatchPattern: "**/*.java|**/*.properties|**/*.sql"
---

# Regras Críticas de Banco de Dados

## Regra 01 — Proteção do Banco Existente
**Se o banco de dados existe, NUNCA cria novo.**
- Sempre verificar se `DBLRadar.db` existe antes de qualquer operação
- Se existir, apenas ADICIONAR colunas/tabelas (ddl-auto=update)
- Nunca deletar, mover ou sobrescrever o arquivo

## Regra 02 — Validação de Caminho do Banco
**Se o banco não existe, avisar em VERMELHO e paralizar.**
- Exibir: `❌ ERRO CRÍTICO: Banco não encontrado em <CAMINHO_ESPERADO>`
- Questionar: "Este é o caminho correto? (Sim/Não)"
- Aguardar resposta explícita do usuário
- **NÃO prosseguir** até confirmação

## Regra 03 — Proteção de Tabelas Existentes
**Se a tabela existe, NUNCA remove.**
- Verificar `PRAGMA table_info()` antes de DROP TABLE
- Se existir, apenas adicionar colunas faltantes
- Nunca usar DROP TABLE ou TRUNCATE

## Regra 04 — Proteção de Colunas Existentes
**Se a coluna existe, NUNCA remove.**
- Verificar `PRAGMA table_info()` antes de ALTER TABLE
- Se existir, preservar dados
- Nunca usar ALTER TABLE ... DROP COLUMN

## Regra 05 — Alertas de Remoção em Vermelho
**Toda remoção deve ter alerta visual destacado.**
- Exibir em **VERMELHO** antes de executar:
  ```
  ⚠️  ALERTA CRÍTICO ⚠️
  SERÁ REMOVIDO: [especificar exatamente o quê]
  POTENCIAL PERDA DE DADOS: [sim/não]
  ```
- Descrição clara do impacto

## Regra 06 — Remoção de Tabelas com Backup
**Se precisar deletar uma tabela, SEMPRE criar backup antes.**
1. ❌ Exibir alerta em VERMELHO:
   ```
   ⚠️  ALERTA: Tabela 'X' será DELETADA
   Dados que serão perdidos: [listar colunas]
   ```
2. ⏸️ Paralyse e aguarde confirmação explícita
3. ✅ Crie backup: `CREATE TABLE [TABLENAME]_backup_[TIMESTAMP] AS SELECT * FROM [TABLENAME]`
4. 🗑️ Proceda com DROP TABLE
5. 📝 Registre o backup no log

## Regra 07 — Backup Antes de Alterações no Banco
**Sempre que for alterar o banco de dados, crie uma cópia antes.**
- Comando: `PRAGMA integrity_check;` para validar
- Fazer backup: `COPY DBLRadar.db DBLRadar.db.backup.[TIMESTAMP]`
- Executar alteração
- Validar com `PRAGMA integrity_check;` após

## Regra 08 — Verificação de Regras em Código/Classes
**Toda execução que altere o banco deve verificar as regras.**
- Antes de qualquer migração, validar:
  - [ ] Banco existe?
  - [ ] Tabela/coluna já existe?
  - [ ] Backup foi feito?
  - [ ] Alertas em vermelho foram exibidos?
  - [ ] Confirmação do usuário foi obtida?
- Usar checklist antes de prosseguir

## Regra 09 — Limpeza de Cache
**Sempre limpar o cache ao final das alterações.**
- Após qualquer mudança no banco:
  1. Parar servidor (Ctrl+C ou stop process)
  2. Limpar cache do navegador: `Ctrl+Shift+R` ou DevTools → Limpar cache
  3. Reiniciar servidor: `.\mvnw spring-boot:run`
  4. Testar endpoints: `/api/categorias`, `/api/fornecedores`, etc.

---

## Workflow Padrão de Alteração de Banco

```
1️⃣  Validar banco existe → Regra 02
2️⃣  Criar backup → Regra 07
3️⃣  Verificar tabelas/colunas → Regras 03, 04
4️⃣  Exibir alertas (se remover) → Regras 05, 06
5️⃣  Aguardar confirmação (se crítico) → Regras 02, 06
6️⃣  Executar alteração
7️⃣  Validar integridade
8️⃣  Limpar cache → Regra 09
9️⃣  Testar endpoints
```

---

## Caminho Correto do Banco (Regra 01)

```
C:\Visão_Futura\Projeto_Radar_Fornecedor\radar\DBLRadar.db
```

**Sempre relativo à raiz do módulo `radar/`**
