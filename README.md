# Puntos clave
![DynamoDB](https://img.shields.io/badge/DynamoDB-%234EBB4B.svg?&style=for-the-badge&logo=amazondynamodb&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-%23336791.svg?&style=for-the-badge&logo=postgresql&logoColor=white)

Sistema de gestión de historial de streaming con persistencia políglota. Implementa la ingesta de datos desde JSON (Jackson) hacia una arquitectura híbrida utilizando PostgreSQL (RDS) para datos relacionales de usuarios y DynamoDB para el almacenamiento escalable de historiales de reproducción. Incluye integración con AWS SDK v2, operaciones Batch y arquitectura modular en Java.

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
