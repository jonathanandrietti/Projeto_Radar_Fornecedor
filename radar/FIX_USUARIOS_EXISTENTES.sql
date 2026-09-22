-- Script para corrigir usuários já aprovados que não têm o campo cadastroCompleto

-- 1. Atualizar usuários existentes (exceto admin) para cadastroCompleto = false
UPDATE USUARIOS 
SET CADASTRO_COMPLETO = false 
WHERE USERNAME != 'admin' AND (CADASTRO_COMPLETO IS NULL OR CADASTRO_COMPLETO = true);

-- 2. Verificar resultado
SELECT ID, USERNAME, CADASTRO_COMPLETO, FORNECEDOR, COMPRADOR, REPRESENTANTE, CLIENTE 
FROM USUARIOS 
WHERE USERNAME != 'admin';

-- Resultado esperado: todos os usuários não-admin devem ter CADASTRO_COMPLETO = false
