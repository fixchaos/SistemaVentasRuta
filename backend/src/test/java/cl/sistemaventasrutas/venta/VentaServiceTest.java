package cl.sistemaventasrutas.venta;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import cl.sistemaventasrutas.cliente.Cliente;
import cl.sistemaventasrutas.cliente.ClienteRepository;
import cl.sistemaventasrutas.producto.Producto;
import cl.sistemaventasrutas.producto.ProductoRepository;
import cl.sistemaventasrutas.ruta.Ruta;
import cl.sistemaventasrutas.shared.ResourceNotFoundException;

@ExtendWith(MockitoExtension.class)
public class VentaServiceTest {

    @Mock
    private VentaRepository ventaRepository;

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private ProductoRepository productoRepository;

    @InjectMocks
    private VentaService ventaService;

    // ==========================================
    // 1. PRUEBAS UNITARIAS (TEST CASES)
    // ==========================================

    @Test
    void deberiaObtenerVentaPorId() {
        // Arrange
        Venta venta = crearVentaValida();

        when(ventaRepository.findById(1L)).thenReturn(Optional.of(venta));

        // Act
        VentaResponse response = ventaService.obtenerPorId(1L);

        // Assert - Mantenemos TODAS las verificaciones exhaustivas
        assertEquals("La Pica del Hornito", response.clienteNombre());
        assertEquals("PAGADO", response.estadoPago());
        assertEquals("CONFIRMADA", response.estadoVenta());
        assertEquals(900L, response.neto());
        assertEquals(171L, response.iva());
        assertEquals(1071L, response.total());
        assertEquals(1, response.items().size());

        // Verificaciones del detalle
        DetalleVentaResponse item = response.items().get(0);
        assertEquals("PAP001", item.codigo());
        assertEquals("Bolsa Papel Kraft", item.nombre());
        assertEquals(2L, item.cantidad());
        assertEquals(450L, item.precioUnitario());
        assertEquals(900L, item.subtotal());
    }

    @Test
    void deberiaLanzarExcepcionCuandoVentaNoExiste() {
        // Arrange
        when(ventaRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
            ResourceNotFoundException.class,
            () -> ventaService.obtenerPorId(999L)
        );

        assertEquals("Venta no encontrada", exception.getMessage());
    }

    @Test
    void deberiaCrearVentaExitosamente() {
        // Arrange
        Cliente cliente = crearCliente();
        Producto producto = crearProducto("PAP001", 100L); // Stock inicial 100

        VentaItemRequest itemRequest = new VentaItemRequest(1L, 5L, 600L); // Solicita 5
        VentaRequest ventaRequest = new VentaRequest(1L, EstadoPago.PAGADO, List.of(itemRequest));

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));

        // Act
        VentaResponse response = ventaService.crear(ventaRequest);

        // Assert
        assertNotNull(response);
        assertEquals("La Pica del Hornito", response.clienteNombre());
        assertEquals(95L, producto.getStock()); // Verifica descuento de stock (100 - 5 = 95)
        verify(ventaRepository).save(any(Venta.class));
    }

    @Test
    void deberiaLanzarExcepcionCuandoStockEsInsuficiente() {
        // Arrange
        Cliente cliente = crearCliente();
        Producto producto = crearProducto("PAP001", 2L); // Solo hay 2 en stock

        VentaItemRequest itemRequest = new VentaItemRequest(1L, 10L, 600L); // Pide 10
        VentaRequest ventaRequest = new VentaRequest(1L, EstadoPago.PAGADO, List.of(itemRequest));

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> ventaService.crear(ventaRequest)
        );

        assertTrue(exception.getMessage().contains("Stock insuficiente"));
    }

    @Test
    void deberiaAnularVentaYDevolverStock() {
        // Arrange
        Producto producto = crearProducto("PAP001", 50L);
        Venta venta = crearVentaConProducto(producto, 10L);

        when(ventaRepository.findById(1L)).thenReturn(Optional.of(venta));

        // Act
        VentaResponse response = ventaService.anular(1L);

        // Assert
        assertEquals("ANULADA", response.estadoVenta());
        assertEquals(60L, producto.getStock()); // Devuelve stock: 50 + 10 = 60
    }

    // ==========================================
    // 2. MÉTODOS AUXILIARES (HELPERS)
    // ==========================================

    private Ruta crearRuta() {
        return new Ruta("Melipilla");
    }

    private Cliente crearCliente() {
        return new Cliente("La Pica del Hornito", "987654321", "Av. Principal 123", crearRuta());
    }

    private Producto crearProducto(String codigo, Long stock) {
        return new Producto(codigo, "Bolsa Papel Kraft", 300L, 600L, stock);
    }

    private Venta crearVentaValida() {
        Cliente cliente = crearCliente();
        Producto producto = crearProducto("PAP001", 100L);

        Venta venta = new Venta(cliente, EstadoPago.PAGADO);
        DetalleVenta detalle = new DetalleVenta(venta, producto, 2L, 450L);
        venta.agregarDetalle(detalle);
        venta.calcularTotales();

        return venta;
    }

    private Venta crearVentaConProducto(Producto producto, Long cantidad) {
        Cliente cliente = crearCliente();
        Venta venta = new Venta(cliente, EstadoPago.PAGADO);
        DetalleVenta detalle = new DetalleVenta(venta, producto, cantidad, producto.getPrecioVenta());
        venta.agregarDetalle(detalle);
        venta.calcularTotales();

        return venta;
    }
}