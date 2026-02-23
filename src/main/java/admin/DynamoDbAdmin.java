package admin;

import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.AwsSessionCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;
import software.amazon.awssdk.services.dynamodb.waiters.DynamoDbWaiter;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class DynamoDbAdmin {

    private static DynamoDbClient clienteDynamoDb;

    private static DynamoDbClient getCliente() {
        if (clienteDynamoDb == null) {
            Properties properties = new Properties();
            try (InputStream input = DynamoDbAdmin.class.getClassLoader().getResourceAsStream("dynamodb.properties")) {
                properties.load(input);

                String accessKeyId = properties.getProperty("aws_access_key_id");
                String secretAccessKey = properties.getProperty("aws_secret_access_key");
                String sessionToken = properties.getProperty("aws_session_token");

                AwsCredentials awsCredentials = AwsSessionCredentials.create(accessKeyId, secretAccessKey, sessionToken);

                clienteDynamoDb = DynamoDbClient.builder()
                        .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
                        .region(Region.US_EAST_1)
                        .build();
            } catch (IOException ex) {
                System.err.println("Error al cargar el archivo de propiedades: " + ex.getMessage());
            }
        }
        return clienteDynamoDb;
    }

    public static void crearTabla(String nombreTabla) {
        try {
            getCliente().describeTable(r -> r.tableName(nombreTabla));
            System.out.println("La tabla " + nombreTabla + " ya existe");
        }
        catch (ResourceNotFoundException rnfEx) {
            try {
                CreateTableRequest createTableRequest = CreateTableRequest.builder()
                        .tableName(nombreTabla)
                        .keySchema(
                                KeySchemaElement.builder()
                                        .attributeName("idUsuario") // Nombre de la clave de partición
                                        .keyType(KeyType.HASH) // HASH indica clave de partición
                                        .build()
                        )
                        .attributeDefinitions(
                                AttributeDefinition.builder()
                                        .attributeName("idUsuario") // Define el atributo idUsuario
                                        .attributeType(ScalarAttributeType.N) // Tipo de atributo: Número
                                        .build()
                        )
                        .provisionedThroughput(
                                ProvisionedThroughput.builder()
                                        .readCapacityUnits(5L)
                                        .writeCapacityUnits(5L)
                                        .build()
                        )
                        .build();

                getCliente().createTable(createTableRequest);
                System.out.println("Creando tabla " + nombreTabla);
                try (DynamoDbWaiter waiter = getCliente().waiter()) {
                    waiter.waitUntilTableExists(DescribeTableRequest.builder().tableName(nombreTabla).build());
                    System.out.println("Tabla " + nombreTabla + " creada y activa.");
                }
            } catch (Exception e) {
                System.err.println("Error al esperar a que la tabla esté activa: " + e.getMessage());
            }
        }
    }

    public static void borrarTabla(String nombreTabla) {
        try {
            getCliente().deleteTable(r -> r.tableName(nombreTabla));
            System.out.println("Borrando tabla " + nombreTabla);
            try (DynamoDbWaiter waiter = getCliente().waiter()) {
                waiter.waitUntilTableNotExists(DescribeTableRequest.builder().tableName(nombreTabla).build());
                System.out.println("Tabla " + nombreTabla + " borrada.");
            } catch (Exception e) {
                System.err.println("Error al esperar a que la tabla ya no exista: " + e.getMessage());
            }
        }
        catch (ResourceNotFoundException rnfEx) {
            System.out.println("La tabla " + nombreTabla + " no existe");
        }
    }


    public static void main(String[] args) {
        String nombreTabla = "examenjose";
        borrarTabla(nombreTabla);
        crearTabla(nombreTabla);
    }

}

