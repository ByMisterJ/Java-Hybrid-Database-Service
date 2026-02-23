package funcionalidad;

import modelos.Historial;
import modelos.Reproduccion;
import modelos.Usuario;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class OperacionesSql {

    private static String URL;
    private static String USER;
    private static String PASSWORD;

    // Cargar las propiedades de conexión desde rds.properties
    public static void loadDatabaseProperties() {
        Properties properties = new Properties();
        try (InputStream input = OperacionesSql.class.getClassLoader().getResourceAsStream("rds.properties")) {
            properties.load(input);
            URL = properties.getProperty("db.url");
            USER = properties.getProperty("db.user");
            PASSWORD = properties.getProperty("db.password");

            if (URL == null || USER == null || PASSWORD == null) {
                throw new RuntimeException("Error: propiedades de conexión no válidas.");
            }
        } catch (IOException ex) {
            System.err.println("Error al cargar el archivo de propiedades: " + ex.getMessage());
        }
    }

    // Insertar un usuario en la tabla usuarios y devolver el id_usuario generado
    public static int insertarUsuario(Connection conn, Usuario usuario) {
        int idGenerado = -1;
        String sql = "INSERT INTO usuarios (nombre_completo, email) VALUES (?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, usuario.getNombre());
            pstmt.setString(2, usuario.getEmail());
            pstmt.executeUpdate();

            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                idGenerado = rs.getInt(1);
            }
            rs.close();
            System.out.println("Usuario insertado con ID: " + idGenerado + " - " + usuario.getNombre());
        } catch (SQLException e) {
            System.err.println("Error al insertar el usuario: " + e.getMessage());
        }
        return idGenerado;
    }

    // Insertar las reproducciones asociadas a un id_usuario
    public static void insertarReproducciones(Connection conn, int idUsuario, List<Reproduccion> reproducciones) {
        String sql = "INSERT INTO contenidos_reproducidos (id_usuario, contenido, fecha) VALUES (?, ?, ?::TIMESTAMP)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (Reproduccion rep : reproducciones) {
                pstmt.setInt(1, idUsuario);
                pstmt.setString(2, rep.getContenido());
                pstmt.setString(3, rep.getFecha());
                pstmt.addBatch();
            }
            pstmt.executeBatch();
            System.out.println("  Reproducciones insertadas para usuario ID: " + idUsuario);
        } catch (SQLException e) {
            System.err.println("Error al insertar reproducciones: " + e.getMessage());
        }
    }

    // Almacenar usuarios y sus reproducciones en PostgreSQL
    public static void almacenarUsuariosYHistorial(Connection conn, List<Usuario> usuarios, List<Historial> historiales) {
        for (Usuario usuario : usuarios) {
            int idUsuario = insertarUsuario(conn, usuario);
            if (idUsuario > 0) {
                // Buscar el historial correspondiente a este usuario (por email)
                for (Historial historial : historiales) {
                    if (historial.getUsuario().equals(usuario.getEmail())) {
                        insertarReproducciones(conn, idUsuario, historial.getReproducciones());
                        break;
                    }
                }
            }
        }
    }

    // Obtener los contenidos reproducidos para un usuario específico
    public static List<String> obtenerContenidosPorUsuario(String email) {
        List<String> contenidos = new ArrayList<>();
        if (URL == null) loadDatabaseProperties();

        String query = "SELECT cr.contenido FROM contenidos_reproducidos cr " +
                "JOIN usuarios u ON cr.id_usuario = u.id_usuario " +
                "WHERE u.email = ?";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    contenidos.add(rs.getString("contenido"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener contenidos para el usuario " + email + ": " + e.getMessage());
        }
        return contenidos;
    }

    // Listar los emails de usuarios que han reproducido un contenido determinado
    public static List<String> obtenerUsuariosPorContenido(String contenido) {
        List<String> emails = new ArrayList<>();
        if (URL == null) loadDatabaseProperties();

        String query = "SELECT DISTINCT u.email FROM usuarios u " +
                "JOIN contenidos_reproducidos cr ON u.id_usuario = cr.id_usuario " +
                "WHERE cr.contenido = ?";

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

    // Borrar un usuario y sus reproducciones por email en PostgreSQL
    public static void borrarUsuarioPorEmail(Connection conn, String email) {
        // Primero borrar las reproducciones (por la FK)
        String deleteReproducciones = "DELETE FROM contenidos_reproducidos WHERE id_usuario = " +
                "(SELECT id_usuario FROM usuarios WHERE email = ?)";
        String deleteUsuario = "DELETE FROM usuarios WHERE email = ?";

        try {
            // Borrar reproducciones
            try (PreparedStatement ps = conn.prepareStatement(deleteReproducciones)) {
                ps.setString(1, email);
                int filas = ps.executeUpdate();
                System.out.println("Reproducciones borradas: " + filas + " para email: " + email);
            }
            // Borrar usuario
            try (PreparedStatement ps = conn.prepareStatement(deleteUsuario)) {
                ps.setString(1, email);
                int filas = ps.executeUpdate();
                System.out.println("Usuarios borrados: " + filas + " para email: " + email);
            }
        } catch (SQLException e) {
            System.err.println("Error al borrar usuario por email " + email + ": " + e.getMessage());
        }
    }

    // Obtener el id_usuario a partir de un email
    public static int obtenerIdUsuarioPorEmail(Connection conn, String email) {
        String query = "SELECT id_usuario FROM usuarios WHERE email = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id_usuario");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener id_usuario para email " + email + ": " + e.getMessage());
        }
        return -1;
    }

    // Obtener una conexión (útil para reutilizar desde App.java)
    public static Connection getConnection() throws SQLException {
        if (URL == null) loadDatabaseProperties();
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}