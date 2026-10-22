package modeloArma;

public class Arma {
    private String nombre;
    private int danio;

    public Arma(String nombre, int danio) {
        this.nombre = nombre;
        this.danio = danio;
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
}

