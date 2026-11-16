
package cl.sistemaventasrutas.venta;

import java.util.List;
import java.util.UUID;

import cl.sistemaventasrutas.cliente.Cliente;
import cl.sistemaventasrutas.cliente.ClienteRepository;
import cl.sistemaventasrutas.movimiento.MovimientoStock;
import cl.sistemaventasrutas.movimiento.MovimientoStockService;
import cl.sistemaventasrutas.movimiento.MovimientoStockRepository;
import cl.sistemaventasrutas.movimiento.TipoMovimientoStock;
import cl.sistemaventasrutas.producto.Producto;
import cl.sistemaventasrutas.producto.ProductoRepository;
import cl.sistemaventasrutas.ruta.Ruta;
import cl.sistemaventasrutas.ruta.RutaRepository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.context.ActiveProfiles;

import static org.mockito.Mockito.doThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class VentaServiceIntegrationTest {

    @Autowired
    private VentaService ventaService;

    @Autowired
    private VentaRepository ventaRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private RutaRepository rutaRepository;

    @Autowired
    private MovimientoStockRepository movimientoStockRepository;

    @MockitoSpyBean
    private MovimientoStockService movimientoStockService;

    private Ruta ruta;
    private Cliente cliente;
    private Producto producto;

    private Long productoId;
    private Long clienteId;
    private Long rutaId;

    private final List<Long> ventasCreadas = new java.util.ArrayList<>();

    @BeforeEach
    void prepararDatos() {

        String identificador = UUID.randomUUID().toString();

        ruta = rutaRepository.save(
            new Ruta("Ruta prueba " + identificador)
        );

        rutaId = ruta.getId();

        cliente = clienteRepository.save(
            new Cliente(
                "Cliente prueba",
                "9" + identificador.substring(0, 9),
                "Direccion prueba " + identificador,
                ruta
            )
        );

        clienteId = cliente.getId();

        producto = productoRepository.save(
            new Producto(
                "TEST-" + identificador,
                "Producto prueba",
                "unidad",
                500L,
                900L,
                20L
            )
        );

        productoId = producto.getId();
    }

    @AfterEach
    void limpiarDatos() {

        // Eliminar movimientos asociados al producto de prueba
        if(productoId != null){
            movimientoStockRepository.deleteAll(
                movimientoStockRepository
                    .findByProductoIdOrderByFechaDesc(productoId)
            );
        }

        // Eliminar las ventas creadas durante la prueba
        for (Long ventaId : ventasCreadas) {
            ventaRepository.deleteById(ventaId);
        }

        // Eliminar entidades dependientes
        if(productoId != null){
            productoRepository.deleteById(productoId);
        }

        if(clienteId != null){
            clienteRepository.deleteById(clienteId);
        }

        if(rutaId != null){
            rutaRepository.deleteById(rutaId);
        }

        ventasCreadas.clear();
    }



    private VentaResponse crearVenta(Long cantidad) {

        VentaRequest request = new VentaRequest(
            clienteId,
            EstadoPago.PAGADO,
            List.of(
                new VentaItemRequest(productoId, cantidad)
            )
        );

        VentaResponse response = ventaService.crear(request);

        ventasCreadas.add(response.id());

        return response;
    }

    @Test
    void deberiaPersistirVentaYMovimientoDeSalida() {

        // Arrange
        Long stockInicial = 20L;

        // Act
        VentaResponse response = crearVenta(5L);

        // Assert
        assertNotNull(response.id());
        assertEquals("CONFIRMADA", response.estadoVenta());

        Producto productoActualizado = productoRepository
            .findById(productoId)
            .orElseThrow();

        assertEquals(stockInicial - 5L, productoActualizado.getStock());

        List<MovimientoStock> movimientos =
            movimientoStockRepository.findByVentaId(response.id());

        assertEquals(1, movimientos.size());

        MovimientoStock movimiento = movimientos.get(0);

        assertEquals(TipoMovimientoStock.VENTA, movimiento.getTipo());
        assertEquals(-5L, movimiento.getCantidad());
        assertEquals(productoId, movimiento.getProducto().getId());
        assertEquals(response.id(), movimiento.getVenta().getId());
    }

    @Test
    void deberiaPersistirAnulacionYRestaurarStock() {

        // Arrange
        VentaResponse venta = crearVenta(5L);

        // Act
        VentaResponse respuestaAnulacion =
            ventaService.anular(venta.id());

        // Assert
        assertEquals("ANULADA", respuestaAnulacion.estadoVenta());

        Producto productoActualizado = productoRepository
            .findById(productoId)
            .orElseThrow();

        assertEquals(20L, productoActualizado.getStock());

        List<MovimientoStock> movimientos =
            movimientoStockRepository.findByVentaId(venta.id());

        assertEquals(2, movimientos.size());

        assertTrue(movimientos.stream().anyMatch(m ->
            m.getTipo() == TipoMovimientoStock.VENTA
                && m.getCantidad() == -5L
        ));

        assertTrue(movimientos.stream().anyMatch(m ->
            m.getTipo() == TipoMovimientoStock.ANULACION
                && m.getCantidad() == 5L
        ));
    }

    @Test
    void noDeberiaDuplicarMovimientosAlAnularDosVeces() {

        // Arrange
        VentaResponse venta = crearVenta(5L);

        ventaService.anular(venta.id());

        // Act & Assert
        assertThrows(
            IllegalStateException.class,
            () -> ventaService.anular(venta.id())
        );

        List<MovimientoStock> movimientos =
            movimientoStockRepository.findByVentaId(venta.id());

        assertEquals(2, movimientos.size());

        Producto productoActualizado = productoRepository
            .findById(productoId)
            .orElseThrow();

        assertEquals(20L, productoActualizado.getStock());
    }

    @Test
    void deberiaRevertirLaVentaSiFallaElRegistroDeMovimientoStock(){

        //Arrange
        Long stockInicial = 20L;
        long ventasAntes = ventaRepository.count();

        doThrow(new RuntimeException("Error simulado al registrar movimiento"))
            .when(movimientoStockService)
            .registrar(
                any(Producto.class),
                any(Venta.class),
                eq(TipoMovimientoStock.VENTA),
                anyLong(),
                anyString()
            );


        VentaRequest request = new VentaRequest(
            clienteId,
            EstadoPago.PAGADO,
            List.of(
                new VentaItemRequest(productoId, 5L)
            )
        );

        //Acto y Assert
        assertThrows(
            RuntimeException.class,
        () -> ventaService.crear(request)
        );

        //Verificar que el stock volvió a su valor original
        Producto productoActualizado = productoRepository
            .findById(productoId)
            .orElseThrow();

        assertEquals(stockInicial, productoActualizado.getStock());

        //Verificar que no se persistió ninguna venta nueva
        assertEquals(ventasAntes, ventaRepository.count());

        //Verififcar que no se persistió ningún movimiento
        List<MovimientoStock> movimientos =
            movimientoStockRepository
                .findByProductoIdOrderByFechaDesc(productoId);

        assertTrue(movimientos.isEmpty());
    }
}
