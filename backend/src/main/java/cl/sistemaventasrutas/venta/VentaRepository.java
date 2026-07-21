package cl.sistemaventasrutas.venta;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface VentaRepository extends JpaRepository<Venta, Long> {
    Optional<Venta> findByUuid(UUID uuid);
}
