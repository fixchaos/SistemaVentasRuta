
package cl.sistemaventasrutas.producto;

// Java
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

// JUnit
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

// Spring
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.support.TransactionTemplate;

@SpringBootTest
@ActiveProfiles("test")
class ProductoConcurrencyTest {

    // Dependencias
    private final ProductoRepository productoRepository;
    private final TransactionTemplate transactionTemplate;

    // Datos de prueba
    private Long productoId;

    // Constructor
    @Autowired
    ProductoConcurrencyTest(
        ProductoRepository productoRepository,
        TransactionTemplate transactionTemplate
    ) {
        this.productoRepository = productoRepository;
        this.transactionTemplate = transactionTemplate;
    }

    // Preparación
    @BeforeEach
    void prepararProducto() {
        Producto producto = new Producto(
            "CONC-001",
            "Producto prueba",
            "unidad",
            100L,
            200L,
            5L
        );

        producto = productoRepository.saveAndFlush(producto);
        productoId = producto.getId();
    }

    // Limpieza
    @AfterEach
    void limpiar() {
        if (productoId != null) {
            productoRepository.deleteById(productoId);
        }
    }

    // Pruebas
    @Test
    void deberiaRechazarUnaModificacionConcurrentePorVersion() throws Exception {
        CountDownLatch ambosLeyeron = new CountDownLatch(2);
        ExecutorService executor = Executors.newFixedThreadPool(2);

        try {
            Future<String> modificacionA = executor.submit(() ->
                ejecutarModificacionConcurrente(ambosLeyeron));

            Future<String> modificacionB = executor.submit(() ->
                ejecutarModificacionConcurrente(ambosLeyeron));

            String resultadoA = modificacionA.get();
            String resultadoB = modificacionB.get();

            List<String> resultados = List.of(resultadoA, resultadoB);

            assertEquals(1, resultados.stream()
                .filter("OK"::equals)
                .count());

            assertEquals(1, resultados.stream()
                .filter("CONFLICTO"::equals)
                .count());

            Producto productoFinal = productoRepository
                .findById(productoId)
                .orElseThrow();

            assertEquals(0L, productoFinal.getStock());

        } finally {
            executor.shutdownNow();
        }
    }

    // Método auxiliar
    private String ejecutarModificacionConcurrente(
        CountDownLatch ambosLeyeron
    ) {
        try {
            transactionTemplate.executeWithoutResult(status -> {
                Producto producto = productoRepository
                    .findByIdAndActivoTrue(productoId)
                    .orElseThrow();

                assertEquals(5L, producto.getStock());

                ambosLeyeron.countDown();

                try {
                    if (!ambosLeyeron.await(10, TimeUnit.SECONDS)) {
                        throw new RuntimeException("Tiempo de espera agotado");
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException(e);
                }

                producto.descontarStock(5L);
                productoRepository.saveAndFlush(producto);
            });

            return "OK";

        } catch (ObjectOptimisticLockingFailureException ex) {
            return "CONFLICTO";
        }
    }
}
