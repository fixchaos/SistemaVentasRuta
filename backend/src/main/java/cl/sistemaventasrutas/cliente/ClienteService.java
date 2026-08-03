package cl.sistemaventasrutas.cliente;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.sistemaventasrutas.ruta.Ruta;
import cl.sistemaventasrutas.ruta.RutaRepository;

@Service
@Transactional
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final RutaRepository rutaRepository;

    public ClienteService(
        ClienteRepository clienteRepository,
        RutaRepository rutaRepository) {

        this.clienteRepository = clienteRepository;
        this.rutaRepository = rutaRepository;
    }

    public ClienteResponse crear(ClienteRequest request) {
        Ruta ruta = rutaRepository.findByIdAndActivoTrue(request.rutaId()).orElseThrow(() ->
        new IllegalArgumentException("Ruta no encontrada"));
        if(clienteRepository.existsByTelefono(request.telefono())){
            throw new IllegalArgumentException("Ya existe un cliente con ese número de teléfono");
        }
        if(clienteRepository.existsByDireccion(request.direccion())){
            throw new IllegalArgumentException("Ya existe un cliente con esa dirección");
        }

        Cliente cliente = new Cliente(
            request.nombre(),
            request.telefono(),
            request.direccion(),
            ruta);
            cliente = clienteRepository.save(cliente);
            return toResponse(cliente);
        }

    @Transactional(readOnly = true)
    public List<ClienteResponse> listar() {
    return clienteRepository
        .findAllByActivoTrueOrderByNombreAsc()
        .stream()
        .map(this::toResponse)
        .toList();
    }

    @Transactional(readOnly = true)
    public ClienteResponse obtenerPorId(Long id){
        Cliente cliente = clienteRepository.findByIdAndActivoTrue(id)
        .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado"));
        return toResponse(cliente);
    }

    public ClienteResponse actualizar(Long id, ClienteRequest request){
        Cliente cliente = clienteRepository.findByIdAndActivoTrue(id)
            .orElseThrow(()-> new IllegalArgumentException("Cliente no encontrado"));

        if(clienteRepository.existsByTelefonoAndIdNot(request.telefono(), id)){
            throw new IllegalArgumentException("Ya existe un cliente con ese número de teléfono");
        };
        if(clienteRepository.existsByDireccionAndIdNot(request.direccion(), id)){
            throw new IllegalArgumentException("Ya existe un cliente con esa dirección");
        }

        Ruta ruta = rutaRepository.findByIdAndActivoTrue(request.rutaId())
            .orElseThrow(() -> new IllegalArgumentException("Ruta no encontrada"));

        cliente.actualizarDatos(
            request.nombre(),
            request.telefono(),
            request.direccion(),
             ruta);
        return toResponse(cliente);
    }

    public ClienteResponse reactivar(Long id){
        Cliente cliente = clienteRepository.findById(id)
        .orElseThrow(()-> new IllegalArgumentException("Cliente no encontrado"));
        cliente.reactivar();
        return toResponse(cliente);
    }

    public ClienteResponse eliminar(Long id) {
        Cliente cliente = clienteRepository
        .findByIdAndActivoTrue(id)
        .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado"));
        cliente.desactivar();
        return toResponse(cliente);
    }

    private ClienteResponse toResponse(Cliente cliente) {
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
