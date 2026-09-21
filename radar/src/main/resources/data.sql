-- Dados iniciais para Categorias e Atividades

INSERT OR IGNORE INTO categorias (id, nome, ativa) VALUES (1, 'Eletrônicos', 1);
INSERT OR IGNORE INTO categorias (id, nome, ativa) VALUES (2, 'Alimentos', 1);
INSERT OR IGNORE INTO categorias (id, nome, ativa) VALUES (3, 'Vestuário', 1);

INSERT OR IGNORE INTO atividades (id, nome, ativa) VALUES (1, 'Comércio Varejista', 1);
INSERT OR IGNORE INTO atividades (id, nome, ativa) VALUES (2, 'Comércio Atacadista', 1);
INSERT OR IGNORE INTO atividades (id, nome, ativa) VALUES (3, 'Fabricação', 1);
