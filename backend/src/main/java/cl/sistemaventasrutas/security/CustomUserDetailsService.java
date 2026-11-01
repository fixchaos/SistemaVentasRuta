package cl.sistemaventasrutas.security;

import cl.sistemaventasrutas.usuario.Usuario;
import cl.sistemaventasrutas.usuario.UsuarioRepository;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService{

    private final UsuarioRepository usuarioRepository;

    public CustomUserDetailsService(UsuarioRepository usuarioRepository){
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username){
        Usuario usuario = usuarioRepository.findByUsername(username)
            .orElseThrow(()->
                new UsernameNotFoundException("Credenciales inválidas")
            );
        return org.springframework.security.core.userdetails.User
            .withUsername(usuario.getUsername())
            .password(usuario.getPasswordHash())
            .roles(usuario.getRol().name())
            .disabled(!usuario.isActivo())
            .build();
    }
}
