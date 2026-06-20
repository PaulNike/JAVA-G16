# reniec-feign

Consulta de DNI con **cache en 3 niveles** (Redis -> PostgreSQL -> API RENIEC),
consumiendo el API externo con **Spring Cloud OpenFeign**.

Proyecto didactico para el curso de Spring Boot backend.

## Stack
- Spring Boot 3.5.15
- Java 21
- Spring Cloud 2025.0.0 (OpenFeign)
- PostgreSQL + Redis
- Lombok, Maven

## El flujo
1. **Redis** (cache en memoria) -> si esta, responde al toque.
2. **PostgreSQL** -> si esta, responde y sube el dato al cache.
3. **API RENIEC** (decolecta) -> consulta externa; al obtener el dato lo
   guarda en BD y en cache para las proximas consultas.

## Antes de arrancar
1. Levanta Redis y Postgres:
   ```bash
   docker compose up -d
   ```
2. Pon tu token de decolecta en `src/main/resources/application.properties`:
   ```properties
   reniec.api.token=PON_AQUI_TU_TOKEN
   ```

## Compilar y ejecutar
```bash
mvn clean package        # compila y empaqueta
mvn spring-boot:run      # levanta la app en el puerto 8080
```

## Probar
```bash
# 1) Consulta valida (1ra vez va a RENIEC, 2da vez sale de Redis)
curl http://localhost:8080/api/v1/personas/46027897

# 2) Formato invalido -> 400
curl http://localhost:8080/api/v1/personas/123

# 3) DNI inexistente -> 404
curl http://localhost:8080/api/v1/personas/00000000
```

Mira los logs: la primera consulta imprime "Consultando RENIEC..." y la
segunda "encontrado en REDIS". Ahi se ve el cache funcionando.

## Estructura
```
pe.codigo.reniec
├── controller/   ConsultaController      (capa web)
├── service/      ConsultaService          (contrato)
│   └── impl/     ConsultaServiceImpl       (logica de los 3 niveles)
├── client/       ReniecFeignClient         (consumo del API externo)
├── config/       FeignConfig, RedisConfig
├── dto/          PersonaResponse
├── entity/       Persona
├── repository/   PersonaRepository
└── exception/    GlobalExceptionHandler, ErrorResponse, + 2 excepciones
```
