import funcionalidad.OperacionesDynamoDB;
import funcionalidad.OperacionesSql;
import modelos.Historial;
import modelos.Reproduccion;
import modelos.Usuario;

import java.nio.file.Path;
import java.sql.Connection;
import java.util.List;

import static funcionalidad.OperacionesJson.leerHistorialDeJson;
import static funcionalidad.OperacionesJson.leerUsuarioDeJson;

public class App {
    public static void main(String[] args) {
        System.out.println("=== Aplicación StreamIt iniciada ===");
        System.out.println();

        // 1. Lectura de ficheros JSON con Jackson
        final Path JSON_HISTORIAL_PATH = Path.of(".", "src", "main", "resources", "historial.json");
        final Path JSON_USUARIOS_PATH = Path.of(".", "src", "main", "resources", "usuarios.json");

        List<Historial> historiales = leerHistorialDeJson(JSON_HISTORIAL_PATH);
        List<Usuario> usuarios = leerUsuarioDeJson(JSON_USUARIOS_PATH);

        System.out.println("--- Usuarios leídos del JSON ---");
        for (Usuario usuario : usuarios) {
            System.out.println(usuario);
        }
        System.out.println();

        System.out.println("--- Historiales leídos del JSON ---");
        for (Historial historial : historiales) {
            System.out.println(historial);
        }
        System.out.println();

        // 2. Cargar propiedades de conexión PostgreSQL
        OperacionesSql.loadDatabaseProperties();

        try (Connection conn = OperacionesSql.getConnection()) {

            // 3. Almacenar usuarios y reproducciones en PostgreSQL
            System.out.println("=== Almacenando datos en PostgreSQL ===");
            OperacionesSql.almacenarUsuariosYHistorial(conn, usuarios, historiales);
            System.out.println();

            // 4. Almacenar historial en DynamoDB
            System.out.println("=== Almacenando historial en DynamoDB ===");
            OperacionesDynamoDB.almacenarHistorial(usuarios, historiales, conn);
            System.out.println();

            // 5. Consultas PostgreSQL
            System.out.println("=== Consultas PostgreSQL ===");
            String emailConsulta = "paulinaencinas@gmail.com";
            List<String> contenidos = OperacionesSql.obtenerContenidosPorUsuario(emailConsulta);
            System.out.println("Contenidos reproducidos por " + emailConsulta + ": " + contenidos);

            String contenidoConsulta = "Breaking Bad";
            List<String> emailsContenido = OperacionesSql.obtenerUsuariosPorContenido(contenidoConsulta);
            System.out.println("Usuarios que reprodujeron '" + contenidoConsulta + "': " + emailsContenido);
            System.out.println();

            // 6. Consultas DynamoDB
            System.out.println("=== Consultas DynamoDB ===");
            List<Reproduccion> historial = OperacionesDynamoDB.obtenerHistorialPorUsuario(emailConsulta, conn);
            System.out.println("Historial DynamoDB de " + emailConsulta + ":");
            for (Reproduccion rep : historial) {
                System.out.println("  - " + rep.getContenido() + " (" + rep.getFecha() + ")");
            }

            int totalReproducciones = OperacionesDynamoDB.contarReproduccionesPorContenido(contenidoConsulta);
            System.out.println("Total reproducciones de '" + contenidoConsulta + "': " + totalReproducciones);
            System.out.println();

            // 7. Funcionalidad adicional: borrar usuario
            System.out.println("=== Borrar usuario por email ===");
            String emailBorrar = "juannieto@gmail.com";
            // Primero borrar de DynamoDB (necesita el id_usuario de PostgreSQL)
            OperacionesDynamoDB.borrarUsuarioPorEmail(emailBorrar, conn);
            // Después borrar de PostgreSQL (respetando FK)
            OperacionesSql.borrarUsuarioPorEmail(conn, emailBorrar);
            System.out.println("Usuario " + emailBorrar + " borrado de ambas bases de datos.");

        } catch (Exception e) {
            System.err.println("Error en la aplicación: " + e.getMessage());
        }

        System.out.println();
        System.out.println("=== Aplicación finalizada ===");
    }
}
