package com.ecommerce.unit;

import com.ecommerce.entity.Category;
import com.ecommerce.entity.Product;
import com.ecommerce.service.StockValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Sesión 2 — Tests unitarios puros: StockValidator
 *
 * Lo primero que noto cuando reviso estos tests es que hay uno
 * que estudiantes principiantes omiten siempre: el caso borde.
 *
 * Pregunten: ¿qué pasa cuando el cliente pide exactamente las últimas
 * 5 unidades y en stock hay exactamente 5?
 * ¿Debe aceptarse o rechazarse el pedido?
 *
 * La respuesta correcta de negocio es: debe ACEPTARSE.
 * El cliente compra exactamente lo que queda. Es un caso válido.
 * Pero si la condición estuviera mal escrita como stock <= quantity
 * en lugar de stock < quantity, ese pedido se rechazaría en silencio.
 *
 * Los casos borde (boundary cases) son exactamente esas situaciones
 * donde el comportamiento cambia de "funciona" a "falla".
 * Son los más importantes de testear y los más frecuentes de olvidar.
 */
@DisplayName("StockValidator — valida disponibilidad de stock")
class StockValidatorTest {

    private StockValidator validator;

    @BeforeEach
    void setUp() {
        validator = new StockValidator();
    }

    private Product productWithStock(int stock) {
        return new Product("Producto Test", 100.0, stock, Category.ELECTRONICS);
    }

    @Test
    @DisplayName("Happy path: no lanza excepción con stock suficiente")
    void validate_noLanzaExcepcion_cuandoHayStockSuficiente() {
        // ARRANGE — stock 10, pedido 5: hay de sobra
        Product product = productWithStock(10);

        // ACT & ASSERT
        // assertDoesNotThrow verifica explícitamente que el "camino feliz"
        // no lanza ninguna excepción. Es útil porque documenta la intención:
        // "este caso debe pasar sin error".
        assertDoesNotThrow(
                () -> validator.validate(product, 5),
                "Con stock 10 y pedido 5, no debe haber excepción"
        );
    }

    @Test
    @DisplayName("Stock cero: lanza IllegalStateException")
    void validate_lanzaExcepcion_cuandoStockEsCero() {
        // ARRANGE — stock 0: no hay nada para vender
        Product product = productWithStock(0);

        // ACT & ASSERT
        // assertThrows devuelve la excepción para que podamos inspeccionarla.
        // Verificamos también el mensaje: en producción, ese mensaje llega
        // al cliente en el response body (ver GlobalExceptionHandler).
        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> validator.validate(product, 1),
                "Stock 0 debe lanzar IllegalStateException"
        );

        // El mensaje debe ser útil para el cliente de la API
        assertTrue(ex.getMessage().contains("Stock insuficiente"),
                "El mensaje de error debe mencionar 'Stock insuficiente'");
    }

    @Test
    @DisplayName("Pedido mayor que stock: lanza IllegalStateException")
    void validate_lanzaExcepcion_cuandoCantidadMayorQueStock() {
        // ARRANGE — stock 5, pedido 10: más de lo disponible
        Product product = productWithStock(5);

        // ACT & ASSERT
        assertThrows(
                IllegalStateException.class,
                () -> validator.validate(product, 10),
                "Stock 5 con pedido 10 debe lanzar IllegalStateException"
        );
    }

    @Test
    @DisplayName("BORDE: quantity == stock exacto debe aceptarse")
    void validate_noLanzaExcepcion_cuandoQuantityEsIgualAlStock() {
        // ARRANGE — caso borde: pido exactamente lo que hay
        Product product = productWithStock(5);

        // ACT & ASSERT
        // Este es el caso límite más importante del validador.
        // La condición en StockValidator es stock < quantity (estricto).
        // Si fuera stock <= quantity, este test fallaría y nos alertaría
        // de un bug que rechaza pedidos perfectamente válidos.
        assertDoesNotThrow(
                () -> validator.validate(product, 5),
                "Comprar exactamente las últimas 5 unidades debe ser aceptado"
        );
    }
}
