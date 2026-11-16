
package cl.sistemaventasrutas.movimiento;

import cl.sistemaventasrutas.cliente.Cliente;
import cl.sistemaventasrutas.producto.Producto;
import cl.sistemaventasrutas.ruta.Ruta;
import cl.sistemaventasrutas.venta.EstadoPago;
import cl.sistemaventasrutas.venta.Venta;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MovimientoStockTest {

    private Producto producto;
    private Venta venta;

    @BeforeEach
    void prepararDatos() {
        Ruta ruta = new Ruta("Ruta de prueba");

        Cliente cliente = new Cliente(
            "Cliente de prueba",
            "999999999",
            "Dirección de prueba",
            ruta
        );

        producto = new Producto(
            "PROD-001",
            "Producto de prueba",
            "UNIDAD",
            500L,
            900L,
            20L
        );

        venta = new Venta(cliente, EstadoPago.PAGADO);
    }

    @Test
    void deberiaCrearMovimientoVentaConCantidadNegativa() {
        MovimientoStock movimiento = new MovimientoStock(
            producto, venta, TipoMovimientoStock.VENTA, -5L, "Venta de prueba"
        );

        assertEquals(TipoMovimientoStock.VENTA, movimiento.getTipo());
        assertEquals(-5L, movimiento.getCantidad());
        assertSame(venta, movimiento.getVenta());
    }

    @Test
    void deberiaRechazarVentaConCantidadPositiva() {
        assertThrows(IllegalArgumentException.class, () ->
            new MovimientoStock(
                producto, venta, TipoMovimientoStock.VENTA, 5L, null
            )
        );
    }

    @Test
    void deberiaRechazarVentaSinVentaAsociada() {
        assertThrows(IllegalArgumentException.class, () ->
            new MovimientoStock(
                producto, null, TipoMovimientoStock.VENTA, -5L, null
            )
        );
    }

    @Test
    void deberiaCrearMovimientoAnulacionConCantidadPositiva() {
        MovimientoStock movimiento = new MovimientoStock(
            producto, venta, TipoMovimientoStock.ANULACION, 5L, "Anulación"
        );

        assertEquals(5L, movimiento.getCantidad());
        assertSame(venta, movimiento.getVenta());
    }

    @Test
    void deberiaRechazarAnulacionConCantidadNegativa() {
        assertThrows(IllegalArgumentException.class, () ->
            new MovimientoStock(
                producto, venta, TipoMovimientoStock.ANULACION, -5L, null
            )
        );
    }

    @Test
    void deberiaCrearMovimientoEntradaConCantidadPositiva() {
        MovimientoStock movimiento = new MovimientoStock(
            producto, null, TipoMovimientoStock.ENTRADA, 10L, "Stock inicial"
        );

        assertEquals(10L, movimiento.getCantidad());
        assertNull(movimiento.getVenta());
    }

    @Test
    void deberiaRechazarEntradaConCantidadNegativa() {
        assertThrows(IllegalArgumentException.class, () ->
            new MovimientoStock(
                producto, null, TipoMovimientoStock.ENTRADA, -10L, null
            )
        );
    }

    @Test
    void deberiaCrearMovimientoAjusteConCantidadPositiva() {
        MovimientoStock movimiento = new MovimientoStock(
            producto, null, TipoMovimientoStock.AJUSTE, 5L, "Ajuste"
        );

        assertEquals(5L, movimiento.getCantidad());
    }

    @Test
    void deberiaCrearMovimientoEdicionConCantidadNegativa() {
        MovimientoStock movimiento = new MovimientoStock(
            producto, null, TipoMovimientoStock.EDICION, -3L, "Edición"
        );

        assertEquals(-3L, movimiento.getCantidad());
    }

    @Test
    void deberiaRechazarAjusteConVentaAsociada() {
        assertThrows(IllegalArgumentException.class, () ->
            new MovimientoStock(
                producto, venta, TipoMovimientoStock.AJUSTE, 5L, null
            )
        );
    }

    @Test
    void deberiaRechazarCantidadCero() {
        assertThrows(IllegalArgumentException.class, () ->
            new MovimientoStock(
                producto, null, TipoMovimientoStock.ENTRADA, 0L, null
            )
        );
    }

    @Test
    void deberiaRechazarCantidadNula() {
        assertThrows(IllegalArgumentException.class, () ->
            new MovimientoStock(
                producto, null, TipoMovimientoStock.ENTRADA, null, null
            )
        );
    }
}
