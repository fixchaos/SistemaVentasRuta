package cl.sistemaventasrutas.venta;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface VentaRepository extends JpaRepository<Venta, Long> {
    Optional<Venta> findByUuid(UUID uuid);

    List<Venta> findByFechaGreaterThanEqualAndFechaLessThan(
        Instant inicio,
        Instant fin
    );

    List<Venta> findByEstadoVenta(
        EstadoVenta estadoVenta
    );

    List<Venta> findByFechaGreaterThanEqualAndFechaLessThanAndEstadoVenta(
        Instant inicio,
        Instant fin,
        EstadoVenta estadoVenta
    );
}
