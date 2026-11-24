package es.iesjuanbosco.biblioteca.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor @AllArgsConstructor
public class UsuarioResponse {
    private String id;
    private String email;
    private String nombre;
    private String apellidos;
}
