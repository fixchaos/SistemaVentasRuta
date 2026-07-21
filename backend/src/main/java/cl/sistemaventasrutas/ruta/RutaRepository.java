package cl.sistemaventasrutas.ruta;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RutaRepository extends JpaRepository<Ruta, Long> {

    List<Ruta> findAllByActivoTrueOrderByNombreAsc();

    Optional<Ruta> findByIdAndActivoTrue(Long id);

    boolean existsByNombreIgnoreCase(String nombre);
}