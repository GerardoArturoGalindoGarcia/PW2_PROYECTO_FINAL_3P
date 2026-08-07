import java.sql.*;

public class TestDatabaseConnection {
    private static final String DB_URL = "jdbc:sqlite:sistemafacturacion.db";

    public static void main(String[] args) {
        System.out.println("====== PRUEBA DE CONEXION A BASE DE DATOS ======");
        System.out.println();

        try {
            // Cargar driver
            Class.forName("org.sqlite.JDBC");
            System.out.println("[OK] Driver SQLite cargado correctamente");

            // Establecer conexión
            Connection connection = DriverManager.getConnection(DB_URL);
            System.out.println("[OK] Conexion establecida a: " + DB_URL);
            System.out.println();

            // Verificar tablas
            System.out.println("====== VERIFICACION DE TABLAS ======");
            System.out.println();
            verificarTablas(connection);

            // Prueba de autenticación
            System.out.println();
            System.out.println("====== PRUEBA DE AUTENTICACION ======");
            System.out.println();
            pruebaAutenticacion(connection);

            connection.close();
            System.out.println();
            System.out.println("====== PRUEBAS COMPLETADAS EXITOSAMENTE ======");

        } catch (ClassNotFoundException e) {
            System.out.println("[ERROR] Driver SQLite no encontrado");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("[ERROR] Error de conexion: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void verificarTablas(Connection conn) throws SQLException {
        String[] tablas = {"Roles", "Usuarios", "Clientes", "Productos", "CAI", "Facturas", "DetalleFacturas", "HistorialTransacciones"};

        for (String tabla : tablas) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as total FROM " + tabla);
            if (rs.next()) {
                int count = rs.getInt("total");
                System.out.println("[OK] Tabla '" + tabla + "' existe - Registros: " + count);
            }
            rs.close();
            stmt.close();
        }
    }

    private static void pruebaAutenticacion(Connection conn) throws SQLException {
        // Datos de prueba
        String[][] usuariossPrueba = {
                {"admin", "admin123"},
                {"usuario", "usuario123"},
                {"admin", "contrasenaIncorrecta"},
                {"usuarioNoExiste", "cualquierContrasena"}
        };

        for (String[] credenciales : usuariossPrueba) {
            String nombreUsuario = credenciales[0];
            String contrasena = credenciales[1];

            String query = "SELECT idUsuario, nombreUsuario, nombre, apellido, email, estado FROM Usuarios " +
                    "WHERE nombreUsuario = ? AND contrasena = ?";

            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, nombreUsuario);
            pstmt.setString(2, contrasena);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                System.out.println("[OK] Autenticacion EXITOSA");
                System.out.println("  Usuario: " + rs.getString("nombreUsuario"));
                System.out.println("  Nombre: " + rs.getString("nombre") + " " + rs.getString("apellido"));
                System.out.println("  Email: " + rs.getString("email"));
                System.out.println("  Estado: " + rs.getString("estado"));
                System.out.println();
            } else {
                System.out.println("[FALLO] Autenticacion fallida para usuario: " + nombreUsuario);
                System.out.println();
            }

            rs.close();
            pstmt.close();
        }
    }
}
