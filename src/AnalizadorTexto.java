import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class AnalizadorTexto {


    // Clase interna con constructor, getters y setters
    static class EstadisticasTexto {
        private int numeroLineas;
        private int numeroPalabras;
        private int numeroCaracteres;
        private String palabraMasLarga;

        // Constructor vacío
        public EstadisticasTexto() {
        }

        // Constructor con parámetros
        public EstadisticasTexto(int numeroLineas, int numeroPalabras, int numeroCaracteres, String palabraMasLarga) {
            this.numeroLineas = numeroLineas;
            this.numeroPalabras = numeroPalabras;
            this.numeroCaracteres = numeroCaracteres;
            this.palabraMasLarga = palabraMasLarga;
        }

        // Getters y Setters
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

        // Mostrar estadísticas
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


    // Métodos principales



    public static EstadisticasTexto analizarArchivo(String nombreArchivo) throws IOException {
        Path ruta = Path.of(nombreArchivo);

        if (!Files.exists(ruta)) {
            throw new FileNotFoundException("El archivo no existe: " + nombreArchivo);
        }

        int lineas = 0;
        int palabras = 0;
        int caracteres = 0;
        String palabraMasLarga = "";


        try (BufferedReader reader = Files.newBufferedReader(ruta)) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                lineas++;
                caracteres += linea.length();

                String[] partes = linea.split("\\s+");
                for (String palabra : partes) {
                    if (!palabra.isEmpty()) {
                        palabras++;
                        if (palabra.length() > palabraMasLarga.length()) {
                            palabraMasLarga = palabra;
                        }
                    }
                }
            }
        }

        EstadisticasTexto estadisticas = new EstadisticasTexto();
        estadisticas.setNumeroLineas(lineas);
        estadisticas.setNumeroPalabras(palabras);
        estadisticas.setNumeroCaracteres(caracteres);
        estadisticas.setPalabraMasLarga(palabraMasLarga);

        return estadisticas;
    }


    public static void guardarEstadisticas(EstadisticasTexto estadisticas, String archivoSalida) throws IOException {

        try (BufferedWriter writer = Files.newBufferedWriter(Path.of(archivoSalida))) {
            writer.write(estadisticas.toString());
        }
    }


    public static void main(String[] args) {
        try {
            String archivoEntrada = "C:\\Users\\AlumnoAfternoon\\IdeaProjects\\Acceso-a-datos\\src\\archivo.txt";
            String archivoSalida = "estadisticas.txt";

            EstadisticasTexto stats = analizarArchivo(archivoEntrada);
            System.out.println(stats);

            guardarEstadisticas(stats, archivoSalida);


        } catch (IOException e) {
            System.err.println("Error al procesar el archivo: " + e.getMessage());
        }
    }
}
