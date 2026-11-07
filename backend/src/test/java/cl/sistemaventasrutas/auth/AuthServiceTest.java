package cl.sistemaventasrutas.auth;

import java.util.Base64;
import java.util.Optional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import cl.sistemaventasrutas.security.JwtService;
import cl.sistemaventasrutas.usuario.Rol;
import cl.sistemaventasrutas.usuario.Usuario;
import cl.sistemaventasrutas.usuario.UsuarioRepository;

import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.BadCredentialsException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @InjectMocks
    private AuthService authService;

    @Test
    @DisplayName("Debería autenticar al usuario exitosamente y generar tokens de acceso y refresco")
    void deberiaAutenticarUsuarioyGenerarTokens() {

        // Arrange
        Usuario usuario = new Usuario(
            "Juan Pérez",
            "12345678-9",
            "juan@example.com",
            "Juan",
            "hash-de-password",
            Rol.USUARIO
        );

        LoginRequest request = new LoginRequest(
            "Juan",
            "password123"
        );

        when(usuarioRepository.findByUsername("Juan"))
            .thenReturn(Optional.of(usuario));

        when(jwtService.generateAccessToken(usuario))
            .thenReturn("access-token-prueba");

        when(authenticationManager.authenticate(
            any(UsernamePasswordAuthenticationToken.class)
        )).thenReturn(mock(Authentication.class));

        // Act
        LoginResponse response = authService.login(request);

        // Assert
        assertEquals(
            "access-token-prueba",
            response.accessToken()
        );

        assertNotNull(response.refreshToken());

        verify(authenticationManager)
            .authenticate(any(UsernamePasswordAuthenticationToken.class));

        verify(usuarioRepository)
            .findByUsername("Juan");

        verify(jwtService)
            .generateAccessToken(usuario);

        ArgumentCaptor<RefreshToken> refreshTokenCaptor =
            ArgumentCaptor.forClass(RefreshToken.class);

        verify(refreshTokenRepository)
            .save(refreshTokenCaptor.capture());

        RefreshToken refreshTokenGuardado =
            refreshTokenCaptor.getValue();

        assertNotEquals(
            response.refreshToken(),
            refreshTokenGuardado.getTokenHash()
        );
    }

    @Test
    @DisplayName("Debería rechazar credenciales inválidas")
    void deberiaRechazarCredencialesInvalidas(){
        when(authenticationManager.authenticate(
            any(UsernamePasswordAuthenticationToken.class)
        )).thenThrow(
            new BadCredentialsException("Credenciales inválidas")
        );

        LoginRequest request = new LoginRequest("Juan", "password-incorrecta");

        assertThrows(BadCredentialsException.class,
            () -> authService.login(request));
    }

    @Test
    @DisplayName("Debería almacenar el hash SHA-256 del refresh token")
    void deberiaAlmacenarHashSha256DelRefreshToken()
        throws NoSuchAlgorithmException{
        Usuario usuario = new Usuario(
            "Juan Pérez",
             "12345678-9",
             "juan@example.com",
             "juan",
             "hash-de-password",
             Rol.USUARIO);

        LoginRequest request = new LoginRequest("Juan", "password123");

        when(usuarioRepository.findByUsername("Juan"))
            .thenReturn(Optional.of(usuario));

        when(authenticationManager.authenticate(
            any(UsernamePasswordAuthenticationToken.class)
        )).thenReturn(mock(Authentication.class));

        LoginResponse response = authService.login(request);

        ArgumentCaptor<RefreshToken> refreshTokenCaptor =
            ArgumentCaptor.forClass(RefreshToken.class);

        verify(refreshTokenRepository)
            .save(refreshTokenCaptor.capture());

        RefreshToken refreshTokenGuardado = refreshTokenCaptor.getValue();

        String refreshToken = response.refreshToken();

        MessageDigest digest = MessageDigest.getInstance("SHA-256");

        byte[] hashBytes = digest.digest(
            refreshToken.getBytes(StandardCharsets.UTF_8)
        );

        String hashEsperado = Base64.getUrlEncoder()
            .withoutPadding()
            .encodeToString(hashBytes);

        assertEquals(hashEsperado, refreshTokenGuardado.getTokenHash());
    }
}