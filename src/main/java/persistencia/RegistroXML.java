
package persistencia;

import enums.Estado;
import modeloArma.Arma;
import modeloNave.Escudo;
import modeloNave.MotorDeCohete;
import modeloNave.Nave;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import java.nio.file.Files;
import java.nio.file.Path;

public class RegistroXML {

    private final Path ruta;

    public RegistroXML(Path ruta) {
        this.ruta = ruta;
    }

    public void guardar(Nave nave) throws Exception {

        Path directorio = ruta.getParent();

        if (directorio != null) {
            Files.createDirectories(directorio);
        }

        DocumentBuilderFactory factory =
                DocumentBuilderFactory.newInstance();

        DocumentBuilder builder =
                factory.newDocumentBuilder();

        Document documento = builder.newDocument();

        Element raiz = documento.createElement("nave");
        raiz.setAttribute("nombre", nave.getNombre());

        documento.appendChild(raiz);


        MotorDeCohete motor = nave.getMotor();

        Element elementoMotor =
                documento.createElement("motor");

        elementoMotor.setAttribute("numeroDeSerie", motor.getNumeroDeSerie());
        elementoMotor.setAttribute("id", motor.getId());

        agregarElemento(
                documento,
                elementoMotor,
                "estado",
                motor.getEstado().name()
        );

        raiz.appendChild(elementoMotor);


        Escudo escudo = nave.getEscudo();

        if (escudo != null) {
            Element elementoEscudo = documento.createElement("escudo");
            elementoEscudo.setAttribute("id", escudo.getId());
            agregarElemento(documento, elementoEscudo, "estado", escudo.getEstado().name());

            raiz.appendChild(elementoEscudo);
        }


        Element elementoArmas = documento.createElement("armas");

        for (Arma arma : nave.getArmas()) {


            if (arma == null) {
                continue;
            }

            Element elementoArma = documento.createElement("arma");
            elementoArma.setAttribute("id", arma.getId());
            agregarElemento(documento, elementoArma, "nombre", arma.getNombre());
            agregarElemento(documento, elementoArma, "estado", arma.getEstado().name());

            elementoArmas.appendChild(elementoArma);
        }

        raiz.appendChild(elementoArmas);


        Transformer transformer =
                TransformerFactory.newInstance()
                        .newTransformer();

        transformer.setOutputProperty(OutputKeys.INDENT, "yes");

        transformer.transform(new DOMSource(documento), new StreamResult(ruta.toFile())
        );
    }

    private void agregarElemento(Document documento, Element padre, String nombre, String valor) {

        Element elemento =
                documento.createElement(nombre);

        elemento.setTextContent(valor);

        padre.appendChild(elemento);
    }
}
