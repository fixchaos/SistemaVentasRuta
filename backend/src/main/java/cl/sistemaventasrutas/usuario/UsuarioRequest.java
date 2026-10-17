package cl.sistemaventasrutas.usuario;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UsuarioRequest(

        @NotBlank
        @Size(max = 160)
        String nombre,

        @NotBlank
        @Size(max = 20)
        String rut,

        @NotBlank
        @Email
        @Size(max = 160)
        String email,

        @NotBlank
        @Size(max = 80)
        String username,

        @NotBlank
        @Size(min = 8, max = 100)
        String password,

        @NotNull
        Rol rol
) {
}