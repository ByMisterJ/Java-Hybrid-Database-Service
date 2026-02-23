package funcionalidad;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

public class OperacionesSql {

    private static String URL;
    private static String USER;
    private static String PASSWORD;

    public static void main(String[] args) {
        // Cargar las propiedades de conexión
        loadDatabaseProperties();

        // Conectar a la base de datos
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            // Mostrar usuarios y cuentas
            System.out.println("Usuarios y cuentas iniciales:");
            almacenarUsuariosYHistorial(conn);
            System.out.println("Operación completada.");
            System.out.println();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static void almacenarUsuariosYHistorial(Connection conn) {
        String query = "SELECT u.id_usuario, u.nombre, u.email" +
                "FROM usuarios u " +
                "LEFT JOIN contenidos_reproducidos cr ON u.id_usuario = cr.id_usuario " +
                "GROUP BY u.id_usuario, u.nombre, u.email " +
                "ORDER BY u.id_usuario";


        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query);) {

            while (rs.next()) {
                int idUsuario = rs.getInt("id_usuario");
                String nombreCompleto = rs.getString("nombre");
                String email = rs.getString("email");

                System.out.printf("ID Usuario: %d, Nombre: %s, Email: %s, Total Reproducciones: %d%n",
                        idUsuario, nombreCompleto, email);
            }

        } catch (SQLException e) {
            System.err.println("Error al ejecutar la consulta o al preparar la inserción: " + e.getMessage());
        }
    }

    public static java.util.List<String> obtenerContenidosPorUsuario(String email) {
        java.util.List<String> contenidos = new java.util.ArrayList<>();
        if (URL == null || USER == null || PASSWORD == null) {
            loadDatabaseProperties();
        }

        String query = "SELECT cr.id_contenido FROM contenidos_reproducidos cr " +
                "JOIN usuarios u ON cr.id_usuario = u.id_usuario " +
                "WHERE u.email = ?";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    contenidos.add(rs.getString(1));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener contenidos para el usuario " + email + ": " + e.getMessage());
        }

        return contenidos;
    }

    public static java.util.List<String> obtenerUsuariosPorContenido(String contenido) {
        java.util.List<String> emails = new java.util.ArrayList<>();
        if (URL == null || USER == null || PASSWORD == null) {
            loadDatabaseProperties();
        }

        String query = "SELECT DISTINCT u.email FROM usuarios u " +
                "JOIN contenidos_reproducidos cr ON u.id_usuario = cr.id_usuario " +
                "WHERE cr.id_contenido = ?";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, contenido);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    emails.add(rs.getString("email"));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al listar usuarios para el contenido " + contenido + ": " + e.getMessage());
        }

        return emails;
    }

    private static void loadDatabaseProperties() {
        Properties properties = new Properties();
        try (InputStream input = OperacionesSql.class.getClassLoader().getResourceAsStream("db.properties")) {
            properties.load(input);
            URL = properties.getProperty("db.url");
            USER = properties.getProperty("db.user");
            PASSWORD = properties.getProperty("db.password");

            if (URL == null || USER == null || PASSWORD == null) {
                throw new SQLException("Error: propiedades de conexión no válidas.");
            }
        } catch (IOException ex) {
            System.err.println("Error al cargar el archivo de propiedades: " + ex.getMessage());
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
    }


}
