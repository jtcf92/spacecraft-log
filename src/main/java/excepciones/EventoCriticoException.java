package excepciones;

public class EventoCriticoException extends Exception {
    public EventoCriticoException(String mensaje) {
        super(mensaje);
    }
}