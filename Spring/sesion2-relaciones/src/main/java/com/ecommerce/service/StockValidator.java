package com.ecommerce.service;

import com.ecommerce.entity.Product;
import org.springframework.stereotype.Component;

/**
 * Sesión 1 — Principio de Responsabilidad Única (S de SOLID)
 *
 * Esta clase tiene UNA sola razón para existir: decidir si hay
 * suficiente stock para atender un pedido. Nada más.
 *
 * ¿Por qué la extraemos de OrderService?
 * Porque antes, esta validación vivía mezclada con la búsqueda
 * de productos, la creación de items y la persistencia del pedido.
 * Cualquier cambio en la regla de negocio ("reservar 10% adicional
 * de stock", "bloquear si stock < 5 unidades", etc.) obligaba a
 * entender todo OrderService para hacer una modificación puntual.
 *
 * Ahora, si cambia la regla de validación, solo tocamos esta clase.
 * Los demás componentes no se enteran.
 *
 * Analogía: en una tienda física, el cajero no baja al almacén
 * a contar las cajas. Eso lo hace el encargado de bodega.
 * StockValidator es ese encargado de bodega.
 */
@Component
public class StockValidator {

    /**
     * Valida que el producto tenga stock suficiente para cubrir la cantidad pedida.
     *
     * La condición es estricta-menor-que: si stock == quantity, el pedido
     * se acepta (el cliente compra exactamente lo que queda en inventario).
     * Pueden revisar ese caso borde en StockValidatorTest.
     *
     * @throws IllegalStateException si no hay stock suficiente,
     *         con un mensaje descriptivo para que el cliente sepa qué falló
     */
    public void validate(Product product, Integer quantity) {
        if (product.getStock() < quantity) {
            throw new IllegalStateException(
                "Stock insuficiente para producto '" + product.getName() +
                "'. Disponible: " + product.getStock() + ", solicitado: " + quantity
            );
        }
    }
}
