package br.com.motiva.exception;

/**
 * Excecao lancada quando uma regra de negocio e violada (validacoes do Service).
 * Mapeada para HTTP 400 (Bad Request).
 */
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }
}
