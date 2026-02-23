package modelos;

import lombok.Data;


import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*;

@Data
@NoArgsConstructor
@DynamoDbBean
public class Usuario {

    private String nombre;
    private String email;

    @DynamoDbPartitionKey
    @DynamoDbAttribute("Nombre")
    public String getNombre() {
        return nombre;
    }

    @DynamoDbAttribute("Email")
    public String getEmail() {
        return email;
    }
}
