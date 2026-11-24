package es.iesjuanbosco.biblioteca.repository;

import es.iesjuanbosco.biblioteca.entity.Libro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LibroRepository extends JpaRepository<Libro, Long> {
    List<Libro> findByDisponible(boolean disponible);
    boolean existsByIsbn(String isbn);
}
