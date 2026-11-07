package cl.sistemaventasrutas.venta;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

import cl.sistemaventasrutas.cliente.Cliente;


@Entity
@Table(name="ventas")
public class Venta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;

    private UUID uuid;

    private Instant fecha;

    @ManyToOne(optional = false)
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @Enumerated(EnumType.STRING)
    private EstadoPago estadoPago;

    @Enumerated(EnumType.STRING)
    private EstadoVenta estadoVenta;


    private Long neto;

    private BigDecimal tasaIva;

    private Long iva;

    private Long total;

    @Version
    private Integer version;

    @OneToMany(
        mappedBy = "venta",
        cascade = CascadeType.ALL,
        orphanRemoval = true)
        private List<DetalleVenta> detalles = new ArrayList<>();

        protected Venta(){

        }

        public Venta (Cliente cliente, EstadoPago estadoPago){
            this.cliente = cliente;
            this.fecha = Instant.now();
            this.uuid = UUID.randomUUID();
            this.estadoPago = estadoPago;
            this.estadoVenta = EstadoVenta.CONFIRMADA;
            this.tasaIva = new BigDecimal("0.19");

        }

        public void agregarDetalle(DetalleVenta detalle) {
            detalles.add(detalle);
        }

        public void calcularTotales() {

            //El total corresponde a la suma de los precios con IVA incluido.
            long total = 0;

            for(DetalleVenta detalle : detalles){
                total += detalle.getSubtotal();
            }

            this.total = total;

            //Separar IVA del precio que ya lo incluye
            BigDecimal divisor = BigDecimal.ONE.add(this.tasaIva);

            BigDecimal netoCalculado = BigDecimal.valueOf(this.total)
                .divide(divisor, 0, RoundingMode.HALF_UP);

            this.neto = netoCalculado.longValueExact();

            //Calcular iva
            this.iva = this.total - this.neto;
        }

        public Long getId() {
            return id;
        }

        public UUID getUuid() {
            return uuid;
        }

        public Cliente getCliente() {
            return cliente;
        }

        public Instant getFecha() {
            return fecha;
        }

        public Long getNeto() {
            return neto;
        }

        public Long getIva() {
            return iva;
        }

        public Long getTotal() {
            return total;
        }

        public EstadoPago getEstadoPago() {
            return estadoPago;
        }

        public EstadoVenta getEstadoVenta() {
            return estadoVenta;
        }
        public List<DetalleVenta> getDetalles() {
            return detalles;
        }

        public void anular() {
            if (this.estadoVenta == EstadoVenta.ANULADA) {
            throw new IllegalStateException("La venta ya está anulada");
            }
            this.estadoVenta = EstadoVenta.ANULADA;
        }
    }
