package cl.sistemaventasrutas.ruta;

public record RutaResponse(
        Long id,
        String nombre,
        boolean activo
) {

    public static RutaResponse from(Ruta ruta) {
        return new RutaResponse(
                ruta.getId(),
                ruta.getNombre(),
                ruta.isActivo()
        );
    }
}