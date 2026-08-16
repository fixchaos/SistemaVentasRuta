package cl.sistemaventasrutas.producto;

public record ProductoResponse(

        Long id,
        String codigo,
        String nombre,
        String unidad,
        Long precioCosto,
        Long precioVenta,
        Long stock,
        boolean activo

) {
        public static ProductoResponse from(Producto producto){
                return new ProductoResponse(
                        producto.getId(),
                        producto.getCodigo(),
                        producto.getNombre(),
                        producto.getUnidad(),
                        producto.getPrecioCosto(),
                        producto.getPrecioVenta(),
                        producto.getStock(),
                        producto.isActivo()

                );
        }
}