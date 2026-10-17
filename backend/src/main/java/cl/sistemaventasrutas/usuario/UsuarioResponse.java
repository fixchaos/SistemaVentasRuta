package cl.sistemaventasrutas.usuario;

public record UsuarioResponse(
        Long id,
        String nombre,
        String rut,
        String email,
        String username,
        Rol rol,
        boolean activo
) {

    public static UsuarioResponse from(Usuario usuario) {
        return new UsuarioResponse(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getRut(),
                usuario.getEmail(),
                usuario.getUsername(),
                usuario.getRol(),
                usuario.isActivo()
        );
    }
}