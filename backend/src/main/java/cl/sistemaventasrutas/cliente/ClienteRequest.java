package cl.sistemaventasrutas.cliente;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ClienteRequest(

        @NotBlank
        @Size(max = 160)
        String nombre,

        @NotBlank
        @Size(max = 30)
        String telefono,

        @NotBlank
        @Size(max = 255)
        String direccion,

        @NotNull
        Long rutaId

) {
}