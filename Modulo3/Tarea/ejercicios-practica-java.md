# 📚 Ejercicios de Práctica en Casa — Java Moderno
### Curso: Pre-Spring Boot · Docente: [Tu nombre]

> **Instrucciones generales**
> - Crea un proyecto Java (puedes usar el mismo del curso) y resuelve cada ejercicio en su propio método o clase.
> - Cada ejercicio tiene una pista entre corchetes `[ ]` para orientarte si te atascas.
> - El nivel de dificultad está indicado con ⭐ (básico), ⭐⭐ (intermedio) o ⭐⭐⭐ (desafío).

---

## 🔷 TEMA 3 — Expresiones Lambda

> **Recuerda:** Una lambda es una función anónima con la forma `(parámetros) -> cuerpo`.
> Siempre implementa una **interfaz funcional** (una interfaz con un solo método abstracto).

---

**Ejercicio 3.1** ⭐
Crea una interfaz funcional llamada `Saludo` con el método `String saludar(String nombre)`.
Luego instancia la interfaz usando una lambda que retorne `"¡Hola, [nombre]! Bienvenido al curso."`.

```java
// Resultado esperado al llamar saludo.saludar("Ana"):
// ¡Hola, Ana! Bienvenido al curso.
```
> 📌 Pista: La anotación `@FunctionalInterface` es opcional pero recomendada.

---

**Ejercicio 3.2** ⭐
Crea una interfaz funcional `Conversor` con el método `double convertir(double valor)`.
Define tres lambdas:
- `dolaresASoles` → multiplica por 3.75
- `solesADolares` → divide entre 3.75
- `celsiusAFahrenheit` → aplica la fórmula `(°C × 9/5) + 32`

```java
// Resultado esperado:
// 100 dólares = S/. 375.0
// S/. 375.0   = 100.0 dólares
// 0°C         = 32.0°F
```

---

**Ejercicio 3.3** ⭐
Crea una interfaz funcional `Validador<T>` con el método `boolean validar(T valor)`.
Implementa dos lambdas:
- Una que valide si un `String` no está vacío y tiene más de 3 caracteres.
- Una que valide si un `Integer` está entre 1 y 100.

```java
// Prueba con: "", "Ab", "Java", 0, 50, 150
```
> 📌 Pista: Usa genéricos en la interfaz: `interface Validador<T> { boolean validar(T valor); }`

---

**Ejercicio 3.4** ⭐⭐
Crea una interfaz funcional `Operacion` con el método `int calcular(int a, int b)`.
Define lambdas para las cuatro operaciones básicas: suma, resta, multiplicación y división.
Guárdalas en un `Map<String, Operacion>` donde la clave sea el nombre de la operación.
Luego recorre el mapa e imprime el resultado de cada operación con los valores `12` y `4`.

```java
// Resultado esperado:
// suma         → 16
// resta        → 8
// multiplicacion→ 48
// division     → 3
```

---

**Ejercicio 3.5** ⭐⭐
Crea una interfaz funcional `Transformador` con el método `String aplicar(String texto)`.
Define tres lambdas:
- `aMayusculas` → convierte a mayúsculas
- `invertir` → invierte la cadena (`StringBuilder`)
- `quitarEspacios` → elimina todos los espacios

Luego **encadénalas** manualmente: aplica `quitarEspacios` primero, luego `invertir`, luego `aMayusculas`.

```java
// Entrada: "  hola mundo  "
// Resultado esperado: "ODNUM ALOH"
```

---

**Ejercicio 3.6** ⭐⭐
Crea una interfaz funcional `Ejecutor` con el método `void ejecutar()`.
Luego crea un método llamado `repetir(int veces, Ejecutor tarea)` que ejecute la lambda `veces` veces.
Pruébalo imprimiendo `"Java es genial"` exactamente 5 veces usando una lambda.

```java
// Resultado esperado (5 líneas):
// Java es genial
// Java es genial
// ...
```
> 📌 Pista: El método `repetir` recibe la lambda como parámetro. Es como `Runnable` pero con tu propia interfaz.

---

**Ejercicio 3.7** ⭐⭐
Crea una clase `Boton` con un atributo `Ejecutor onClick` (usa tu interfaz del ejercicio anterior).
Agrega un método `click()` que invoque `onClick.ejecutar()`.
Crea tres botones con lambdas distintas y simula hacer clic en cada uno.

```java
// Ejemplo:
// Boton guardar = new Boton(() -> System.out.println("Guardando datos..."));
// guardar.click(); → Guardando datos...
```

---

**Ejercicio 3.8** ⭐⭐
Crea una interfaz funcional `Comparador<T>` con el método `int comparar(T a, T b)`.
Implementa dos lambdas:
- Una que compare dos `Integer` (orden ascendente).
- Una que compare dos `String` por su longitud.

Úsalas para determinar cuál es mayor en cada caso e imprimir el resultado.

```java
// Comparar 10 y 25   → 25 es mayor
// Comparar "Java" y "Spring" → "Spring" es más largo
```

---

**Ejercicio 3.9** ⭐⭐⭐
Crea una interfaz funcional `Filtro<T>` con el método `boolean aplicar(T elemento)`.
Luego crea un método genérico:

```java
public static <T> List<T> filtrarLista(List<T> lista, Filtro<T> filtro)
```

Pruébalo con:
- Una lista de números: filtra solo los mayores a 10.
- Una lista de palabras: filtra solo las que empiezan con `"S"`.

```java
// Lista: [3, 15, 8, 22, 5, 11]  → Resultado: [15, 22, 11]
// Lista: ["Spring", "Java", "Stream", "Lambda"] → ["Spring", "Stream"]
```

---

**Ejercicio 3.10** ⭐⭐⭐
Crea una calculadora de impuestos usando lambdas.
Define una interfaz `CalculadorImpuesto` con el método `double calcular(double monto)`.
Crea un `Map<String, CalculadorImpuesto>` con al menos tres tipos:
- `"IGV"` → 18%
- `"ISC"` → 10%
- `"RENTA"` → 30% sobre el exceso de 7 UIT (1 UIT = S/. 5150)

Crea un método que reciba un tipo de impuesto y un monto, busque la lambda en el mapa y retorne el impuesto calculado. Si el tipo no existe, lanza una excepción con un mensaje claro.

```java
// calcularImpuesto("IGV", 1000.0)   → S/. 180.0
// calcularImpuesto("ISC", 500.0)    → S/. 50.0
// calcularImpuesto("XYZ", 100.0)    → IllegalArgumentException: Tipo de impuesto no encontrado: XYZ
```

---

## 🔷 TEMA 4 — Streams

> **Recuerda:** `coleccion.stream()` → operaciones intermedias → operación terminal.
> Las operaciones intermedias son **perezosas**: solo se ejecutan cuando llega la terminal.

---

**Ejercicio 4.1** ⭐
Dada la siguiente lista de números:
```java
List<Integer> numeros = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
```
Usa un Stream para:
- a) Filtrar solo los pares e imprimirlos con `forEach`.
- b) Obtener una lista con el cuadrado de cada número impar.
- c) Contar cuántos números son mayores que 5.

```java
// a) 2 4 6 8 10
// b) [1, 9, 25, 49, 81]
// c) 5
```

---

**Ejercicio 4.2** ⭐
Dada una lista de nombres:
```java
List<String> nombres = List.of("Ana", "Carlos", "Beatriz", "David", "Elena", "Carlos", "Ana");
```
Usa Streams para:
- a) Obtener la lista sin duplicados y ordenada alfabéticamente.
- b) Obtener solo los nombres que tienen más de 4 letras.
- c) Obtener el primer nombre que empiece con `"B"`.

```java
// a) [Ana, Beatriz, Carlos, David, Elena]
// b) [Carlos, Beatriz, David, Elena]
// c) Optional[Beatriz]
```

---

**Ejercicio 4.3** ⭐
Crea una lista de productos:
```java
List<String> productos = List.of("Laptop", "Mouse", "Teclado", "Monitor", "Audífonos");
```
Usa Streams para:
- a) Convertir todos a mayúsculas.
- b) Mostrar solo los 3 primeros con `limit`.
- c) Mostrar solo los que contienen la letra `"o"` (mayúscula o minúscula).

---

**Ejercicio 4.4** ⭐⭐
Crea una clase `Producto` con atributos `nombre` (String), `precio` (double) y `categoria` (String).
Crea una lista con al menos 8 productos de distintas categorías.
Luego usa Streams para:
- a) Filtrar los productos de una categoría específica.
- b) Obtener el producto más caro (usa `max`).
- c) Calcular el precio promedio de todos los productos (usa `mapToDouble` y `average`).

```java
// Ejemplo de resultado:
// Productos de "Electrónica": [Laptop, Monitor]
// Producto más caro: Monitor - S/. 1200.0
// Precio promedio: S/. 650.0
```

---

**Ejercicio 4.5** ⭐⭐
Usando la clase `Producto` del ejercicio anterior, usa `Collectors.groupingBy` para:
- a) Agrupar los productos por categoría en un `Map<String, List<Producto>>`.
- b) Contar cuántos productos hay por categoría (`groupingBy` + `Collectors.counting()`).
- c) Obtener el precio total por categoría (`groupingBy` + `Collectors.summingDouble`).

```java
// a) {Electrónica=[Laptop, Monitor], Accesorios=[Mouse, Teclado]}
// b) {Electrónica=2, Accesorios=2}
// c) {Electrónica=2700.0, Accesorios=350.0}
```
> 📌 Pista: `Collectors.groupingBy(Producto::getCategoria)`

---

**Ejercicio 4.6** ⭐⭐
Dado el siguiente texto:
```java
String texto = "java stream lambda optional function predicate consumer java stream";
```
- a) Divide la cadena en palabras (usa `Arrays.stream` o `Stream.of`).
- b) Elimina duplicados.
- c) Ordena alfabéticamente.
- d) Une todas en una sola cadena separada por comas (usa `Collectors.joining`).

```java
// Resultado: "consumer,function,java,lambda,optional,predicate,stream"
```

---

**Ejercicio 4.7** ⭐⭐
Crea una lista de números del 1 al 20 usando `IntStream.rangeClosed(1, 20)`.
Luego:
- a) Obtén la suma total de todos los números.
- b) Obtén la suma de los múltiplos de 3.
- c) Obtén el promedio de los números impares.
- d) Verifica si todos los números son positivos (`allMatch`).
- e) Verifica si alguno es mayor que 15 (`anyMatch`).

```java
// a) 210
// b) 63  (3+6+9+12+15+18)
// c) 10.0
// d) true
// e) true
```

---

**Ejercicio 4.8** ⭐⭐
Crea una clase `Estudiante` con `nombre` (String), `nota` (double) y `aprobado` (boolean).
Construye una lista con 6 estudiantes (algunos aprobados, otros no).
Usa Streams para:
- a) Separar aprobados y desaprobados con `Collectors.partitioningBy`.
- b) Obtener el nombre del estudiante con la nota más alta.
- c) Calcular el promedio solo de los aprobados.

```java
// a) {true=[Ana-18.0, Carlos-15.5], false=[Luis-9.0, María-11.0]}
// b) Ana
// c) 16.75
```
> 📌 Pista: `partitioningBy` devuelve un `Map<Boolean, List<Estudiante>>`

---

**Ejercicio 4.9** ⭐⭐⭐
Crea una lista de facturas, donde cada `Factura` tiene `id`, `monto` y `pagada` (boolean).
Usa Streams para:
- a) Calcular el total de todas las facturas.
- b) Calcular el total solo de las facturas **pagadas**.
- c) Calcular el total solo de las facturas **pendientes**.
- d) Obtener los IDs de las facturas pendientes en una sola lista.
- e) Verificar si hay alguna factura pendiente mayor a S/. 5000.

---

**Ejercicio 4.10** ⭐⭐⭐
Crea una lista de `Empleado` (nombre, salario, departamento).
Usando Streams con `Collectors.toMap`, construye:
- a) Un `Map<String, Double>` donde la clave sea el nombre y el valor su salario.
- b) Un `Map<String, Double>` donde la clave sea el departamento y el valor sea el **salario promedio** del departamento.
- c) Obtén la lista de empleados ordenados por salario de mayor a menor, y si empatan, por nombre alfabéticamente.

> ⚠️ Desafío extra: Si dos empleados tienen el mismo nombre en el mapa del punto a), maneja el conflicto en `toMap` con el tercer parámetro (merge function).

---

## 🔷 TEMA 5 — Optional

> **Recuerda:** `Optional<T>` evita los `NullPointerException`. Nunca uses `.get()` sin verificar antes.
> Métodos principales: `of`, `empty`, `ofNullable`, `isPresent`, `ifPresent`, `orElse`, `orElseGet`, `orElseThrow`, `map`, `filter`.

---

**Ejercicio 5.1** ⭐
Crea un método `buscarPais(String nombre)` que reciba el nombre de un país y:
- Si el nombre es `"Perú"`, `"Chile"` o `"Colombia"` → retorna `Optional.of(nombre)`.
- Para cualquier otro → retorna `Optional.empty()`.

Pruébalo con 4 países distintos e imprime si fue encontrado o el mensaje `"País no registrado"` usando `orElse`.

---

**Ejercicio 5.2** ⭐
Dado este Map:
```java
Map<Integer, String> empleados = Map.of(1, "Ana", 2, "Carlos", 3, "Beatriz");
```
Crea un método `buscarEmpleado(int id)` que envuelva el resultado en un `Optional` usando `Optional.ofNullable`.
Prueba con ids que existen y con ids que no existen.

```java
// buscarEmpleado(1) → Optional[Ana]
// buscarEmpleado(9) → Optional.empty
```
> 📌 Pista: `Map.get()` devuelve `null` si no encuentra la clave. `ofNullable` convierte ese null en `Optional.empty()`.

---

**Ejercicio 5.3** ⭐
Crea tres variables:
```java
Optional<String> con = Optional.of("Spring Boot");
Optional<String> vacio = Optional.empty();
```
Para cada una, usa los métodos:
- `isPresent()` → ¿tiene valor?
- `isEmpty()` → ¿está vacío? (Java 11+)
- `orElse("Sin valor")` → valor o defecto
- `orElseGet(() -> "Generado dinámicamente")` → valor o lambda

Imprime los resultados y explica con un comentario la diferencia entre `orElse` y `orElseGet`.

---

**Ejercicio 5.4** ⭐⭐
Crea una clase `Configuracion` con un atributo `Optional<String> tema` (puede ser `null`).
Crea un método `getTema()` que devuelva el tema si está presente, o `"claro"` como valor por defecto.
Instancia la clase con y sin tema y verifica que siempre se obtenga un resultado válido.

```java
// Con tema    → "oscuro"
// Sin tema    → "claro"
```

---

**Ejercicio 5.5** ⭐⭐
Crea una clase `Usuario` con `nombre` y un `Optional<String> email`.
Crea un método que, dado un `Optional<Usuario>`, extraiga el email del usuario usando `map`.
Si el usuario no existe o no tiene email, imprime `"Email no disponible"`.

```java
// Optional<Usuario> con email  → "ana@email.com"
// Optional<Usuario> sin email  → "Email no disponible"
// Optional.empty()             → "Email no disponible"
```
> 📌 Pista: Puedes encadenar `optUsuario.map(Usuario::getEmail).orElse("Email no disponible")`

---

**Ejercicio 5.6** ⭐⭐
Crea un método `dividir(int a, int b)` que retorne un `Optional<Double>`:
- Si `b == 0` → retorna `Optional.empty()`.
- En caso contrario → retorna el resultado envuelto en `Optional.of`.

Pruébalo con varias combinaciones e imprime el resultado o `"División no permitida"` con `orElse`.

```java
// dividir(10, 2) → Optional[5.0]
// dividir(10, 0) → "División no permitida"
```

---

**Ejercicio 5.7** ⭐⭐
Crea una lista de `Optional<String>` que contenga algunos valores y algunos vacíos:
```java
List<Optional<String>> lista = List.of(
    Optional.of("Java"), Optional.empty(), Optional.of("Spring"),
    Optional.empty(), Optional.of("Lambda")
);
```
Usa Streams sobre la lista para extraer solo los valores presentes en una nueva `List<String>`.

```java
// Resultado: ["Java", "Spring", "Lambda"]
```
> 📌 Pista: `filter(Optional::isPresent)` + `map(Optional::get)`

---

**Ejercicio 5.8** ⭐⭐
Crea un método `buscarProducto(String codigo)` que simule una búsqueda.
Si el código empieza con `"P"` → retorna un producto ficticio en un Optional.
Si no → retorna `Optional.empty()`.

Usa `orElseThrow` para lanzar una excepción personalizada cuando no se encuentre:
```java
throw new RuntimeException("Producto con código " + codigo + " no encontrado");
```
Prueba con un código válido y uno inválido capturando la excepción.

---

**Ejercicio 5.9** ⭐⭐⭐
Crea tres clases anidadas: `Pedido` tiene un `Optional<Cliente>`, y `Cliente` tiene un `Optional<Direccion>`, y `Direccion` tiene un atributo `ciudad` (String).

Usando encadenamiento de `map`, extrae la ciudad del pedido de forma segura.
Si en cualquier nivel el Optional está vacío, el resultado debe ser `"Ciudad desconocida"`.

```java
// Pedido con todo completo → "Lima"
// Pedido sin dirección     → "Ciudad desconocida"
// Pedido sin cliente       → "Ciudad desconocida"
```
> 📌 Pista: `pedido.getCliente().flatMap(Cliente::getDireccion).map(Direccion::getCiudad).orElse(...)`

---

**Ejercicio 5.10** ⭐⭐⭐
Crea un mini sistema de autenticación.
Implementa un método:
```java
public static Optional<String> autenticar(String usuario, String clave)
```
Que simule una lista de usuarios válidos (nombre + clave en un Map).
- Si las credenciales son correctas → retorna `Optional.of("TOKEN_" + usuario.toUpperCase())`.
- Si no → retorna `Optional.empty()`.

Luego crea un método `acceder(String token)` que solo acepte Strings que empiecen con `"TOKEN_"`.
Encadena `autenticar` con `filter` y `map` para obtener un mensaje de bienvenida o `"Acceso denegado"`.

```java
// autenticar("admin", "1234") → Optional["TOKEN_ADMIN"]
// acceder con token válido    → "Bienvenido, ADMIN"
// autenticar("admin", "xxxx")→ Optional.empty
// acceder sin token           → "Acceso denegado"
```

---

## 🔷 TEMA 6 — Interfaces Funcionales

> **Recuerda la tabla:**
>
> | Interfaz | Entrada | Salida | Úsala para... |
> |---|---|---|---|
> | `Predicate<T>` | T | boolean | verificar condiciones |
> | `Function<T,R>` | T | R | transformar valores |
> | `Consumer<T>` | T | void | ejecutar acciones |
> | `Supplier<T>` | — | T | proveer valores |
> | `BiFunction<A,B,R>` | A, B | R | transformar con dos entradas |

---

**Ejercicio 6.1** ⭐
Crea los siguientes `Predicate<String>`:
- `noEsNulo` → verifica que el String no sea null.
- `noEstaVacio` → verifica que no esté vacío.
- `esEmail` → verifica que contenga `"@"` y `"."`.

Combínalos con `and` para crear un `Predicate` compuesto `esEmailValido`.
Prueba con: `"ana@email.com"`, `""`, `null`, `"sinArroba.com"`.

```java
// "ana@email.com" → true
// ""              → false
// "sinArroba.com" → false
```
> 📌 Pista: `predicate1.and(predicate2)` devuelve un Predicate que exige ambas condiciones.

---

**Ejercicio 6.2** ⭐
Crea los siguientes `Predicate<Integer>`:
- `esPar` → número par
- `esPositivo` → número positivo
- `esMayorQueDiez` → número mayor que 10

Combínalos con `and`, `or` y `negate` para responder:
- a) ¿Es par Y positivo?
- b) ¿Es par O mayor que 10?
- c) ¿NO es par?

Prueba con los valores: `-4`, `8`, `15`, `12`.

---

**Ejercicio 6.3** ⭐
Crea las siguientes `Function`:
- `Function<String, Integer>` que retorne la longitud del texto.
- `Function<Integer, String>` que retorne `"Par"` si es par o `"Impar"` si no.
- `Function<String, String>` que capitalice la primera letra de cada palabra.

Prueba cada una con al menos 3 valores distintos.

---

**Ejercicio 6.4** ⭐⭐
Usa `Function` con `andThen` y `compose` para crear pipelines de transformación:

```java
Function<String, String>  limpiar      = s -> s.trim();
Function<String, String>  mayusculas   = s -> s.toUpperCase();
Function<String, Integer> contar       = s -> s.length();
```

- a) Encadena `limpiar` → `mayusculas` → `contar` usando `andThen`.
- b) Crea la misma cadena al revés usando `compose`.
- c) Aplica el pipeline a `"  hola mundo  "` y verifica que ambos dan el mismo resultado.

```java
// "  hola mundo  " → 10
```
> 📌 Pista: `f.andThen(g)` aplica f primero y luego g. `g.compose(f)` aplica f primero y luego g. Son equivalentes.

---

**Ejercicio 6.5** ⭐⭐
Crea los siguientes `Consumer`:
- `imprimirMayusculas` → imprime el texto en mayúsculas.
- `imprimirConFecha` → imprime el texto con `LocalDate.now()` al inicio.
- `guardarEnLog` → imprime `"[LOG] " + texto`.

Combínalos con `andThen` para crear un `Consumer` que haga las tres cosas seguidas.
Prueba con el texto `"Inicio de sesión"`.

```java
// INICIO DE SESIÓN
// 2025-06-15: Inicio de sesión
// [LOG] Inicio de sesión
```

---

**Ejercicio 6.6** ⭐⭐
Crea los siguientes `Supplier`:
- `Supplier<String>` que retorne `"Usuario_" + UUID.randomUUID()`.
- `Supplier<LocalDate>` que retorne la fecha de hoy.
- `Supplier<List<String>>` que retorne una lista vacía de ArrayList.

Crea un método genérico:
```java
public static <T> T obtenerValor(Supplier<T> supplier)
```
y prueba los tres Suppliers.

---

**Ejercicio 6.7** ⭐⭐
Crea una `BiFunction<String, Integer, String>` que reciba un texto y un número, y retorne el texto repetido ese número de veces separado por `" | "`.

Luego usa `andThen` en la BiFunction para que el resultado final sea en mayúsculas.

```java
// repetir("java", 3) → "JAVA | JAVA | JAVA"
```

---

**Ejercicio 6.8** ⭐⭐
Crea una clase `Procesador<T, R>` con:
- Atributo `Function<T, R> transformacion`
- Atributo `Predicate<R> validacion`
- Atributo `Consumer<R> accion`

Y un método `procesar(T entrada)` que:
1. Aplique la transformación.
2. Valide el resultado.
3. Si es válido, ejecute la acción; si no, imprima `"Resultado inválido"`.

Instancia la clase para procesar nombres: transformar a mayúsculas, validar que tengan más de 3 letras, e imprimirlos.

---

**Ejercicio 6.9** ⭐⭐⭐
Crea un mini pipeline de procesamiento de pedidos usando solo interfaces funcionales.
Dado un `Pedido` (monto double, tipo String), crea:

- `Function<Pedido, Double>` → calcula el descuento según tipo:
  - `"PREMIUM"` → 20%, `"REGULAR"` → 10%, otro → 0%
- `Function<Double, Double>` → agrega IGV (18%) al monto con descuento
- `Predicate<Double>` → verifica que el total no supere S/. 10,000
- `Consumer<Double>` → imprime el total formateado

Encadena las Functions con `andThen`, valida con el Predicate, y ejecuta el Consumer solo si es válido.

---

**Ejercicio 6.10** ⭐⭐⭐
Crea un sistema de notificaciones usando interfaces funcionales.
Define un `Map<String, Consumer<String>>` con estos canales:
- `"EMAIL"` → simula enviar un email (imprime el mensaje formateado como email).
- `"SMS"` → simula enviar un SMS (máximo 160 caracteres, recorta si es necesario).
- `"PUSH"` → simula una notificación push (imprime en mayúsculas con `"🔔"`).

Crea un método:
```java
public static void enviarNotificacion(String canal, String mensaje, Map<String, Consumer<String>> canales)
```
Que busque el Consumer en el mapa y lo ejecute. Si el canal no existe, lanza una excepción.

Combina dos canales con `andThen` para enviar la misma notificación por EMAIL y PUSH simultáneamente.

```java
// enviarNotificacion("EMAIL", "Tu pedido fue confirmado", canales)
// → [EMAIL] Para: usuario@email.com | Asunto: Notificación | Tu pedido fue confirmado

// enviarNotificacion("PUSH", "Tu pedido fue confirmado", canales)
// → 🔔 TU PEDIDO FUE CONFIRMADO
```

---

## ✅ Checklist de entrega

Cuando termines cada ejercicio, márcalo con ✅ y asegúrate de:

- [ ] El código compila sin errores
- [ ] Probaste con al menos los valores de ejemplo
- [ ] Agregaste un comentario breve explicando tu solución
- [ ] Los resultados coinciden con los esperados

> 💡 **Tip del docente:** No busques la solución directamente. Intenta resolver cada ejercicio durante al menos 15 minutos antes de consultar. Los errores que cometes solo tú son los que más enseñan.

---

*Curso Pre-Spring Boot — Material de práctica en casa*
