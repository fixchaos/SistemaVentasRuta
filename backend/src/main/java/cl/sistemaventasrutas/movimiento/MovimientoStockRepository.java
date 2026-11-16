package cl.sistemaventasrutas.movimiento;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MovimientoStockRepository
        extends JpaRepository<MovimientoStock, Long> {

    List<MovimientoStock> findByProductoIdOrderByFechaDesc(Long productoId);

    List<MovimientoStock> findByVentaId(Long ventaId);
}