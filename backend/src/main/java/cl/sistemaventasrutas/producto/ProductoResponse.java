package cl.sistemaventasrutas.producto;

public record ProductoResponse(

        Long id,
        String codigo,
        String nombre,
        Long precioCosto,
        Long precioVenta,
        Long stock,
        boolean activo

) {
}