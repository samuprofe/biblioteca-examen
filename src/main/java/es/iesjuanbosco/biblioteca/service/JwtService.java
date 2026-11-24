package es.iesjuanbosco.biblioteca.service;

import io.jsonwebtoken.Jwts;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;

@Service
public class JwtService {
    // Duración del token: 24 horas
    private static final long DURACION_TOKEN_MS = 24 * 60 * 60 * 1000;

    private final SecretKey secretKey;

    public JwtService() {
        // Genera automáticamente una clave secreta segura de 256 bits para HS256.
        // Al reiniciar la aplicación se genera otra, así que los tokens anteriores dejan de valer.
        this.secretKey = Jwts.SIG.HS256.key().build();
    }

    // Genera un token JWT para un usuario autenticado
    public String generateToken(Authentication authentication) {
        // Roles del usuario como lista: ["ROLE_USER", "ROLE_ADMIN"]
        // Filtramos por ROLE_ porque Spring Security también añade "FACTOR_PASSWORD" al hacer login
        List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(authority -> authority.startsWith("ROLE_"))
                .toList();

        return Jwts.builder()
                .subject(authentication.getName())  // Email del usuario
                .issuer("App-Biblioteca")           // Quién emite el token
                .issuedAt(new Date())               // Cuándo se creó
                .expiration(new Date(System.currentTimeMillis() + DURACION_TOKEN_MS))
                .claim("roles", roles)              // Roles del usuario
                .signWith(secretKey)                // Firmar con clave secreta
                .compact();
    }

    public SecretKey getSecretKey() {
        return secretKey;
    }
}
