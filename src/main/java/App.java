import com.ctc.wstx.exc.WstxOutputException;
import modelos.Historial;
import modelos.Usuario;

import java.nio.file.Path;
import java.util.List;

import static funcionalidad.OperacionesJson.leerHistorialDeJson;
import static funcionalidad.OperacionesJson.leerUsuarioDeJson;

public class App {
    public static void main(String[] args) {
        System.out.println("Aplicación iniciada");

        // Lectura de ficheros
        // Lee y procesa los dos archivos de entrada con la biblioteca Jackson mediante el uso de clases y anotaciones para convertir los datos leídos en objetos Java.

        final Path JSON_READ_Historial_PATH = Path.of(".", "src", "main", "resources", "historial.json");
        final Path JSON_READ_Usuarios_PATH = Path.of(".", "src", "main", "resources", "usuarios.json");
        List<Historial> historiales = leerHistorialDeJson(JSON_READ_Historial_PATH);
        List<Usuario> usuarios = leerUsuarioDeJson(JSON_READ_Usuarios_PATH);

        System.out.println("Usuarios leídos:");
        for (Usuario usuario : usuarios) {
            System.out.println(usuario);
        }

        System.out.println("Historiales leídos:");
        for (Historial historial : historiales) {
            System.out.println(historial);
        }


        //PostgreSQL
        //Almacena la información leída de usuarios y sus reproducciones asociadas.
        //RdsAdmin.vaciarTabla();


        //DynamoDB
    }
}
