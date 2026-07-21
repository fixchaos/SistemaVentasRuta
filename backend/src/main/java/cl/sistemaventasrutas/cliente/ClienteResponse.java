package cl.sistemaventasrutas.cliente;

public record ClienteResponse(

        Long id,
        String nombre,
        String telefono,
        String direccion,
        Long rutaId,
        String rutaNombre,
        boolean activo

) {
}