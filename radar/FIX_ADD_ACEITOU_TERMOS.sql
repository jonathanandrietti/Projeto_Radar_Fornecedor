-- Script para adicionar coluna ACEITOU_TERMOS na tabela SOLICITACOES_CADASTRO
-- Execute este script no H2 Console: http://localhost:8080/h2-console

-- 1. Adicionar a coluna como NULLABLE primeiro
ALTER TABLE SOLICITACOES_CADASTRO ADD COLUMN IF NOT EXISTS ACEITOU_TERMOS BOOLEAN;

-- 2. Atualizar registros existentes para TRUE (assumir que já aceitaram)
UPDATE SOLICITACOES_CADASTRO SET ACEITOU_TERMOS = TRUE WHERE ACEITOU_TERMOS IS NULL;

-- 3. Tornar a coluna NOT NULL agora que todos têm valor
ALTER TABLE SOLICITACOES_CADASTRO ALTER COLUMN ACEITOU_TERMOS SET NOT NULL;

-- 4. Verificar
SELECT ID, USUARIO, ACEITOU_TERMOS FROM SOLICITACOES_CADASTRO;
