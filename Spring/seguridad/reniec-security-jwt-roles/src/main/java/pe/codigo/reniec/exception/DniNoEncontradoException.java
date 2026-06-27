package pe.codigo.reniec.exception;

/**
 * Se lanza cuando el DNI no existe en ninguna fuente (cache, bd ni RENIEC).
 * Es una RuntimeException; la atrapa el GlobalExceptionHandler.
 */
public class DniNoEncontradoException extends RuntimeException {
    public DniNoEncontradoException(String dni) {
        super("No se encontraron datos para el DNI: " + dni);
    }
}
