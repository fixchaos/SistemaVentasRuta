
package cl.sistemaventasrutas.movimiento;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

import cl.sistemaventasrutas.producto.Producto;
import cl.sistemaventasrutas.venta.Venta;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "movimientos_stock")
public class MovimientoStock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, updatable = false)
    private UUID uuid;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venta_id")
    private Venta venta;

    @Column(nullable = false, updatable = false)
    private Instant fecha;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoMovimientoStock tipo;

    @Column(nullable = false)
    private Long cantidad;

    @Column(length = 500)
    private String observacion;

    protected MovimientoStock() {
    }

    public MovimientoStock(
            Producto producto,
            Venta venta,
            TipoMovimientoStock tipo,
            Long cantidad,
            String observacion) {

        this.producto = Objects.requireNonNull(producto);
        this.tipo = Objects.requireNonNull(tipo);

        if (cantidad == null || cantidad == 0) {
            throw new IllegalArgumentException(
                "La cantidad del movimiento no puede ser cero");
        }

        this.uuid = UUID.randomUUID();
        this.fecha = Instant.now();
        this.venta = venta;
        this.cantidad = cantidad;
        this.observacion = observacion;

        validarMovimiento();
    }

    private void validarMovimiento() {
        switch (tipo) {
            case VENTA -> {
                if (cantidad >= 0 || venta == null) {
                    throw new IllegalArgumentException(
                        "Una venta requiere cantidad negativa y una venta asociada");
                }
            }
            case ANULACION -> {
                if (cantidad <= 0 || venta == null) {
                    throw new IllegalArgumentException(
                        "Una anulación requiere cantidad positiva y una venta asociada");
                }
            }
            case ENTRADA -> {
                if (cantidad <= 0 || venta != null) {
                    throw new IllegalArgumentException(
                        "Una entrada requiere cantidad positiva y no debe asociarse a una venta");
                }
            }
            case AJUSTE, EDICION -> {
                if (venta != null) {
                    throw new IllegalArgumentException(
                        "Un ajuste o edición no debe asociarse a una venta");
                }
            }
        }
    }

    public Long getId() {
        return id;
    }

    public UUID getUuid() {
        return uuid;
    }

    public Producto getProducto() {
        return producto;
    }

    public Venta getVenta() {
        return venta;
    }

    public Instant getFecha() {
        return fecha;
    }

    public TipoMovimientoStock getTipo() {
        return tipo;
    }

    public Long getCantidad() {
        return cantidad;
    }

    public String getObservacion() {
        return observacion;
    }
}
