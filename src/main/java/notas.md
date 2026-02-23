# Puntos clave de la solución
##### Si lo he hecho yo apenas GPTeadas sin domir y a martes 24/02/2026 a las 04:32

| Requisito                                | Implementación                                                                        |
|------------------------------------------|---------------------------------------------------------------------------------------|
| **Lectura JSON con Jackson**             | `OperacionesJson` con `ObjectMapper` y anotaciones `@JsonProperty`                   |
| **PostgreSQL: almacenar usuarios**       | `OperacionesSql.insertarUsuario()` con `PreparedStatement` y `RETURN_GENERATED_KEYS` |
| **PostgreSQL: almacenar reproducciones** | `OperacionesSql.insertarReproducciones()` con batch insert                            |
| **PostgreSQL: contenidos por usuario**   | `OperacionesSql.obtenerContenidosPorUsuario(email)`                                  |
| **PostgreSQL: usuarios por contenido**   | `OperacionesSql.obtenerUsuariosPorContenido(contenido)`                               |
| **DynamoDB: almacenar historial**        | `OperacionesDynamoDB.almacenarHistorial()` con Enhanced Client                        |
| **DynamoDB: historial por usuario**      | `OperacionesDynamoDB.obtenerHistorialPorUsuario(email)`                               |
| **DynamoDB: contar reproducciones**      | `OperacionesDynamoDB.contarReproduccionesPorContenido(contenido)`                     |
| **Borrar usuario en ambas BBDD**         | `borrarUsuarioPorEmail()` en ambas clases, respetando FK                              |
| **Try-with-resources**                   | Usado en todas las conexiones y statements                                            |
| **Código modular**                       | Separado en paquetes: `modelos`, `funcionalidad`, `admin`                             |