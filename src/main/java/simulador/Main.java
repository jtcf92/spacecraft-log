package simulador;

import analizadorForense.AnalizadorForense;
import enums.Estado;
import enums.TipoIncidente;
import excepciones.EventoCriticoException;
import modeloArma.Arma;
import modeloNave.Escudo;
import modeloNave.MotorDeCohete;
import modeloNave.Nave;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import persistencia.Persistencia;
import simulador.Simulador;

import java.nio.file.Path;

public class Main {

    private static final Logger LOG = LogManager.getLogger(Main.class);
    public static void main(String[] args) {

        Path rutaDatosEntrada = Path.of("datos");
        Path rutaDatosSalida = Path.of("ResultadoForense");

        try {
            Persistencia persistencia = new Persistencia(rutaDatosEntrada);
            persistencia.reiniciar();

            MotorDeCohete motor = new MotorDeCohete("1234", Estado.OPERATIVO);
            motor.setEnergia(50);

            Nave nave = new Nave(motor, "Pegasus");
            nave.setEscudo(new Escudo(80, Estado.OPERATIVO));
            nave.setArma(0, new Arma("Laser", 50, 10, Estado.OPERATIVO));
            nave.setArma(1, new Arma("Torpedo", 100, 25, Estado.OPERATIVO));

            Simulador simulador = new Simulador(persistencia, nave);

            // ===== ELIGE EL MODO =====
            boolean modoPrueba = true;

            if (modoPrueba) {
                // Modo prueba: escribes tú el incidente que quieres forzar
                simulador.ejecutar(4, TipoIncidente.FALLO_ARMAMENTO);
            } else {
                // Modo normal: el simulador elige un incidente al azar
                simulador.ejecutar(4);
            }

        } catch (EventoCriticoException e) {
            LOG.error("Evento crítico capturado: {}", e.getMessage());

            LOG.info("Simulación detenida. Los ficheros están en {}", rutaDatosEntrada);
            //Es por si quiero hacer los dos al mismo tiempo
           /* try {
                AnalizadorForense analizador =
                        new AnalizadorForense(rutaDatosEntrada, rutaDatosSalida);

                LOG.info("Análisis terminado: incidente {}, resultados en {}",
                        analizador.getIncidente(), rutaDatosSalida);

            } catch (Exception ex) {
                LOG.error("Fallo en el analizador forense", ex);
            }*/

        } catch (Exception e) {
            LOG.error("Error inesperado en el simulador", e);
        }
    }

}