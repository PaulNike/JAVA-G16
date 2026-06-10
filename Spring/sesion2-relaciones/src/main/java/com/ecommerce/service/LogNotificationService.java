package com.ecommerce.service;

import com.ecommerce.entity.Order;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Implementación de NotificationService que notifica mediante logs.
 *
 * Esta es nuestra implementación inicial. En el módulo de integración
 * del curso crearemos EmailNotificationService que enviará correos reales.
 * Lo importante es que OrderService no necesitará cambiar en absoluto:
 * Spring simplemente inyectará la nueva implementación.
 *
 * Ese es exactamente el Principio de Inversión de Dependencias funcionando:
 * el código de alto nivel (OrderService) no depende del código de bajo nivel
 * (este servicio de log o el futuro de email); ambos dependen de la
 * abstracción (NotificationService).
 *
 * Noten el uso de @Service en lugar de @Component. Técnicamente son
 * equivalentes, pero @Service comunica que esta clase contiene lógica
 * de negocio o integración. Es una convención semántica importante.
 */
@Service
@Slf4j
public class LogNotificationService implements NotificationService, DataSales {

    @Override
    public void notifyOrderCreated(Order order) {
        // Por ahora solo registramos en el log.
        // En producción real esto enviaría un email o una notificación push.
        log.info("[NOTIFICACION] Pedido #{} creado para el cliente '{}'. Total: S/ {}",
                order.getId(),
                order.getCustomer().getName(),
                order.getTotal());
    }

    @Override
    public void createLog(String message) {

    }
}
