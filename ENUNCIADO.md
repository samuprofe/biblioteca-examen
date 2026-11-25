# Examen práctico · API REST de préstamos de la biblioteca

**Módulo:** Programación de aplicaciones utilizando frameworks · 2.º DAM
**Duración:** 1 h 30 min
**Puntuación:** 7 puntos (los 3 restantes corresponden a la parte tipo test)
**Material:** se permiten apuntes, tareas y ejercicios. **No se permite usar IA ni Internet.**

## Contexto

El proyecto `biblioteca-base` es una API REST con Spring Boot que gestiona el catálogo de libros de la biblioteca del centro. Ya incluye:

- Registro (`POST /auth/register`) e inicio de sesión (`POST /auth/login`) con **JWT**, configurado igual que en el proyecto de Matrículas.
- En `SecurityConfig`, login y registro (`/auth/**`) son públicos y el resto de endpoints requieren estar autenticado.
- La entidad `Usuario`, con los roles guardados en un String (`ROLE_USER`, `ROLE_BIBLIOTECARIO`, `ROLE_ADMIN`).
- La entidad `Libro`, con su repositorio, servicio, DTO (`LibroRequest` y `LibroResponse`) y el CRUD completo en `/libros`.
- Dos excepciones que el `GlobalExceptionHandler` ya gestiona y que puedes lanzar desde tus servicios:
  - `RecursoNoEncontradoException` → responde **404** con `{"error": "mensaje"}`.
  - `ConflictoException` → responde **409** con `{"error": "mensaje"}`.
- Los errores de validación responden **400** con un mapa `campo: mensaje`.

## Puesta en marcha

1. Arranca MySQL. La base de datos `biblioteca` se crea sola (usuario `root`, contraseña `1234`).
2. Ejecuta `BibliotecaApplication`. Al arrancar con la base de datos vacía se crean estos usuarios y 5 libros:

| Email | Contraseña | Rol |
|---|---|---|
| `admin@biblioteca.es` | `1234` | `ROLE_ADMIN` |
| `bibliotecaria@biblioteca.es` | `1234` | `ROLE_BIBLIOTECARIO` |
| `ana@biblioteca.es` | `1234` | `ROLE_USER` |
| `luis@biblioteca.es` | `1234` | `ROLE_USER` |

3. Haz login con `POST /auth/login`.

> La clave de firma del JWT se genera al arrancar, así que **cada vez que reinicies la aplicación tienes que volver a hacer login**.

## Normas

- La aplicación y los endpoints que se piden **deben compilar y funcionar** para ser evaluados. No vale copiar y pegar código sin saber qué hace.
- Los endpoints de préstamos **reciben y devuelven DTO**, nunca entidades.
- Usa inyección de dependencias por constructor.
- El usuario de un préstamo se obtiene **de la autenticación** (`authentication.getName()` devuelve el email; `Authentication` se inyecta como parámetro en el método del controlador). Ejemplo:

  ```java
  @GetMapping("/saludo")
  public ResponseEntity<Map<String, String>> saludo(Authentication authentication) {
      // Spring rellena el parámetro con el usuario del token de la petición
      String email = authentication.getName();   // por ejemplo "ana@biblioteca.es"
      return ResponseEntity.ok(Map.of("mensaje", "Hola, " + email));
  }
  ```

  Si necesitas el usuario en el servicio, pásale el email como parámetro, por ejemplo `miServicio.hacerAlgo(request, authentication.getName())`, y búscalo allí con `usuarioRepository.findByEmail(email)`.

---

## Ejercicio 1 · Entidad `Prestamo` (1,5 puntos)

Crea la entidad `Prestamo` con estos atributos:

| Atributo | Tipo | Detalles |
|---|---|---|
| `id` | `Long` | Clave primaria autoincremental |
| `fechaPrestamo` | `LocalDate` | Se asigna **automáticamente** con la fecha actual al insertar el préstamo |
| `fechaDevolucionPrevista` | `LocalDate` | Obligatoria |
| `libro` | `Libro` | Libro prestado |
| `usuario` | `Usuario` | Usuario que se lleva el libro |

Relaciones:

- **Libro – Préstamo:** un libro puede tener muchos préstamos y cada préstamo es de un libro. La relación debe ser **bidireccional**. Al borrar un libro deben borrarse sus préstamos automáticamente.
- **Usuario – Préstamo:** un usuario puede tener muchos préstamos y cada préstamo es de un usuario. Esta relación debe ser **bidireccional**. Al borrar un usuario deben borrarse sus préstamos automáticamente.

## Ejercicio 2 · Repositorio (0,5 puntos)

Crea `PrestamoRepository` con los métodos de consulta que necesites para:

- Obtener los préstamos de un libro a partir del **libroId**.

## Ejercicio 3 · DTO (1 punto)

**`PrestamoRequest`** (entrada), **con validación y mensajes personalizados**:

| Campo | Tipo | Validación |
|---|---|---|
| `libroId` | `Long` | Obligatorio |
| `fechaDevolucionPrevista` | `LocalDate` | Obligatoria y **posterior a la fecha actual** |

**`PrestamoResponse`** (salida):

| Campo | Tipo |
|---|---|
| `id` | `Long` |
| `libroId` | `Long` |
| `tituloLibro` | `String` |
| `emailUsuario` | `String` |
| `fechaPrestamo` | `LocalDate` |
| `fechaDevolucionPrevista` | `LocalDate` |

## Ejercicio 4 · Servicio y controlador de préstamos (3 puntos)

Crea `PrestamoService` y `PrestamoController` con estos endpoints:

| Método y ruta | Recibe | Respuesta correcta | Errores |
|---|---|---|---|
| `GET /libros/{libroId}/prestamos` | — | **200** + lista de `PrestamoResponse` del libro | 404 el libro no existe |
| `POST /prestamos` | `PrestamoRequest` + usuario autenticado (`Authentication`) | **201** + `PrestamoResponse` | 400 `libroId` o `fechaDevolucionPrevista` nulos, o fecha no posterior a hoy · 404 el libro no existe · 409 el libro no está disponible |
| `DELETE /prestamos/{id}` | — | **204** sin cuerpo | 404 el préstamo no existe |

Reglas de negocio:

- Al **crear** un préstamo, el libro pasa a `disponible = false`.
- Al **borrar** un préstamo (el libro se ha devuelto), su libro vuelve a `disponible = true`.

Ejemplo de `POST /prestamos` (con el token de Ana):

```json
{
  "libroId": 1,
  "fechaDevolucionPrevista": "2026-12-10"
}
```

Respuesta **201 Created**:

```json
{
  "id": 1,
  "libroId": 1,
  "tituloLibro": "Don Quijote de la Mancha",
  "emailUsuario": "ana@biblioteca.es",
  "fechaPrestamo": "2026-11-25",
  "fechaDevolucionPrevista": "2026-12-10"
}
```

Si Luis intenta pedir después el mismo libro, la respuesta es **409 Conflict**:

```json
{ "error": "El libro no está disponible" }
```

**Todos los errores que no sean de validación deben devolver un JSON con el formato `{"error": "mensaje del error"}`.** Puedes hacerlo lanzando las excepciones que ya existen (`RecursoNoEncontradoException` y `ConflictoException`) con el mensaje correspondiente, o devolviendo directamente un JSON con ese mismo formato.

## Ejercicio 5 · Seguridad (1 punto)

Ya está configurado que `/auth/**` es público y que el resto de endpoints requieren estar autenticado. Añade estos permisos por rol. Puedes hacerlo en `SecurityConfig` o en los controladores.

| Operación | USER | BIBLIOTECARIO | ADMIN |
|---|:---:|:---:|:---:|
| Crear, modificar y borrar libros (`POST`, `PUT` y `DELETE` sobre `/libros/**`) | No | No | Sí |
| Ver el historial de un libro (`GET /libros/{libroId}/prestamos`) | No | Sí | Sí |
| Borrar un préstamo (`DELETE /prestamos/{id}`) | No | Sí | Sí |

El resto de endpoints (consultar libros, crear un préstamo…) siguen permitidos a cualquier usuario autenticado.

---

## Entrega

Sube a la tarea de Aulas el proyecto comprimido en `.zip`. **El nombre del archivo debe ser tu nombre y apellidos**, por ejemplo `AnaGarciaLopez.zip`.
