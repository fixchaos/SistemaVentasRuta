package cl.sistemaventasrutas.producto;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ProductoService {

    private final ProductoRepository productoRepository;

    public ProductoService(
            ProductoRepository productoRepository) {

        this.productoRepository = productoRepository;
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
                request.precioCosto(),
                request.precioVenta(),
                request.stock());

        producto = productoRepository.save(producto);

        return toResponse(producto);
    }

    @Transactional(readOnly = true)
    public List<ProductoResponse> listar() {

        return productoRepository
                .findAllByActivoTrueOrderByNombreAsc()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public void eliminar(Long id) {

        Producto producto = productoRepository
                .findByIdAndActivoTrue(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Producto no encontrado"));

        producto.desactivar();
    }

    private ProductoResponse toResponse(
            Producto producto) {

        return new ProductoResponse(
                producto.getId(),
                producto.getCodigo(),
                producto.getNombre(),
                producto.getPrecioCosto(),
                producto.getPrecioVenta(),
                producto.getStock(),
                producto.isActivo());
    }
}