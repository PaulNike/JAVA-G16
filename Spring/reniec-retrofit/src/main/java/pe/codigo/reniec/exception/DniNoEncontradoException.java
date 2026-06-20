package pe.codigo.reniec.exception;

/**
 * La lanzamos cuando el DNI no aparece en ninguna fuente: ni en cache, ni en
 * la base de datos, ni en RENIEC.
 *
 * Es una RuntimeException (no chequeada) para no andar arrastrando 'throws'
 * por todo el codigo. Quien la atrapa al final es el GlobalExceptionHandler.
 */
public class DniNoEncontradoException extends RuntimeException {

    public DniNoEncontradoException(String dni) {
        super("No se encontraron datos para el DNI: " + dni);
    }
}
