ALTER TABLE Categorias ADD COLUMN criadaEm TIMESTAMP;
ALTER TABLE Categorias ADD COLUMN atualizadaEm TIMESTAMP;
ALTER TABLE Atividades ADD COLUMN criadaEm TIMESTAMP;
ALTER TABLE Atividades ADD COLUMN atualizadaEm TIMESTAMP;
UPDATE Categorias SET criadaEm = datetime('now') WHERE criadaEm IS NULL;
UPDATE Categorias SET atualizadaEm = datetime('now') WHERE atualizadaEm IS NULL;
UPDATE Atividades SET criadaEm = datetime('now') WHERE criadaEm IS NULL;
UPDATE Atividades SET atualizadaEm = datetime('now') WHERE atualizadaEm IS NULL;