package funcionalidad;

import modelos.Historial;
import modelos.HistorialDynamo;
import modelos.Reproduccion;
import modelos.Usuario;
import software.amazon.awssdk.auth.credentials.AwsSessionCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class OperacionesDynamoDB {

    private static DynamoDbClient dynamoDbClient;
    private static DynamoDbEnhancedClient enhancedClient;
    private static final String NOMBRE_TABLA = "josarapar-historial"; // Cambia por tu usuario GVA sin número

    // Obtener cliente DynamoDB bajo demanda (singleton)
    public static DynamoDbClient getCliente() {
        if (dynamoDbClient == null) {
            Properties properties = new Properties();
            //try-with-resources
            try (InputStream input = OperacionesDynamoDB.class.getClassLoader().getResourceAsStream("dynamodb.properties")) {
                properties.load(input);

                String accessKeyId = properties.getProperty("aws_access_key_id");
                String secretAccessKey = properties.getProperty("aws_secret_access_key");
                String sessionToken = properties.getProperty("aws_session_token");

                dynamoDbClient = DynamoDbClient.builder()
                        .credentialsProvider(StaticCredentialsProvider.create(
                                AwsSessionCredentials.create(accessKeyId, secretAccessKey, sessionToken)))
                        .region(Region.US_EAST_1)
                        .build();
            } catch (IOException ex) {
                System.err.println("Error al cargar el archivo de propiedades de DynamoDB: " + ex.getMessage());
            }
        }
        return dynamoDbClient;
    }

    // Obtener el cliente enhanced para operaciones con beans
    public static DynamoDbEnhancedClient getEnhancedClient() {
        if (enhancedClient == null) {
            enhancedClient = DynamoDbEnhancedClient.builder()
                    .dynamoDbClient(getCliente())
                    .build();
        }
        return enhancedClient;
    }

    // Obtener la referencia a la tabla DynamoDB
    private static DynamoDbTable<HistorialDynamo> getTabla() {
        return getEnhancedClient().table(NOMBRE_TABLA, TableSchema.fromBean(HistorialDynamo.class));
    }

    // Almacenar el historial completo de reproducciones en DynamoDB
    public static void almacenarHistorial(List<Usuario> usuarios, List<Historial> historiales,
                                          java.sql.Connection connSql) {
        DynamoDbTable<HistorialDynamo> tabla = getTabla();

        for (Usuario usuario : usuarios) {
            // Obtener el idUsuario generado por PostgreSQL
            int idUsuario = OperacionesSql.obtenerIdUsuarioPorEmail(connSql, usuario.getEmail());
            if (idUsuario < 0) {
                System.err.println("No se encontró id_usuario para el email: " + usuario.getEmail());
                continue;
            }

            // Buscar el historial de reproducciones correspondiente
            List<Reproduccion> reproducciones = new ArrayList<>();
            for (Historial historial : historiales) {
                if (historial.getUsuario().equals(usuario.getEmail())) {
                    reproducciones = historial.getReproducciones();
                    break;
                }
            }

            // Crear el item para DynamoDB
            HistorialDynamo item = new HistorialDynamo();
            item.setIdUsuario(idUsuario);
            item.setEmail(usuario.getEmail());
            item.setReproducciones(reproducciones);

            tabla.putItem(item);
            System.out.println("Historial almacenado en DynamoDB para usuario ID: " + idUsuario
                    + " - " + usuario.getEmail());
        }
    }

    // Obtener el historial completo de un usuario por email
    public static List<Reproduccion> obtenerHistorialPorUsuario(String email, java.sql.Connection connSql) {
        int idUsuario = OperacionesSql.obtenerIdUsuarioPorEmail(connSql, email);
        if (idUsuario < 0) {
            System.err.println("No se encontró id_usuario para el email: " + email);
            return List.of();
        }

        DynamoDbTable<HistorialDynamo> tabla = getTabla();
        HistorialDynamo item = tabla.getItem(Key.builder().partitionValue(idUsuario).build());
        if (item != null && item.getReproducciones() != null) {
            return item.getReproducciones();
        }
        return List.of();
    }

    // Contar las veces que un contenido específico ha sido reproducido (recorriendo toda la tabla)
    public static int contarReproduccionesPorContenido(String contenido) {
        DynamoDbTable<HistorialDynamo> tabla = getTabla();
        int contador = 0;

        for (HistorialDynamo item : tabla.scan().items()) {
            if (item.getReproducciones() != null) {
                for (Reproduccion rep : item.getReproducciones()) {
                    if (rep.getContenido().equals(contenido)) {
                        contador++;
                    }
                }
            }
        }
        return contador;
    }

    // Borrar un usuario de DynamoDB por email
    public static void borrarUsuarioPorEmail(String email, java.sql.Connection connSql) {
        int idUsuario = OperacionesSql.obtenerIdUsuarioPorEmail(connSql, email);
        if (idUsuario < 0) {
            System.err.println("No se encontró id_usuario para borrar en DynamoDB: " + email);
            return;
        }

        DynamoDbTable<HistorialDynamo> tabla = getTabla();
        tabla.deleteItem(Key.builder().partitionValue(idUsuario).build());
        System.out.println("Historial borrado en DynamoDB para usuario ID: " + idUsuario);
    }
}