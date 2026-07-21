package cl.sistemaventasrutas.ruta;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RutaRequest(

        @NotBlank
        @Size(max = 120)
        String nombre

) {
}