package persistencia;

import enums.TipoMedicion;
import modeloNave.Nave;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Persistencia {

    private final RegistroTexto texto;
    private final RegistroBinario binario;
    private final RegistroXML xml;

    public Persistencia(Path directorio) throws IOException {
        Files.createDirectories(directorio);
        this.texto = new RegistroTexto(directorio.resolve("operaciones.txt"));
        this.binario = new RegistroBinario(directorio.resolve("mediciones.dat"));
        this.xml = new RegistroXML(directorio.resolve("estado.xml"));
    }

    public void registrarEvento(String mensaje) throws IOException {
        texto.registrar(mensaje);
    }

    public Medicion registrarMedicion(String idComponente, TipoMedicion tipo,
                                      double valor) throws IOException {

        return binario.anadir(idComponente, tipo, valor, tipo.esAnomalo(valor));
    }

    public void guardarEstado(Nave nave) throws Exception {
        xml.guardar(nave);
    }
}