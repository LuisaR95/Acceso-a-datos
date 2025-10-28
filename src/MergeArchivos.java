import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class MergeArchivos {

    public static int combinarArchivos(String[] archivosEntrada, String archivoSalida, String filtro) throws IOException {
        int totalEscritas = 0;

        // Validar que todos los archivos existen antes de comenzar
        for (String archivo : archivosEntrada) {
            if (!Files.exists(Path.of(archivo))) {
                throw new FileNotFoundException("El archivo no existe: " + archivo);
            }
        }

        try (BufferedWriter writer = Files.newBufferedWriter(Path.of(archivoSalida), StandardCharsets.UTF_8)) {

            for (String archivo : archivosEntrada) {
                int lineasCoinciden = 0;

                try (BufferedReader reader = Files.newBufferedReader(Path.of(archivo), StandardCharsets.UTF_8)) {
                    String linea;
                    while ((linea = reader.readLine()) != null) {

                        // ⚡ Convertir "\n" literales a saltos de línea reales
                        String[] subLineas = linea.split("\\\\n"); // \\n porque \ es carácter de escape

                        for (String subLinea : subLineas) {
                            if (cumpleFiltro(subLinea, filtro)) {
                                writer.write(subLinea);
                                writer.newLine();
                                totalEscritas++;
                                lineasCoinciden++;
                            }
                        }
                    }
                }

                System.out.println("Procesando " + archivo + ": " + lineasCoinciden +
                        (lineasCoinciden == 1 ? " línea coincide" : " líneas coinciden"));
            }
        }

        System.out.println("Total: " + totalEscritas + " líneas escritas en " + archivoSalida);
        return totalEscritas;
    }

    private static boolean cumpleFiltro(String linea, String filtro) {
        if (filtro == null || filtro.isEmpty()) return true;
        return linea.contains(filtro);
    }

    public static void main(String[] args) {
        try {
            String[] archivosEntrada = {"C:\\Users\\AlumnoAfternoon\\IdeaProjects\\Acceso-a-datos\\src\\archivo1.txt", "C:\\Users\\AlumnoAfternoon\\IdeaProjects\\Acceso-a-datos\\src\\archivo2.txt"};
            String archivoSalida = "C:\\Users\\AlumnoAfternoon\\IdeaProjects\\Acceso-a-datos\\src\\combinado.txt";
            String filtro = "Java"; // null = incluir todo

            combinarArchivos(archivosEntrada, archivoSalida, filtro);
        } catch (IOException e) {
            System.err.println("Error al combinar archivos: " + e.getMessage());
        }
    }
}
