# Projeto_Radar_Fornecedor

## Atualizações do banco SQLite

Use `AtualizaBancoSQLite.java` como ponto único para criar, alterar ou remover
tabelas e colunas. Adicione uma nova chamada `aplicarAtualizacao` na área de
edição, sempre com uma versão ainda não utilizada. A atualização será aplicada
uma vez e registrada em `schema_migrations`.

As operações de remoção estão comentadas por segurança: elas podem apagar dados.

