package pe.codigo.reniec.exception;

/**
 * Error que viene del API externo de RENIEC. Lo separamos del "no encontrado"
 * porque son problemas distintos y merecen codigos HTTP distintos (502 vs 404).
 */
public class ReniecApiException extends RuntimeException {
    public ReniecApiException(String mensaje) {
        super(mensaje);
    }
}
