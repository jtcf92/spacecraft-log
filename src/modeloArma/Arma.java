package modeloArma;

import enums.Estado;

public class Arma {
    private String nombre;
    private int danio;
    private int costeEnergia;
    private Estado estado;
    public Arma(String nombre, int danio, int costeEnergia, Estado estado) {
        this.nombre = nombre;
        this.danio = danio;
        this.costeEnergia= costeEnergia;
        this.estado=estado;
    }

    public String getNombre() {
        return nombre;
    }

    public int getDanio() {
        return danio;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setDanio(int danio) {
        this.danio = danio;
    }

    public int getCosteEnergia() {
        return costeEnergia;
    }

    public void setCosteEnergia(int costeEnergia) {
        this.costeEnergia = costeEnergia;
    }
}

