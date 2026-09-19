-- MIGRAÇÃO DDL - Adicionar campos novos após merge
-- Execute este script no banco de dados SQLite para sincronizar com os modelos Java atualizados

-- ============================================================================
-- TABELA: Clientes
-- ============================================================================
-- Adicionar campos de contato e foto

ALTER TABLE Clientes ADD COLUMN Email VARCHAR(100);
ALTER TABLE Clientes ADD COLUMN Telefone VARCHAR(20);
ALTER TABLE Clientes ADD COLUMN Endereco VARCHAR(500);
ALTER TABLE Clientes ADD COLUMN Foto BLOB;
ALTER TABLE Clientes ADD COLUMN FotoNome VARCHAR(255);

-- ============================================================================
-- TABELA: Fornecedores  
-- ============================================================================
-- Adicionar campos de contato e logo

ALTER TABLE Fornecedores ADD COLUMN Email VARCHAR(100);
ALTER TABLE Fornecedores ADD COLUMN Telefone VARCHAR(20);
ALTER TABLE Fornecedores ADD COLUMN Foto BLOB;
ALTER TABLE Fornecedores ADD COLUMN FotoNome VARCHAR(255);

-- ============================================================================
-- VERIFICAÇÃO
-- ============================================================================
-- Após executar, verifique com:
-- PRAGMA table_info(Clientes);
-- PRAGMA table_info(Fornecedores);
