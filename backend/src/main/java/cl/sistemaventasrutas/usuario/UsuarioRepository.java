package cl.sistemaventasrutas.usuario;
import org.springframework.data.jpa.repository.*;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long>{
    Optional<Usuario> findByEmail(String email);
    boolean existsByRut(String rut);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
}
