package modeloNave;

public class Escudo {
private boolean activo;
private int energia;

    public Escudo(int energia, boolean activo) {
        this.energia = energia;
        this.activo = activo;
    }

    public boolean isActivo() {
        return activo;
    }

    public int isEnergia() {
        return energia;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public void setEnergia(int energia) {
        this.energia = energia;
    }
}
