package analizadorForense;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.nio.file.Path;

public class Main {
    private static final Logger LOG = LogManager.getLogger(AnalizadorForense.class);
    public static void main(String[] args) {

        Path rutaDatos = Path.of("datos");
        Path rutaSalida = Path.of("ResultadoForense");

        try {

            new AnalizadorForense(rutaDatos, rutaSalida);

        } catch (Exception e) {
            LOG.error("Fallo en el análisis forense", e);
        }
    }
}
