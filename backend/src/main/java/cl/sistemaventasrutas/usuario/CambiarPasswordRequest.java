package cl.sistemaventasrutas.usuario;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CambiarPasswordRequest(

        @NotBlank
        @Size(min = 8, max = 100)
        String nuevaPassword
) {
}