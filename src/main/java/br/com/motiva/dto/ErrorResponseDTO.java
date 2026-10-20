package br.com.motiva.dto;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Formato padronizado de erro para todas as respostas HTTP 4xx e 5xx da API.
 */
public record ErrorResponseDTO(
        LocalDateTime timestamp,
        int status,
        String error,
        String message,
        String path,
        Map<String, String> fieldErrors
) {
    public static ErrorResponseDTO simples(int status, String error, String message, String path) {
        return new ErrorResponseDTO(LocalDateTime.now(), status, error, message, path, null);
    }

    public static ErrorResponseDTO comCampos(int status, String error, String message, String path, Map<String, String> fieldErrors) {
        return new ErrorResponseDTO(LocalDateTime.now(), status, error, message, path, fieldErrors);
    }
}
