
package cl.sistemaventasrutas.venta;

import java.util.Objects;

import cl.sistemaventasrutas.producto.Producto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
    name = "detalles_venta",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_detalle_venta_producto",
            columnNames = {"venta_id", "producto_id"}
        )
    }
)
public class DetalleVenta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "venta_id", nullable = false)
    private Venta venta;

    @ManyToOne(optional = false)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @Column(nullable = false)
    private Long cantidad;

    @Column(name = "precio_unitario", nullable = false)
    private Long precioUnitario;

    @Column(nullable = false)
    private Long subtotal;

    protected DetalleVenta() {
    }

    public DetalleVenta(
            Venta venta,
            Producto producto,
            Long cantidad,
            Long precioUnitario) {

        this.venta = Objects.requireNonNull(venta);
        this.producto = Objects.requireNonNull(producto);

        if (cantidad == null || cantidad <= 0) {
            throw new IllegalArgumentException(
                "La cantidad debe ser mayor que cero"
            );
        }

        if (precioUnitario == null || precioUnitario < 0) {
            throw new IllegalArgumentException(
                "El precio unitario no puede ser negativo"
            );
        }

        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.subtotal = Math.multiplyExact(cantidad, precioUnitario);
    }

    public Long getId() {
        return id;
    }

    public Venta getVenta() {
        return venta;
    }

    public Producto getProducto() {
        return producto;
    }

    public Long getCantidad() {
        return cantidad;
    }

    public Long getPrecioUnitario() {
        return precioUnitario;
    }

    public Long getSubtotal() {
        return subtotal;
    }
}
