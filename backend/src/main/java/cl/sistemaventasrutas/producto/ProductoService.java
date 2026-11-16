package cl.sistemaventasrutas.producto;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.sistemaventasrutas.movimiento.MovimientoStockService;
import cl.sistemaventasrutas.movimiento.TipoMovimientoStock;
import cl.sistemaventasrutas.shared.Mensajes;
import cl.sistemaventasrutas.shared.ResourceNotFoundException;

@Service
@Transactional
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final MovimientoStockService movimientoStockService;

    public ProductoService(
            ProductoRepository productoRepository,
            MovimientoStockService movimientoStockService) {

        this.productoRepository = productoRepository;
        this.movimientoStockService = movimientoStockService;
    }

    public ProductoResponse crear(
            ProductoRequest request) {

        if (productoRepository.existsByCodigoIgnoreCase(
                request.codigo())) {

            throw new IllegalArgumentException(
                    "Ya existe un producto con ese código");
        }

        Producto producto = new Producto(
                request.codigo(),
                request.nombre(),
                request.unidad(),
                request.precioCosto(),
                request.precioVenta(),
                request.stock());

        producto = productoRepository.save(producto);

        if (producto.getStock() > 0) {
            movimientoStockService.registrar(
                producto,
                null,
                TipoMovimientoStock.ENTRADA,
                producto.getStock(),
                "Stock inicial del producto"
            );
        }

        return ProductoResponse.from(producto);
    }

    @Transactional(readOnly = true)
    public List<ProductoResponse> listar() {

        return productoRepository
                .findAllByActivoTrueOrderByNombreAsc()
                .stream()
                .map(ProductoResponse::from)
                .toList();
    }

    public void eliminar(Long id) {

        Producto producto = productoRepository
                .findByIdAndActivoTrue(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(Mensajes.PRODUCTO_NO_ENCONTRADO
                                ));

        producto.desactivar();
    }


}