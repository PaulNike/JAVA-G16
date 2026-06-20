package pe.codigo.reniec.exception;

/**
 * Excepcion de negocio para errores que vienen del API externo de RENIEC.
 *
 * Separamos "el DNI no existe" (DniNoEncontradoException) de
 * "el servicio fallo" (esta clase). Son problemas distintos y merecen
 * codigos HTTP distintos: 404 vs 502.
 */
public class ReniecApiException extends RuntimeException {

    public ReniecApiException(String mensaje) {
        super(mensaje);
    }
}
