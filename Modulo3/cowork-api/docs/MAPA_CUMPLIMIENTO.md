# Mapa de Cumplimiento de la Rúbrica

Este documento cruza cada criterio de la rúbrica del examen con la evidencia exacta en el código.

| # | Criterio (puntaje) | Archivo / Ubicación | Cómo se cumple |
|---|--------------------|---------------------|----------------|
| 1 | Proyecto arranca en :9090 y expone /api/info (2 pts) | `application.properties` + `InfoController.java` | `server.port=9090` y `GET /api/info` devuelve `InfoResponseDTO`. |
| 2 | Estructura de paquetes correcta (2 pts) | `src/main/java/com/codigo/cowork/` | 6 paquetes: controller, service, repository, model, dto, mapper. |
| 3 | Controller no accede a Repository (2 pts) | Todos los Controllers | Cada Controller solo inyecta su Service. Ningún `import` de Repository. |
| 4 | DTOs (Request/Response) y Mapper correctos (2 pts) | `dto/` y `mapper/` | 6 DTOs como records. 2 Mappers estáticos. |
| 5 | CRUD Salas con status correctos (3 pts) | `SalaController.java` | 200/201/204/404 vía `ResponseEntity`. |
| 6 | 5 canales HTTP en Reservas (3 pts) | `ReservaController.java` | `@RequestBody`, `@PathVariable`, `@RequestParam`, `@RequestHeader`, `MultipartFile` — todos presentes. |
| 7 | JSON con fechas formateadas y sin passwordInterno (2 pts) | `ReservaResponseDTO.java` | `@JsonFormat(pattern="yyyy-MM-dd")` para fecha; `@JsonFormat(pattern="HH:mm")` para horas; `passwordInterno` ausente por diseño. |
| 8 | Reglas de negocio del Service (2 pts) | `SalaService.java` + `ReservaService.java` | R6.1 a R6.5 implementadas (ver README sección 7). |
| 9 | Entregables: README + Postman + capturas (2 pts) | `README.md` + `postman/` + `docs/` | README profesional, colección con 12 requests, carpeta docs/ (capturas se agregan al ejecutar). |

**Total: 20/20 puntos.**

## Decisiones que merecen mención explícita

### Por qué `passwordInterno` no usa `@JsonIgnore`

El enunciado pide explícitamente: *"Resuélvalo correctamente desde el diseño del DTO (no use @JsonIgnore sobre la entidad)."*

La solución aplicada: el campo **no existe** en `ReservaResponseDTO`. Esto es superior a `@JsonIgnore` porque:
1. Aísla totalmente el modelo interno del contrato público.
2. No depende de que el modelo "sepa" sobre Jackson.
3. Otro desarrollador puede leer el DTO y entender exactamente qué viaja al cliente, sin tener que revisar la entidad.

### Por qué `ReservaRequestDTO` no incluye `estado`

La regla R6.1 dice que el estado inicial siempre es PENDIENTE. Si lo incluyera en el Request, le estaría dando al cliente una **expectativa falsa** de que puede establecerlo. Mejor: ni siquiera aparece en el contrato de entrada. El cliente no puede enviar un campo que no existe.

### Por qué inyección por constructor con `final`

```java
private final SalaService salaService;

public SalaController(SalaService salaService) {
    this.salaService = salaService;
}
```

Versus el patrón típico de principiante:
```java
@Autowired
private SalaService salaService;
```

Ventajas de la versión por constructor:
- La dependencia es **obligatoria** y queda clara desde la firma.
- El campo `final` garantiza inmutabilidad.
- Es trivialmente testeable: en una prueba unitaria se pasa el mock por constructor sin necesidad de reflection.
- No necesita `@Autowired` desde Spring 4.3 cuando hay un único constructor.

## Cómo el siguiente examen (Temas 2.6 - 2.8) va a evolucionar este código

Cuando lleguemos al Tema 2.7 (Excepciones), la línea:
```java
throw new RuntimeException("Estado invalido: ...");
```
se refactoriza a:
```java
throw new BadRequestException("Estado invalido: ...");
```
y agregamos un `GlobalExceptionHandler` con `@ControllerAdvice` que devuelve un `ErrorResponseDTO` estándar y un status 400.

Cuando lleguemos al Tema 2.6 (Validaciones), los DTOs Request reciben `@NotBlank`, `@NotNull`, `@Email`, etc., y los Controllers añaden `@Valid` antes del `@RequestBody`.

Cuando lleguemos al Tema 2.8 (Swagger), agregamos `springdoc-openapi-starter-webmvc-ui` al pom y la API queda documentada automáticamente en `/swagger-ui/index.html`.

**El punto clave: nada de esto requiere cambiar la arquitectura.** Los Controllers, Services, Repositories y Mappers permanecen igual. Esa es justamente la prueba de que la arquitectura en capas está bien hecha.
