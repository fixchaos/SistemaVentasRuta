package cl.sistemaventasrutas.cliente;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.sistemaventasrutas.ruta.Ruta;
import cl.sistemaventasrutas.ruta.RutaRepository;
import cl.sistemaventasrutas.shared.ResourceNotFoundException;
import cl.sistemaventasrutas.shared.Mensajes;


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

    //Métodos públicos

    public ClienteResponse crear(ClienteRequest request) {
        Ruta ruta = buscarRuta(request.rutaId());
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
        Cliente cliente = buscarClienteActivo(id);
        return toResponse(cliente);
    }

    public ClienteResponse actualizar(Long id, ClienteRequest request){
        Cliente cliente = buscarClienteActivo(id);
        if(clienteRepository.existsByTelefonoAndIdNot(request.telefono(), id)){
            throw new IllegalArgumentException("Ya existe un cliente con ese número de teléfono");
        };
        if(clienteRepository.existsByDireccionAndIdNot(request.direccion(), id)){
            throw new IllegalArgumentException("Ya existe un cliente con esa dirección");
        }

        Ruta ruta = buscarRuta(request.rutaId());

        cliente.actualizarDatos(
            request.nombre(),
            request.telefono(),
            request.direccion(),
             ruta);
        return toResponse(cliente);
    }

    public ClienteResponse reactivar(Long id){
        Cliente cliente = buscarCliente(id);
        cliente.reactivar();
        return toResponse(cliente);
    }

    public ClienteResponse eliminar(Long id) {
        Cliente cliente = buscarClienteActivo(id);
        cliente.desactivar();
        return toResponse(cliente);
    }

    //Métodos privados

    private Cliente buscarClienteActivo(Long id){
        return clienteRepository.findByIdAndActivoTrue(id)
        .orElseThrow(() -> new ResourceNotFoundException(Mensajes.CLIENTE_NO_ENCONTRADO));
    }

    private Cliente buscarCliente(Long id){
        return clienteRepository.findById(id)
        .orElseThrow(()->
        new ResourceNotFoundException(Mensajes.CLIENTE_NO_ENCONTRADO));
    }

    private Ruta buscarRuta(Long rutaId){
        return rutaRepository.findByIdAndActivoTrue(rutaId)
        .orElseThrow(()-> new IllegalArgumentException(Mensajes.RUTA_NO_ENCONTRADA));
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
