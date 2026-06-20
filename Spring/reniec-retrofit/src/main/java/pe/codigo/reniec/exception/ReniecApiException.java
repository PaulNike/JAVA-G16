package pe.codigo.reniec.exception;

/**
 * La usamos para errores que vienen del API externo de RENIEC.
 *
 * Fijense que separamos dos cosas distintas: "el DNI no existe"
 * (DniNoEncontradoException) y "el servicio fallo" (esta clase). Son
 * problemas diferentes y por eso merecen codigos HTTP diferentes: 404 vs 502.
 */
public class ReniecApiException extends RuntimeException {

    public ReniecApiException(String mensaje) {
        super(mensaje);
    }
}
