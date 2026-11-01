package persistencia;

import enums.Estado;
import modeloArma.Arma;
import modeloNave.Escudo;
import modeloNave.MotorDeCohete;
import modeloNave.Nave;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class RegistroXMLTest {

    @TempDir
    Path directorio;

    @Test
    void guardarGeneraXMLConIdsYEstados() throws Exception {
        MotorDeCohete motor = new MotorDeCohete("1234", Estado.AVERIADO);
        Nave nave = new Nave(motor, "Pegasus");
        nave.setEscudo(new Escudo(80, Estado.INACTIVO));
        nave.setArma(0, new Arma("Laser", 50, 10, Estado.OPERATIVO));

        Path ruta = directorio.resolve("estado.xml");
        new RegistroXML(ruta).guardar(nave);

        assertTrue(Files.exists(ruta));
        String contenido = Files.readString(ruta);

        assertTrue(contenido.contains("id=\"MOTOR-1234\""));
        assertTrue(contenido.contains("id=\"ESCUDO\""));
        assertTrue(contenido.contains("id=\"ARMA-LASER\""));
        assertTrue(contenido.contains("AVERIADO"));
        assertTrue(contenido.contains("INACTIVO"));
    }
}