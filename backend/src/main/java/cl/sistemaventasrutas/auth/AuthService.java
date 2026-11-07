package cl.sistemaventasrutas.auth;

import cl.sistemaventasrutas.security.JwtService;
import cl.sistemaventasrutas.usuario.Usuario;
import cl.sistemaventasrutas.usuario.UsuarioRepository;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RefreshTokenRepository refreshTokenRepository;

    public AuthService(
        UsuarioRepository usuarioRepository,
        AuthenticationManager authenticationManager,
        JwtService jwtService,
        RefreshTokenRepository refreshTokenRepository
    ) {
        this.usuarioRepository = usuarioRepository;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public LoginResponse login(LoginRequest request){

        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.username(), request.password())
        );

        Usuario usuario = usuarioRepository.findByUsername(request.username())
            .orElseThrow(()-> new IllegalStateException("Credenciales inválidas"));

        String accessToken = jwtService.generateAccessToken(usuario);

        String refreshToken = generarRefreshToken();

        RefreshToken refreshTokenEntity =
        crearRefreshToken(usuario, refreshToken);

        refreshTokenRepository.save(refreshTokenEntity);

        return new LoginResponse(
            accessToken,
            refreshToken
        );
    }

    private String generarRefreshToken() {
    byte[] bytes = new byte[32];
    new SecureRandom().nextBytes(bytes);
    return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String hashRefreshToken(String refreshToken) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(
                    refreshToken.getBytes(StandardCharsets.UTF_8)
            );

            return Base64.getUrlEncoder()
                    .withoutPadding()
                    .encodeToString(hash);

        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("No se pudo generar el hash del refresh token", e);
        }
    }

    private RefreshToken crearRefreshToken(Usuario usuario, String token) {
        String tokenHash = hashRefreshToken(token);

        return new RefreshToken(
                tokenHash,
                usuario,
                Instant.now().plus(30, ChronoUnit.DAYS)
        );
    }
}
