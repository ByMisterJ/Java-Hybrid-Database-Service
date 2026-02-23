package modelos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*;

@Data
@NoArgsConstructor
@DynamoDbBean
public class Historial {
    private String Usuario;
    private java.util.List<Reproduccion> Reproducciones;

    @DynamoDbPartitionKey
    @DynamoDbAttribute("Usuario")
    public String getUsuario() {
        return Usuario;
    }

    @DynamoDbAttribute("Reproducciones")
    public java.util.List<Reproduccion> getReproducciones() {
        return Reproducciones;
    }
}
