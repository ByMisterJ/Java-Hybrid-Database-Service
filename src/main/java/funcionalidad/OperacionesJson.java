package funcionalidad;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import modelos.Historial;
import modelos.Usuario;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

// Lee y procesa los dos archivos de entrada con la biblioteca Jackson
// mediante el uso de clases y anotaciones para convertir los datos leídos en objetos Java.
public class OperacionesJson {

    private static final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    public static List<Historial> leerHistorialDeJson(Path ruta) {
        //try-with-resources
        try (Reader reader = Files.newBufferedReader(ruta)) {
            return objectMapper.readValue(reader, new TypeReference<>() {});
        } catch (IOException e) {
            System.err.println("Error al leer el archivo de historial: " + e.getMessage());
            return List.of();
        }
    }

    public static List<Usuario> leerUsuarioDeJson(Path ruta) {
        try (Reader reader = Files.newBufferedReader(ruta)) {
            return objectMapper.readValue(reader, new TypeReference<>() {});
        } catch (IOException e) {
            System.err.println("Error al leer el archivo de usuarios: " + e.getMessage());
            return List.of();
        }
    }
}
