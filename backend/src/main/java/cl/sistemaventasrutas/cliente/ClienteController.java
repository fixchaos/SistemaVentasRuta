package cl.sistemaventasrutas.cliente;
import java.util.List;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ClienteResponse crear(
        @Valid @RequestBody ClienteRequest request){
        return clienteService.crear(request);
    }

    @GetMapping
    public List<ClienteResponse> listar() {
        return clienteService.listar();
    }

    @GetMapping("/{id}")
    public ClienteResponse obtenerPorId(@PathVariable Long id){
        return clienteService.obtenerPorId(id);
    }

    @PatchMapping("/{id}")
    public ClienteResponse actualizar(
        @PathVariable Long id,
        @Valid @RequestBody ClienteRequest request){
        return clienteService.actualizar(id, request);
    }

    @PatchMapping("/{id}/reactivar")
    public ClienteResponse reactivar(@PathVariable Long id){
        return clienteService.reactivar(id);
    }

    @DeleteMapping("/{id}")
    public ClienteResponse eliminar(@PathVariable Long id) {
        return clienteService.eliminar(id);
    }
}
