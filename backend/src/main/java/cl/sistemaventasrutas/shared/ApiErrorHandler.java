package cl.sistemaventasrutas.shared;

import java.time.Instant;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiErrorHandler {

    public record ErrorResponse(
            Instant timestamp,
            String mensaje) {
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleResourceNotFoundException(ResourceNotFoundException ex) {
        return new ErrorResponse(Instant.now(), ex.getMessage());
    }

    // Captura tanto IllegalArgumentException como IllegalStateException (Venta ya anulada)
    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBusinessExceptions(RuntimeException ex) {
        return new ErrorResponse(Instant.now(), ex.getMessage());
    }

    // Captura fallos de validación de los DTOs (@Valid)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(MethodArgumentNotValidException ex) {
        // Toma el mensaje del primer campo que falló en la validación
        String mensajeError = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .findFirst()
                .orElse("Error de validación en la petición");

        return new ErrorResponse(Instant.now(), mensajeError);
    }
}