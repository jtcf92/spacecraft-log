
package enums;


public enum TipoMedicion {

    // (código, mínimo normal, máximo normal)
    ENERGIA_MOTOR(1, 10, 100),
    VIDA_ESCUDO(2, 20, 100),
    DANIO_ARMA(3, 0, 150),
    CALOR_MOTOR(4, 20, 90);
    private final int codigo;
    private final double minNormal;
    private final double maxNormal;

    TipoMedicion(int codigo, double minNormal, double maxNormal) {
        this.codigo = codigo;
        this.minNormal = minNormal;
        this.maxNormal = maxNormal;
    }

    public int getCodigo() {
        return codigo;
    }

    public double getMinNormal() {
        return minNormal;
    }

    public double getMaxNormal() {
        return maxNormal;
    }

    // Un valor es anómalo si se sale del rango habitual de su tipo
    public boolean esAnomalo(double valor) {
        return valor < minNormal || valor > maxNormal;
    }

    public static TipoMedicion desdeCodigo(int codigo) {
        for (TipoMedicion tipo : values()) {
            if (tipo.codigo == codigo) {
                return tipo;
            }
        }
        throw new IllegalArgumentException("Tipo de medición desconocido: " + codigo);
    }
}