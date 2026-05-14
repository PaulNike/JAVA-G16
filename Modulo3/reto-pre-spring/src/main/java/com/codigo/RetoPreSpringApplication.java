package com.codigo;

import com.codigo.lambda.Operacion;
import com.codigo.optional.Usuario;
import com.codigo.reto1.Desarrollador;
import com.codigo.reto1.Empleado;
import com.codigo.reto1.Gerente;
import com.codigo.reto2.Factura;
import com.codigo.reto2.Nomina;
import com.codigo.reto2.Pagable;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;
import java.util.Optional;
import java.util.function.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

/**
 * ============================================================
 *  CLASE PRINCIPAL - EJERCICIOS PRE-SPRING
 * ============================================================
 *
 *  Esta clase reúne los conceptos fundamentales de Java moderno
 *  que necesitas dominar antes de trabajar con Spring Boot:
 *
 *    RETO 1 → Herencia y Polimorfismo
 *    RETO 2 → Interfaces y Contratos
 *    TEMA 3 → Expresiones Lambda
 *    TEMA 4 → Streams
 *    TEMA 5 → Optional
 *    TEMA 6 → Interfaces Funcionales (Function, Predicate, Consumer)
 *
 * ============================================================
 */
@SpringBootApplication
public class RetoPreSpringApplication {

	public static void main(String[] args) {
		SpringApplication.run(RetoPreSpringApplication.class, args);

		// -------------------------------------------------------
		// RETO 1: HERENCIA Y POLIMORFISMO
		// -------------------------------------------------------
		// TEORÍA:
		//   La herencia permite crear clases hijas que reutilizan
		//   el comportamiento de una clase padre (Empleado).
		//
		//   El polimorfismo permite que una misma referencia de tipo
		//   padre (Empleado) apunte a distintos tipos de objetos hijos
		//   (Gerente, Desarrollador), y que cada uno se comporte
		//   de forma diferente al llamar al mismo método.
		//
		//   Estructura:
		//     Empleado (padre)
		//     ├── Gerente     (hijo - tiene bono adicional)
		//     └── Desarrollador (hijo - tiene bono adicional)
		//
		//   Nota: usamos el tipo padre "Empleado" para la referencia
		//   aunque el objeto real sea Gerente o Desarrollador.
		// -------------------------------------------------------

		System.out.println("========== RETO 1: HERENCIA Y POLIMORFISMO ==========");

		// Polimorfismo en acción: referencia de tipo Empleado, objeto de tipo Gerente
		Empleado gerente = new Gerente("Ivonne", 3000, 500);

		// Referencia de tipo Empleado, objeto de tipo Desarrollador
		Empleado desarrollador = new Desarrollador("Daniel", 2500, 300);

		// toString() de cada subclase muestra el sueldo total (salario + bono)
		System.out.println(gerente);
		System.out.println(desarrollador);

		System.out.println("=====================================================");
		System.out.println();


		// -------------------------------------------------------
		// RETO 2: INTERFACES Y CONTRATOS
		// -------------------------------------------------------
		// TEORÍA:
		//   Una interfaz define un CONTRATO: cualquier clase que la
		//   implemente DEBE proveer los métodos declarados.
		//
		//   Interfaz: Pagable
		//     └── método: calcularTotal()
		//
		//   Clases que firman el contrato:
		//     ├── Factura  → total = monto + IGV
		//     └── Nomina   → total = salario + beneficios
		//
		//   Ventaja: podemos tratar a Factura y Nomina de la misma
		//   forma (tipo Pagable) sin importar su implementación interna.
		// -------------------------------------------------------

		System.out.println("========== RETO 2: INTERFACES Y CONTRATOS ===========");

		// Ambos objetos son de tipo Pagable (polimorfismo con interfaz)
		Pagable factura = new Factura("F001-123", 1500.00, 270.00);
		Pagable nomina  = new Nomina("Nike Rodriguez", 2500.00, 500.00);

		// calcularTotal() se comporta diferente en cada clase, pero la
		// firma del método es la misma → eso es el contrato de la interfaz
		System.out.println("Total de la Factura : S/. " + factura.calcularTotal());
		System.out.println("Total de la Nomina  : S/. " + nomina.calcularTotal());

		System.out.println("=====================================================");
		System.out.println();


		// -------------------------------------------------------
		// TEMA 3: EXPRESIONES LAMBDA
		// -------------------------------------------------------
		// TEORÍA:
		//   Una lambda es una función anónima (sin nombre) que se
		//   puede pasar como parámetro o almacenar en una variable.
		//
		//   Sintaxis:
		//     (parámetros) -> cuerpo
		//
		//   Ejemplo mental:
		//     Antes (clase anónima): new Runnable() { public void run(){...} }
		//     Ahora (lambda)       :  () -> { ... }
		//
		//   Las lambdas implementan interfaces funcionales, es decir,
		//   interfaces que tienen UN SOLO método abstracto.
		// -------------------------------------------------------

		System.out.println("============== TEMA 3: LAMBDAS ======================");

		// --- Forma clásica (antes de Java 8): clase anónima ---
		Runnable tareaClasica = new Runnable() {
			@Override
			public void run() {
				System.out.println("Tarea ejecutada con clase anónima (forma antigua)");
			}
		};
		tareaClasica.run();

		// --- Forma moderna: lambda equivalente ---
		// () -> no recibe parámetros, solo ejecuta el println
		Runnable tareaLambda = () -> System.out.println("Tarea ejecutada con Lambda (forma moderna)");
		tareaLambda.run();

		// --- Lambda con parámetros y retorno ---
		// Interfaz propia: Operacion tiene el método calcular(a, b)
		// La lambda recibe dos enteros y devuelve su diferencia
		Operacion resta = (a, b) -> a - b;
		int resultado = resta.calcular(10, 5);
		System.out.println("Resultado de la resta (10 - 5): " + resultado);

		System.out.println("=====================================================");
		System.out.println();


		// -------------------------------------------------------
		// TEMA 4: STREAMS
		// -------------------------------------------------------
		// TEORÍA:
		//   Un Stream es una secuencia de elementos sobre la que
		//   podemos aplicar operaciones en cadena (pipeline).
		//
		//   Estructura del pipeline:
		//     coleccion.stream()
		//              .operacionIntermedia()   // devuelve otro Stream
		//              .operacionIntermedia()
		//              .operacionTerminal()     // produce el resultado final
		//
		//   Operaciones INTERMEDIAS (perezosas, no se ejecutan solas):
		//     filter()  → filtra elementos según una condición
		//     map()     → transforma cada elemento
		//     sorted()  → ordena
		//     distinct()→ elimina duplicados
		//     limit()   → limita la cantidad de elementos
		//     peek()    → observa sin modificar (útil para debug)
		//
		//   Operaciones TERMINALES (activan el pipeline):
		//     collect() → agrupa en una colección
		//     count()   → cuenta elementos
		//     forEach() → aplica una acción a cada elemento
		//     reduce()  → combina todos en un valor
		//     findFirst()→ obtiene el primer elemento
		//     anyMatch()→ verifica si alguno cumple una condición
		// -------------------------------------------------------

		System.out.println("============== TEMA 4: STREAMS ======================");

		List<String> nombres = List.of("Fabian", "Manuel", "Arana", "Gordillo", "Luis");

		// Pipeline:
		//  1. limit(2)        → toma solo los 2 primeros: ["Fabian", "Manuel"]
		//  2. map(toUpperCase)→ transforma cada uno a mayúsculas
		//  3. collect(toList) → agrupa el resultado en una nueva lista
		List<String> primerosDosEnMayusculas = nombres.stream()
				.limit(2)                            // operación intermedia
				.map(nombre -> nombre.toUpperCase()) // operación intermedia
				// alternativa con referencia a método: .map(String::toUpperCase)
				.collect(toList());                  // operación terminal

		System.out.println("Lista original      : " + nombres);
		System.out.println("Primeros 2 en mayús : " + primerosDosEnMayusculas);

		System.out.println("=====================================================");
		System.out.println();


		// -------------------------------------------------------
		// TEMA 5: OPTIONAL
		// -------------------------------------------------------
		// TEORÍA:
		//   Optional<T> es un contenedor que PUEDE o NO contener
		//   un valor. Evita el temido NullPointerException.
		//
		//   Métodos clave:
		//     Optional.of(valor)    → crea un Optional con valor
		//     Optional.empty()      → crea un Optional sin valor
		//     optional.isPresent()  → true si tiene valor
		//     optional.ifPresent()  → ejecuta acción solo si hay valor
		//     optional.orElse(x)    → devuelve el valor, o x si está vacío
		//     optional.get()        → obtiene el valor (lanza excepción si vacío)
		//
		//   Regla de oro: NUNCA usar optional.get() sin verificar antes.
		// -------------------------------------------------------

		System.out.println("============== TEMA 5: OPTIONAL =====================");

		// Caso 1: el usuario SÍ existe (id = 1)
		Optional<Usuario> usuarioEncontrado = buscarUsuarioPorId(1);

		// ifPresent solo ejecuta el bloque si el Optional tiene valor
		usuarioEncontrado.ifPresent(usuario ->
				System.out.println("✓ Usuario encontrado: " + usuario.getNombre())
		);

		// Caso 2: el usuario NO existe (id = 99)
		Optional<Usuario> usuarioNoEncontrado = buscarUsuarioPorId(99);

		// orElse devuelve el valor por defecto cuando el Optional está vacío
		Usuario invitado = usuarioNoEncontrado.orElse(new Usuario("Invitado"));
		System.out.println("✗ Usuario no encontrado, se usa por defecto: " + invitado.getNombre());

		System.out.println("=====================================================");
		System.out.println();


		// -------------------------------------------------------
		// TEMA 6: INTERFACES FUNCIONALES (java.util.function)
		// -------------------------------------------------------
		// TEORÍA:
		//   Java 8 incluye interfaces funcionales listas para usar.
		//   Son plantillas para lambdas con diferentes firmas:
		//
		//   ┌─────────────────┬──────────────────────────────────────────┐
		//   │ Interfaz        │ Firma                                    │
		//   ├─────────────────┼──────────────────────────────────────────┤
		//   │ Predicate<T>    │ T  → boolean  (¿cumple una condición?)   │
		//   │ BiPredicate<A,B>│ A,B → boolean (condición con 2 valores)  │
		//   │ Function<T,R>   │ T  → R        (transforma un valor)      │
		//   │ Consumer<T>     │ T  → void     (consume sin devolver nada)│
		//   │ Supplier<T>     │ () → T        (provee un valor)          │
		//   └─────────────────┴──────────────────────────────────────────┘
		// -------------------------------------------------------

		System.out.println("======= TEMA 6: INTERFACES FUNCIONALES ==============");

		// --- Predicate<T>: recibe un valor, devuelve boolean ---
		// Úsalo cuando necesitas VERIFICAR una condición
		Predicate<Integer> esPar = n -> n % 2 == 0;
		System.out.println("¿10 es par?   → " + esPar.test(10));  // true
		System.out.println("¿7  es par?   → " + esPar.test(7));   // false

		System.out.println();

		// --- BiPredicate<A,B>: recibe DOS valores, devuelve boolean ---
		// Útil cuando la condición depende de dos parámetros
		BiPredicate<String, Integer> esLargo = (texto, minimo) -> texto.length() >= minimo;
		System.out.println("¿'Java' tiene 5+ letras?          → " + esLargo.test("Java", 5));
		System.out.println("¿'Spring' tiene 5+ letras?        → " + esLargo.test("Spring", 5));
		System.out.println("¿'Microservices' tiene 10+ letras?→ " + esLargo.test("Microservices", 10));

		System.out.println();

		// --- Function<T,R>: recibe T, devuelve R ---
		// Úsalo cuando necesitas TRANSFORMAR un valor en otro tipo
		Function<String, Integer> contarLetras = texto -> texto.length();
		System.out.println("Longitud de 'Hola'  → " + contarLetras.apply("Hola"));
		System.out.println("Longitud de 'Spring'→ " + contarLetras.apply("Spring"));

		System.out.println();

		// --- Consumer<T>: recibe T, NO devuelve nada (void) ---
		// Úsalo cuando necesitas EJECUTAR una acción con el valor

		// Forma 1: lambda explícita
		Consumer<String> imprimir = texto -> System.out.println(texto);

		// Forma 2: referencia a método (equivalente a la forma 1)
		Consumer<String> imprimirRef = System.out::println;

		imprimir.accept("Java    (usando lambda)");
		imprimirRef.accept("Spring  (usando referencia a método)");

		System.out.println();

		// Consumer con lógica más compleja (múltiples líneas)
		Consumer<String> registrarProducto = producto -> {
			System.out.println("-----------------------------------");
			System.out.println("Registrando producto...");
			System.out.println("Producto : " + producto);
			System.out.println("Estado   : Registro completado ✓");
			System.out.println("-----------------------------------");
		};

		// El mismo Consumer reutilizado con distintos productos
		registrarProducto.accept("Laptop");
		registrarProducto.accept("Mouse");
		registrarProducto.accept("Teclado");

		System.out.println("=====================================================");
	}


	// -------------------------------------------------------
	// MÉTODO DE APOYO PARA EL TEMA DE OPTIONAL
	// -------------------------------------------------------
	// Simula una búsqueda en base de datos.
	// Si el id es 1 → retorna un Optional con usuario.
	// Cualquier otro id → retorna Optional vacío.
	//
	// En la vida real, este método haría una consulta a BD y
	// devolvería Optional para que el llamador maneje el
	// caso "no encontrado" sin riesgo de NullPointerException.
	// -------------------------------------------------------
	public static Optional<Usuario> buscarUsuarioPorId(int id) {
		if (id == 1) {
			return Optional.of(new Usuario("Carlos"));
		} else {
			return Optional.empty();
		}
	}
}