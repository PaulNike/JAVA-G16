package com.ecommerce.service;

import com.ecommerce.entity.Category;
import com.ecommerce.entity.OrderItem;
import com.ecommerce.entity.Product;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Sesión 2 — Tests unitarios puros: PriceCalculator
 *
 * Pirámide de testing: este es el nivel base de la pirámide.
 * Son los tests más rápidos, más baratos de escribir y más fáciles
 * de mantener. Cuando algo falla aquí, sabemos exactamente qué falla
 * y dónde, sin ruido de base de datos ni contexto de Spring.
 *
 * Noten que creamos PriceCalculator con new PriceCalculator().
 * No hay @SpringBootTest, no hay @Autowired, no hay base de datos.
 * Esto es posible porque PriceCalculator no tiene dependencias externas.
 * Un test debería ser tan simple como la clase que testea.
 *
 * Patrón AAA (Arrange-Act-Assert): todos los tests siguen esta estructura.
 * Arrange = preparar los datos de entrada
 * Act     = ejecutar el método bajo prueba
 * Assert  = verificar el resultado
 * Cuando los tres pasos están separados visualmente, el test se convierte
 * en documentación: dice exactamente qué hace el sistema, con qué entrada
 * y qué resultado se espera.
 */
@DisplayName("PriceCalculator — calcula totales correctamente")
class PriceCalculatorTest {

    private PriceCalculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new PriceCalculator();
    }

    // Helper para no repetir la construcción de Product + OrderItem en cada test
    private OrderItem item(double price, int quantity) {
        Product product = new Product("Producto Test", price, 100, Category.ELECTRONICS);
        return new OrderItem(product, quantity);
    }



    @Test
    @DisplayName("Con lista vacía: devuelve 0.0")
    void calcular_conListaVacia_devuelveCero() {
        // ARRANGE
        List<OrderItem> itemsVacios = List.of();

        // ACT
        Double resultado = calculator.calculate(itemsVacios);

        // ASSERT
        assertEquals(0.0, resultado, 0.001,
                "Lista vacía debe devolver exactamente 0.0");
    }

    @Disabled("bug #123 pendiente de fix")
    @Test
    @DisplayName("Con null: devuelve 0.0 sin NullPointerException")
    void calcular_conNull_devuelveCeroSinExcepcion() {
        // ARRANGE — null como entrada borde (no debería pasar en producción,
        // pero el método debe ser robusto ante ello)

        // ACT
        Double resultado = calculator.calculate(null);

        // ASSERT — null se trata igual que lista vacía
        assertEquals(0.0, resultado, 0.001,
                "null debe tratarse igual que lista vacía, nunca debe lanzar NPE");
    }

    /**
     * Test parametrizado: misma lógica de prueba, 5 conjuntos de datos distintos.
     *
     * @ParameterizedTest evita duplicar tests casi idénticos que solo cambian
     * en los valores. @CsvSource provee las filas de datos en formato CSV.
     * El nombre del test muestra los valores de cada ejecución.
     *
     * Lean los valores y noten que cubrimos: precio entero, precio con decimales,
     * cantidad unitaria, cantidad grande, y precio de centavos (9.99).
     */
    @ParameterizedTest(name = "precio={0} × cantidad={1} = {2}")
    @CsvSource({
            "10.0,  1,   10.0",   // cantidad 1 — el caso más simple
            "50.0,  2,  100.0",   // duplicado exacto
            "25.5,  4,  102.0",   // precio con decimales
            "100.0, 3,  300.0",   // precio alto
            "9.99, 10,   99.9"    // precio de centavos × decena
    })
    @DisplayName("Subtotal parametrizado: precio × cantidad = esperado")
    void calcular_subtotalParametrizado(double precio, int cantidad, double esperado) {
        // ARRANGE
        OrderItem item = item(precio, cantidad);

        // ACT
        Double resultado = calculator.calculate(List.of(item));

        // ASSERT
        assertEquals(esperado, resultado, 0.01,
                String.format("%.2f × %d debe ser %.2f", precio, cantidad, esperado));
    }

    @Nested
    @DisplayName("Cuando no hay items")
    class SinItems {

        @Test
        @DisplayName("Con lista null: devuelve 0.0")
        void calcular_conNull_devuelveCero() {
            // ACT
            double resultado = calculator.calculate(null);

            // ASSERT
            assertEquals(0.0, resultado, 0.001,
                    "Si la lista es null debe devolver 0.0");
        }

        @Test
        @DisplayName("Con lista vacía: devuelve 0.0")
        void calcular_conListaVacia_devuelveCero() {
            // ACT
            double resultado = calculator.calculate(List.of());

            // ASSERT
            assertEquals(0.0, resultado, 0.001,
                    "Si la lista está vacía debe devolver 0.0");
        }
    }

    @Nested
    @DisplayName("Cuando hay items")
    class ConItems {

        @Test
        @DisplayName("Con 1 item: calcula precio × cantidad correctamente")
        void calcular_conUnItem_devuelveSubtotalCorrecto() {
            // ARRANGE
            List<OrderItem> items = List.of(
                    item(50.0, 2)
            );

            // ACT
            double resultado = calculator.calculate(items);

            // ASSERT
            assertEquals(100.0, resultado, 0.001,
                    "50.0 × 2 debe ser 100.0");
        }

        @Test
        @DisplayName("Con múltiples items: suma todos los subtotales")
        void calcular_conMultiplesItems_sumaCorrecta() {
            // ARRANGE
            List<OrderItem> items = List.of(
                    item(30.0, 3),
                    item(10.0, 5)
            );

            // ACT
            double resultado = calculator.calculate(items);

            // ASSERT
            assertEquals(140.0, resultado, 0.001,
                    "30×3 + 10×5 debe ser 140.0");
        }

    }
}
