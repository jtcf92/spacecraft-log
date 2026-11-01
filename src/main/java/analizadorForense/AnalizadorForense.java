package analizadorForense;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import persistencia.Medicion;
import persistencia.RegistroBinario;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class AnalizadorForense {
    private final Path datos;
    private final Path salida;

    public AnalizadorForense(Path datos, Path salida) {
        this.datos = datos;
        this.salida = salida;
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
    public List<String> leerTexto() throws IOException {
        List<String> criticas = new ArrayList<>();
        Path ruta = datos.resolve("operaciones.txt");

        if (!Files.exists(ruta)) {
            return criticas;   // si no hay fichero, devolvemos la lista vacía
        }

        for (String linea : Files.readAllLines(ruta)) {
            if (linea.contains("| CRITICO |")) {
                criticas.add(linea);
            }
        }
        return criticas;
    }

    public List<Medicion> leerAnomalias() throws IOException {
        List<Medicion> anomalas = new ArrayList<>();
        Path ruta = datos.resolve("mediciones.dat");

        if (!Files.exists(ruta)) {
            return anomalas;
        }

        RegistroBinario binario = new RegistroBinario(ruta);

        for (Medicion medicion : binario.leerTodas()) {
            if (medicion.isAnomala()) {
                anomalas.add(medicion);
            }
        }
        return anomalas;
    }
    // Lee el motor, el escudo y las armas con su estado
    public List<Componente> leerXml() throws Exception {
        List<Componente> lista = new ArrayList<>();
        Path ruta = datos.resolve("estado.xml");

        if (!Files.exists(ruta)) {
            return lista;
        }
//cargamos la estructura xml en memoria
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
        return lista;
    }

}
