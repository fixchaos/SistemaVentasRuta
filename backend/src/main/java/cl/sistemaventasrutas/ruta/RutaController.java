package cl.sistemaventasrutas.ruta;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rutas")
public class RutaController {

    private final RutaService rutaService;

    public RutaController(RutaService rutaService) {
        this.rutaService = rutaService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RutaResponse crear(@Valid @RequestBody RutaRequest request) {
        return rutaService.crear(request);
    }

    @GetMapping
    public List<RutaResponse> listar() {
        return rutaService.listar();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        rutaService.eliminar(id);
    }
}