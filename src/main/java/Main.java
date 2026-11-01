import analizadorForense.AnalizadorForense;
import enums.Estado;
import enums.TipoIncidente;
import excepciones.EventoCriticoException;
import modeloArma.Arma;
import modeloNave.Escudo;
import modeloNave.MotorDeCohete;
import modeloNave.Nave;
import persistencia.Persistencia;
import simulador.Simulador;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        Path rutaDatosEntrada = null;
        try {
            rutaDatosEntrada = Path.of("datos");
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
                simulador.ejecutar(4, TipoIncidente.PERDIDA_ESCUDOS);
            } else {
                // Modo normal: el simulador elige un incidente al azar
                simulador.ejecutar(4);
            }

        } catch (EventoCriticoException e) {

        } catch (Exception e) {
            System.err.println("Error inesperado: " + e.getMessage());
        }
    }
}
