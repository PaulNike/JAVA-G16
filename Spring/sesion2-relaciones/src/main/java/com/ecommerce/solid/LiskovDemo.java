package com.ecommerce.solid;

/**
 * Sesión 1 — Principio de Sustitución de Liskov (L de SOLID)
 *
 * IMPORTANTE: Este archivo no pertenece a la lógica de producción.
 * Es material de estudio. Léanlo de arriba a abajo junto con el docente.
 *
 * El Principio de Liskov dice, en palabras simples:
 * "Cualquier objeto de una subclase debe poder usarse en lugar de
 *  un objeto de la superclase SIN que el programa se rompa."
 *
 * Vamos a ver primero el problema, luego la solución.
 */
@SuppressWarnings({"unused", "all"})
public class LiskovDemo {

    // ─────────────────────────────────────────────────────────────────────
    // PARTE 1 — LA VIOLACIÓN: herencia que rompe el contrato
    // ─────────────────────────────────────────────────────────────────────

    /**
     * Simulamos la entidad Order de nuestro proyecto (simplificada).
     * Order establece un contrato claro: puedo agregarle items.
     */
    static class Order {
        protected java.util.List<String> items = new java.util.ArrayList<>();

        public void addItem(String item) {
            items.add(item);
        }

        public int itemCount() {
            return items.size();
        }
    }

    /**
     * ¡ANTI-PATRÓN! Esta clase viola LSP.
     *
     * SpecialOrder hereda de Order pero sobreescribe addItem()
     * lanzando una excepción. Rompe el contrato que Order estableció.
     *
     * El problema no es técnico: el código compila sin errores.
     * El problema es conceptual: cualquier método que reciba un Order
     * y llame a addItem() va a EXPLOTAR si le pasamos un SpecialOrder.
     * Y el compilador no nos avisa. El error aparece en producción.
     */
    static class SpecialOrderViolatingLSP extends Order {

        @Override
        public void addItem(String item) {
            // VIOLA LSP: Order prometió que addItem() funciona siempre.
            // Esta subclase rompe esa promesa silenciosamente.
            throw new UnsupportedOperationException(
                "SpecialOrder no acepta items individuales");
        }

        public void addBundle(java.util.List<String> bundle) {
            items.addAll(bundle);
        }
    }

    /**
     * Este método funciona perfectamente con un Order normal.
     * Pero si le pasamos un SpecialOrderViolatingLSP, explota.
     * Eso es una violación de LSP: la subclase NO es sustituible.
     */
    static void procesarPedido(Order order) {
        order.addItem("Producto A");   // ← lanza UnsupportedOperationException
        order.addItem("Producto B");   //   si order es SpecialOrderViolatingLSP
        System.out.println("Pedido con " + order.itemCount() + " items");
    }

    static void demoProblema() {
        procesarPedido(new Order());                    // OK
        procesarPedido(new SpecialOrderViolatingLSP()); // EXPLOTA
    }


    // ─────────────────────────────────────────────────────────────────────
    // PARTE 2 — LA SOLUCIÓN: composición en lugar de herencia rota
    // ─────────────────────────────────────────────────────────────────────

    /**
     * SpecialOrder correcto: no hereda de Order, la CONTIENE.
     *
     * Esto se llama "Composición sobre Herencia" (Composition over Inheritance).
     * En lugar de pretender ser un Order y romper su contrato,
     * SpecialOrder tiene un Order interno y lo usa donde lo necesita.
     *
     * Ahora los contratos son independientes y claros:
     *   - Order: "puedo agregar items uno a uno"
     *   - SpecialOrder: "acepto bundles de items"
     * Nadie engaña a nadie.
     */
    static class SpecialOrderCorrect {

        private final Order innerOrder = new Order();

        public void addBundle(java.util.List<String> bundle) {
            // Delega en el Order interno, respetando su contrato
            bundle.forEach(item -> innerOrder.addItem(item));
        }

        public Order getOrder() {
            return innerOrder;
        }

        public int itemCount() {
            return innerOrder.itemCount();
        }
    }

    static void demoSolucion() {
        SpecialOrderCorrect special = new SpecialOrderCorrect();
        special.addBundle(java.util.List.of("A", "B", "C"));

        // Cuando necesitamos un Order, usamos el interno — LSP respetado
        procesarPedido(special.getOrder());
    }


    // ─────────────────────────────────────────────────────────────────────
    // REGLA PRÁCTICA
    // ─────────────────────────────────────────────────────────────────────
    //
    // Señal de alerta: si en una subclase estás a punto de escribir
    // throw new UnsupportedOperationException() en un método heredado,
    // detente. Ese es el síntoma clásico de una violación de LSP.
    //
    // Pregúntate: ¿realmente esta clase ES-UN del padre?
    // Si la respuesta honesta es "no exactamente", usa composición.
    //
    // "Prefiere composición sobre herencia." — Effective Java, Joshua Bloch
}
