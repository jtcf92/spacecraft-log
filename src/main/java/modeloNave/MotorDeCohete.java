package modeloNave;

import enums.Estado;

public class MotorDeCohete {
    private String numeroDeSerie;
    private int energia;
    private Estado estado;
    private double calor;
    private static final double TEMPERATURA_AMBIENTE = 20.0;
    public static final double CALOR_CRITICO = 120;
    public static final int ENERGIA_MINIMA = 5;
    public MotorDeCohete(String numeroDeSerie,Estado estado) {
        this.calor = 20.0;
        this.energia = 0;
        this.numeroDeSerie = numeroDeSerie;
        this.estado=estado;
    }

    public String getNumeroDeSerie() {
        return numeroDeSerie;
    }
    public String getId() {
        return "MOTOR-" + numeroDeSerie;
    }
    public int getEnergia() {
        return energia;
    }



    public void setNumeroDeSerie(String numeroDeSerie) {
        this.numeroDeSerie = numeroDeSerie;
    }

    public void setEnergia(int energia) {
        if (energia<0){
            energia=0;
        }
        if (energia>100){
            energia=100;
        }
        this.energia = energia;
    }

    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }
    public double getCalor() {
        return calor;
    }

    public void setCalor(double calor) {
        if (calor < 0) {
            throw new IllegalArgumentException("El calor no puede ser negativo.");
        }

        this.calor = calor;
    }

    public void calentar(double grados) {
        this.calor += grados;
    }


    public void enfriar(double grados) {
        this.calor = Math.max(TEMPERATURA_AMBIENTE, this.calor - grados);
    }
    public boolean estaSobrecalentado() {
        return calor >= CALOR_CRITICO;
    }

    public boolean tieneEnergiaMinima() {
        return energia >= ENERGIA_MINIMA;
    }
}

