package funcionalidad;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import modelos.Historial;
import modelos.Reproduccion;
import modelos.Usuario;

import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

// Lee y procesa los dos archivos de entrada con la biblioteca Jackson mediante el uso de clases y anotaciones para convertir los datos leídos en objetos Java.
public class OperacionesJson {

    private static final Path JSON_READ_Historial_PATH = Path.of(".", "src", "main", "resources", "historial.json");
    private static final Path JSON_READ_Usuarios_PATH = Path.of(".", "src", "main", "resources", "usuarios.json");

    static ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    public static List<Historial> leerHistorialDeJson(Path ruta) {

        try (Reader reader = Files.newBufferedReader(ruta)) {
            return objectMapper.readValue(reader, new TypeReference<>() {});
        } catch (IOException e) {
            System.out.println("Error al leer el archivo: " + e.getMessage());
            return List.of();
        }
    }

    public static List<Usuario> leerUsuarioDeJson(Path ruta) {

        try (Reader reader = Files.newBufferedReader(ruta)) {
            return objectMapper.readValue(reader, new TypeReference<>() {});
        } catch (IOException e) {
            System.out.println("Error al leer el archivo: " + e.getMessage());
            return List.of();
        }
    }

}
