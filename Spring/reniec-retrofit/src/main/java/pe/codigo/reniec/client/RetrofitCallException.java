package pe.codigo.reniec.client;

/**
 * Esta excepcion es "de plomeria" interna del cliente Retrofit.
 *
 * Como Retrofit no lanza excepcion solo, nosotros la creamos para poder llevar
 * la informacion del problema desde el cliente hasta el service, que es quien
 * decide el mensaje final para el usuario.
 *
 * Lleva el codigo HTTP cuando RENIEC respondio con error (404, 401, 500...).
 * Si fue un problema de red (no hubo respuesta), el codigo queda en null.
 */
public class RetrofitCallException extends RuntimeException {

    private final Integer statusCode;

    public RetrofitCallException(Integer statusCode, String mensaje) {
        super(mensaje);
        this.statusCode = statusCode;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    /** true cuando no hubo respuesta del servidor (timeout, conexion, DNS...). */
    public boolean esErrorDeRed() {
        return statusCode == null;
    }
}
