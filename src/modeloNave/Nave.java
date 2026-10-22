package modeloNave;

import modeloArma.Arma;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public class Nave {
    private String nombre;
    private MotorDeCohete motor;
    private Map<PosicionEscudo, Escudo> escudos;
    private Arma[] armas;

    public Nave(MotorDeCohete motor, String nombre) {
        this.motor = motor;
        this.nombre = nombre;
        this.escudos = new EnumMap<>(PosicionEscudo.class);
        this.armas = new Arma[2];
    }



    public MotorDeCohete getMotor() {
        return motor;
    }

    public String getNombre() {
        return nombre;
    }

    public Escudo getEscudo(PosicionEscudo posicion) {
        return escudos.get(posicion);
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

    public boolean setEscudo(PosicionEscudo posicion, Escudo escudo){
        if (posicion == null || escudo == null) {
            return false;
        }
        if (!escudos.containsKey(posicion)){
            escudos.put(posicion,escudo);
            return true;
        }
        return false;
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
}