package cl.sistemaventasrutas.producto;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.transaction.support.TransactionTemplate;

@SpringBootTest
@ActiveProfiles("test")
class ProductoConcurrencyTest {

    private final ProductoRepository productoRepository;
    private final TransactionTemplate transactionTemplate;

    @Autowired
    ProductoConcurrencyTest(
        ProductoRepository productoRepository,
        TransactionTemplate transactionTemplate){

            this.productoRepository = productoRepository;
            this.transactionTemplate = transactionTemplate;
        }

        private Long productoId;

        @BeforeEach
        void prepararProducto(){
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

        @AfterEach
        void limpiar(){
            if(productoId != null){
                productoRepository.deleteById(productoId);
            }
        }

        @Test
        void deberiaRechazarUnaModificacionConcurrentePorVersion() throws Exception{
            CountDownLatch ambosLeyeron = new CountDownLatch(2);
            ExecutorService executor = Executors.newFixedThreadPool(2);

            try {
                Future<String> modificacionA = executor.submit(()->
                    ejecutarModificacionConcurrente(ambosLeyeron));

                Future<String> modificacionB = executor.submit(()->
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
            }finally{
                executor.shutdownNow();
            }
        }

        private String ejecutarModificacionConcurrente(
            CountDownLatch ambosLeyeron){
                try{
                    transactionTemplate.executeWithoutResult(status ->{
                        Producto producto = productoRepository
                            .findByIdAndActivoTrue(productoId)
                            .orElseThrow();

                        assertEquals(5L, producto.getStock());

                        ambosLeyeron.countDown();

                        try{
                            ambosLeyeron.await();
                        }catch(InterruptedException e){
                            Thread.currentThread().interrupt();
                            throw new RuntimeException(e);
                        }

                        producto.descontarStock(5l);
                        productoRepository.saveAndFlush(producto);
                    });

                    return "OK";
                } catch(ObjectOptimisticLockingFailureException ex){
                    return "CONFLICTO";
                }
            }


}