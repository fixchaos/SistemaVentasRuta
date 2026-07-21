package cl.sistemaventasrutas.venta;


public record DetalleVentaResponse(
    Long productoId,
    String codigo,
    String nombre,
    Long cantidad,
    Long precioUnitario,
    Long subtotal) {
    }
