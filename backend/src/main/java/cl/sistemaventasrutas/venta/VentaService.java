package cl.sistemaventasrutas.venta;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.sistemaventasrutas.cliente.Cliente;
import cl.sistemaventasrutas.cliente.ClienteRepository;
import cl.sistemaventasrutas.producto.Producto;
import cl.sistemaventasrutas.producto.ProductoRepository;
import cl.sistemaventasrutas.shared.ResourceNotFoundException;

@Service
@Transactional(readOnly = true) // Optimiza lecturas en la BD por defecto
public class VentaService {

    private final VentaRepository ventaRepository;
    private final ClienteRepository clienteRepository;
    private final ProductoRepository productoRepository;

    public VentaService(
            VentaRepository ventaRepository,
            ClienteRepository clienteRepository,
            ProductoRepository productoRepository) {
        this.ventaRepository = ventaRepository;
        this.clienteRepository = clienteRepository;
        this.productoRepository = productoRepository;
    }

    @Transactional // Permiso de escritura
    public VentaResponse crear(VentaRequest request) {
        // Mejora: Validar lista de items vacía
        if (request.items() == null || request.items().isEmpty()) {
            throw new IllegalArgumentException("La venta debe incluir al menos un producto.");
        }

        Cliente cliente = buscarCliente(request.clienteId());
        Venta venta = new Venta(cliente, request.estadoPago());

        // Mejora: Un solo bucle para validación, detalle y descuento
        for (VentaItemRequest item : request.items()) {
            Producto producto = buscarProducto(item.productoId());
            validarStock(producto, item.cantidad());

            DetalleVenta detalle = new DetalleVenta(
                venta,
                producto,
                item.cantidad(),
                item.precioUnitario()
            );

            venta.agregarDetalle(detalle);
            producto.descontarStock(item.cantidad());
        }

        venta.calcularTotales();
        ventaRepository.save(venta);

        return convertirAResponse(venta);
    }

    public List<VentaResponse> listar() {
        // Mejora: Simplificación con Streams
        return ventaRepository.findAll().stream()
                .map(this::convertirAResponse)
                .toList();
    }

    public VentaResponse obtenerPorId(Long id) {
        return convertirAResponse(buscarVenta(id));
    }

    // Mejora opcional: Buscar por UUID para el frontend
    public VentaResponse obtenerPorUuid(UUID uuid) {
        Venta venta = ventaRepository.findByUuid(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("Venta no encontrada"));
        return convertirAResponse(venta);
    }

    @Transactional // Permiso de escritura
    public VentaResponse anular(Long id) {
        Venta venta = buscarVenta(id);

        // Mejora: Evitar re-anulación y duplicidad de stock retornado
        if (venta.getEstadoVenta() == EstadoVenta.ANULADA) {
            throw new IllegalStateException("La venta ya se encuentra anulada.");
        }

        for (DetalleVenta detalle : venta.getDetalles()) {
            Producto producto = detalle.getProducto();
            producto.aumentarStock(detalle.getCantidad());
        }

        venta.anular();
        return convertirAResponse(venta);
    }

    @Transactional
    public void eliminar(Long id) {
        Venta venta = buscarVenta(id);

        // Opcional: Si la venta NO estaba anulada, reponer el stock antes de borrarla de la BD
        if (venta.getEstadoVenta() != EstadoVenta.ANULADA) {
            for (DetalleVenta detalle : venta.getDetalles()) {
                Producto producto = detalle.getProducto();
                producto.aumentarStock(detalle.getCantidad());
            }
        }

        ventaRepository.delete(venta);
    }

    // --- Métodos Auxiliares ---

    private Cliente buscarCliente(Long clienteId) {
        return clienteRepository.findById(clienteId)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado"));
    }

    private Producto buscarProducto(Long productoId) {
        return productoRepository.findById(productoId)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));
    }

    private Venta buscarVenta(Long ventaId) {
        return ventaRepository.findById(ventaId)
                .orElseThrow(() -> new ResourceNotFoundException("Venta no encontrada"));
    }

    private void validarStock(Producto producto, Long cantidadSolicitada) {
        if (producto.getStock().compareTo(cantidadSolicitada) < 0) {
            throw new IllegalArgumentException("Stock insuficiente para el producto: " + producto.getNombre());
        }
    }

    private VentaResponse convertirAResponse(Venta venta) {
        List<DetalleVentaResponse> items = venta.getDetalles()
                .stream()
                .map(this::convertirDetalleAResponse)
                .toList();

        return new VentaResponse(
            venta.getId(),
            venta.getUuid(),
            venta.getCliente().getId(),
            venta.getCliente().getNombre(),
            venta.getFecha(),
            venta.getNeto(),
            venta.getIva(),
            venta.getTotal(),
            venta.getEstadoPago().name(),
            venta.getEstadoVenta().name(),
            items
        );
    }

    private DetalleVentaResponse convertirDetalleAResponse(DetalleVenta detalle) {
        return new DetalleVentaResponse(
            detalle.getProducto().getId(),
            detalle.getProducto().getCodigo(),
            detalle.getProducto().getNombre(),
            detalle.getCantidad(),
            detalle.getPrecioUnitario(),
            detalle.getSubtotal()
        );
    }
}