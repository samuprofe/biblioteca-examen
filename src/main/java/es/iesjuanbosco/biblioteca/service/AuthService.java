package es.iesjuanbosco.biblioteca.service;

import es.iesjuanbosco.biblioteca.DTO.UsuarioRequest;
import es.iesjuanbosco.biblioteca.DTO.UsuarioResponse;
import es.iesjuanbosco.biblioteca.entity.Usuario;
import es.iesjuanbosco.biblioteca.exception.EmailRepetidoException;
import es.iesjuanbosco.biblioteca.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UsuarioResponse register(UsuarioRequest request) {
        if (usuarioRepository.existsByEmail(request.email())) {
            throw new EmailRepetidoException();
        }

        Usuario usuario = Usuario.builder()
                .email(request.email())
                .nombre(request.nombre())
                .apellidos(request.apellidos())
                .password(passwordEncoder.encode(request.password()))
                .roles("ROLE_USER") // El registro público siempre crea usuarios con ROLE_USER
                .activo(true)
                .build();
        usuarioRepository.save(usuario);

        return UsuarioResponse.builder()
                .id(usuario.getId())
                .email(usuario.getEmail())
                .nombre(usuario.getNombre())
                .apellidos(usuario.getApellidos())
                .build();
    }
}
