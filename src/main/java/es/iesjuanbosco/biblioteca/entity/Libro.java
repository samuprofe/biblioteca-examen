package es.iesjuanbosco.biblioteca.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
@Entity
public class Libro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titulo;

    @Column(nullable = false)
    private String autor;

    @Column(unique = true, nullable = false, length = 13)
    private String isbn;

    private Integer anioPublicacion;

    // true si el libro está en la biblioteca y se puede prestar
    private boolean disponible;
}
