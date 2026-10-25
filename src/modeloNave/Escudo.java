package modeloNave;

import enums.Estado;

public class Escudo {
private Estado estado;
private int vida;

    public Escudo(int vida, Estado estado) {
        if (vida<0){
            vida=0;
        }
        if (vida>100){
            vida=100;
        }
        this.vida = vida;
        this.estado = estado;
    }



    public int getVida() {
        return vida;
    }

    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    public void setVida(int energia) {
        this.vida = energia;
    }
}
