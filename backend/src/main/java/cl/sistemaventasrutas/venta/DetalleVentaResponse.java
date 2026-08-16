package cl.sistemaventasrutas.venta;


public record DetalleVentaResponse(
    Long productoId,
    String codigo,
    String nombre,
    Long cantidad,
    Long precioUnitario,
    Long subtotal
) {
    public static DetalleVentaResponse from(DetalleVenta detalle){
        return new DetalleVentaResponse(
            detalle.getProducto().getId(),
            detalle.getProducto().getCodigo(),
            detalle.getProducto().getNombre(),
            detalle.getCantidad(),
            detalle.getPrecioUnitario(),
            detalle.getSubtotal()
        );
    }
}
