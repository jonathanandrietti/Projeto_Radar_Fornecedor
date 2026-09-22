-- Adicionar colunas à tabela Categorias
ALTER TABLE Categorias ADD COLUMN criadaEm TIMESTAMP;
ALTER TABLE Categorias ADD COLUMN atualizadaEm TIMESTAMP;

-- Adicionar colunas à tabela Atividades
ALTER TABLE Atividades ADD COLUMN criadaEm TIMESTAMP;
ALTER TABLE Atividades ADD COLUMN atualizadaEm TIMESTAMP;
