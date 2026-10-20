package br.com.motiva.exception;

import br.com.motiva.dto.ErrorResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * Tratamento global de erros da API MOTIVA (@RestControllerAdvice).
 * Intercepta excecoes e padroniza a resposta JSON com codigos HTTP adequados.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Trata recursos nao encontrados (404 Not Found).
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleResourceNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
        ErrorResponseDTO erro = ErrorResponseDTO.simples(
                HttpStatus.NOT_FOUND.value(),
                "Recurso Nao Encontrado",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(erro);
    }

    /**
     * Trata falhas de validacao do Bean Validation (@Valid) -> 400 Bad Request com mapa de campos.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleValidationErrors(MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, String> fieldErrors = new HashMap<>();
        for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.put(fe.getField(), fe.getDefaultMessage());
        }

        ErrorResponseDTO erro = ErrorResponseDTO.comCampos(
                HttpStatus.BAD_REQUEST.value(),
                "Erro de Validacao nos Campos",
                "Um ou mais campos contem valores invalidos.",
                request.getRequestURI(),
                fieldErrors
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
    }

    /**
     * Trata violacoes de regras de negocio (400 Bad Request).
     */
    @ExceptionHandler({BusinessException.class, IllegalArgumentException.class})
    public ResponseEntity<ErrorResponseDTO> handleBusinessException(RuntimeException ex, HttpServletRequest request) {
        ErrorResponseDTO erro = ErrorResponseDTO.simples(
                HttpStatus.BAD_REQUEST.value(),
                "Violacao de Regra de Negocio",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
    }

    /**
     * Trata erros genericos inesperados (500 Internal Server Error).
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleGeneralException(Exception ex, HttpServletRequest request) {
        ErrorResponseDTO erro = ErrorResponseDTO.simples(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Erro Interno do Servidor",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(erro);
    }
}
