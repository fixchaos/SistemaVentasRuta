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

public static VentaResponse from(Venta venta){
        List<DetalleVentaResponse> items = venta.getDetalles()
                .stream()
                .map(DetalleVentaResponse::from)
                .toList();

        return new VentaResponse(
                venta.getId(),
                venta.getUuid(),
                venta.getCliente().getId(),
                venta.getCliente().getNombre(),
                venta.getFecha(),
                venta.getNeto(),
                venta.getIva(),
                venta.getTotal(),
                venta.getEstadoPago().name(),
                venta.getEstadoVenta().name(),
                items
        );
}
}