package persistencia;

import enums.TipoMedicion;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class RegistroBinario {

    private static final int LONGITUD_ID = 20;

    // int (4) + 20 chars (20 * 2) + byte (1) + double (8) + boolean (1) = 54
    private static final int TAMANO_REGISTRO =
            Integer.BYTES
                    + LONGITUD_ID * Character.BYTES
                    + Byte.BYTES
                    + Double.BYTES
                    + 1;

    private final Path ruta;

    public RegistroBinario(Path ruta) throws IOException {
        this.ruta = ruta;

        Path directorio = ruta.getParent();
        if (directorio != null) {
            Files.createDirectories(directorio);
        }
    }

    public static int getTamanoRegistro() {
        return TAMANO_REGISTRO;
    }


    public Medicion anadir(String idComponente, TipoMedicion tipo,
                           double valor, boolean anomala) throws IOException {

        int nuevoId = (int) getCantidadRegistros() + 1;
        Medicion medicion = new Medicion(nuevoId, idComponente, tipo, valor, anomala);
        guardar(medicion);
        return medicion;
    }

    public void guardar(Medicion medicion) throws IOException {

        validarMedicion(medicion);

        try (RandomAccessFile archivo =
                     new RandomAccessFile(ruta.toFile(), "rw")) {

            validarEstructura(archivo);

            long cantidad = archivo.length() / TAMANO_REGISTRO;

            if (medicion.getId() != cantidad + 1) {
                throw new IllegalArgumentException(
                        "El ID debe ser " + (cantidad + 1)
                );
            }

            archivo.seek(archivo.length());

            escribirMedicion(archivo, medicion);
        }
    }

    public Medicion leer(int id) throws IOException {

        try (RandomAccessFile archivo =
                     new RandomAccessFile(ruta.toFile(), "r")) {

            validarEstructura(archivo);

            long posicion = calcularPosicion(id);

            if (posicion + TAMANO_REGISTRO > archivo.length()) {
                throw new IllegalArgumentException(
                        "No existe una medición con ID: " + id
                );
            }

            archivo.seek(posicion);

            return leerMedicion(archivo);
        }
    }

    public void modificar(Medicion medicion) throws IOException {

        validarMedicion(medicion);

        try (RandomAccessFile archivo =
                     new RandomAccessFile(ruta.toFile(), "rw")) {

            validarEstructura(archivo);

            long posicion = calcularPosicion(medicion.getId());

            if (posicion + TAMANO_REGISTRO > archivo.length()) {
                throw new IllegalArgumentException(
                        "No existe una medición con ID: " + medicion.getId()
                );
            }

            archivo.seek(posicion);

            escribirMedicion(archivo, medicion);
        }
    }


    public List<Medicion> leerTodas() throws IOException {

        List<Medicion> lista = new ArrayList<>();

        if (!Files.exists(ruta)) {
            return lista;
        }

        try (RandomAccessFile archivo =
                     new RandomAccessFile(ruta.toFile(), "r")) {

            validarEstructura(archivo);

            long total = archivo.length() / TAMANO_REGISTRO;

            for (long i = 0; i < total; i++) {
                lista.add(leerMedicion(archivo));
            }
        }

        return lista;
    }

    public long getCantidadRegistros() throws IOException {

        if (!Files.exists(ruta)) {
            return 0;
        }

        try (RandomAccessFile archivo =
                     new RandomAccessFile(ruta.toFile(), "r")) {

            validarEstructura(archivo);

            return archivo.length() / TAMANO_REGISTRO;
        }
    }

    private void escribirMedicion(RandomAccessFile archivo,
                                  Medicion medicion) throws IOException {

        archivo.writeInt(medicion.getId());

        String idComponente = medicion.getIdComponente();

        for (int i = 0; i < LONGITUD_ID; i++) {
            if (i < idComponente.length()) {
                archivo.writeChar(idComponente.charAt(i));
            } else {
                archivo.writeChar(' ');
            }
        }

        archivo.writeByte(medicion.getTipo().getCodigo());
        archivo.writeDouble(medicion.getValor());
        archivo.writeBoolean(medicion.isAnomala());
    }

    private Medicion leerMedicion(RandomAccessFile archivo) throws IOException {

        int id = archivo.readInt();

        StringBuilder idComponente = new StringBuilder();
        for (int i = 0; i < LONGITUD_ID; i++) {
            idComponente.append(archivo.readChar());
        }

        String componente = idComponente.toString().trim();

        byte codigoTipo = archivo.readByte();
        TipoMedicion tipo = TipoMedicion.desdeCodigo(codigoTipo);

        double valor = archivo.readDouble();
        boolean anomala = archivo.readBoolean();

        return new Medicion(id, componente, tipo, valor, anomala);
    }

    private long calcularPosicion(int id) {

        if (id <= 0) {
            throw new IllegalArgumentException("El ID debe ser mayor que cero");
        }

        return (long) (id - 1) * TAMANO_REGISTRO;
    }

    private void validarEstructura(RandomAccessFile archivo) throws IOException {

        if (archivo.length() % TAMANO_REGISTRO != 0) {
            throw new IOException("El fichero contiene registros incompletos");
        }
    }

    private void validarMedicion(Medicion medicion) {

        if (medicion == null) {
            throw new IllegalArgumentException("La medición no puede ser null");
        }

        if (medicion.getId() <= 0) {
            throw new IllegalArgumentException("El ID debe ser mayor que cero");
        }

        if (medicion.getIdComponente() == null
                || medicion.getIdComponente().isBlank()) {
            throw new IllegalArgumentException(
                    "El identificador del componente es obligatorio"
            );
        }

        if (medicion.getIdComponente().length() > LONGITUD_ID) {
            throw new IllegalArgumentException(
                    "El identificador no puede superar " + LONGITUD_ID + " caracteres"
            );
        }

        if (medicion.getTipo() == null) {
            throw new IllegalArgumentException("El tipo de medición es obligatorio");
        }
    }
}