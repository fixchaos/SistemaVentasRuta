package cl.sistemaventasrutas.venta;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/ventas")
public class VentaController {

    private final VentaService ventaService;

    public VentaController(VentaService ventaService) {
        this.ventaService = ventaService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VentaResponse crear(@Valid @RequestBody VentaRequest request){
        return ventaService.crear(request);
    }

    @GetMapping
    public List<VentaResponse> listar() {
        return ventaService.listar();
    }

    @GetMapping("/{id}")
    public VentaResponse obtenerPorId(@PathVariable Long id) {
        return ventaService.obtenerPorId(id);
    }

    // Endpoint opcional para buscar por UUID desde la web
    @GetMapping("/uuid/{uuid}")
    public VentaResponse obtenerPorUuid(@PathVariable UUID uuid) {
        return ventaService.obtenerPorUuid(uuid);
    }

    @PatchMapping("/{id}/anular")
    public VentaResponse anular(@PathVariable Long id) {
        return ventaService.anular(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        ventaService.eliminar(id);
    }
}