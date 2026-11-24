# Biblioteca · API REST con Spring Boot y JWT

Proyecto base del examen práctico. El enunciado está en [`ENUNCIADO.md`](ENUNCIADO.md).

## Requisitos

- JDK 21
- MySQL en `localhost:3306`, con el usuario `root` y la contraseña `1234`. La base de datos `biblioteca` se crea automáticamente.

## Ejecución

Abre la carpeta en IntelliJ (*File → Open*) y ejecuta `BibliotecaApplication`, o desde la terminal:

```bash
./mvnw spring-boot:run
```

Al arrancar con la base de datos vacía, `DataInitializer` crea cuatro usuarios de prueba (todos con la contraseña `1234`) y cinco libros.

## Endpoints disponibles

En el proyecto base, login y registro son públicos y el resto de endpoints requieren estar autenticado. Los permisos por rol se configuran en el examen (ver `ENUNCIADO.md`).

| Método | Ruta | Acceso | Descripción |
|---|---|---|---|
| POST | `/auth/register` | Público | Registro. Crea un usuario con `ROLE_USER` |
| POST | `/auth/login` | Público | Devuelve `{"token": "..."}` |
| GET | `/libros` | Autenticado | Lista de libros. Admite `?disponible=true\|false` |
| GET | `/libros/{id}` | Autenticado | Un libro |
| POST | `/libros` | Autenticado | Crea un libro (`LibroRequest`) |
| PUT | `/libros/{id}` | Autenticado | Modifica un libro (`LibroRequest`) |
| DELETE | `/libros/{id}` | Autenticado | Borra un libro |

`LibroRequest`:

```json
{
  "titulo": "El nombre de la rosa",
  "autor": "Umberto Eco",
  "isbn": "9788426403568",
  "anioPublicacion": 1980
}
```

## Estructura

- `config/`: `SecurityConfig` (Spring Security con JWT) y `DataInitializer` (datos de prueba).
- `controller/`: `AuthController` y `LibroController`.
- `DTO/`: DTO de entrada (`...Request`) y de salida (`...Response`).
- `entity/`: `Usuario` y `Libro`.
- `exception/`: excepciones propias y `GlobalExceptionHandler`.
- `repository/`: repositorios de Spring Data JPA.
- `service/`: lógica de negocio, `JwtService` y `CustomUserDetailsService`.
