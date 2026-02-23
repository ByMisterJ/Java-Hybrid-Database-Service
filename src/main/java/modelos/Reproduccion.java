package modelos;

import lombok.Data;


import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*;

@Data
@NoArgsConstructor
@DynamoDbBean
public class Reproduccion {
    private String contenido;
    private String fecha;

    @DynamoDbAttribute("Contenido")
    public String getContenido() {
        return contenido;
    }

    @DynamoDbAttribute("Fecha")
    public String getFecha() {
        return fecha;
    }
}
