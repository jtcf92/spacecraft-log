package persistencia;

import enums.TipoMedicion;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RegistroBinarioTest {

    @TempDir
    Path directorio;

    @Test
    void anadeLeeDirectoYModificaUnRegistro() throws IOException {
        Path ruta = directorio.resolve("mediciones.dat");
        RegistroBinario binario = new RegistroBinario(ruta);


        binario.anadir("MOTOR-1234", TipoMedicion.ENERGIA_MOTOR, 80.0, false);
        binario.anadir("MOTOR-1234", TipoMedicion.CALOR_MOTOR, 95.0, true);
        binario.anadir("ESCUDO-1", TipoMedicion.VIDA_ESCUDO, 70.0, false);

        assertEquals(3L, binario.getCantidadRegistros());
        assertEquals(3L * RegistroBinario.getTamanoRegistro(), Files.size(ruta));

        // Lectura del registro 2
        Medicion leida = binario.leer(2);
        assertEquals(2, leida.getId());
        assertEquals("MOTOR-1234", leida.getIdComponente());
        assertEquals(TipoMedicion.CALOR_MOTOR, leida.getTipo());
        //Delta como me lo indico la IA es el rango de tolerancia asi que es importante ponerlo
        assertEquals(95.0, leida.getValor(), 0.0001);
        assertTrue(leida.isAnomala());

        //modifico el registro 2
        leida.setValor(60.0);
        leida.setAnomala(false);
        binario.modificar(leida);

        Medicion modificada = binario.leer(2);

        assertEquals(60.0, modificada.getValor(), 0.0001);
        assertFalse(modificada.isAnomala());

        // Reviso si los vecinos no cambian su valor
        assertEquals(80.0, binario.leer(1).getValor(), 0.0001);
        assertEquals(70.0, binario.leer(3).getValor(), 0.0001);
        assertEquals(3L * RegistroBinario.getTamanoRegistro(), Files.size(ruta));
    }

    @Test
    void leerUnRegistroInexistenteLanzaExcepcion() throws IOException {
        Path ruta = directorio.resolve("mediciones.dat");
        RegistroBinario binario = new RegistroBinario(ruta);
        binario.anadir("MOTOR-1234", TipoMedicion.ENERGIA_MOTOR, 80.0, false);

        assertThrows(IllegalArgumentException.class, () -> binario.leer(99));
    }
}