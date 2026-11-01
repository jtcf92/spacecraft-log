package modeloNave;

import enums.Estado;
import interfaces.Danable;
import interfaces.Movible;
import modeloArma.Arma;
import persistencia.RegistroTexto;

import java.io.IOException;
import java.nio.file.Path;
import java.util.EnumMap;
import java.util.Map;

public class Nave implements Danable, Movible {

    private String nombre;
    private MotorDeCohete motor;
    private Escudo escudo;
    private Arma[] armas;

    public Nave(MotorDeCohete motor, String nombre)  {
        this.motor = motor;
        this.nombre = nombre;
        this.armas = new Arma[2];


    }



    public MotorDeCohete getMotor() {
        return motor;
    }

    public String getNombre() {
        return nombre;
    }



    public Arma getArmaPorIndice(int indice) {
        if (indice < 0 || indice >= armas.length) {
            return null;
        }

        return armas[indice];
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setMotor(MotorDeCohete motor) {
        this.motor = motor;
    }

    public Escudo getEscudo() {
        return escudo;
    }

    public void setEscudo(Escudo escudo) {
        this.escudo = escudo;
    }

    public boolean setArma(int indice, Arma arma) {
        if (indice < 0 || indice >= armas.length || arma == null) {
            return false;
        }
        if (armas[indice] != null) {
            return false;
        }
        armas[indice] = arma;

        return true;
    }

    @Override
    public boolean recibirDanio(int cantidad) {
        if (cantidad <= 0 || escudo == null) {
            return false;
        }

        if (escudo.getVida() <= 0) {
            return false;
        }

        int vidaRestante = escudo.getVida() - cantidad;

        if (vidaRestante < 0) {
            vidaRestante = 0;
        }

        escudo.setVida(vidaRestante);

        return true;

    }


    @Override
    public boolean mover(int distancia) {
        if (distancia <= 0) {
            return false;
        }
        if (motor == null) {
            return false;
        }
        if (motor.getEstado() != Estado.OPERATIVO) {
            return false;
        }
        if (!motor.tieneEnergiaMinima()) {
            return false;
        }

        motor.setEnergia(motor.getEnergia() - 5);
        motor.calentar(8);
        return true;
    }
    public boolean disparar(int indice) {
        Arma arma = getArmaPorIndice(indice);

        if (arma == null || arma.getEstado() != Estado.OPERATIVO) {
            return false;
        }
        if (motor == null || motor.getEstado() != Estado.OPERATIVO) {
            return false;
        }
        if (motor.getEnergia() < arma.getCosteEnergia()) {
            return false;
        }

        motor.setEnergia(motor.getEnergia() - arma.getCosteEnergia());
        motor.calentar(arma.getCosteEnergia() * 0.8);   // más coste = más calor
        return true;
    }
    public Arma[] getArmas() {
        return armas;
    }

    public void setArmas(Arma[] nuevasArmas) {
        this.armas = new Arma[nuevasArmas.length];
        for (int i = 0; i < nuevasArmas.length; i++) {
            setArma(i, nuevasArmas[i]);
        }
    }
}