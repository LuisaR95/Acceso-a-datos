import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Pattern;

public class MergeArchivos {

    /**
     * Combina varios archivos de texto en uno solo aplicando un filtro de palabra completa.
     *
     */
    public static int combinarArchivos(String[] archivosEntrada, String archivoSalida, String filtro) throws IOException {
        int totalLineasEscritas = 0;

        // Verificar que todos los archivos de entrada existen
        for (String archivo : archivosEntrada) {
            if (!Files.exists(Path.of(archivo))) {
                throw new FileNotFoundException("El archivo no existe: " + archivo);
            }
        }

        // Crear un patrón regex para buscar la palabra completa exacta
        Pattern patron = null;
        if (filtro != null && !filtro.isEmpty()) {
            // \b asegura que sea palabra completa
            patron = Pattern.compile("\\b" + Pattern.quote(filtro) + "\\b");
        }

        // Abrir archivo de salida con BufferedWriter
        try (BufferedWriter writer = Files.newBufferedWriter(Path.of(archivoSalida), StandardCharsets.UTF_8)) {

            // Recorrer cada archivo de entrada
            for (String archivo : archivosEntrada) {
                int lineasCoinciden = 0;

                // Abrir archivo de entrada con BufferedReader
                try (BufferedReader reader = Files.newBufferedReader(Path.of(archivo), StandardCharsets.UTF_8)) {
                    String linea;
                    while ((linea = reader.readLine()) != null) {

                        // Reemplazar secuencias literales "\n" por saltos de línea reales
                        linea = linea.replace("\\n", "\n");

                        // Dividir línea en sublíneas por saltos de línea reales
                        String[] subLineas = linea.split("\n");
                        for (String subLinea : subLineas) {

                            // Si no hay filtro o la sublínea cumple con el patrón, escribirla
                            if (patron == null || patron.matcher(subLinea).find()) {
                                writer.write(subLinea);
                                writer.newLine(); // Añadir salto de línea en archivo de salida
                                totalLineasEscritas++;
                                lineasCoinciden++;
                            }
                        }
                    }
                }

                // Mostrar en consola cuántas líneas coinciden por archivo
                System.out.println("Procesando " + archivo + ": " + lineasCoinciden +
                        (lineasCoinciden == 1 ? " línea coincide" : " líneas coinciden"));
            }
        }

        // Mostrar total de líneas escritas en el archivo de salida
        System.out.println("Total: " + totalLineasEscritas + " líneas escritas en " + archivoSalida);
        return totalLineasEscritas;
    }

    public static void main(String[] args) {
        try {
            // Archivos de entrada a combinar
            String[] archivosEntrada = {
                    "C:\\Users\\AlumnoAfternoon\\IdeaProjects\\Acceso-a-datos\\archivo1.txt",
                    "C:\\Users\\AlumnoAfternoon\\IdeaProjects\\Acceso-a-datos\\archivo2.txt"
            };

            // Archivo de salida combinado
            String archivoSalida = "C:\\Users\\AlumnoAfternoon\\IdeaProjects\\Acceso-a-datos\\combinado.txt";

            // Filtro de palabra: solo incluir líneas que contengan "Java"
            String filtro = "Java";

            // Ejecutar la combinación de archivos
            combinarArchivos(archivosEntrada, archivoSalida, filtro);
        } catch (IOException e) {
            // Captura errores de lectura/escritura y los muestra por consola
            System.err.println("Error al combinar archivos: " + e.getMessage());
        }
    }
}
