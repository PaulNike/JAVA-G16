package com.ecommerce.unit;

import com.ecommerce.discount.NewCustomerDiscount;
import com.ecommerce.discount.NoDiscount;
import com.ecommerce.discount.VipDiscount;
import com.ecommerce.entity.Customer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Sesión 2 — Tests unitarios puros: estrategias de descuento
 *
 * Noten que estas clases no tienen ninguna dependencia de Spring.
 * Son POJO puros que reciben un Customer y devuelven un Double.
 * Por eso se testean sin @SpringBootTest, sin Mockito, sin nada.
 *
 * @ParameterizedTest es la herramienta correcta aquí: la lógica es
 * siempre la misma (aplicar el descuento), solo cambian los valores.
 * Escribir 5 métodos @Test separados para la misma lógica sería
 * duplicación de código en los propios tests, lo que también viola DRY.
 *
 * Cada estrategia se testea independientemente. Eso es posible porque
 * VipDiscount, NewCustomerDiscount y NoDiscount son completamente
 * independientes entre sí. Ese desacoplamiento es OCP en acción:
 * agregar una nueva estrategia no afecta los tests de las existentes.
 */
@DisplayName("DiscountStrategy — cada estrategia funciona correctamente (Strategy Pattern)")
class DiscountStrategyTest {

    // Helpers para crear clientes en el estado que necesitamos probar
    private Customer vipCustomer() {
        Customer c = new Customer("Cliente VIP", "vip@test.com", null);
        c.setVip(true);
        return c;
    }

    private Customer newCustomer() {
        // Customer nuevo: su lista de órdenes está vacía por defecto
        return new Customer("Cliente Nuevo", "nuevo@test.com", null);
    }

    private Customer regularCustomer() {
        return new Customer("Cliente Normal", "normal@test.com", null);
    }


    // ─────────────────────────────────────────────────────────────
    // VipDiscount — aplica 10% de descuento
    // ─────────────────────────────────────────────────────────────

    @ParameterizedTest(name = "total={0} → esperado con 10% off = {1}")
    @CsvSource({
            "100.0,  90.0",
            "200.0, 180.0",
            " 50.0,  45.0",
            "1000.0, 900.0",
            "150.0, 135.0"
    })
    @DisplayName("VipDiscount: aplica 10% correctamente para distintos montos")
    void vipDiscount_aplica10Porciento(double total, double esperado) {
        // ARRANGE
        VipDiscount discount = new VipDiscount();
        Customer customer = vipCustomer();

        // ASSERT pre-condición: verificamos que este descuento SÍ aplica
        // para un cliente VIP antes de verificar el cálculo
        assertTrue(discount.isApplicable(customer),
                "VipDiscount debe ser aplicable a un cliente marcado como VIP");

        // ACT
        Double resultado = discount.apply(total);

        // ASSERT — 100 - 10% = 90, 200 - 10% = 180, etc.
        assertEquals(esperado, resultado, 0.001,
                String.format("%.2f con 10%% de descuento debe ser %.2f", total, esperado));
    }

    @ParameterizedTest(name = "VipDiscount no aplica a cliente no-VIP (total={0})")
    @CsvSource({"100.0", "200.0"})
    @DisplayName("VipDiscount: NO aplica a clientes sin el flag VIP")
    void vipDiscount_noAplica_aClienteNoVip(double total) {
        // ARRANGE — cliente sin el flag vip (isVip = false por defecto)
        VipDiscount discount = new VipDiscount();
        Customer customer = newCustomer();

        // ASSERT — la condición de aplicabilidad debe ser false
        assertFalse(discount.isApplicable(customer),
                "VipDiscount no debe aplicar a clientes con isVip=false");
    }


    // ─────────────────────────────────────────────────────────────
    // NewCustomerDiscount — aplica 5% en la primera compra
    // ─────────────────────────────────────────────────────────────

    @ParameterizedTest(name = "total={0} → esperado con 5% off = {1}")
    @CsvSource({
            "100.0,  95.0",
            "200.0, 190.0",
            " 50.0,  47.5",
            "1000.0, 950.0",
            " 80.0,  76.0"
    })
    @DisplayName("NewCustomerDiscount: aplica 5% correctamente para distintos montos")
    void newCustomerDiscount_aplica5Porciento(double total, double esperado) {
        // ARRANGE — cliente sin órdenes previas (lista vacía)
        NewCustomerDiscount discount = new NewCustomerDiscount();
        Customer customer = newCustomer();

        // ASSERT pre-condición
        assertTrue(discount.isApplicable(customer),
                "NewCustomerDiscount debe aplicar a un cliente sin órdenes previas");

        // ACT
        Double resultado = discount.apply(total);

        // ASSERT
        assertEquals(esperado, resultado, 0.001,
                String.format("%.2f con 5%% de descuento debe ser %.2f", total, esperado));
    }


    // ─────────────────────────────────────────────────────────────
    // NoDiscount — Null Object Pattern: no cambia nada
    // ─────────────────────────────────────────────────────────────

    @ParameterizedTest(name = "total={0} → el mismo total, sin cambio")
    @CsvSource({
            "100.0",
            "200.0",
            "  0.0",
            "999.99",
            " 50.5"
    })
    @DisplayName("NoDiscount: devuelve el total intacto (Null Object Pattern)")
    void noDiscount_noModificaElTotal(double total) {
        // ARRANGE
        NoDiscount discount = new NoDiscount();
        Customer customer = regularCustomer();

        // ASSERT pre-condición — NoDiscount SIEMPRE aplica (es el caso por defecto)
        assertTrue(discount.isApplicable(customer),
                "NoDiscount debe siempre ser aplicable — es el Null Object que evita retornar null");

        // ACT
        Double resultado = discount.apply(total);

        // ASSERT — la función identidad: lo que entra, sale
        assertEquals(total, resultado, 0.001,
                "NoDiscount debe devolver exactamente el mismo total, sin ninguna modificación");
    }
}
