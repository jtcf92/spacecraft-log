package simulador;

import enums.Estado;
import enums.TipoMedicion;
import excepciones.EventoCriticoException;
import modeloArma.Arma;
import modeloNave.Escudo;
import modeloNave.MotorDeCohete;
import modeloNave.Nave;
import persistencia.Persistencia;

import java.nio.file.Path;
import java.util.Random;

public class Simulador {
    private static final double CALOR_CRITICO = 120;
    private static final int ENERGIA_MINIMA = 5;
    private final Persistencia persistencia;
    private final Nave nave;
    private final Random azar = new Random();

    private final String idMotor;
    private static final String ID_ESCUDO = "ESCUDO-1";

    public Simulador(Persistencia persistencia, Nave nave) {
        this.persistencia = persistencia;
        this.nave = nave;
        this.idMotor = "MOTOR-" + nave.getMotor().getNumeroDeSerie();
    }

    public void ejecutar(int pasos) throws Exception {

        persistencia.registrarEvento("Inicio de la simulación de la nave " + nave.getNombre());
        persistencia.guardarEstado(nave);

        for (int paso = 1; paso <= pasos; paso++) {
            ejecutarPaso();
            registrarMediciones();
            comprobarSituacionCritica();
        }
//Guardamos el estado final de la nave
        persistencia.guardarEstado(nave);
        persistencia.registrarEvento("Fin de la simulación");
    }

    private void ejecutarPaso() throws Exception {

        MotorDeCohete motor = nave.getMotor();

        switch (azar.nextInt(4)) {

            case 0 -> { // mover
                if (nave.mover(10)) {
                    persistencia.registrarEvento("MOVIMIENTO " + idMotor
                            + " distancia=10 calor=" + motor.getCalor());
                } else {
                    persistencia.registrarEvento("AVISO " + idMotor + " no puede moverse");
                }
            }

            case 1 -> { // disparar un arma al azar
                int slot = azar.nextInt(nave.getArmas().length);
                Arma arma = nave.getArmaPorIndice(slot);
                if (nave.disparar(slot)) {
                    persistencia.registrarEvento("DISPARO " + idMotor + " arma="
                            + arma.getNombre() + " calor=" + motor.getCalor());
                } else {
                    persistencia.registrarEvento("AVISO " + idMotor + " no puede disparar slot=" + slot);
                }
            }

            case 2 -> { // recibir daño
                int danio = 5 + azar.nextInt(10);
                if (nave.recibirDanio(danio)) {
                    persistencia.registrarEvento("IMPACTO " + ID_ESCUDO + " danio=" + danio);
                }
            }

            default -> { // descansar
                motor.enfriar(8);
                motor.setEnergia(motor.getEnergia() + 3);
                persistencia.registrarEvento("DESCANSO " + idMotor + " calor=" + motor.getCalor());
            }
        }

        motor.enfriar(2);   // enfriamiento pasivo en cada paso
    }

    private void registrarMediciones() throws Exception {
        persistencia.registrarMedicion(idMotor, TipoMedicion.ENERGIA_MOTOR, nave.getMotor().getEnergia());
        persistencia.registrarMedicion(idMotor, TipoMedicion.CALOR_MOTOR, nave.getMotor().getCalor());
        persistencia.registrarMedicion(ID_ESCUDO, TipoMedicion.VIDA_ESCUDO, nave.getEscudo().getVida());
    }
    private void comprobarSituacionCritica() throws Exception {

        MotorDeCohete motor = nave.getMotor();
        String causa = null;

        if (motor.getCalor() >= CALOR_CRITICO) {
            motor.setEstado(Estado.AVERIADO);
            causa = "Motor sobrecalentado";
        } else if (motor.getEnergia() < ENERGIA_MINIMA) {
            motor.setEstado(Estado.INACTIVO);
            causa = "Motor sin energía";
        } else if (nave.getEscudo() != null && nave.getEscudo().getVida() <= 0) {
            nave.getEscudo().setEstado(Estado.INACTIVO);
            causa = "Escudo destruido";
        }

        if (causa != null) {
            // 1º persistir la anomalía (TXT + XML; el BIN ya se escribió en este paso)
            persistencia.registrarEvento("CRITICO " + idMotor + " " + causa);
            persistencia.guardarEstado(nave);

            // 2º lanzar la excepción
            throw new EventoCriticoException(causa);
        }
    }

}