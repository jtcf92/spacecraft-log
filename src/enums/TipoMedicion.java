
package enums;

public enum TipoMedicion {

    ENERGIA_MOTOR(1),
    VIDA_ESCUDO(2),
    DANIO_ARMA(3),
    CALOR_MOTOR(4);

    private final byte codigo;

    TipoMedicion(int codigo) {
        this.codigo = (byte) codigo;
    }

    public byte getCodigo() {
        return codigo;
    }

    public static TipoMedicion desdeCodigo(byte codigo) {

        for (TipoMedicion tipo : values()) {
            if (tipo.codigo == codigo) {
                return tipo;
            }
        }

        throw new IllegalArgumentException(
                "Tipo de medición desconocido: " + codigo
        );
    }
}
