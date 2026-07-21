package cl.sistemaventasrutas.venta;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record VentaRequest(

        @NotNull(message = "El ID del cliente es obligatorio")
        Long clienteId,

        @NotNull(message = "El estado de pago es obligatorio")
        EstadoPago estadoPago,

        @NotEmpty(message = "La venta debe incluir al menos un producto")
        List<@Valid VentaItemRequest> items

) {
}