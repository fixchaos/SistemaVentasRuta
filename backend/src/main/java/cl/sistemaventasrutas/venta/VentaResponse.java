package cl.sistemaventasrutas.venta;

import java.time.Instant;
import java.util.UUID;
import java.util.List;

public record VentaResponse(

        Long id,
        UUID uuid,
        Long clienteId,
        String clienteNombre,
        Instant fecha,
        Long neto,
        Long iva,
        Long total,
        String estadoPago,
        String estadoVenta,
        List<DetalleVentaResponse> items
) {
}