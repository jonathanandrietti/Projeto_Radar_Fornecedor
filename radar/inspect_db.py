#!/usr/bin/env python3
# -*- coding: utf-8 -*-

import sqlite3
import os
import sys
from pathlib import Path

DB_PATH = r"C:\Visão_Futura\Projeto_Radar_Fornecedor\radar\DBLRadar.db"

def main():
    print("\n" + "="*70)
    print("INSPECAO COMPLETA DO BANCO DBLRadar.db")
    print("="*70)
    
    if not os.path.exists(DB_PATH):
        print(f"[ERRO] Banco nao encontrado: {DB_PATH}")
        return False
    
    print(f"[OK] Banco encontrado: {DB_PATH}")
    
    try:
        conn = sqlite3.connect(DB_PATH)
        cursor = conn.cursor()
        
        print_all_tables(cursor)
        print_schema_migrations(cursor)
        print_record_counts(cursor)
        print_table_schemas(cursor)
        print_sample_data(cursor)
        
        conn.close()
        print("\n" + "="*70)
        print("INSPECAO CONCLUIDA COM SUCESSO")
        print("="*70 + "\n")
        return True
        
    except Exception as e:
        print(f"[ERRO] {e}")
        import traceback
        traceback.print_exc()
        return False

def print_all_tables(cursor):
    print("\n" + "-"*70)
    print("1. TODAS AS TABELAS DO BANCO")
    print("-"*70)
    
    cursor.execute("SELECT name, type, sql FROM sqlite_master WHERE type='table' ORDER BY name")
    rows = cursor.fetchall()
    
    if not rows:
        print("  [AVISO] Nenhuma tabela encontrada!")
    else:
        for i, (name, type_, sql) in enumerate(rows, 1):
            print(f"  {i}. {name}")
            if sql:
                sql_preview = sql.replace('\n', ' ')[:80]
                print(f"     DDL: {sql_preview}")

def print_schema_migrations(cursor):
    print("\n" + "-"*70)
    print("2. VERIFICANDO TABELA 'schema_migrations'")
    print("-"*70)
    
    try:
        cursor.execute("SELECT name FROM sqlite_master WHERE type='table' AND name='schema_migrations'")
        if not cursor.fetchone():
            print("[AVISO] Tabela 'schema_migrations' NAO EXISTE")
            print("  Migracoes nao foram registradas!")
            return
        
        print("[OK] Tabela 'schema_migrations' EXISTE")
        
        try:
            cursor.execute("SELECT * FROM schema_migrations ORDER BY version")
            rows = cursor.fetchall()
            
            if not rows:
                print("  (tabela vazia)")
            else:
                print("\n  Versoes executadas:")
                cursor.execute("PRAGMA table_info(schema_migrations)")
                columns = [row[1] for row in cursor.fetchall()]
                print(f"  Colunas: {columns}")
                
                for row in rows:
                    print(f"    {row}")
        except Exception as e:
            print(f"  [ERRO ao ler schema_migrations] {e}")
    
    except Exception as e:
        print(f"[ERRO] {e}")

def print_record_counts(cursor):
    print("\n" + "-"*70)
    print("3. CONTAGEM DE REGISTROS NAS TABELAS PRINCIPAIS")
    print("-"*70)
    
    tables = ["Categorias", "Atividades", "fornecedores", "compradores", "representantes"]
    
    for table in tables:
        try:
            cursor.execute(f"SELECT COUNT(*) FROM {table}")
            count = cursor.fetchone()[0]
            status = "[VAZIO]" if count == 0 else "[OK]"
            print(f"  {table:<20}: {count:>6} registros {status}")
        except sqlite3.OperationalError as e:
            print(f"  {table:<20}: [ERRO] {e}")
        except Exception as e:
            print(f"  {table:<20}: [ERRO] {e}")

def print_table_schemas(cursor):
    print("\n" + "-"*70)
    print("4. SCHEMA (COLUNAS) DE CADA TABELA PRINCIPAL")
    print("-"*70)
    
    tables = ["Categorias", "Atividades", "fornecedores", "compradores", "representantes"]
    
    for table in tables:
        print(f"\n  --- TABELA: {table} ---")
        try:
            cursor.execute(f"PRAGMA table_info({table})")
            rows = cursor.fetchall()
            
            if not rows:
                print("    (tabela nao encontrada)")
            else:
                print("    | cid | name                 | type         | notnull | dflt | pk |")
                print("    |----|----------------------|--------------|---------|------|-----|")
                
                for cid, name, type_, notnull, dflt_value, pk in rows:
                    dflt_str = dflt_value if dflt_value else "null"
                    nn_str = "YES" if notnull else "NO"
                    print(f"    | {cid:3} | {name:<20} | {type_:<12} | {nn_str:>7} | {dflt_str:<4} | {pk:>3} |")
        except Exception as e:
            print(f"    [ERRO] {e}")

def print_sample_data(cursor):
    print("\n" + "-"*70)
    print("5. PRIMEIROS 3 REGISTROS DE CADA TABELA")
    print("-"*70)
    
    tables = ["Categorias", "Atividades", "fornecedores", "compradores", "representantes"]
    
    for table in tables:
        print(f"\n  --- TABELA: {table} ---")
        try:
            cursor.execute(f"SELECT * FROM {table} LIMIT 3")
            rows = cursor.fetchall()
            
            if not rows:
                print("    (nenhum registro)")
            else:
                # Get column names
                cursor.execute(f"PRAGMA table_info({table})")
                columns = [row[1] for row in cursor.fetchall()]
                
                # Print header
                print("    " + " | ".join(f"{col:<15}" for col in columns))
                
                # Print rows
                for row in rows:
                    print("    " + " | ".join(f"{str(val)[:15]:<15}" for val in row))
        except Exception as e:
            print(f"    [ERRO] {e}")

if __name__ == "__main__":
    success = main()
    sys.exit(0 if success else 1)
