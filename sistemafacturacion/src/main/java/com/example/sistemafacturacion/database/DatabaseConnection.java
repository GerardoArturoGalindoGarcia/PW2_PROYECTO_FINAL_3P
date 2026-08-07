package com.example.sistemafacturacion.database;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {
    private static DatabaseConnection instance;
    private Connection connection;
    private static final String DB_NAME = "sistemafacturacion.db";
    private static String DB_URL;

    static {
        try {
            // Cargar el driver SQLite
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.err.println("ERROR: No se encontró el driver SQLite");
            e.printStackTrace();
        }
    }

    private DatabaseConnection() {
        inicializarConexion();
    }

    private void inicializarConexion() {
        try {
            // Construir la ruta a la base de datos
            String dbPath = construirRutaBaseDatos();
            DB_URL = "jdbc:sqlite:" + dbPath;
            System.out.println("Conectando a: " + DB_URL);
            
            connection = DriverManager.getConnection(DB_URL);
            System.out.println("Conexión establecida exitosamente");
            
            inicializarBaseDatos();
        } catch (SQLException e) {
            System.err.println("ERROR al conectar a la base de datos: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String construirRutaBaseDatos() {
        // Intentar varias ubicaciones
        String[] ubicaciones = {
            DB_NAME, // Directorio actual
            "sistemafacturacion/" + DB_NAME, // Subcarpeta sistemafacturacion
            "./target/sistemafacturacion/" + DB_NAME, // Maven target
            System.getProperty("user.dir") + File.separator + DB_NAME,
            System.getProperty("user.dir") + File.separator + "sistemafacturacion" + File.separator + DB_NAME
        };
        
        for (String ubicacion : ubicaciones) {
            File file = new File(ubicacion);
            if (file.exists()) {
                System.out.println("Base de datos encontrada en: " + file.getAbsolutePath());
                return file.getAbsolutePath();
            }
        }
        
        // Si no existe en ningún lado, usar la primera ubicación (se creará)
        System.out.println("Base de datos no encontrada, se creará en: " + System.getProperty("user.dir"));
        return System.getProperty("user.dir") + File.separator + DB_NAME;
    }

    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(DB_URL);
                System.out.println("Reconectado a la base de datos");
            }
        } catch (SQLException e) {
            System.err.println("ERROR al obtener conexión: " + e.getMessage());
            e.printStackTrace();
        }
        return connection;
    }

    private void inicializarBaseDatos() {
        try (Statement stmt = connection.createStatement()) {
            // Tabla Roles
            stmt.execute("CREATE TABLE IF NOT EXISTS Roles (" +
                    "idRol INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "nombre TEXT UNIQUE NOT NULL," +
                    "descripcion TEXT)");

            // Tabla Usuarios
            stmt.execute("CREATE TABLE IF NOT EXISTS Usuarios (" +
                    "idUsuario INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "nombreUsuario TEXT UNIQUE NOT NULL," +
                    "contrasena TEXT NOT NULL," +
                    "nombre TEXT NOT NULL," +
                    "apellido TEXT NOT NULL," +
                    "email TEXT NOT NULL," +
                    "idRol INTEGER NOT NULL," +
                    "estado TEXT DEFAULT 'activo'," +
                    "FOREIGN KEY(idRol) REFERENCES Roles(idRol))");

            // Tabla Clientes
            stmt.execute("CREATE TABLE IF NOT EXISTS Clientes (" +
                    "idCliente INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "nombre TEXT NOT NULL," +
                    "rtn TEXT UNIQUE NOT NULL," +
                    "email TEXT," +
                    "telefono TEXT," +
                    "direccion TEXT," +
                    "ciudad TEXT," +
                    "estado TEXT DEFAULT 'activo')");

            // Tabla Productos
            stmt.execute("CREATE TABLE IF NOT EXISTS Productos (" +
                    "idProducto INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "nombre TEXT NOT NULL," +
                    "descripcion TEXT," +
                    "precioVenta REAL NOT NULL," +
                    "stock INTEGER DEFAULT 0," +
                    "estado TEXT DEFAULT 'activo')");

            // Tabla CAI
            stmt.execute("CREATE TABLE IF NOT EXISTS CAI (" +
                    "idCAI INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "cai TEXT UNIQUE NOT NULL," +
                    "rtn TEXT NOT NULL," +
                    "rangoInicial INTEGER NOT NULL," +
                    "rangoFinal INTEGER NOT NULL," +
                    "siguienteFactura INTEGER NOT NULL," +
                    "fechaEmision TEXT NOT NULL," +
                    "fechaVencimiento TEXT NOT NULL," +
                    "estado TEXT DEFAULT 'activo')");

            // Tabla Facturas
            stmt.execute("CREATE TABLE IF NOT EXISTS Facturas (" +
                    "idFactura INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "numeroFactura INTEGER NOT NULL," +
                    "cai TEXT NOT NULL," +
                    "idCliente INTEGER NOT NULL," +
                    "fechaFactura TEXT NOT NULL," +
                    "subtotal REAL NOT NULL," +
                    "descuento REAL DEFAULT 0," +
                    "impuesto REAL NOT NULL," +
                    "total REAL NOT NULL," +
                    "estado TEXT DEFAULT 'activo'," +
                    "FOREIGN KEY(idCliente) REFERENCES Clientes(idCliente)," +
                    "FOREIGN KEY(cai) REFERENCES CAI(cai))");

            // Tabla DetalleFacturas
            stmt.execute("CREATE TABLE IF NOT EXISTS DetalleFacturas (" +
                    "idDetalle INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "idFactura INTEGER NOT NULL," +
                    "idProducto INTEGER NOT NULL," +
                    "cantidad INTEGER NOT NULL," +
                    "precioUnitario REAL NOT NULL," +
                    "subtotal REAL NOT NULL," +
                    "FOREIGN KEY(idFactura) REFERENCES Facturas(idFactura)," +
                    "FOREIGN KEY(idProducto) REFERENCES Productos(idProducto))");

            // Tabla HistorialTransacciones
            stmt.execute("CREATE TABLE IF NOT EXISTS HistorialTransacciones (" +
                    "idTransaccion INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "idFactura INTEGER," +
                    "idUsuario INTEGER NOT NULL," +
                    "fechaTransaccion TEXT NOT NULL," +
                    "tipoTransaccion TEXT NOT NULL," +
                    "descripcion TEXT," +
                    "FOREIGN KEY(idFactura) REFERENCES Facturas(idFactura)," +
                    "FOREIGN KEY(idUsuario) REFERENCES Usuarios(idUsuario))");

            System.out.println("Base de datos inicializada correctamente");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
