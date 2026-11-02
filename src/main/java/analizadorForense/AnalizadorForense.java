package analizadorForense;

import enums.TipoIncidente;
import enums.TipoMedicion;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import persistencia.Medicion;
import persistencia.RegistroBinario;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
public class AnalizadorForense {
    private final Path datos;
    private final Path salida;
    //Ficheros
    private final List<String> criticas;
    private final List<Medicion> anomalas;
    private final List<Componente> componentes;
    private final TipoIncidente incidente;
    private static final Logger LOG = LogManager.getLogger(AnalizadorForense.class);
    public AnalizadorForense(Path datos, Path salida) throws Exception {
        this.datos = datos;
        this.salida = salida;

        LOG.info("Inicio del análisis forense. Datos: {} Salida: {}", datos, salida);

        this.criticas = leerTexto();
        this.anomalas = leerAnomalias();
        this.componentes = leerXml();
        this.incidente = identificar();

        if (incidente == TipoIncidente.INDETERMINADO) {
            LOG.warn("Las pruebas no permiten identificar ningún incidente");
        } else {
            LOG.info("Incidente identificado: {}", incidente);
        }

        Files.createDirectories(salida);
        escribirInforme();
        escribirBinario();
        escribirXml();

        LOG.info("Análisis terminado. Informe, BIN y XML escritos en {}", salida);
    }

    // === cortesia de la IA ====
    // Guarda los datos de un elemento leído del XML
    private static class Componente {
        final String id;       // por ejemplo MOTOR-1234
        final String tipo;     // motor, escudo o arma
        final String estado;   // OPERATIVO, AVERIADO...

        Componente(String id, String tipo, String estado) {
            this.id = id;
            this.tipo = tipo;
            this.estado = estado;
        }
    }

    private List<String> leerTexto() throws IOException {
        List<String> lista = new ArrayList<>();
        Path ruta = datos.resolve("operaciones.txt");

        if (!Files.exists(ruta)) {
            LOG.warn("No existe {}: se analiza sin esta fuente", ruta);
            return lista;
        }

        List<String> lineas = Files.readAllLines(ruta);
        for (String linea : lineas) {
            if (linea.contains("| CRITICO |")) {
                lista.add(linea);
            }
        }

        LOG.debug("TXT leído: {} líneas, {} críticas", lineas.size(), lista.size());
        return lista;
    }

    private List<Medicion> leerAnomalias() throws IOException {
        List<Medicion> lista = new ArrayList<>();
        Path ruta = datos.resolve("mediciones.dat");

        if (!Files.exists(ruta)) {
            LOG.warn("No existe {}: se analiza sin esta fuente", ruta);
            return lista;
        }

        RegistroBinario binario = new RegistroBinario(ruta);
        List<Medicion> todas = binario.leerTodas();

        for (Medicion medicion : todas) {
            if (medicion.isAnomala()) {
                lista.add(medicion);
            }
        }

        LOG.info("BIN leído: {} registros procesados, {} anómalos", todas.size(), lista.size());
        return lista;
    }

    private List<Componente> leerXml() throws Exception {
        List<Componente> lista = new ArrayList<>();
        Path ruta = datos.resolve("estado.xml");

        if (!Files.exists(ruta)) {
            LOG.warn("No existe {}: se analiza sin esta fuente", ruta);
            return lista;
        }

        Document documento = DocumentBuilderFactory.newInstance()
                .newDocumentBuilder()
                .parse(ruta.toFile());

        String[] etiquetas = {"motor", "escudo", "arma"};

        for (String etiqueta : etiquetas) {
            NodeList nodos = documento.getElementsByTagName(etiqueta);

            for (int i = 0; i < nodos.getLength(); i++) {
                Element elemento = (Element) nodos.item(i);

                String id = elemento.getAttribute("id");
                String estado = elemento.getElementsByTagName("estado")
                        .item(0).getTextContent();

                lista.add(new Componente(id, etiqueta, estado));
            }
        }

        LOG.debug("XML leído: {} componentes", lista.size());
        return lista;
    }
    private TipoIncidente identificar() {

        String motor = estadoDe("motor");
        String escudo = estadoDe("escudo");


        boolean textoMotor = criticoMenciona("| MOTOR-");
        boolean textoEscudo = criticoMenciona("| ESCUDO |");
        boolean textoArma = criticoMenciona("| ARMA-");
        LOG.debug("Pruebas: textoMotor={} textoEscudo={} textoArma={} motor={} escudo={}",
                textoMotor, textoEscudo, textoArma, motor, escudo);
        if (textoMotor && hayAnomalia(TipoMedicion.CALOR_MOTOR)
                && motor.equals("AVERIADO")) {
            return TipoIncidente.SOBRECALENTAMIENTO_MOTOR;
        }

        if (textoMotor && hayAnomalia(TipoMedicion.ENERGIA_MOTOR)
                && motor.equals("AVERIADO")) {
            return TipoIncidente.FALLO_MOTOR;
        }

        if (textoMotor && hayAnomalia(TipoMedicion.VIDA_ESCUDO)
                && motor.equals("EMERGENCIA") && escudo.equals("INACTIVO")) {
            return TipoIncidente.IMPACTO_DIRECTO;
        }

        if (textoEscudo && hayAnomalia(TipoMedicion.VIDA_ESCUDO)
                && escudo.equals("INACTIVO")) {
            return TipoIncidente.PERDIDA_ESCUDOS;
        }

        if (textoArma && hayAnomalia(TipoMedicion.DANIO_ARMA) && hayArmaAveriada()) {
            return TipoIncidente.FALLO_ARMAMENTO;
        }

        return TipoIncidente.INDETERMINADO;
    }

    private boolean criticoMenciona(String texto) {
        for (String linea : criticas) {
            if (linea.contains(texto)) {
                return true;
            }
        }
        return false;
    }


    private boolean hayAnomalia(TipoMedicion tipo) {
        for (Medicion medicion : anomalas) {
            if (medicion.getTipo() == tipo) {
                return true;
            }
        }
        return false;
    }


    private boolean hayArmaAveriada() {
        for (Componente componente : componentes) {
            if (componente.tipo.equals("arma") && componente.estado.equals("AVERIADO")) {
                return true;
            }
        }
        return false;
    }

    // Devuelve el estado del primer componente de ese tipo, o "" si no existe
    private String estadoDe(String tipo) {
        for (Componente componente : componentes) {
            if (componente.tipo.equals(tipo)) {
                return componente.estado;
            }
        }
        return "";
    }

    // Los componentes que NO están operativos son los afectados por el accidente
    private List<Componente> afectados() {
        List<Componente> lista = new ArrayList<>();
        for (Componente componente : componentes) {
            if (!componente.estado.equals("OPERATIVO")) {
                lista.add(componente);
            }
        }
        return lista;
    }

    // Frase final del informe según el incidente
    private String conclusion() {
        return switch (incidente) {
            case SOBRECALENTAMIENTO_MOTOR -> "El motor superó su temperatura crítica y quedó fuera de servicio. "
                    + "La nave no puede propulsarse.";
            case FALLO_MOTOR -> "El motor perdió toda su energía y quedó averiado. "
                    + "La nave no puede propulsarse.";
            case PERDIDA_ESCUDOS -> "El escudo ha sido destruido. La nave queda sin protección "
                    + "y vulnerable a nuevos impactos.";
            case IMPACTO_DIRECTO -> "Tras caer el escudo, un impacto alcanzó la nave. El motor está "
                    + "en emergencia y la integridad de la nave está en riesgo.";
            case FALLO_ARMAMENTO -> "Un arma ha fallado. La capacidad defensiva de la nave queda reducida.";
            default -> "Los datos no permiten identificar ningún incidente con seguridad.";
        };
    }


    private void escribirInforme() throws IOException {

        DateTimeFormatter formato = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        List<Componente> efectos = afectados();

        List<String> lineas = new ArrayList<>();
        lineas.add("INFORME FORENSE");
        lineas.add("Fecha del análisis: " + LocalDateTime.now().format(formato));
        lineas.add("");
        lineas.add("INCIDENTE IDENTIFICADO: " + incidente);
        lineas.add("");

        lineas.add("EFECTOS DETECTADOS:");
        if (efectos.isEmpty()) {
            lineas.add("  Ninguno");
        }
        for (Componente componente : efectos) {
            lineas.add("  " + componente.id + " (" + componente.tipo + "): " + componente.estado);
        }
        lineas.add("");

        lineas.add("BASE DEL ANÁLISIS:");
        lineas.add("  Eventos críticos en el TXT: " + criticas.size());
        lineas.add("  Mediciones anómalas en el BIN: " + anomalas.size());
        lineas.add("  Elementos afectados en el XML: " + efectos.size());
        lineas.add("");

        lineas.add("CONCLUSIÓN:");
        lineas.add("  " + conclusion());

        Files.write(salida.resolve("informe_forense.txt"), lineas);
    }


    private void escribirBinario() throws IOException {
        Path ruta = salida.resolve("anomalias.dat");
        Files.deleteIfExists(ruta);   // para no mezclar con un análisis anterior

        RegistroBinario binario = new RegistroBinario(ruta);

        for (Medicion medicion : anomalas) {
            binario.anadir(medicion.getIdComponente(), medicion.getTipo(),
                    medicion.getValor(), true);
        }
    }


    private void escribirXml() throws Exception {

        Document documento = DocumentBuilderFactory.newInstance()
                .newDocumentBuilder().newDocument();

        Element raiz = documento.createElement("informe");
        raiz.setAttribute("incidente", incidente.name());
        documento.appendChild(raiz);

        Element elementoAfectados = documento.createElement("afectados");
        raiz.appendChild(elementoAfectados);

        for (Componente componente : afectados()) {
            Element elemento = documento.createElement("elemento");
            elemento.setAttribute("id", componente.id);
            elemento.setAttribute("tipo", componente.tipo);
            elemento.setAttribute("estado", componente.estado);
            elementoAfectados.appendChild(elemento);
        }

        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.transform(new DOMSource(documento),
                new StreamResult(salida.resolve("afectados.xml").toFile()));
    }
    public TipoIncidente getIncidente() {
        return incidente;
    }

}
