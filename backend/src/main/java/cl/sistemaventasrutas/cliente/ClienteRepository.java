package cl.sistemaventasrutas.cliente;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    List<Cliente> findAllByActivoTrueOrderByNombreAsc();
    Optional<Cliente> findByIdAndActivoTrue(Long id);

    boolean existsByTelefono(String telefono);
    boolean existsByTelefonoAndIdNot(String telefono, Long id);
    boolean existsByDireccion(String direccion);
    boolean existsByDireccionAndIdNot(String direccion, Long id);
}
