# reniec-resttemplate

Consulta de DNI con **cache en 3 niveles** (Redis -> PostgreSQL -> API RENIEC),
consumiendo el API externo con **RestTemplate**.

Mismo flujo que los proyectos de Feign y WebClient; solo cambia el cliente HTTP.

## Stack
- Spring Boot 3.5.15
- Java 21
- RestTemplate (incluido en spring-boot-starter-web)
- PostgreSQL + Redis
- Lombok, Maven

## Que cambia respecto a Feign y WebClient
1. No agregamos ninguna dependencia extra: RestTemplate ya viene con spring-web.
2. El RestTemplate se arma en RestTemplateConfig con rootUri() y un interceptor
   que mete el token Bearer en cada peticion.
3. Es bloqueante de nacimiento: getForObject() devuelve el objeto directo,
   sin .block().
4. El manejo de error separa dos casos:
   - HttpStatusCodeException (con subtipos NotFound y Unauthorized): RENIEC
     respondio con un codigo de error.
   - RestClientException: ni siquiera hubo respuesta (timeout, conexion, DNS).

## Antes de arrancar
1. Levanta Redis y Postgres:
   ```bash
   docker compose up -d
   ```
2. Pon tu token de decolecta en src/main/resources/application.properties:
   ```properties
   reniec.api.token=PON_AQUI_TU_TOKEN
   ```

## Compilar y ejecutar
```bash
mvn clean package
mvn spring-boot:run
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
