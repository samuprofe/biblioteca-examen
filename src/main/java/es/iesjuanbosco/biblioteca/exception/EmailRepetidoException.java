package es.iesjuanbosco.biblioteca.exception;

public class EmailRepetidoException extends RuntimeException {
    public EmailRepetidoException() {
        super("El email ya está registrado");
    }
}
