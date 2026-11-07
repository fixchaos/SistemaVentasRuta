package cl.sistemaventasrutas.auth;

import cl.sistemaventasrutas.usuario.Usuario;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "refresh_tokens")
public class RefreshToken {

    //@Id -> clave primaria / @GeneratedValue -> id automático
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "token_hash", nullable = false, unique = true, length = 64)
    private String tokenHash;

    @ManyToOne
    @JoinColumn(name= "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(name= "fecha_expiracion", nullable = false)
    private Instant fechaExpiracion;

    @Column(nullable = false)
    private boolean revocado;

    @Column(name = "creado_en", nullable = false)
    private Instant creadoEn;

    public RefreshToken(){

    }

    public RefreshToken(String tokenHash, Usuario usuario, Instant fechaExpiracion){
        this.tokenHash = tokenHash;
        this.usuario = usuario;
        this.fechaExpiracion = fechaExpiracion;
        this.revocado = false;
        this.creadoEn = Instant.now();
    }

    public void revocar(){
        this.revocado = true;
    }

    public boolean estaExpirado(){
        return Instant.now().isAfter(fechaExpiracion);
    }

    public Long getId(){
        return id;
    }

    public String getTokenHash(){
        return tokenHash;
    }

    public Usuario getUsuario(){
        return usuario;
    }

    public Instant getFechaExpiracion(){
        return fechaExpiracion;
    }

    public boolean isRevocado(){
        return revocado;
    }

    public Instant getCreadoEn(){
        return creadoEn;
    }
}
