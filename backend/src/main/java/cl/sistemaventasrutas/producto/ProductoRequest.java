package cl.sistemaventasrutas.producto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotNull;

public record ProductoRequest(

        @NotBlank       
        @Size(max = 80)
        String codigo,

        @NotBlank
        @Size(max = 160)
        String nombre,

        @NotBlank
        @Size(max = 30)
        String unidad,

        @NotNull
        @Min(0)
        Long precioCosto,

        @NotNull
        @Min(0)
        Long precioVenta,

        @NotNull
        @Min(0)
        Long stock

) {
}