package cl.sistemaventasrutas.security;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import cl.sistemaventasrutas.usuario.Rol;
import cl.sistemaventasrutas.usuario.Usuario;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class SecurityAuthorizationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    @Test void deberiaRechazarUsuarioNoAdmin() throws Exception{

        Usuario usuario = new Usuario(
            "Usuario test",
            "22.222.222-2",
            "usuario@test.cl",
            "usuariotest",
            "hash-password",
            Rol.USUARIO
        );

        String token = jwtService.generateAccessToken(usuario);

        mockMvc.perform(
            get("/api/usuarios")
                .header("Authorization", "Bearer " + token)
        )
        .andExpect(status().isForbidden());
    }
}
