package cl.sistemaventasrutas.config;
import cl.sistemaventasrutas.security.CustomUserDetailsService;

import java.nio.charset.StandardCharsets;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;


@Configuration
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers("/api/auth/**").permitAll()
                    .anyRequest().authenticated())
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(Customizer.withDefaults())
            );

        return http.build();

    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    AuthenticationProvider authenticationProvider(
        CustomUserDetailsService userDetailsService,
        PasswordEncoder passwordEncoder
    ){
        DaoAuthenticationProvider provider =
            new DaoAuthenticationProvider(userDetailsService);

            provider.setPasswordEncoder(passwordEncoder);

            return provider;
    }

    @Bean
    AuthenticationManager authenticationManager(
        AuthenticationConfiguration configuration
    ) throws Exception{
        return configuration.getAuthenticationManager();
    }

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Bean
    JwtDecoder jwtDecoder(){
        SecretKey key = new SecretKeySpec(
            jwtSecret.getBytes(StandardCharsets.UTF_8),
        "HmacSHA256");

        return NimbusJwtDecoder
            .withSecretKey(key)
            .build();
    }
}