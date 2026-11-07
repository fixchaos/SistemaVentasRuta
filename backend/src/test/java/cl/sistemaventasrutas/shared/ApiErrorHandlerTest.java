package cl.sistemaventasrutas.shared;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

class ApiErrorHandlerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void configurarMockMvc() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new ControladorPrueba())
                .setControllerAdvice(new ApiErrorHandler())
                .build();
    }

    @Test
    void deberiaResponderBadRequestCuandoExisteDesbordamientoAritmetico() throws Exception {

        mockMvc.perform(get("/test/desbordamiento"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje")
                        .value("El importe de la venta excede el rango permitido"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @RestController
    static class ControladorPrueba {

        @GetMapping("/test/desbordamiento")
        public void provocarDesbordamiento() {
            throw new ArithmeticException("Overflow");
        }
    }
}