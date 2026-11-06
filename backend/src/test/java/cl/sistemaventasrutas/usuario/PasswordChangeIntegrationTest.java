package cl.sistemaventasrutas.usuario;

//JUnit
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;


//Spring
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

//Imports estáticos
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//Clases del proyecto
import cl.sistemaventasrutas.security.JwtService;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PasswordChangeIntegrationTest {

    //Dependencias
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    //Datos de prueba
    private Usuario usuario;

    //Preparacion
    @BeforeEach
    void prepararUsuario(){
        usuarioRepository.findByUsername("passwordTest")
            .ifPresent(usuarioRepository::delete);

        usuario = new Usuario(
            "Usuario Integracion",
            "22.222.222-2",
            "integration@test.cl",
            "passwordTest",
            passwordEncoder.encode("ClaveActual123"),
            Rol.USUARIO
        );

        usuario = usuarioRepository.save(usuario);
    }

    //Limpieza
    @AfterEach
    void limpiarUsuario(){
        if(usuario != null && usuario.getId() != null){
            usuarioRepository.deleteById(usuario.getId());
        }
    }

    //Método auxiliar
    private String generarToken(){
        return jwtService.generateAccessToken(usuario);
    }

    //Pruebas
    @Test
    void deberiaRechazarCambioPasswordSinAutenticacion() throws Exception {

        mockMvc.perform(
            patch("/api/usuarios/me/password")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                {
                    "passwordActual": "ClaveActual123",
                    "nuevaPassword": "NuevaClave123"
                }

                """)
            )
        .andExpect(status().isUnauthorized());
    }

    @Test
    void deberiaCambiarPasswordCuandoUsuarioEstaAutenticado() throws Exception{

        //Generar un token JWT para autenticar la petición
        String token = generarToken();

        mockMvc.perform(
            patch("/api/usuarios/me/password")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                {
                    "passwordActual": "ClaveActual123",
                    "nuevaPassword": "NuevaClave123"
                }
                """)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.username").value("passwordTest"))
            .andExpect(jsonPath("$.passwordHash").doesNotExist())
            .andExpect(jsonPath("$.password").doesNotExist());

            Usuario usuarioActualizado = usuarioRepository
                .findByUsername("passwordTest")
                .orElseThrow();

            assertTrue(
                passwordEncoder.matches(
                    "NuevaClave123",
                    usuarioActualizado.getPasswordHash()
                )
            );

            assertFalse(
                passwordEncoder.matches(
                    "ClaveActual123",
                    usuarioActualizado.getPasswordHash()
                )
            );
    }

    @Test
    void deberiaRechazarCambioCuandoPasswordActualEsIncorrecta() throws Exception{

        //Generar un token JWT para autenticar la petición
        String token = generarToken();

        //Intenta cambiar la contraseña utilizando una contraseña actual incorrecta
        mockMvc.perform(
            patch("/api/usuarios/me/password")
            .header("Authorization", "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                    "passwordActual": "ContraseñaIncorrecta123",
                    "nuevaPassword": "NuevaClave123"
                }

              """)
        )
        .andExpect(status().isBadRequest());

        //Recuperar usuario para comprobar si contraseña original sigue intacta
        Usuario usuarioActualizado = usuarioRepository
            .findByUsername("passwordTest")
            .orElseThrow();

        //Comprueba que la contraseña original sigue seiendo válida
        assertTrue(
            passwordEncoder.matches(
                "ClaveActual123",
                usuarioActualizado.getPasswordHash()
            )
        );
    }
}
