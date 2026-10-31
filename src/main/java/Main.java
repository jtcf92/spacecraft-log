import enums.Estado;
import modeloArma.Arma;
import modeloNave.Escudo;
import modeloNave.MotorDeCohete;
import modeloNave.Nave;
import persistencia.Persistencia;
import simulador.Simulador;

import java.nio.file.Path;

public class Main {
    public static void main(String[] args) {
        try {
            Persistencia persistencia = new Persistencia(Path.of("datos"));

            MotorDeCohete motor = new MotorDeCohete("1234", Estado.OPERATIVO);
            motor.setEnergia(50);

            Nave nave = new Nave(motor, "Pegasus");
            nave.setEscudo(new Escudo(80, Estado.OPERATIVO));
            nave.setArma(0, new Arma("Laser", 50, 10, Estado.OPERATIVO));
            nave.setArma(1, new Arma("Torpedo", 100, 25, Estado.OPERATIVO));

            persistencia.registrarEvento("Nave ensamblada: " + nave.getNombre());

            new Simulador(persistencia, nave).ejecutar(15);

            System.out.println("Simulación terminada. Revisa la carpeta datos/");

        } catch (Exception e) {
            System.err.println("Error en la simulación: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
