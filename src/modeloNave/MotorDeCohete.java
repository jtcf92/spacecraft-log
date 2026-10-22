package modeloNave;

public class MotorDeCohete {
    private String numeroDeSerie;
    private int energia;
    private boolean activo;
    public MotorDeCohete( String numeroDeSerie) {
        this.energia = 0;
        this.numeroDeSerie = numeroDeSerie;
        activo= false;
    }

    public String getNumeroDeSerie() {
        return numeroDeSerie;
    }

    public int getEnergia() {
        return energia;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setNumeroDeSerie(String numeroDeSerie) {
        this.numeroDeSerie = numeroDeSerie;
    }

    public void setEnergia(int energia) {
        this.energia = energia;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
}
