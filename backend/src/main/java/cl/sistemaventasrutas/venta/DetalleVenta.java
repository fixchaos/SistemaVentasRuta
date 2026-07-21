package cl.sistemaventasrutas.venta;

import cl.sistemaventasrutas.producto.Producto;
import jakarta.persistence.*;

@Entity
@Table(name = "detalles_venta")
public class DetalleVenta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "venta_id")
    private Venta venta;

    @ManyToOne(optional = false)
    @JoinColumn(name = "producto_id")
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
            Long precioUnitario
            ) {

        this.venta = venta;
        this.producto = producto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.subtotal = cantidad * precioUnitario;
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


