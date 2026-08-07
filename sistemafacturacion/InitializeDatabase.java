import java.sql.*;

public class InitializeDatabase {
    private static final String DB_URL = "jdbc:sqlite:sistemafacturacion.db";

    public static void main(String[] args) {
        System.out.println("Inicializando base de datos SQLite...");
        try {
            Class.forName("org.sqlite.JDBC");
            Connection connection = DriverManager.getConnection(DB_URL);
            System.out.println("✓ Conexión establecida a: " + DB_URL);

            initializeDatabase(connection);
            connection.close();
            System.out.println("✓ Base de datos creada exitosamente");
            System.out.println("✓ Ubicación: sistemafacturacion.db");
        } catch (ClassNotFoundException e) {
            System.out.println("✗ Error: Driver SQLite no encontrado");
            System.out.println("Asegúrate de tener org.sqlite.sqlite-jdbc en el classpath");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("✗ Error SQL: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void initializeDatabase(Connection connection) throws SQLException {
        Statement stmt = connection.createStatement();

        // Tabla Roles
        stmt.execute("CREATE TABLE IF NOT EXISTS Roles (" +
                "idRol INTEGER PRIMARY KEY AUTOINCREMENT," +
                "nombre TEXT UNIQUE NOT NULL," +
                "descripcion TEXT)");
        System.out.println("✓ Tabla 'Roles' creada");

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
        System.out.println("✓ Tabla 'Usuarios' creada");

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
        System.out.println("✓ Tabla 'Clientes' creada");

        // Tabla Productos
        stmt.execute("CREATE TABLE IF NOT EXISTS Productos (" +
                "idProducto INTEGER PRIMARY KEY AUTOINCREMENT," +
                "nombre TEXT NOT NULL," +
                "descripcion TEXT," +
                "precioVenta REAL NOT NULL," +
                "stock INTEGER DEFAULT 0," +
                "estado TEXT DEFAULT 'activo')");
        System.out.println("✓ Tabla 'Productos' creada");

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
        System.out.println("✓ Tabla 'CAI' creada");

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
        System.out.println("✓ Tabla 'Facturas' creada");

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
        System.out.println("✓ Tabla 'DetalleFacturas' creada");

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
        System.out.println("✓ Tabla 'HistorialTransacciones' creada");

        stmt.close();
    }
}
