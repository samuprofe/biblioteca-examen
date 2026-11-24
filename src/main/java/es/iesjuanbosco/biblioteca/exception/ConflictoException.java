package es.iesjuanbosco.biblioteca.exception;

// Se lanza cuando la operación no se puede realizar por el estado actual de los datos
// (ISBN repetido, libro no disponible...). El GlobalExceptionHandler responde 409.
public class ConflictoException extends RuntimeException {
    public ConflictoException(String mensaje) {
        super(mensaje);
    }
}
