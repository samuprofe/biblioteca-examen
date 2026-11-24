package es.iesjuanbosco.biblioteca.config;

import es.iesjuanbosco.biblioteca.service.JwtService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity  // Permite usar @PreAuthorize en controladores
public class SecurityConfig {

    private final JwtService jwtService;

    public SecurityConfig(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                // Deshabilitar CSRF (no necesario en APIs REST con JWT)
                .csrf(csrf -> csrf.disable())

                // Qué rutas son públicas y cuáles requieren autenticación o un rol
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**").permitAll()  // login y registro son públicos
                        .anyRequest().authenticated()              // El resto requiere estar autenticado
                )

                // Validación automática de tokens JWT
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                .decoder(jwtDecoder())                                   // Cómo validar el token
                                .jwtAuthenticationConverter(jwtAuthenticationConverter()) // Cómo extraer roles
                        )
                )

                // Sin sesiones (stateless): cada petición debe llevar su token
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );

        return http.build();
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        // Valida la firma con la clave secreta
        NimbusJwtDecoder decoder = NimbusJwtDecoder.withSecretKey(jwtService.getSecretKey()).build();
        // Valida además la expiración y que el token lo haya emitido nuestra API
        decoder.setJwtValidator(JwtValidators.createDefaultWithIssuer("App-Biblioteca"));
        return decoder;
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        // Configura cómo extraer los roles del token
        JwtGrantedAuthoritiesConverter authoritiesConverter = new JwtGrantedAuthoritiesConverter();
        authoritiesConverter.setAuthoritiesClaimName("roles");  // Buscar en el claim "roles"
        authoritiesConverter.setAuthorityPrefix("");             // Sin prefijo: los roles ya traen "ROLE_"

        JwtAuthenticationConverter jwtConverter = new JwtAuthenticationConverter();
        jwtConverter.setJwtGrantedAuthoritiesConverter(authoritiesConverter);
        return jwtConverter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // BCrypt para cifrar contraseñas en la base de datos
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        // Necesario para validar email/password en el login
        return authConfig.getAuthenticationManager();
    }
}
