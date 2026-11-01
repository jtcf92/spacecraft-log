import enums.Estado;
import excepciones.EventoCriticoException;
import modeloArma.Arma;
import modeloNave.Escudo;
import modeloNave.MotorDeCohete;
import modeloNave.Nave;
import persistencia.Persistencia;
import simulador.Simulador;

import java.io.IOException;
import java.nio.file.Path;

public class Main {
    public static void main(String[] args) {
        try {
            Persistencia persistencia = new Persistencia(Path.of("datos"));
            persistencia.reiniciar();
            MotorDeCohete motor = new MotorDeCohete("1234", Estado.OPERATIVO);
            motor.setEnergia(50);

            Nave nave = new Nave(motor, "Pegasus");
            nave.setEscudo(new Escudo(80, Estado.OPERATIVO));
            nave.setArma(0, new Arma("Laser", 50, 10, Estado.OPERATIVO));
            nave.setArma(1, new Arma("Torpedo", 100, 25, Estado.OPERATIVO));

            persistencia.registrarEvento("Nave ensamblada: " + nave.getNombre());

            new Simulador(persistencia, nave).ejecutar(15);

            System.out.println("Simulación terminada. Revisa la carpeta datos/");

        } catch (EventoCriticoException ece) {
            System.err.println("EVENTO CRÍTICO: " + ece.getMessage());
            System.err.println("Los datos ya están guardados en datos ");
            //Ejecuta el analizador.
        } catch (IOException ioe) {
            System.err.println("Error de escritura: " + ioe.getMessage());
        } catch (Exception e) {
            System.err.println("Error inesperado: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
