package com.ecommerce.service;

import com.ecommerce.entity.Order;

/**
 * Sesión 1 — Principio de Inversión de Dependencias (D de SOLID)
 *
 * Esta es una interfaz, no una clase. ¿Por qué?
 *
 * OrderService necesita notificar al cliente cuando se crea un pedido.
 * Hoy notificamos con un log. Mañana puede ser un email. Pasado, un SMS.
 * Si OrderService dependiera de LogNotificationService directamente,
 * tendríamos que modificar OrderService cada vez que cambie el canal
 * de notificación. Eso viola el principio de responsabilidad única
 * y dificulta enormemente los tests.
 *
 * Con esta interfaz:
 *   - OrderService depende de la ABSTRACCIÓN (NotificationService)
 *   - Spring decide QUÉ implementación inyectar en tiempo de ejecución
 *   - En tests, Mockito puede crear un doble de prueba de esta interfaz
 *     y verificar que se llamó sin enviar ningún email real
 *
 * Esta es la razón por la que en OrderServiceTest podemos escribir:
 *   verify(notificationService).notifyOrderCreated(any())
 *
 * Sin esta interfaz, eso sería imposible.
 */
public interface NotificationService {

    /**
     * Notifica que un pedido fue creado exitosamente.
     * La implementación concreta decide cómo: log, email, push notification...
     */
    void notifyOrderCreated(Order order);
}
