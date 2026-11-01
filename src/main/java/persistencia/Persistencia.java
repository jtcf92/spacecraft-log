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
    private final Path rutaTexto;
    private final Path rutaBinario;
    private final Path rutaXml;
    public Persistencia(Path directorio) throws IOException {
        Files.createDirectories(directorio);

        this.rutaTexto = directorio.resolve("operaciones.txt");
        this.rutaBinario = directorio.resolve("mediciones.dat");
        this.rutaXml = directorio.resolve("estado.xml");
        this.texto = new RegistroTexto(directorio.resolve("operaciones.txt"));
        this.binario = new RegistroBinario(directorio.resolve("mediciones.dat"));
        this.xml = new RegistroXML(directorio.resolve("estado.xml"));
    }
    public void reiniciar() throws IOException {
        Files.deleteIfExists(rutaTexto);
        Files.deleteIfExists(rutaBinario);
        Files.deleteIfExists(rutaXml);
    }
    public void registrarEvento(String mensaje) throws IOException {
        texto.registrar(mensaje);
    }
    public void registrarEvento(String tipo, String componente, String detalle)
            throws IOException {
        texto.registrar(tipo + " | " + componente + " | " + detalle);
    }

    public Medicion registrarMedicion(String idComponente, TipoMedicion tipo,
                                      double valor) throws IOException {

        return binario.anadir(idComponente, tipo, valor, tipo.esAnomalo(valor));
    }

    public void guardarEstado(Nave nave) throws Exception {
        xml.guardar(nave);
    }
}