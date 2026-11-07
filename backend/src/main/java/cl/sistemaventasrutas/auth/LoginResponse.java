package cl.sistemaventasrutas.auth;

public record LoginResponse(
    String accessToken,
    String refreshToken
){}
