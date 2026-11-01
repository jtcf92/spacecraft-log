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

import java.nio.file.Path;

public class Main {
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
            System.out.println("Se ha detectado un evento crítico: " + e.getMessage());
            System.out.println("Se activa el analizador forense...");

            try {
                AnalizadorForense analizador =
                        new AnalizadorForense(rutaDatosEntrada, rutaDatosSalida);

                System.out.println("Incidente identificado: " + analizador.getIncidente());
                System.out.println("Compruebe los ficheros generados en " + rutaDatosSalida);

            } catch (Exception ex) {
                System.err.println("Error en el analizador: " + ex.getMessage());
            }

        } catch (Exception e) {
            System.err.println("Error inesperado: " + e.getMessage());
        }
    }
}