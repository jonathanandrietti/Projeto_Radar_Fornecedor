-- ========================================
-- INSPEÇÃO COMPLETA DO BANCO DBLRadar.db
-- ========================================

-- 1. LISTAR TODAS AS TABELAS
.mode column
.header on
.width 50

PRAGMA database_list;

.print "========================================="
.print "1. TODAS AS TABELAS DO BANCO"
.print "========================================="

SELECT name, type, sql FROM sqlite_master 
WHERE type='table' 
ORDER BY name;

.print "========================================="
.print "2. VERIFICA TABELA schema_migrations"
.print "========================================="

SELECT name FROM sqlite_master WHERE type='table' AND name='schema_migrations';

-- Se existe, mostrar conteúdo
SELECT 
  CASE WHEN (SELECT COUNT(*) FROM sqlite_master WHERE type='table' AND name='schema_migrations') > 0
    THEN 'schema_migrations EXISTE'
    ELSE 'schema_migrations NÃO EXISTE'
  END as status;

-- Se existe, listar conteúdo
.print ""
.print "Conteúdo de schema_migrations (se existir):"
.print ""

SELECT * FROM schema_migrations;

.print "========================================="
.print "3. CONTAGEM DE REGISTROS NAS TABELAS PRINCIPAIS"
.print "========================================="

.mode column
.header on

SELECT 
  'Categorias' as tabela,
  (SELECT COUNT(*) FROM Categorias) as quantidade
UNION ALL
SELECT 
  'Atividades' as tabela,
  (SELECT COUNT(*) FROM Atividades) as quantidade
UNION ALL
SELECT 
  'fornecedores' as tabela,
  (SELECT COUNT(*) FROM fornecedores) as quantidade
UNION ALL
SELECT 
  'compradores' as tabela,
  (SELECT COUNT(*) FROM compradores) as quantidade
UNION ALL
SELECT 
  'representantes' as tabela,
  (SELECT COUNT(*) FROM representantes) as quantidade;

.print "========================================="
.print "4. SCHEMA (COLUNAS) DE CADA TABELA PRINCIPAL"
.print "========================================="

.print ""
.print "--- TABELA: Categorias ---"
PRAGMA table_info(Categorias);

.print ""
.print "--- TABELA: Atividades ---"
PRAGMA table_info(Atividades);

.print ""
.print "--- TABELA: fornecedores ---"
PRAGMA table_info(fornecedores);

.print ""
.print "--- TABELA: compradores ---"
PRAGMA table_info(compradores);

.print ""
.print "--- TABELA: representantes ---"
PRAGMA table_info(representantes);

.print ""
.print "========================================="
.print "5. PRIMEIROS 5 REGISTROS DE CADA TABELA"
.print "========================================="

.print ""
.print "--- Categorias ---"
SELECT * FROM Categorias LIMIT 5;

.print ""
.print "--- Atividades ---"
SELECT * FROM Atividades LIMIT 5;

.print ""
.print "--- fornecedores ---"
SELECT * FROM fornecedores LIMIT 5;

.print ""
.print "--- compradores ---"
SELECT * FROM compradores LIMIT 5;

.print ""
.print "--- representantes ---"
SELECT * FROM representantes LIMIT 5;
