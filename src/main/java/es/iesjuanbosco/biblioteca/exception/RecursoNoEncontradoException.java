package es.iesjuanbosco.biblioteca.exception;

// Se lanza cuando no existe el recurso solicitado. El GlobalExceptionHandler responde 404.
public class RecursoNoEncontradoException extends RuntimeException {
    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
