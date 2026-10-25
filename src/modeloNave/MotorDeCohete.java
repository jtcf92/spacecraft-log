package modeloNave;

import enums.Estado;

public class MotorDeCohete {
    private String numeroDeSerie;
    private int energia;
    private Estado estado;
    public MotorDeCohete( int energia,String numeroDeSerie,Estado estado) {
        this.energia = energia;
        this.numeroDeSerie = numeroDeSerie;
        this.estado=estado;
    }

    public String getNumeroDeSerie() {
        return numeroDeSerie;
    }

    public int getEnergia() {
        return energia;
    }



    public void setNumeroDeSerie(String numeroDeSerie) {
        this.numeroDeSerie = numeroDeSerie;
    }

    public void setEnergia(int energia) {
        this.energia = energia;
    }

    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }
}
