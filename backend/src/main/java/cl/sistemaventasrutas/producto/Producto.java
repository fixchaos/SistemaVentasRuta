package cl.sistemaventasrutas.producto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "productos")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 80)
    private String codigo;

    @Column(nullable = false, length = 160)
    private String nombre;


    @Column(nullable = false)
    private Long precioCosto;

    @Column(nullable = false)
    private Long precioVenta;

    @Column(nullable = false)
    private Long stock;

    @Column(nullable = false)
    private boolean activo = true;

    protected Producto() {
    }

    public Producto(
            String codigo,
            String nombre,
            Long precioCosto,
            Long precioVenta,
            Long stock) {

        this.codigo = codigo;
        this.nombre = nombre;
        this.precioCosto = precioCosto;
        this.precioVenta = precioVenta;
        this.stock = stock;
    }

    public Long getId() {
        return id;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNombre() {
        return nombre;
    }


    public Long getPrecioCosto() {
        return precioCosto;
    }

    public Long getPrecioVenta() {
        return precioVenta;
    }

    public Long getStock() {
        return stock;
    }

    public boolean isActivo() {
        return activo;
    }

    public void desactivar() {
        this.activo = false;
    }

    public void descontarStock(long cantidadVendida) {
        this.stock -= cantidadVendida;
    }

    public void aumentarStock(long cantidad) {
        this.stock += cantidad;
    }
}