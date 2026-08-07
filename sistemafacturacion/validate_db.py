#!/usr/bin/env python3
# -*- coding: utf-8 -*-
import sqlite3
import sys
import os

def validate_database():
    db_path = "sistemafacturacion.db"
    
    try:
        # Conectar a la base de datos
        conn = sqlite3.connect(db_path)
        cursor = conn.cursor()
        
        print("=" * 60)
        print("VALIDACION DE BASE DE DATOS SQLite - SISTEMA DE FACTURACION")
        print("=" * 60)
        print()
        
        # Obtener lista de tablas
        cursor.execute("SELECT name FROM sqlite_master WHERE type='table' ORDER BY name;")
        tables = cursor.fetchall()
        
        print("[OK] Conectado a: " + db_path)
        print("[OK] Total de tablas: " + str(len(tables)))
        print()
        
        # Información de cada tabla
        print("TABLAS CREADAS:")
        print("-" * 60)
        
        expected_tables = [
            'Roles',
            'Usuarios',
            'Clientes',
            'Productos',
            'CAI',
            'Facturas',
            'DetalleFacturas',
            'HistorialTransacciones'
        ]
        
        for i, table_name in enumerate(tables, 1):
            table = table_name[0]
            print()
            print(str(i) + ". " + table)
            
            # Obtener información de columnas
            cursor.execute("PRAGMA table_info(" + table + ")")
            columns = cursor.fetchall()
            
            print("   Columnas: " + str(len(columns)))
            for col in columns:
                col_id, col_name, col_type, not_null, default, pk = col
                pk_indicator = " [PRIMARY KEY]" if pk else ""
                print("     - " + col_name + " (" + col_type + ")" + pk_indicator)
            
            # Contar registros
            cursor.execute("SELECT COUNT(*) FROM " + table)
            count = cursor.fetchone()[0]
            print("   Registros: " + str(count))
        
        print()
        print("=" * 60)
        print("RESUMEN")
        print("=" * 60)
        
        # Verificar que todas las tablas esperadas existan
        created_tables = [t[0] for t in tables]
        missing_tables = [t for t in expected_tables if t not in created_tables]
        
        if missing_tables:
            print("[ERROR] Tablas faltantes: " + ", ".join(missing_tables))
        else:
            print("[OK] Todas las tablas esperadas han sido creadas")
        
        # Información del archivo
        file_size = os.path.getsize(db_path)
        print("[OK] Tamaño de archivo: " + str(file_size) + " bytes")
        print()
        print("=" * 60)
        print("[OK] VALIDACION COMPLETADA EXITOSAMENTE")
        print("=" * 60)
        
        conn.close()
        return True
        
    except sqlite3.Error as e:
        print("[ERROR] Error de base de datos: " + str(e))
        return False
    except Exception as e:
        print("[ERROR] Error: " + str(e))
        return False

if __name__ == "__main__":
    success = validate_database()
    sys.exit(0 if success else 1)

