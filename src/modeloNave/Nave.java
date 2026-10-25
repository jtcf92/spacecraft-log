package modeloNave;

import interfaces.Danable;
import interfaces.Movible;
import modeloArma.Arma;

import java.util.EnumMap;
import java.util.Map;

public class Nave implements Danable, Movible {
    private String nombre;
    private MotorDeCohete motor;
    private Escudo escudo;
    private Arma[] armas;

    public Nave(MotorDeCohete motor, String nombre) {
        this.motor = motor;
        this.nombre = nombre;
        this.escudo =escudo;
        this.armas = new Arma[2];
    }



    public MotorDeCohete getMotor() {
        return motor;
    }

    public String getNombre() {
        return nombre;
    }



    public Arma getArma(int indice) {
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
    public void recibirDanio(int cantidad) {

    }

    @Override
    public boolean mover(int distancia) {
        return false;
    }
}