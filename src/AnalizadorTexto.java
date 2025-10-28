import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

// Clase principal para analizar un archivo de texto
public class AnalizadorTexto {

    // Clase interna que almacena estadísticas de un archivo de texto
    static class EstadisticasTexto {
        private int numeroLineas;
        private int numeroPalabras;
        private int numeroCaracteres;
        private String palabraMasLarga;

        // Constructor vacío
        public EstadisticasTexto() {
        }

        // Constructor que inicializa todas las estadísticas
        public EstadisticasTexto(int numeroLineas, int numeroPalabras, int numeroCaracteres, String palabraMasLarga) {
            this.numeroLineas = numeroLineas;
            this.numeroPalabras = numeroPalabras;
            this.numeroCaracteres = numeroCaracteres;
            this.palabraMasLarga = palabraMasLarga;
        }

        // Getters y setters para acceder o modificar los valores
        public int getNumeroLineas() {
            return numeroLineas;
        }

        public void setNumeroLineas(int numeroLineas) {
            this.numeroLineas = numeroLineas;
        }

        public int getNumeroPalabras() {
            return numeroPalabras;
        }

        public void setNumeroPalabras(int numeroPalabras) {
            this.numeroPalabras = numeroPalabras;
        }

        public int getNumeroCaracteres() {
            return numeroCaracteres;
        }

        public void setNumeroCaracteres(int numeroCaracteres) {
            this.numeroCaracteres = numeroCaracteres;
        }

        public String getPalabraMasLarga() {
            return palabraMasLarga;
        }

        public void setPalabraMasLarga(String palabraMasLarga) {
            this.palabraMasLarga = palabraMasLarga;
        }

        // Método para mostrar las estadísticas en formato legible
        @Override
        public String toString() {
            return "=== Estadísticas del archivo ===\n" +
                    "Líneas: " + numeroLineas + "\n" +
                    "Palabras: " + numeroPalabras + "\n" +
                    "Caracteres: " + numeroCaracteres + "\n" +
                    "Palabra más larga: " + palabraMasLarga +
                    " (" + palabraMasLarga.length() + " caracteres)";
        }
    }

    // Método que analiza un archivo y devuelve sus estadísticas
    public static EstadisticasTexto analizarArchivo(String nombreArchivo) throws IOException {
        Path ruta = Path.of(nombreArchivo); // Crear la ruta del archivo

        // Verificar si el archivo existe
        if (!Files.exists(ruta)) {
            throw new FileNotFoundException("El archivo no existe: " + nombreArchivo);
        }

        // Inicializar contadores
        int lineas = 0;
        int palabras = 0;
        int caracteres = 0;
        String palabraMasLarga = "";

        // Leer el archivo línea por línea usando BufferedReader
        try (BufferedReader reader = Files.newBufferedReader(ruta)) {
            String linea;
            while ((linea = reader.readLine()) != null) { // Mientras haya líneas
                lineas++;                        // Contar línea
                caracteres += linea.length();     // Contar caracteres de la línea

                // Separar la línea en palabras usando espacios en blanco
                String[] partes = linea.split("\\s+");
                for (String palabra : partes) {
                    if (!palabra.isEmpty()) {    // Evitar contar palabras vacías
                        palabras++;              // Contar palabra
                        // Verificar si esta es la palabra más larga
                        if (palabra.length() > palabraMasLarga.length()) {
                            palabraMasLarga = palabra;
                        }
                    }
                }
            }
        }

        // Crear objeto EstadisticasTexto con los valores calculados
        EstadisticasTexto estadisticas = new EstadisticasTexto();
        estadisticas.setNumeroLineas(lineas);
        estadisticas.setNumeroPalabras(palabras);
        estadisticas.setNumeroCaracteres(caracteres);
        estadisticas.setPalabraMasLarga(palabraMasLarga);

        return estadisticas; // Devolver objeto con estadísticas
    }

    // Método para guardar las estadísticas en un archivo de salida
    public static void guardarEstadisticas(EstadisticasTexto estadisticas, String archivoSalida) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(Path.of(archivoSalida))) {
            writer.write(estadisticas.toString()); // Escribir estadísticas en el archivo
        }
    }

    // Método principal
    public static void main(String[] args) {
        try {
            // Archivo de entrada y salida
            String archivoEntrada = "C:\\Users\\AlumnoAfternoon\\IdeaProjects\\Acceso-a-datos\\src\\archivo.txt";
            String archivoSalida = "estadisticas.txt";

            // Analizar archivo y mostrar estadísticas
            EstadisticasTexto stats = analizarArchivo(archivoEntrada);
            System.out.println(stats);

            // Guardar estadísticas en un archivo
            guardarEstadisticas(stats, archivoSalida);

        } catch (IOException e) {
            // Manejar errores de archivo
            System.err.println("Error al procesar el archivo: " + e.getMessage());
        }
    }
}
