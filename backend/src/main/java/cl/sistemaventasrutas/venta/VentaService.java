
package cl.sistemaventasrutas.venta;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.sistemaventasrutas.cliente.Cliente;
import cl.sistemaventasrutas.cliente.ClienteRepository;
import cl.sistemaventasrutas.movimiento.MovimientoStockService;
import cl.sistemaventasrutas.movimiento.TipoMovimientoStock;
import cl.sistemaventasrutas.producto.Producto;
import cl.sistemaventasrutas.producto.ProductoRepository;
import cl.sistemaventasrutas.shared.ResourceNotFoundException;
import cl.sistemaventasrutas.shared.Mensajes;

@Service
@Transactional(readOnly = true)
public class VentaService {

    private final VentaRepository ventaRepository;
    private final ClienteRepository clienteRepository;
    private final ProductoRepository productoRepository;
    private final MovimientoStockService movimientoStockService;

    public VentaService(
            VentaRepository ventaRepository,
            ClienteRepository clienteRepository,
            ProductoRepository productoRepository,
            MovimientoStockService movimientoStockService) {

        this.ventaRepository = ventaRepository;
        this.clienteRepository = clienteRepository;
        this.productoRepository = productoRepository;
        this.movimientoStockService = movimientoStockService;
    }

    @Transactional
    public VentaResponse crear(VentaRequest request) {

        if (request.items() == null || request.items().isEmpty()) {
            throw new IllegalArgumentException(
                "La venta debe incluir al menos un producto."
            );
        }

        Cliente cliente = buscarCliente(request.clienteId());
        Venta venta = new Venta(cliente, request.estadoPago());

        // Crear detalles y descontar stock
        for (VentaItemRequest item : request.items()) {

            Producto producto = buscarProducto(item.productoId());
            validarStock(producto, item.cantidad());

            DetalleVenta detalle = new DetalleVenta(
                venta,
                producto,
                item.cantidad(),
                producto.getPrecioVenta()
            );

            venta.agregarDetalle(detalle);
            producto.descontarStock(item.cantidad());
        }

        // Calcular neto, IVA y total
        venta.calcularTotales();

        // Guardar la venta antes de asociarle movimientos
        Venta ventaGuardada = ventaRepository.save(venta);

        // Registrar las salidas de inventario
        for (DetalleVenta detalle : ventaGuardada.getDetalles()) {

            movimientoStockService.registrar(
                detalle.getProducto(),
                ventaGuardada,
                TipoMovimientoStock.VENTA,
                Math.negateExact(detalle.getCantidad()),
                "Salida de stock por venta"
            );
        }

        return VentaResponse.from(ventaGuardada);
    }

    public List<VentaResponse> listar(
            LocalDate fecha,
            EstadoVenta estado) {

        ZoneId zonaChile = ZoneId.of("America/Santiago");
        List<Venta> ventas;

        if (fecha != null && estado != null) {

            Instant inicio = fecha.atStartOfDay(zonaChile).toInstant();
            Instant fin = fecha.plusDays(1)
                .atStartOfDay(zonaChile).toInstant();

            ventas = ventaRepository
                .findByFechaGreaterThanEqualAndFechaLessThanAndEstadoVenta(
                    inicio, fin, estado
                );

        } else if (fecha != null) {

            Instant inicio = fecha.atStartOfDay(zonaChile).toInstant();
            Instant fin = fecha.plusDays(1)
                .atStartOfDay(zonaChile).toInstant();

            ventas = ventaRepository
                .findByFechaGreaterThanEqualAndFechaLessThan(inicio, fin);

        } else if (estado != null) {

            ventas = ventaRepository.findByEstadoVenta(estado);

        } else {

            ventas = ventaRepository.findAll();
        }

        return ventas.stream()
            .map(VentaResponse::from)
            .toList();
    }

    public VentaResponse obtenerPorId(Long id) {
        return VentaResponse.from(buscarVenta(id));
    }

    public VentaResponse obtenerPorUuid(UUID uuid) {

        Venta venta = ventaRepository.findByUuid(uuid)
            .orElseThrow(() ->
                new ResourceNotFoundException(
                    Mensajes.VENTA_NO_ENCONTRADA
                )
            );

        return VentaResponse.from(venta);
    }

    @Transactional
    public VentaResponse anular(Long id) {

        Venta venta = buscarVenta(id);

        // Evitar anular dos veces la misma venta
        if (venta.getEstadoVenta() == EstadoVenta.ANULADA) {
            throw new IllegalStateException(
                "La venta ya se encuentra anulada."
            );
        }

        // Restaurar stock y registrar movimientos
        restaurarStock(venta);

        venta.anular();

        return VentaResponse.from(venta);
    }

    // -----------------------------
    // MÉTODOS AUXILIARES
    // -----------------------------

    private Cliente buscarCliente(Long clienteId) {

        return clienteRepository.findById(clienteId)
            .orElseThrow(() ->
                new ResourceNotFoundException(
                    Mensajes.CLIENTE_NO_ENCONTRADO
                )
            );
    }

    private Producto buscarProducto(Long productoId) {

        return productoRepository.findByIdAndActivoTrue(productoId)
            .orElseThrow(() ->
                new ResourceNotFoundException(
                    Mensajes.PRODUCTO_NO_ENCONTRADO
                )
            );
    }

    private Venta buscarVenta(Long ventaId) {

        return ventaRepository.findById(ventaId)
            .orElseThrow(() ->
                new ResourceNotFoundException(
                    Mensajes.VENTA_NO_ENCONTRADA
                )
            );
    }

    private void validarStock(
            Producto producto,
            Long cantidadSolicitada) {

        if (cantidadSolicitada == null || cantidadSolicitada <= 0) {
            throw new IllegalArgumentException(
                "La cantidad solicitada debe ser mayor que cero"
            );
        }

        if (producto.getStock().compareTo(cantidadSolicitada) < 0) {
            throw new IllegalArgumentException(
                "Stock insuficiente para el producto: "
                    + producto.getNombre()
            );
        }
    }

    private void restaurarStock(Venta venta) {

        for (DetalleVenta detalle : venta.getDetalles()) {

            Producto producto = detalle.getProducto();
            Long cantidad = detalle.getCantidad();

            // Devolver unidades al inventario
            producto.aumentarStock(cantidad);

            // Registrar entrada por anulación
            movimientoStockService.registrar(
                producto,
                venta,
                TipoMovimientoStock.ANULACION,
                cantidad,
                "Entrada de stock por anulación de venta"
            );
        }
    }
}
