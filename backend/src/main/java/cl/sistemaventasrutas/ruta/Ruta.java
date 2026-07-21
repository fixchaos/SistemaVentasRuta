package cl.sistemaventasrutas.ruta;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;


@Entity
@Table(name ="rutas")
public class Ruta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 120)
    private String nombre;

    @Column(nullable = false)
    private boolean activo = true;

    protected Ruta(){
    }

    public Ruta(String nombre){
        this.nombre = nombre;
    }

    public Long getId(){
        return id;
    }

    public String getNombre(){
        return nombre;
    }

    public boolean isActivo(){
        return activo;
    }

    public void cambiarNombre(String nombre){
        this.nombre = nombre;
    }

    public void desactivar(){
        this.activo = false;
    }


}
