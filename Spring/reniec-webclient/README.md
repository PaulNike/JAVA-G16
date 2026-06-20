# reniec-webclient

Consulta de DNI con **cache en 3 niveles** (Redis -> PostgreSQL -> API RENIEC),
consumiendo el API externo con **WebClient** (Spring WebFlux).

Mismo flujo que el proyecto con Feign; lo unico que cambia es el cliente HTTP.

## Stack
- Spring Boot 3.5.15
- Java 21
- WebClient (spring-boot-starter-webflux)
- PostgreSQL + Redis
- Lombok, Maven

## Que cambia respecto a Feign
1. En vez de spring-cloud-openfeign usamos spring-boot-starter-webflux.
2. No hace falta @EnableFeignClients.
3. El cliente (ReniecWebClient) arma la llamada con WebClient y usa .block()
   para esperar el resultado (mantenemos el flujo bloqueante).
4. El manejo de error atrapa WebClientResponseException en lugar de FeignException.

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
