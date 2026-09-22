#!/usr/bin/env python3
# -*- coding: utf-8 -*-

import sqlite3

DB_PATH = r"C:\Visão_Futura\Projeto_Radar_Fornecedor\radar\DBLRadar.db"

conn = sqlite3.connect(DB_PATH)
cursor = conn.cursor()

print("\n" + "="*70)
print("DETALHES DA TABELA schema_migrations")
print("="*70)

# Schema da tabela
print("\nSchema da tabela schema_migrations:")
cursor.execute("PRAGMA table_info(schema_migrations)")
columns_info = cursor.fetchall()
for row in columns_info:
    cid, name, type_, notnull, dflt, pk = row
    print(f"  [{cid}] {name:<15} {type_:<15} notnull={notnull} default={dflt} pk={pk}")

# Conteúdo da tabela
print("\nConteúdo da tabela schema_migrations:")
try:
    cursor.execute("SELECT * FROM schema_migrations")
    rows = cursor.fetchall()
    if rows:
        for i, row in enumerate(rows, 1):
            print(f"  Registro {i}: {row}")
    else:
        print("  (tabela vazia)")
except Exception as e:
    print(f"  [ERRO] {e}")

# Contar registros
cursor.execute("SELECT COUNT(*) FROM schema_migrations")
count = cursor.fetchone()[0]
print(f"\nTotal de linhas: {count}")

conn.close()
