-- Tabelas criadas automaticamente pelo Spring Boot
-- Tabelas de Categorias e Atividades

CREATE TABLE IF NOT EXISTS Categorias (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    nome TEXT NOT NULL UNIQUE,
    descricao TEXT,
    icone TEXT,
    ativa INTEGER NOT NULL DEFAULT 1,
    criadaEm TIMESTAMP NOT NULL,
    atualizadaEm TIMESTAMP
);

CREATE TABLE IF NOT EXISTS Atividades (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    cnae TEXT UNIQUE,
    descricao TEXT NOT NULL UNIQUE,
    secao TEXT,
    divisao TEXT,
    ativa INTEGER NOT NULL DEFAULT 1,
    criadaEm TIMESTAMP NOT NULL,
    atualizadaEm TIMESTAMP
);
