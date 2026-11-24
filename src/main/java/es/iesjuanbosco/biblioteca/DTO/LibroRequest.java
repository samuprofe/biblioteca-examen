package es.iesjuanbosco.biblioteca.DTO;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LibroRequest(
        @NotBlank(message = "El título es obligatorio")
        String titulo,

        @NotBlank(message = "El autor es obligatorio")
        String autor,

        @NotBlank(message = "El ISBN es obligatorio")
        @Size(min = 10, max = 13, message = "El ISBN debe tener entre 10 y 13 caracteres")
        String isbn,

        @Min(value = 1450, message = "El año de publicación no es válido")
        Integer anioPublicacion
) {
}
