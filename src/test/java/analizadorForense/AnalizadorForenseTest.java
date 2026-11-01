package analizadorForense;

import enums.Estado;
import enums.TipoIncidente;
import excepciones.EventoCriticoException;
import modeloArma.Arma;
import modeloNave.Escudo;
import modeloNave.MotorDeCohete;
import modeloNave.Nave;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import persistencia.Medicion;
import persistencia.Persistencia;
import persistencia.RegistroBinario;
import simulador.Simulador;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AnalizadorForenseTest {

    @TempDir
    Path directorio;

    // Ejecuta el simulador forzando un incidente y deja los ficheros en "carpeta"
    private void simular(Path carpeta, TipoIncidente incidente) throws Exception {
        MotorDeCohete motor = new MotorDeCohete("1234", Estado.OPERATIVO);
        motor.setEnergia(50);

        Nave nave = new Nave(motor, "Pegasus");
        nave.setEscudo(new Escudo(80, Estado.OPERATIVO));
        nave.setArma(0, new Arma("Laser", 50, 10, Estado.OPERATIVO));
        nave.setArma(1, new Arma("Torpedo", 100, 25, Estado.OPERATIVO));

        Simulador simulador = new Simulador(new Persistencia(carpeta), nave);

        assertThrows(EventoCriticoException.class,
                () -> simulador.ejecutar(4, incidente));
    }

    @Test
    void identificaCorrectamenteCadaIncidente() throws Exception {

        TipoIncidente[] incidentes = {
                TipoIncidente.SOBRECALENTAMIENTO_MOTOR,
                TipoIncidente.FALLO_MOTOR,
                TipoIncidente.PERDIDA_ESCUDOS,
                TipoIncidente.IMPACTO_DIRECTO,
                TipoIncidente.FALLO_ARMAMENTO
        };

        for (TipoIncidente forzado : incidentes) {
            Path datos = directorio.resolve("datos_" + forzado.name());
            Path salida = directorio.resolve("salida_" + forzado.name());

            simular(datos, forzado);

            AnalizadorForense analizador = new AnalizadorForense(datos, salida);

            assertEquals(forzado, analizador.getIncidente());
        }
    }

    @Test
    void sinFicherosDevuelveIndeterminado() throws Exception {

        Path datos = directorio.resolve("datos_vacios");
        Path salida = directorio.resolve("salida");

        AnalizadorForense analizador = new AnalizadorForense(datos, salida);

        assertEquals(TipoIncidente.INDETERMINADO, analizador.getIncidente());
    }

    @Test
    void siFaltaUnaFuenteDevuelveIndeterminado() throws Exception {

        Path datos = directorio.resolve("datos");
        Path salida = directorio.resolve("salida");

        simular(datos, TipoIncidente.FALLO_MOTOR);


        Files.delete(datos.resolve("operaciones.txt"));

        AnalizadorForense analizador = new AnalizadorForense(datos, salida);

        assertEquals(TipoIncidente.INDETERMINADO, analizador.getIncidente());
    }

    @Test
    void integracionGeneraLasTresSalidasConSuContenido() throws Exception {

        Path datos = directorio.resolve("datos");
        Path salida = directorio.resolve("salida");

        simular(datos, TipoIncidente.SOBRECALENTAMIENTO_MOTOR);
        new AnalizadorForense(datos, salida);

        Path informe = salida.resolve("informe_forense.txt");
        Path anomalias = salida.resolve("anomalias.dat");
        Path afectados = salida.resolve("afectados.xml");

        assertTrue(Files.exists(informe));
        assertTrue(Files.exists(anomalias));
        assertTrue(Files.exists(afectados));


        String textoInforme = Files.readString(informe);
        assertTrue(textoInforme.contains("SOBRECALENTAMIENTO_MOTOR"));
        assertTrue(textoInforme.contains("CONCLUSIÓN"));


        String textoXml = Files.readString(afectados);
        assertTrue(textoXml.contains("MOTOR-1234"));
        assertFalse(textoXml.contains("ESCUDO"));


        List<Medicion> todas = new RegistroBinario(datos.resolve("mediciones.dat")).leerTodas();
        List<Medicion> soloAnomalas = new RegistroBinario(anomalias).leerTodas();

        assertFalse(soloAnomalas.isEmpty());
        assertTrue(soloAnomalas.size() < todas.size());
        for (Medicion medicion : soloAnomalas) {
            assertTrue(medicion.isAnomala());
        }
    }
}