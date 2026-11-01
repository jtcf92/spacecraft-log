package persistencia;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RegistroTextoTest {

    @TempDir
    Path directorio;

    @Test
    void escribeYRecuperaLineasDeTexto() throws IOException {
        Path ruta = directorio.resolve("operaciones.txt");
        RegistroTexto registro = new RegistroTexto(ruta);

        registro.registrar("MOVIMIENTO MOTOR-1234 distancia=10");
        registro.registrar("AVISO MOTOR-1234 calor alto");

        List<String> lineas = Files.readAllLines(ruta, StandardCharsets.UTF_8);

        assertEquals(2, lineas.size());   // la segunda se añade, no sobrescribe
        assertTrue(lineas.get(0).endsWith("MOVIMIENTO MOTOR-1234 distancia=10"));
        assertTrue(lineas.get(1).endsWith("AVISO MOTOR-1234 calor alto"));
    }
}