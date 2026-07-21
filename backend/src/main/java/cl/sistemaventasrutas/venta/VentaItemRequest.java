package cl.sistemaventasrutas.venta;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record VentaItemRequest(

        @NotNull(message = "El ID del producto es obligatorio")
        Long productoId,

        @NotNull(message = "La cantidad es obligatoria")
        @Min(value = 1, message = "La cantidad debe ser al menos 1")
        Long cantidad,

        @NotNull(message = "El precio unitario es obligatorio")
        @Min(value = 1, message = "El precio unitario debe ser mayor a 0")
        Long precioUnitario

) {
}