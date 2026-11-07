package cl.sistemaventasrutas.usuario;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;

import org.springframework.security.crypto.password.PasswordEncoder;

import cl.sistemaventasrutas.shared.ResourceNotFoundException;

import java.util.Optional;
import org.springframework.security.core.Authentication;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    //Crear objeto simulado
    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    //instanciar la clase a probar e inyectar mocks
    @InjectMocks
    private UsuarioService usuarioService;

    @Test
    void deberiaObtenerUsuarioPorId(){

        //Arrange
        Usuario usuario = new Usuario(
            "Matias",
            "12.345.678-9",
            "matias@gmail.com",
            "matias",
            "hash-password",
            Rol.USUARIO
        );

        when(usuarioRepository.findById(1L))
            .thenReturn(Optional.of(usuario));

        //Act
        UsuarioResponse response = usuarioService.obtenerPorId(1L);


        //Assert
        assertEquals("Matias", response.nombre());
        assertEquals("12.345.678-9", response.rut());
        assertEquals("matias@gmail.com", response.email());
        assertEquals("matias", response.username());
        assertEquals(Rol.USUARIO, response.rol());
        assertTrue(response.activo());

        verify(usuarioRepository).findById(1L);

    }

    @Test
    void deberiaCambiarPasswordCuandoLaPasswordActualesCorrecta(){

        //Arrange
        Usuario usuario = new Usuario(
            "Matias",
            "12.345.678-9",
            "matias@gmail.com",
            "matias",
            "hash-password",
            Rol.USUARIO
        );

        Authentication authentication = mock(Authentication.class);

        when(authentication.getName()).thenReturn("matias");

        when(usuarioRepository.findByUsername("matias"))
            .thenReturn(Optional.of(usuario));

        when(passwordEncoder.matches("ClaveActual123", "hash-password"))
            .thenReturn(true);

        when(passwordEncoder.encode("NuevaClave123"))
            .thenReturn("nuevo-hash");

        // Act
        UsuarioResponse response = usuarioService.cambiarPassword(
            authentication,
            "ClaveActual123",
            "NuevaClave123"
        );

        //Assert
        assertEquals("matias", response.username());

        verify(usuarioRepository).findByUsername("matias");

        verify(passwordEncoder).matches("ClaveActual123", "hash-password");

        verify(passwordEncoder).encode("NuevaClave123");

        assertEquals("nuevo-hash", usuario.getPasswordHash());
    }


    @Test
    void deberiaRechazarCambioCuandoLaPasswordActualesIncorrecta(){

        // Arrange
        Usuario usuario = new Usuario(
            "Matias",
            "12.345.678-9",
            "matias@gmail.com",
            "matias",
            "hash-password",
            Rol.USUARIO
        );

        Authentication authentication = mock(Authentication.class);

        when(authentication.getName()).thenReturn("matias");

        when(usuarioRepository.findByUsername("matias"))
            .thenReturn(Optional.of(usuario));

        when(passwordEncoder.matches("ClaveIncorrecta", "hash-password"))
            .thenReturn(false);

        //Act + Assert
        assertThrows(
            IllegalArgumentException.class,
        () -> usuarioService.cambiarPassword(
            authentication,
            "ClaveIncorrecta",
            "NuevaClave123")
        );

        //Verificar que no se genere un nuevo hash
        verify(passwordEncoder, never()).encode("NuevaClave123");

        //Verificar que contraseña original siga intacta
        assertEquals("hash-password", usuario.getPasswordHash());
    }

    @Test
    void deberiaRechazarCambioPasswordCuandoUsuarioNoExiste(){

        //Arrange
        Authentication authentication = mock(Authentication.class);

        when(authentication.getName()).thenReturn("usuarioInexistente");

        when(usuarioRepository.findByUsername("usuarioInexistente"))
            .thenReturn(Optional.empty());

        //Act + Assert
        assertThrows(
            ResourceNotFoundException.class,
            () -> usuarioService.cambiarPassword(
                authentication,
                "ClaveActual123",
                "NuevaClave123")
        );

        //Assert
        verify(passwordEncoder, never()).matches(
            "CLaveActual123l",
            "hash-password");

        verify(passwordEncoder, never()).encode("NuevaCLave123");
    }

}
