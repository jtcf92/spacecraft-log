package enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TipoMedicionTest {

    @Test
    void calorFueraDeRangoEsAnomalo() {
        assertTrue(TipoMedicion.CALOR_MOTOR.esAnomalo(95.0));   // máximo normal: 90
        assertFalse(TipoMedicion.CALOR_MOTOR.esAnomalo(50.0));
    }

    @Test
    void desdeCodigoDevuelveElTipoCorrecto() {
        assertEquals(TipoMedicion.CALOR_MOTOR, TipoMedicion.desdeCodigo(4));
    }
}