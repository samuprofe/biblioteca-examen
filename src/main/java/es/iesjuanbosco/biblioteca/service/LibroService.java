package es.iesjuanbosco.biblioteca.service;

import es.iesjuanbosco.biblioteca.DTO.LibroRequest;
import es.iesjuanbosco.biblioteca.DTO.LibroResponse;
import es.iesjuanbosco.biblioteca.entity.Libro;
import es.iesjuanbosco.biblioteca.exception.ConflictoException;
import es.iesjuanbosco.biblioteca.exception.RecursoNoEncontradoException;
import es.iesjuanbosco.biblioteca.repository.LibroRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LibroService {
    private final LibroRepository libroRepository;

    public LibroService(LibroRepository libroRepository) {
        this.libroRepository = libroRepository;
    }

    // Si disponible es null devuelve todos; si no, filtra por disponibilidad
    public List<LibroResponse> findAll(Boolean disponible) {
        List<Libro> libros = (disponible == null)
                ? libroRepository.findAll()
                : libroRepository.findByDisponible(disponible);
        return libros.stream().map(this::toResponse).toList();
    }

    public LibroResponse findById(Long id) {
        return toResponse(buscarLibro(id));
    }

    public LibroResponse create(LibroRequest request) {
        if (libroRepository.existsByIsbn(request.isbn())) {
            throw new ConflictoException("Ya existe un libro con el ISBN " + request.isbn());
        }
        Libro libro = Libro.builder()
                .titulo(request.titulo())
                .autor(request.autor())
                .isbn(request.isbn())
                .anioPublicacion(request.anioPublicacion())
                .disponible(true)   // Un libro nuevo siempre está disponible
                .build();
        return toResponse(libroRepository.save(libro));
    }

    public LibroResponse update(Long id, LibroRequest request) {
        Libro libro = buscarLibro(id);
        if (!libro.getIsbn().equals(request.isbn()) && libroRepository.existsByIsbn(request.isbn())) {
            throw new ConflictoException("Ya existe un libro con el ISBN " + request.isbn());
        }
        libro.setTitulo(request.titulo());
        libro.setAutor(request.autor());
        libro.setIsbn(request.isbn());
        libro.setAnioPublicacion(request.anioPublicacion());
        return toResponse(libroRepository.save(libro));
    }

    public void deleteById(Long id) {
        Libro libro = buscarLibro(id);
        libroRepository.delete(libro);
    }

    private Libro buscarLibro(Long id) {
        return libroRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el libro con id " + id));
    }

    private LibroResponse toResponse(Libro libro) {
        return LibroResponse.builder()
                .id(libro.getId())
                .titulo(libro.getTitulo())
                .autor(libro.getAutor())
                .isbn(libro.getIsbn())
                .anioPublicacion(libro.getAnioPublicacion())
                .disponible(libro.isDisponible())
                .build();
    }
}
