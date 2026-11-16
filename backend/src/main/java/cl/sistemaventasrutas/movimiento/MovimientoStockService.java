
package cl.sistemaventasrutas.movimiento;

import org.springframework.stereotype.Service;

import cl.sistemaventasrutas.producto.Producto;
import cl.sistemaventasrutas.venta.Venta;

@Service
public class MovimientoStockService {

    private final MovimientoStockRepository movimientoStockRepository;

    public MovimientoStockService(
            MovimientoStockRepository movimientoStockRepository) {
        this.movimientoStockRepository = movimientoStockRepository;
    }

    public MovimientoStock registrar(
            Producto producto,
            Venta venta,
            TipoMovimientoStock tipo,
            Long cantidad,
            String observacion) {

        MovimientoStock movimiento = new MovimientoStock(
                producto,
                venta,
                tipo,
                cantidad,
                observacion
        );

        return movimientoStockRepository.save(movimiento);
    }
}
