# reniec-retrofit

Consulta de DNI con **cache en 3 niveles** (Redis -> PostgreSQL -> API RENIEC),
consumiendo el API externo con **Retrofit** (la libreria de Square).

Mismo flujo que los proyectos de Feign, WebClient y RestTemplate; solo cambia
el cliente HTTP.

## Stack
- Spring Boot 3.5.15
- Java 21
- Retrofit 2.11.0 + converter-jackson (OkHttp viene transitivo)
- PostgreSQL + Redis
- Lombok, Maven

## Que cambia respecto a los otros tres
1. Retrofit NO es de Spring: lo construimos a mano en RetrofitConfig con un
   Retrofit.Builder (baseUrl, OkHttp con interceptor para el token, y el
   convertidor Jackson de Spring).
2. La API se define como una interface anotada (@GET, @Query de Retrofit) que
   devuelve un Call<PersonaResponse>.
3. DIFERENCIA CLAVE: Retrofit no lanza excepcion ante 4xx/5xx. Nosotros
   revisamos response.isSuccessful() y response.code() en el cliente, y
   envolvemos el resultado en RetrofitCallException para que el service decida.
4. La baseUrl en Retrofit DEBE terminar en "/" (lo aseguramos en el config).

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
