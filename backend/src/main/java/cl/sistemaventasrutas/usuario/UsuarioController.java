package cl.sistemaventasrutas.usuario;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioResponse crear(@Valid @RequestBody UsuarioRequest request) {
        return usuarioService.crear(request);
    }

    @GetMapping
    public List<UsuarioResponse> listar() {
        return usuarioService.listar();
    }

    @GetMapping("/{id}")
    public UsuarioResponse obtenerPorId(@PathVariable Long id) {
        return usuarioService.obtenerPorId(id);
    }

    @PatchMapping("/{id}/activar")
    public UsuarioResponse activar(@PathVariable Long id) {
        return usuarioService.activar(id);
    }

    @PatchMapping("/{id}/desactivar")
    public UsuarioResponse desactivar(@PathVariable Long id) {
        return usuarioService.desactivar(id);
    }

    @PatchMapping("/me/password")
    public UsuarioResponse cambiarPassword(
        Authentication authentication,
        @Valid @RequestBody CambiarPasswordRequest request
    ){
        return usuarioService.cambiarPassword(
            authentication,
            request.passwordActual(),
            request.nuevaPassword()
        );
    }
}