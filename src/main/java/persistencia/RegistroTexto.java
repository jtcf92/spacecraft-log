package persistencia;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class RegistroTexto {
    private static final DateTimeFormatter FORMATO =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final Path ruta;

    public RegistroTexto(Path ruta) throws IOException {
        this.ruta = ruta;

        Path directorio = ruta.getParent();

        if (directorio != null) {
            Files.createDirectories(directorio);
        }
    }

    public void registrar(String operacion) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(
                new FileWriter(ruta.toFile(), true))) {

            String marcaTemporal = LocalDateTime.now().format(FORMATO);

            writer.write(marcaTemporal + " | " + operacion);
            writer.newLine();
        }
    }

    public void mostrarRegistros() throws IOException {
        try (BufferedReader reader = new BufferedReader(
                new FileReader(ruta.toFile()))) {

            String linea;

            while ((linea = reader.readLine()) != null) {
                System.out.println(linea);
            }
        }
    }

}
