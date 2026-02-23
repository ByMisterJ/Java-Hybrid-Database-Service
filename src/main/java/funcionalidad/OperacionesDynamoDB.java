package funcionalidad;

import admin.DynamoDbAdmin;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import modelos.Historial;
import modelos.Reproduccion;
import modelos.Usuario;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;
import software.amazon.awssdk.enhanced.dynamodb.model.ScanEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;
import software.amazon.awssdk.services.dynamodb.waiters.DynamoDbWaiter;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import com.fasterxml.jackson.core.type.TypeReference;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class OperacionesDynamoDB {
//    private final DynamoDbClient dynamoDbClient;
//    private final DynamoDbEnhancedClient dynamoDbEnhancedClient;
//
//    private OperacionesDynamoDB() {
//        this.dynamoDbClient = DynamoDbAdmin.getCliente();
//        this.dynamoDbEnhancedClient = DynamoDBAdmin.getEnhancedClient();
//    }
//
//    public void crearTabla(String nombreTabla) {
//        if (tablaExiste(nombreTabla)) {
//            System.out.println("La tabla ya existe: " + nombreTabla);
//            return;
//        }
//public void crearTabla(String nombreTabla) {
//    if (tablaExiste(nombreTabla)) {
//        System.out.println("La tabla ya existe: " + nombreTabla);
//        return;
//    }
//
//    // Crear la tabla usando la clase de esquema de la entidad Equipo
//    dynamoDbEnhancedClient.table(nombreTabla, TableSchema.fromBean(Equipo.class)).createTable();
//    System.out.println("Creando tabla: " + nombreTabla);
//
//    // Esperar hasta que la tabla esté activa
//    DynamoDbWaiter waiter = dynamoDbClient.waiter();
//    waiter.waitUntilTableExists(DescribeTableRequest.builder().tableName(nombreTabla).build());
//    System.out.println("Tabla '" + nombreTabla + "' creada y activa.");
//}
//    private boolean tablaExiste (String nombreTabla) {
//        try {
//            // Intenta describir la tabla. Si no existe, se lanzará una excepción
//            dynamoDbClient.describeTable(r -> r.tableName(nombreTabla));
//            return true;
//        } catch (ResourceNotFoundException e) {
//            return false;
//        }
//    }
}
