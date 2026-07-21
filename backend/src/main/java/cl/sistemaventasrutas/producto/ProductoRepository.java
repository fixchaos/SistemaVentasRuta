package cl.sistemaventasrutas.producto;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductoRepository
        extends JpaRepository<Producto, Long> {

    List<Producto> findAllByActivoTrueOrderByNombreAsc();

    Optional<Producto> findByIdAndActivoTrue(Long id);

    boolean existsByCodigoIgnoreCase(String codigo);
}