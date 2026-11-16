
package cl.sistemaventasrutas.producto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProductoTest {

    private Producto producto;

    @BeforeEach
    void prepararProducto() {
        producto = new Producto(
            "TEST-001",
            "Producto de prueba",
            "unidad",
            100L,
            200L,
            10L
        );
    }

    @Test
    void deberiaDescontarStockCorrectamente() {
        producto.descontarStock(3L);

        assertEquals(7L, producto.getStock());
    }

    @Test
    void deberiaRechazarDescuentoConStockInsuficiente() {
        assertThrows(
            IllegalArgumentException.class,
            () -> producto.descontarStock(11L)
        );

        assertEquals(10L, producto.getStock());
    }

    @Test
    void deberiaRechazarDescuentoConCantidadCero() {
        assertThrows(
            IllegalArgumentException.class,
            () -> producto.descontarStock(0L)
        );
    }

    @Test
    void deberiaRechazarDescuentoConCantidadNegativa() {
        assertThrows(
            IllegalArgumentException.class,
            () -> producto.descontarStock(-2L)
        );
    }

    @Test
    void deberiaAumentarStockCorrectamente() {
        producto.aumentarStock(5L);

        assertEquals(15L, producto.getStock());
    }

    @Test
    void deberiaRechazarAumentoConCantidadCero() {
        assertThrows(
            IllegalArgumentException.class,
            () -> producto.aumentarStock(0L)
        );
    }

    @Test
    void deberiaRechazarAumentoConCantidadNegativa() {
        assertThrows(
            IllegalArgumentException.class,
            () -> producto.aumentarStock(-5L)
        );
    }

    @Test
    void deberiaDetectarDesbordamientoAlAumentarStock() {
        Producto productoGrande = new Producto(
            "TEST-002",
            "Producto grande",
            "unidad",
            100L,
            200L,
            Long.MAX_VALUE
        );

        assertThrows(
            ArithmeticException.class,
            () -> productoGrande.aumentarStock(1L)
        );

        assertEquals(Long.MAX_VALUE, productoGrande.getStock());
    }
}
