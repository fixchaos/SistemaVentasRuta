package cl.sistemaventasrutas.cliente;

import cl.sistemaventasrutas.ruta.Ruta;
import jakarta.persistence.*;

@Entity
@Table(name = "clientes")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String nombre;

    @Column(nullable = false, length = 30)
    private String telefono;

    @Column(nullable = false, length = 255)
    private String direccion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ruta_id", nullable = false)
    private Ruta ruta;

    @Column(nullable = false)
    private boolean activo = true;

    protected Cliente(){
    }

    public Cliente(
        String nombre,
        String telefono,
        String direccion,
        Ruta ruta){

            this.nombre = nombre;
            this.telefono = telefono;
            this.direccion = direccion;
            this.ruta = ruta;
        }

        public Long getId(){
            return id;
        }

        public String getNombre(){
            return nombre;
        }

        public String getTelefono(){
            return telefono;
        }

        public String getDireccion(){
            return direccion;
        }

        public Ruta getRuta(){
            return ruta;
        }

        public boolean isActivo(){
            return activo;
        }

        public void desactivar(){
            this.activo = false;
        }
}
