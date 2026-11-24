package es.iesjuanbosco.biblioteca.config;

import es.iesjuanbosco.biblioteca.entity.Libro;
import es.iesjuanbosco.biblioteca.entity.Usuario;
import es.iesjuanbosco.biblioteca.repository.LibroRepository;
import es.iesjuanbosco.biblioteca.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

// Crea usuarios y libros de prueba al arrancar si la base de datos está vacía.
// Todos los usuarios tienen la contraseña 1234.
@Component
public class DataInitializer implements CommandLineRunner {
    private final UsuarioRepository usuarioRepository;
    private final LibroRepository libroRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UsuarioRepository usuarioRepository, LibroRepository libroRepository,
                           PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.libroRepository = libroRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (usuarioRepository.count() == 0) {
            crearUsuario("admin@biblioteca.es", "Admin", "ROLE_ADMIN");
            crearUsuario("bibliotecaria@biblioteca.es", "Marta", "ROLE_BIBLIOTECARIO");
            crearUsuario("ana@biblioteca.es", "Ana", "ROLE_USER");
            crearUsuario("luis@biblioteca.es", "Luis", "ROLE_USER");
        }

        if (libroRepository.count() == 0) {
            crearLibro("Don Quijote de la Mancha", "Miguel de Cervantes", "9788420412146", 1605);
            crearLibro("Cien años de soledad", "Gabriel García Márquez", "9788497592208", 1967);
            crearLibro("La sombra del viento", "Carlos Ruiz Zafón", "9788408163435", 2001);
            crearLibro("Clean Code", "Robert C. Martin", "9780132350884", 2008);
            crearLibro("Spring Boot in Action", "Craig Walls", "9781617292545", 2015);
        }
    }

    private void crearUsuario(String email, String nombre, String roles) {
        usuarioRepository.save(Usuario.builder()
                .email(email)
                .nombre(nombre)
                .password(passwordEncoder.encode("1234"))
                .roles(roles)
                .activo(true)
                .build());
    }

    private void crearLibro(String titulo, String autor, String isbn, Integer anio) {
        libroRepository.save(Libro.builder()
                .titulo(titulo)
                .autor(autor)
                .isbn(isbn)
                .anioPublicacion(anio)
                .disponible(true)
                .build());
    }
}
