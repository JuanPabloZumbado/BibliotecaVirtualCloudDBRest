package teccr.biblioteca.exception;

/**
 * Se lanza cuando un recurso solicitado no existe. El ManejadorErrores la convierte en 404.
 */
public class RecursoNoEncontradoException extends RuntimeException {

    public RecursoNoEncontradoException(String recurso, Object id) {
        super(recurso + " no encontrado con id: " + id);
    }
}
