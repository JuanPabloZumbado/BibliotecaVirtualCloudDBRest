package teccr.biblioteca.exception;

/**
 * Se lanza cuando una operación choca con datos existentes (p. ej. un correo ya registrado).
 * El ManejadorErrores la convierte en 409.
 */
public class ConflictoException extends RuntimeException {

    public ConflictoException(String mensaje) {
        super(mensaje);
    }
}
