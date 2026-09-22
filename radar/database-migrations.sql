-- ========================================
-- MIGRAÇÃO: Consolidar colunas duplicadas e adicionar campos faltantes
-- Data: 2026-09-21
-- Banco: DBLRadar.db (SQLite)
-- ========================================

-- ========================================
-- PASSO 1: CONSOLIDAR FORNECEDORES (remover CodCidade duplicado)
-- ========================================

BEGIN TRANSACTION;

CREATE TABLE fornecedores_new (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    empresa VARCHAR(100) NOT NULL,
    cnpj VARCHAR(255) NOT NULL UNIQUE,
    status VARCHAR(255),
    pontuacao_risco FLOAT,
    logradouro VARCHAR(255),
    numero VARCHAR(255),
    complemento VARCHAR(255),
    bairro VARCHAR(255),
    cidade VARCHAR(255),
    estado VARCHAR(255),
    cep VARCHAR(255),
    latitude DOUBLE,
    longitude DOUBLE,
    cod_cidade BIGINT,
    aceitacpf BOOLEAN,
    prazo_entrega_dias INTEGER,
    email VARCHAR(255),
    telefone VARCHAR(255),
    foto TEXT,
    foto_nome VARCHAR(255),
    atividade_id BIGINT,
    categoria_id BIGINT
);

INSERT INTO fornecedores_new 
SELECT 
    id, empresa, cnpj, status, pontuacao_risco,
    logradouro, numero, complemento, bairro, cidade, estado, cep,
    latitude, longitude,
    COALESCE(cod_cidade, CodCidade) as cod_cidade,
    aceitacpf, prazo_entrega_dias, email, telefone, foto, foto_nome,
    atividade_id, categoria_id
FROM fornecedores;

DROP TABLE fornecedores;
ALTER TABLE fornecedores_new RENAME TO fornecedores;

COMMIT;

-- ========================================
-- PASSO 2: CONSOLIDAR COMPRADORES (remover CodCidade + adicionar campos faltantes)
-- ========================================

BEGIN TRANSACTION;

CREATE TABLE compradores_new (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    empresa VARCHAR(100) NOT NULL,
    cnpj VARCHAR(255) NOT NULL UNIQUE,
    status VARCHAR(255),
    pontuacao_risco FLOAT,
    logradouro VARCHAR(255),
    numero VARCHAR(255),
    complemento VARCHAR(255),
    bairro VARCHAR(255),
    cidade VARCHAR(255),
    estado VARCHAR(255),
    cep VARCHAR(255),
    latitude DOUBLE,
    longitude DOUBLE,
    cod_cidade BIGINT,
    atividade_id BIGINT,
    categoria_id BIGINT,
    email VARCHAR(255),
    telefone VARCHAR(255),
    foto TEXT,
    foto_nome VARCHAR(255)
);

INSERT INTO compradores_new
SELECT
    id, empresa, cnpj, status, pontuacao_risco,
    logradouro, numero, complemento, bairro, cidade, estado, cep,
    latitude, longitude,
    COALESCE(cod_cidade, CodCidade) as cod_cidade,
    atividade_id, categoria_id,
    NULL, NULL, NULL, NULL
FROM compradores;

DROP TABLE compradores;
ALTER TABLE compradores_new RENAME TO compradores;

COMMIT;

-- ========================================
-- PASSO 3: CONSOLIDAR CLIENTES (remover CpfCnpj, TipoPessoa duplicados)
-- ========================================

BEGIN TRANSACTION;

CREATE TABLE clientes_new (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    nome VARCHAR(100) NOT NULL,
    status VARCHAR(255),
    cpf_cnpj VARCHAR(255),
    tipo_pessoa VARCHAR(255),
    logradouro VARCHAR(255),
    numero VARCHAR(255),
    complemento VARCHAR(255),
    bairro VARCHAR(255),
    cidade VARCHAR(255),
    estado VARCHAR(255),
    cep VARCHAR(255),
    latitude DOUBLE,
    longitude DOUBLE,
    email VARCHAR(255),
    telefone VARCHAR(255),
    endereco VARCHAR(255),
    foto TEXT,
    foto_nome VARCHAR(255)
);

INSERT INTO clientes_new
SELECT
    id, nome, status,
    COALESCE(cpf_cnpj, CpfCnpj) as cpf_cnpj,
    COALESCE(tipo_pessoa, TipoPessoa) as tipo_pessoa,
    logradouro, numero, complemento, bairro, cidade, estado, cep,
    latitude, longitude,
    email, telefone, endereco, foto, foto_nome
FROM clientes;

DROP TABLE clientes;
ALTER TABLE clientes_new RENAME TO clientes;

COMMIT;

-- ========================================
-- PASSO 4: CONSOLIDAR REPRESENTANTES (remover CnpjFornecedor, CodEmpresa duplicados)
-- ========================================

BEGIN TRANSACTION;

CREATE TABLE representantes_new (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    nome VARCHAR(100) NOT NULL,
    status VARCHAR(255),
    cnpj VARCHAR(255),
    cnpj_fornecedor VARCHAR(255),
    cod_empresa BIGINT,
    contato VARCHAR(30),
    email VARCHAR(120),
    logradouro VARCHAR(255),
    numero VARCHAR(255),
    complemento VARCHAR(255),
    bairro VARCHAR(255),
    cidade VARCHAR(255),
    estado VARCHAR(255),
    cep VARCHAR(255),
    latitude DOUBLE,
    longitude DOUBLE,
    atividade_id BIGINT,
    categoria_id BIGINT
);

INSERT INTO representantes_new
SELECT
    id, nome, status, cnpj,
    COALESCE(cnpj_fornecedor, CnpjFornecedor) as cnpj_fornecedor,
    COALESCE(cod_empresa, CodEmpresa) as cod_empresa,
    contato, email,
    logradouro, numero, complemento, bairro, cidade, estado, cep,
    latitude, longitude,
    atividade_id, categoria_id
FROM representantes;

DROP TABLE representantes;
ALTER TABLE representantes_new RENAME TO representantes;

COMMIT;

-- ========================================
-- FIM DA MIGRAÇÃO
-- ========================================
-- Todas as colunas duplicadas foram consolidadas
-- Compradores agora tem: email, telefone, foto, foto_nome
-- Dados existentes foram preservados
