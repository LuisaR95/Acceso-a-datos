import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Enumeración para definir los niveles de severidad del log.
 */
enum NivelLog {
    INFO, WARNING, ERROR
}

/**
 * Clase principal que implementa un sistema de logging con rotación
 * basada en el tamaño del archivo, manteniendo logs históricos secuenciales.
 */
class SistemaLog {
    private final File logFile;
    private final String archivoLogNombre;
    private final long tamanoMaximo; // en bytes
    private final DateTimeFormatter formatoFecha;

    // Máximo de archivos rotados a mantener (e.g., app.log.1 a app.log.5)
    private static final int MAX_ROTATIONS = 5;


    public SistemaLog(String archivoLog, long tamanoMaximo) {
        // Validar parámetros
        if (archivoLog == null || archivoLog.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del archivo de log no puede estar vacío.");
        }
        if (tamanoMaximo <= 0) {
            throw new IllegalArgumentException("El tamaño máximo debe ser mayor a cero.");
        }

        this.archivoLogNombre = archivoLog;
        this.logFile = new File(archivoLog);
        this.tamanoMaximo = tamanoMaximo;
        // Formato ISO 8601
        this.formatoFecha = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // Intentar asegurar que el archivo de log base exista
        try {
            if (!logFile.exists()) {
                logFile.createNewFile();
            }
        } catch (IOException e) {
            // Este es un error en tiempo de ejecución, pero lo manejamos para mostrar la causa.
            System.err.println("ERROR al inicializar el archivo de log: " + e.getMessage());
        }
    }


    public synchronized void escribirLog(String mensaje, NivelLog nivel) throws IOException {
        // 1. Verificar y rotar si es necesario antes de escribir
        if (rotarSiNecesario()) {
            System.out.printf("ROTACIÓN: %s renombrado. Archivos 1 a %d desplazados.%n", archivoLogNombre, MAX_ROTATIONS);
        }

        // 2. Formato de la línea de log
        String timestamp = LocalDateTime.now().format(formatoFecha);
        String lineaLog = String.format("[%s] [%s] %s", timestamp, nivel.name(), mensaje);

        // 3. Escritura eficiente usando BufferedWriter y try-with-resources
        try (FileWriter fw = new FileWriter(logFile, true);
             BufferedWriter bw = new BufferedWriter(fw)) {

            bw.write(lineaLog);
            bw.newLine();
            bw.flush(); // flush() para asegurar que el mensaje se escribe inmediatamente

            System.out.println("Log escrito: " + mensaje);

        } catch (IOException e) {
            // Re-lanzar la excepción
            throw new IOException("Error crítico al escribir en el log: " + e.getMessage(), e);
        }
    }


    private long obtenerTamanoLog() {
        return this.logFile.length();
    }


    private boolean rotarSiNecesario() throws IOException {
        if (obtenerTamanoLog() >= tamanoMaximo) {

            // 1. Desplazar archivos existentes (de N a N+1, eliminando el más antiguo)
            for (int i = MAX_ROTATIONS; i >= 1; i--) {
                String nombreFuente = archivoLogNombre + "." + i;
                String nombreDestino = archivoLogNombre + "." + (i + 1);

                File fuente = new File(nombreFuente);
                if (fuente.exists()) {
                    File destino = new File(nombreDestino);

                    // Si el archivo más antiguo (i+1, ej: 6) existe, lo eliminamos.
                    if (destino.exists()) {
                        destino.delete();
                    }

                    // Mover el archivo (ej: app.log.5 -> app.log.6)
                    Files.move(fuente.toPath(), destino.toPath(), StandardCopyOption.REPLACE_EXISTING);
                }
            }

            // 2. Renombrar el archivo actual (app.log -> app.log.1)
            String archivoRotadoNombre = archivoLogNombre + ".1";
            Path fuente = logFile.toPath();
            Path destino = Paths.get(archivoRotadoNombre);

            // Renombrar/mover el archivo actual a la versión .1
            Files.move(fuente, destino, StandardCopyOption.REPLACE_EXISTING);

            // 3. Crear un nuevo archivo de log vacío
            logFile.createNewFile();

            return true;
        }
        return false;
    }

    public static void main(String[] args) {
        final String NOMBRE_LOG = "app.log";
        // Tamaño máximo ajustado a 90 bytes para forzar la rotación después del 2º mensaje
        final long TAMANO_MAXIMO = 90;

        try {
            // Crear instancia del sistema de log
            SistemaLog log = new SistemaLog(NOMBRE_LOG, TAMANO_MAXIMO);

            System.out.println("--- DEMOSTRACIÓN CASO DE USO ORIGINAL ---");
            System.out.printf("Log: %s | Max: %d bytes%n", NOMBRE_LOG, TAMANO_MAXIMO);
            System.out.println("------------------------------------------");

            // 1. Se escriben los dos primeros logs (deben llenar app.log)
            log.escribirLog("Aplicación iniciada", NivelLog.INFO);
            log.escribirLog("Usuario conectado", NivelLog.INFO);

            // 2. El tercer log activa la rotación ANTES de escribirse.
            log.escribirLog("Error de conexión", NivelLog.ERROR);

            System.out.println("------------------------------------------");
            System.out.println("Prueba de rotación finalizada.");
            System.out.println("Verifique el contenido de los archivos:");
            System.out.println("- '" + NOMBRE_LOG + ".1' (Debe contener los dos primeros logs).");
            System.out.println("- '" + NOMBRE_LOG + "' (Debe contener solo el último log).");

        } catch (IllegalArgumentException e) {
            System.err.println("Error de configuración: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Error de I/O en la operación: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
