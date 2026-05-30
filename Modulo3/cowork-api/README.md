# CoworkLima API · Sistema de Reservas de Sala

**Examen Práctico Nivel 2 — Spring Boot & Web (Temas 2.1 a 2.5)**
**Instituto CodiGo · Powered by Tecsup**

---

## Datos del alumno

| Campo | Valor |
|-------|-------|
| Nombres y Apellidos | _(rellenar antes de entregar)_ |
| Curso / Sección | _(rellenar antes de entregar)_ |
| Repositorio GitHub | _(rellenar antes de entregar)_ |

---

## 1. Descripción del proyecto

API REST en Java + Spring Boot para gestionar **salas de reuniones** y **reservas** de la empresa **CoworkLima S.A.C.**. Esta es la versión 1.0.0, que cubre la base web del sistema. La persistencia se implementa **en memoria** (List + AtomicLong) tal como exige el examen; será reemplazada por JPA en el siguiente nivel sin tocar Controllers ni Services.

## 2. Tecnologías

- Java 17+ (probado en Java 21)
- Spring Boot 3.3.4
- Maven 3.6+
- Spring Web (único starter usado)

## 3. Cómo ejecutar

```bash
# Clonar
git clone <URL_DEL_REPO>
cd cowork-api

# Compilar y ejecutar
./mvnw spring-boot:run

# Verificar arranque
curl http://localhost:9090/api/info
```

La aplicación arranca en **http://localhost:9090** (puerto personalizado según R1).

Al iniciar, el log mostrará:
```
Tomcat started on port 9090 (http) with context path ''
Started CoworkApiApplication in 1.xxx seconds
```

El `SalaRepository` precarga **3 salas demo** mediante `@PostConstruct` (ciclo de vida del Bean, Tema 2.1).

## 4. Arquitectura

```
com.codigo.cowork
├── CoworkApiApplication.java       ← @SpringBootApplication
├── controller/                      ← Capa HTTP: recibe requests, delega
│   ├── InfoController.java
│   ├── SalaController.java
│   └── ReservaController.java
├── service/                         ← Reglas de negocio (Tema 2.2)
│   ├── SalaService.java
│   └── ReservaService.java
├── repository/                      ← Acceso a datos en memoria
│   ├── SalaRepository.java
│   └── ReservaRepository.java
├── model/                           ← Entidades internas (NO se exponen)
│   ├── Sala.java
│   └── Reserva.java
├── dto/                             ← Contratos públicos (records)
│   ├── SalaRequestDTO.java
│   ├── SalaResponseDTO.java
│   ├── ReservaRequestDTO.java
│   ├── ReservaResponseDTO.java
│   ├── ComprobanteResponseDTO.java
│   └── InfoResponseDTO.java
└── mapper/                          ← Conversión model ↔ DTO
    ├── SalaMapper.java
    └── ReservaMapper.java
```

### Responsabilidad de cada capa

| Capa | Responsabilidad | Lo que NO hace |
|------|------------------|----------------|
| **Controller** | Recibe HTTP, mapea rutas, valida status codes, delega al Service. | No contiene reglas de negocio ni accede al Repository. |
| **Service** | Aplica reglas de negocio, coordina Repository y Mapper, lanza excepciones de negocio. | No conoce HTTP, no convierte JSON. |
| **Repository** | Lee y escribe datos. Hoy en memoria, mañana JPA. | No tiene reglas de negocio. |
| **DTO** | Contrato público de entrada y salida. Records inmutables. | No contiene lógica. |
| **Mapper** | Convierte model ↔ DTO. Calcula campos derivados. | No usa Repository ni Service. |
| **Model** | Estructura interna del dominio. | NUNCA se devuelve directamente desde un Controller. |

## 5. Endpoints implementados

### Sistema
| Método | Ruta | Descripción |
|--------|------|-------------|
| GET | `/api/info` | Info de la app (nombre, versión, autor) |

### Salas — CRUD completo (R3)
| Método | Ruta | Status éxito | Cuerpo |
|--------|------|--------------|--------|
| GET | `/api/salas` | 200 | `List<SalaResponseDTO>` |
| GET | `/api/salas/{id}` | 200 / 404 | `SalaResponseDTO` |
| POST | `/api/salas` | 201 | `SalaResponseDTO` |
| PUT | `/api/salas/{id}` | 200 / 404 | `SalaResponseDTO` |
| DELETE | `/api/salas/{id}` | 204 / 404 | _(vacío)_ |

### Reservas — los 5 canales HTTP (R4)
| Método | Ruta | Canal HTTP usado |
|--------|------|------------------|
| POST | `/api/reservas` | `@RequestBody` |
| GET | `/api/reservas/{id}` | `@PathVariable` |
| GET | `/api/reservas?estado=&fecha=&salaId=` | `@RequestParam` (combinables) |
| GET | `/api/reservas/sala/{salaId}` | `@PathVariable` |
| PUT | `/api/reservas/{id}/estado?nuevoEstado=` | `@PathVariable` + `@RequestParam` |
| DELETE | `/api/reservas/{id}` | `@PathVariable` |
| POST | `/api/reservas/{id}/comprobante` | `@PathVariable` + `@RequestHeader` + `MultipartFile` |

## 6. Ejemplos de uso

### Crear una sala
```bash
curl -X POST http://localhost:9090/api/salas \
     -H "Content-Type: application/json" \
     -d '{
       "codigo": "SALA-D4",
       "nombre": "Sala Misti",
       "capacidad": 6,
       "ubicacion": "Piso 2"
     }'
```

Respuesta `201 Created`:
```json
{
  "id": 4,
  "codigo": "SALA-D4",
  "nombre": "Sala Misti",
  "capacidad": 6,
  "ubicacion": "Piso 2",
  "activa": true,
  "descripcionCorta": "SALA-D4 - Sala Misti (cap. 6)"
}
```

### Crear una reserva
```bash
curl -X POST http://localhost:9090/api/reservas \
     -H "Content-Type: application/json" \
     -d '{
       "salaId": 1,
       "responsable": "María López",
       "email": "maria@cowork.pe",
       "fecha": "2026-06-10",
       "horaInicio": "09:00",
       "horaFin": "10:30"
     }'
```

Respuesta `201 Created`:
```json
{
  "id": 1,
  "salaId": 1,
  "responsable": "María López",
  "email": "maria@cowork.pe",
  "fecha": "2026-06-10",
  "horaInicio": "09:00",
  "horaFin": "10:30",
  "estado": "PENDIENTE"
}
```

Observe que:
- `fecha` viene en formato `yyyy-MM-dd` (gracias a `@JsonFormat`).
- `horaInicio` y `horaFin` vienen en formato `HH:mm` (sin segundos).
- `estado` es siempre `"PENDIENTE"` al crear (regla R6.1).
- `passwordInterno` **no aparece**: queda excluido por diseño del DTO.

### Filtrar reservas combinando filtros
```bash
curl "http://localhost:9090/api/reservas?estado=CONFIRMADA&fecha=2026-06-10"
```

### Confirmar una reserva
```bash
curl -X PUT "http://localhost:9090/api/reservas/1/estado?nuevoEstado=CONFIRMADA"
```

### Subir un comprobante PDF
```bash
curl -X POST http://localhost:9090/api/reservas/1/comprobante \
     -H "X-Cliente-Id: 1001" \
     -F "file=@comprobante.pdf"
```

Respuesta:
```json
{
  "reservaId": 1,
  "clienteId": "1001",
  "nombreArchivo": "comprobante.pdf",
  "tamanoBytes": 24580,
  "mensaje": "Comprobante recibido correctamente"
}
```

## 7. Reglas de negocio implementadas (R6)

| Regla | Dónde está | Cómo se evidencia |
|-------|------------|-------------------|
| R6.1: Estado inicial siempre `PENDIENTE` | `ReservaService.crear()` | Aunque el cliente envíe otro estado, el Service lo sobrescribe. De hecho, ni siquiera está en `ReservaRequestDTO`. |
| R6.2: `activa = true` por defecto | `SalaMapper.toEntity()` | Si el JSON no envía el campo, se asume `true`. |
| R6.3: Validar estados permitidos | `ReservaService.cambiarEstado()` | Solo se aceptan `PENDIENTE`, `CONFIRMADA`, `CANCELADA`. Otro valor → `RuntimeException`. |
| R6.4: Eliminar reservas en cascada | `SalaService.eliminar()` | Antes de borrar la sala, se borran sus reservas. |
| R6.5: Filtros combinables (intersección) | `ReservaRepository.findByFiltros()` | Los filtros `estado`, `fecha` y `salaId` se aplican con AND lógico. |

## 8. Decisiones técnicas destacadas

1. **Inyección por constructor con `final`**: ningún `@Autowired` en campo. Esto facilita testing y deja claras las dependencias.
2. **Records para todos los DTOs**: inmutables y concisos (Tema 2.5).
3. **`passwordInterno` excluido por diseño del DTO**: no se usa `@JsonIgnore` sobre la entidad porque ensuciaría el modelo. Es más limpio simplemente **no incluir el campo en el DTO de respuesta** — separación total entre modelo interno y contrato público.
4. **`@PostConstruct` en `SalaRepository`** para precargar datos demo (Tema 2.1, ciclo de vida del Bean).
5. **Endpoints REST nombrados con sustantivos en plural**: `/api/salas`, `/api/reservas`. Nunca `/getSalas` o `/createReserva`.
6. **`ResponseEntity` solo donde aporta valor** (status 201, 204, 404). Para 200 OK simple se devuelve el DTO directamente, dejando que Spring infiera el status.

## 9. Lo que NO se implementó (por diseño)

Estos temas corresponden a los exámenes siguientes y se evitaron deliberadamente:

- **Validaciones declarativas** (`@Valid`, `@NotNull`, `@NotBlank`...) → Tema 2.6
- **Manejo global de excepciones** (`@ControllerAdvice`, `@ExceptionHandler`) → Tema 2.7
- **Swagger / OpenAPI** → Tema 2.8
- **Persistencia con JPA** → Nivel 3

Por eso, las validaciones de negocio se hacen manualmente en el Service y se lanzan `RuntimeException` puras. En el examen siguiente refactorizaré estos casos a excepciones personalizadas + handler global.

## 10. Estructura de entrega

```
cowork-api/
├── pom.xml
├── README.md                  ← este archivo
├── src/
│   ├── main/java/...          ← código fuente
│   └── main/resources/
│       └── application.properties
├── postman/
│   └── CoworkLima-API.postman_collection.json
└── docs/
    └── (capturas .png de ejecución)
```

---

**Autor:** _(rellenar)_ · **Fecha de entrega:** _(rellenar)_
