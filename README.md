# Puntos clave del examen
#### Esto lo acabe el 24/02/26 a las 03:44 (Ahora mismo entiendo la mitad)

| Requisito                                | Implementación                                                                        |
|------------------------------------------|---------------------------------------------------------------------------------------|
| **Lectura JSON con Jackson**             | `OperacionesJson` con `ObjectMapper` y anotaciones `@JsonProperty`                    |
| **PostgreSQL: almacenar usuarios**       | `OperacionesSql.insertarUsuario()` con `PreparedStatement` y `RETURN_GENERATED_KEYS`  |
| **PostgreSQL: almacenar reproducciones** | `OperacionesSql.insertarReproducciones()` con batch insert                            |
| **PostgreSQL: contenidos por usuario**   | `OperacionesSql.obtenerContenidosPorUsuario(email)`                                   |
| **PostgreSQL: usuarios por contenido**   | `OperacionesSql.obtenerUsuariosPorContenido(contenido)`                               |
| **DynamoDB: almacenar historial**        | `OperacionesDynamoDB.almacenarHistorial()` con Enhanced Client                        |
| **DynamoDB: historial por usuario**      | `OperacionesDynamoDB.obtenerHistorialPorUsuario(email)`                               |
| **DynamoDB: contar reproducciones**      | `OperacionesDynamoDB.contarReproduccionesPorContenido(contenido)`                     |
| **Borrar usuario en ambas BBDD**         | `borrarUsuarioPorEmail()` en ambas clases                                             |
| **Try-with-resources**                   | Usado en todas las conexiones (igual muchos try)                                      |
| **Código modular**                       | Separado en paquetes: `modelos`, `funcionalidad`, `admin`                             |
