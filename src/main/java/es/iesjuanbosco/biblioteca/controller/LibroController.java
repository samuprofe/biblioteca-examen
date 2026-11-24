package es.iesjuanbosco.biblioteca.controller;

import es.iesjuanbosco.biblioteca.DTO.LibroRequest;
import es.iesjuanbosco.biblioteca.DTO.LibroResponse;
import es.iesjuanbosco.biblioteca.service.LibroService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/libros")
public class LibroController {
    private final LibroService libroService;

    public LibroController(LibroService libroService) {
        this.libroService = libroService;
    }

    // GET /libros  o  GET /libros?disponible=true
    @GetMapping
    public List<LibroResponse> findAll(@RequestParam(required = false) Boolean disponible) {
        return libroService.findAll(disponible);
    }

    @GetMapping("/{id}")
    public LibroResponse findById(@PathVariable Long id) {
        return libroService.findById(id);
    }

    @PostMapping
    public ResponseEntity<LibroResponse> create(@Valid @RequestBody LibroRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(libroService.create(request));
    }

    @PutMapping("/{id}")
    public LibroResponse update(@PathVariable Long id, @Valid @RequestBody LibroRequest request) {
        return libroService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        libroService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
