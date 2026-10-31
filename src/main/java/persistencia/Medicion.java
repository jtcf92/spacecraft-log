package persistencia;


import enums.TipoMedicion;

public class Medicion {

    private final int id;
    private final String idComponente;
    private final TipoMedicion tipo;

    private double valor;
    private boolean anomala;

    public Medicion(int id, String idComponente,
                    TipoMedicion tipo, double valor,
                    boolean anomala) {

        this.id = id;
        this.idComponente = idComponente;
        this.tipo = tipo;
        this.valor = valor;
        this.anomala = anomala;
    }

    public int getId() {
        return id;
    }

    public String getIdComponente() {
        return idComponente;
    }

    public TipoMedicion getTipo() {
        return tipo;
    }

    public double getValor() {
        return valor;
    }

    public boolean isAnomala() {
        return anomala;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public void setAnomala(boolean anomala) {
        this.anomala = anomala;
    }
}

