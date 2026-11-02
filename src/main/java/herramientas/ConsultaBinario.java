package herramientas;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import persistencia.Medicion;
import persistencia.RegistroBinario;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class ConsultaBinario {

    private static final Logger LOG = LogManager.getLogger(ConsultaBinario.class);

    private static final int ID_REGISTRO = 3;

    public static void main(String[] args) {

        Path original = Path.of("datos", "mediciones.dat");
        Path copia = Path.of("datos", "mediciones_demo.dat");

        try {
            if (!Files.exists(original)) {
                LOG.warn("No existe {}: ejecuta antes el simulador", original);
                return;
            }

            Files.copy(original, copia, StandardCopyOption.REPLACE_EXISTING);

            RegistroBinario binario = new RegistroBinario(copia);
            long total = binario.getCantidadRegistros();
            long tamanoRegistro = RegistroBinario.getTamanoRegistro();

            LOG.info("Fichero {}: {} registros de {} bytes cada uno",
                    copia, total, tamanoRegistro);

            if (total < ID_REGISTRO) {
                LOG.warn("El fichero tiene menos de {} registros", ID_REGISTRO);
                return;
            }

            long posicion = (ID_REGISTRO - 1) * tamanoRegistro;
            Medicion medicion = binario.leer(ID_REGISTRO);

            LOG.info("Consulta del registro {} (byte {}): {} {} = {} anómala={}",
                    ID_REGISTRO, posicion, medicion.getIdComponente(),
                    medicion.getTipo(), medicion.getValor(), medicion.isAnomala());

            double valorAntiguo = medicion.getValor();
            double valorNuevo = valorAntiguo + 10;

            medicion.setValor(valorNuevo);
            medicion.setAnomala(medicion.getTipo().esAnomalo(valorNuevo));
            binario.modificar(medicion);


            Medicion modificada = binario.leer(ID_REGISTRO);

            LOG.info("Registro {} modificado: valor {} -> {} anómala={}",
                    ID_REGISTRO, valorAntiguo, modificada.getValor(), modificada.isAnomala());
            LOG.info("El fichero sigue teniendo {} registros ({} bytes)",
                    binario.getCantidadRegistros(), Files.size(copia));

        } catch (Exception e) {
            LOG.error("Error en la consulta del fichero binario", e);
        }
    }
}