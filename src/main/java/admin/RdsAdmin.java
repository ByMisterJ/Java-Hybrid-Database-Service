package admin;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Properties;

public class RdsAdmin {

    private static void vaciarTabla() {
        Properties properties = new Properties();
        try (InputStream input = RdsAdmin.class.getClassLoader().getResourceAsStream("rds.properties")) {
            properties.load(input);
            String url = properties.getProperty("db.url");
            String user = properties.getProperty("db.user");
            String password = properties.getProperty("db.password");

            try (Connection connection = DriverManager.getConnection(url, user, password);
                 Statement statement = connection.createStatement()) {
                String sql = "TRUNCATE TABLE contenidos_reproducidos, usuarios CASCADE";
                statement.executeUpdate(sql);
                System.out.println("Tablas truncadas correctamente");
            }
        } catch (IOException e) {
            System.err.println("Error al cargar el archivo de propiedades: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error al truncar las tablas: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        vaciarTabla();
    }
}

