package simulador;

import enums.Estado;
import enums.TipoIncidente;
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

    private final Persistencia persistencia;
    private final Nave nave;
    private final Random azar = new Random();

    public Simulador(Persistencia persistencia, Nave nave) {
        this.persistencia = persistencia;
        this.nave = nave;
    }


    //Este es un simulador si se quiere hacer de forma normal
    public void ejecutar(int repeticiones) throws Exception {

        persistencia.registrarEvento("INFO", "SISTEMA",
                "Inicio de la simulación de la nave " + nave.getNombre());
        persistencia.guardarEstado(nave);

        for (int paso = 1; paso <= repeticiones; paso++) {
            ejecutarPaso();
            registrarMediciones();
            comprobarSituacionCritica();
        }

        persistencia.guardarEstado(nave);
        persistencia.registrarEvento("INFO", "SISTEMA", "Fin de la simulación");
    }

    //Este es un metodo     que ejecuta si quieres forzar un incidente
    public void ejecutar(int repeticiones, TipoIncidente incidente) throws Exception {

        persistencia.registrarEvento("INFO", "SISTEMA",
                "Inicio de la simulación de la nave " + nave.getNombre());
        persistencia.guardarEstado(nave);

        for (int paso = 1; paso <= repeticiones; paso++) {
            ejecutarPaso();
            registrarMediciones();
            comprobarSituacionCritica();
        }

        // Después de los pasos normales provocamos el incidente
        provocarIncidente(incidente);
        persistencia.guardarEstado(nave);
        comprobarSituacionCritica();
    }

    private void ejecutarPaso() throws Exception {

        MotorDeCohete motor = nave.getMotor();

        switch (azar.nextInt(4)) {

            case 0 -> {
                if (nave.mover(10)) {
                    persistencia.registrarEvento("INFO", motor.getId(), "Movimiento de 10 unidades");
                } else {
                    persistencia.registrarEvento("AVISO", motor.getId(), "No puede moverse");
                }
            }

            case 1 -> {
                int slot = azar.nextInt(nave.getArmas().length);
                Arma arma = nave.getArmaPorIndice(slot);
                if (arma != null && nave.disparar(slot)) {
                    persistencia.registrarEvento("INFO", arma.getId(), "Disparo realizado");
                } else {
                    persistencia.registrarEvento("AVISO", motor.getId(), "No puede disparar el arma " + slot);
                }
            }

            case 2 -> {
                int danio = 5 + azar.nextInt(10);
                if (nave.recibirDanio(danio)) {
                    persistencia.registrarEvento("INFO", nave.getEscudo().getId(),
                            "Golpe recibido en el escudo");
                }
            }

            default -> {
                motor.enfriar(8);
                motor.setEnergia(motor.getEnergia() + 3);
                persistencia.registrarEvento("INFO", motor.getId(), "Descanso del motor");
            }
        }

        motor.enfriar(2);   // enfriamiento pasivo en cada paso
    }

    private void registrarMediciones() throws Exception {
        MotorDeCohete motor = nave.getMotor();
        Escudo escudo = nave.getEscudo();

        persistencia.registrarMedicion(motor.getId(), TipoMedicion.ENERGIA_MOTOR, motor.getEnergia());
        persistencia.registrarMedicion(motor.getId(), TipoMedicion.CALOR_MOTOR, motor.getCalor());
        persistencia.registrarMedicion(escudo.getId(), TipoMedicion.VIDA_ESCUDO, escudo.getVida());
    }

    // Prepara el problema. No lanza ninguna excepción: de eso se encarga comprobarSituacionCritica
    private void provocarIncidente(TipoIncidente incidente) throws Exception {

        MotorDeCohete motor = nave.getMotor();

        switch (incidente) {

            case SOBRECALENTAMIENTO_MOTOR -> {
                while (!motor.estaSobrecalentado()) {
                    motor.calentar(15);
                    registrarMediciones();
                    persistencia.registrarEvento("AVISO", motor.getId(), "El calor sigue subiendo");
                }
            }

            case FALLO_MOTOR -> {
                motor.setEnergia(0);
                motor.setEstado(Estado.AVERIADO);
                registrarMediciones();
            }

            case PERDIDA_ESCUDOS -> romperEscudo();

            case IMPACTO_DIRECTO -> {
                romperEscudo();
                motor.setEstado(Estado.EMERGENCIA);
                persistencia.registrarEvento("AVISO", motor.getId(), "Impacto directo contra el casco");
            }

            case FALLO_ARMAMENTO -> {
                Arma arma = nave.getArmaPorIndice(0);
                persistencia.registrarMedicion(arma.getId(), TipoMedicion.DANIO_ARMA, 200);
                arma.setEstado(Estado.AVERIADO);
            }

            default -> throw new IllegalArgumentException("Incidente no simulable: " + incidente);
        }
    }

    private void romperEscudo() throws Exception {
        Escudo escudo = nave.getEscudo();

        while (escudo.getVida() > 0) {
            nave.recibirDanio(20);
            registrarMediciones();
            persistencia.registrarEvento("AVISO", escudo.getId(), "Impacto recibido en el escudo");
        }

        escudo.setEstado(Estado.INACTIVO);
    }

    private void comprobarSituacionCritica() throws Exception {

        MotorDeCohete motor = nave.getMotor();
        Escudo escudo = nave.getEscudo();
        String componente = null;
        String causa = null;

        if (motor.estaSobrecalentado()) {
            motor.setEstado(Estado.AVERIADO);
        }

        if (motor.getEstado() == Estado.AVERIADO) {
            componente = motor.getId();
            causa = "Motor fuera de servicio";
        } else if (motor.getEstado() == Estado.EMERGENCIA) {
            componente = motor.getId();
            causa = "Impacto directo sin escudo";
        } else if (escudo != null && escudo.getEstado() == Estado.INACTIVO) {
            componente = escudo.getId();
            causa = "Escudo fuera de servicio";
        } else {
            for (Arma arma : nave.getArmas()) {
                if (arma != null && arma.getEstado() == Estado.AVERIADO) {
                    componente = arma.getId();
                    causa = "Arma fuera de servicio";
                }
            }
        }

        if (causa != null) {

            persistencia.registrarEvento("CRITICO", componente, causa);
            persistencia.guardarEstado(nave);


            throw new EventoCriticoException(causa);
        }
    }


    }
