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
        public static ClienteResponse from(Cliente cliente){
                return new ClienteResponse(
                        cliente.getId(),
                        cliente.getNombre(),
                        cliente.getTelefono(),
                        cliente.getDireccion(),
                        cliente.getRuta().getId(),
                        cliente.getRuta().getNombre(),
                        cliente.isActivo()
                );
        }
}