package cl.sistemaventasrutas.usuario;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

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
}
