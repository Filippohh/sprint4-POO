package br.com.motiva.exception;

/**
 * Excecao lancada quando um recurso solicitado por ID nao e encontrado no banco de dados.
 * Mapeada para HTTP 404 (Not Found).
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String recurso, Long id) {
        super(String.format("%s com ID %d nao foi encontrado.", recurso, id));
    }
}
