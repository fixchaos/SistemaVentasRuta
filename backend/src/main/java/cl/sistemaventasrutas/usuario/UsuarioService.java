package cl.sistemaventasrutas.usuario;

import cl.sistemaventasrutas.shared.Mensajes;
import cl.sistemaventasrutas.shared.ResourceNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UsuarioResponse crear(UsuarioRequest request) {

        if (usuarioRepository.existsByRut(request.rut())) {
            throw new IllegalArgumentException("El RUT ya está registrado");
        }

        if (usuarioRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("El email ya está registrado");
        }

        if (usuarioRepository.existsByUsername(request.username())) {
            throw new IllegalArgumentException("El username ya está registrado");
        }

        String passwordHash = passwordEncoder.encode(request.password());

        Usuario usuario = new Usuario(
                request.nombre(),
                request.rut(),
                request.email(),
                request.username(),
                passwordHash,
                request.rol()
        );

        return UsuarioResponse.from(usuarioRepository.save(usuario));
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponse> listar() {
        return usuarioRepository.findAll()
                .stream()
                .map(UsuarioResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public UsuarioResponse obtenerPorId(Long id) {
        Usuario usuario = buscarUsuario(id);
        return UsuarioResponse.from(usuario);
    }

    public UsuarioResponse desactivar(Long id) {
        Usuario usuario = buscarUsuario(id);

        usuario.desactivar();

        return UsuarioResponse.from(usuario);
    }

    public UsuarioResponse activar(Long id) {
        Usuario usuario = buscarUsuario(id);

        usuario.activar();

        return UsuarioResponse.from(usuario);
    }

    public UsuarioResponse cambiarPassword(Long id, String nuevaPassword) {
        Usuario usuario = buscarUsuario(id);

        String passwordHash = passwordEncoder.encode(nuevaPassword);
        usuario.cambiarPassword(passwordHash);

        return UsuarioResponse.from(usuario);
    }

    private Usuario buscarUsuario(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(Mensajes.USUARIO_NO_ENCONTRADO)
                );
    }
}