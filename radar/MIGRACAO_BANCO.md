# Padrão de Migração de Banco de Dados

## IMPORTANTE ⚠️

**NUNCA delete o arquivo `DBLRadar.mv.db` ou o banco em produção!**

Este projeto usa **Flyway** para gerenciar migrações de banco de dados de forma segura.

## Como Funciona

1. **Schema Inicial** (`schema.sql`)
   - Executado APENAS quando o banco é criado do zero
   - Define a estrutura base das tabelas

2. **Migrações Incrementais** (`db/migration/`)
   - Scripts nomeados: `V{numero}__{descricao}.sql`
   - Exemplo: `V1_1__Add_profile_columns_to_solicitacoes.sql`
   - Executadas em ordem numérica, UMA VEZ
   - Preservam dados existentes

## Adicionar Nova Migração

Se precisar alterar o banco em produção:

1. **Criar arquivo** em `src/main/resources/db/migration/`
   ```
   V1_2__Adicionar_novo_campo.sql
   ```

2. **Escrever SQL seguro** (sem DELETE de dados)
   ```sql
   ALTER TABLE minha_tabela ADD COLUMN novo_campo VARCHAR(100);
   ```

3. **Fazer deploy** - Flyway executa automaticamente

## Histórico de Migrações

- **V1_1**: Adiciona campos de perfil (Fornecedor, Comprador, Representante, Cliente)

## Em Produção

- ✅ Flyway controla versões (tabela `flyway_schema_history`)
- ✅ Cada migração é executada uma única vez
- ✅ Rollback automático se erro (Hibernate valida estrutura)
- ❌ NUNCA delete o banco manualmente
- ❌ NUNCA edite migrações já executadas

## Referência

- [Flyway Documentation](https://flywaydb.org/)
- Arquivo de config: `application.properties` (linhas 10-12)
