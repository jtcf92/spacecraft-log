package simulador;

import enums.Estado;
import enums.TipoIncidente;
import excepciones.EventoCriticoException;
import modeloArma.Arma;
import modeloNave.Escudo;
import modeloNave.MotorDeCohete;
import modeloNave.Nave;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import persistencia.Persistencia;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class SimuladorTest {

    @TempDir
    Path directorio;


    private Nave crearNave() {
        MotorDeCohete motor = new MotorDeCohete("1234", Estado.OPERATIVO);
        motor.setEnergia(50);
        Nave nave = new Nave(motor, "Pegasus");
        nave.setEscudo(new Escudo(80, Estado.OPERATIVO));
        nave.setArma(0, new Arma("Laser", 50, 10, Estado.OPERATIVO));
        nave.setArma(1, new Arma("Torpedo", 100, 25, Estado.OPERATIVO));
        return nave;
    }

    // Comprueba que los tres ficheros tienen ya la anomalía guardada
    private void comprobarFicheros(Path carpeta) throws Exception {
        String txt = Files.readString(carpeta.resolve("operaciones.txt"));
        assertTrue(txt.contains("CRITICO"));

        assertTrue(Files.size(carpeta.resolve("mediciones.dat")) > 0);

        String xml = Files.readString(carpeta.resolve("estado.xml"));
        assertTrue(xml.contains("AVERIADO") || xml.contains("INACTIVO")
                || xml.contains("EMERGENCIA"));
    }

    @Test
    void modoPruebaPersisteLaAnomaliaAntesDeLanzarLaExcepcion() throws Exception {

        TipoIncidente[] todos = TipoIncidente.values();

        // Probamos todos menos el último (INDETERMINADO)
        for (int i = 0; i < todos.length - 1; i++) {
            TipoIncidente incidente = todos[i];

            // Una carpeta distinta para cada incidente
            Path carpeta = directorio.resolve(incidente.name());
            Simulador simulador = new Simulador(new Persistencia(carpeta), crearNave());

            assertThrows(EventoCriticoException.class,
                    () -> simulador.ejecutar(4, incidente));

            comprobarFicheros(carpeta);
        }
    }
//Este test puede fallar ya que compruebo si el modo normal lanza una excepcion, como puede no pasar puede fallar el test
    @Test
    void modoNormalTambienProvocaUnIncidente() throws Exception {

        Path carpeta = directorio.resolve("normal");
        Simulador simulador = new Simulador(new Persistencia(carpeta), crearNave());

        assertThrows(EventoCriticoException.class,
                () -> simulador.ejecutar(4));

        comprobarFicheros(carpeta);
    }
}