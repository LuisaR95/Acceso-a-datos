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
 * basada en el tamaño del archivo.
 */
class SistemaLog {
    private final File logFile;
    private final String archivoLogNombre;
    private final long tamanoMaximo; // en bytes
    private final DateTimeFormatter formatoFecha;

    // Esta variable se define en el requisito, pero para una rotación simple a .1 no es necesaria.
    // Se mantiene para compatibilidad futura (e.g., rotación a .2, .3, etc.).
    private final int numeroRotacionBase = 1;

    /**
     * Constructor para inicializar el sistema de log.
     * @param archivoLog Nombre del archivo de log (e.g., "app.log")
     * @param tamanoMaximo Tamaño máximo permitido antes de la rotación (en bytes).
     */
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
            System.err.println("ERROR al inicializar el archivo de log: " + e.getMessage());
        }
    }

    /**
     * Escribe un mensaje en el log con timestamp.
     * El método es sincronizado para asegurar la seguridad en entornos multi-hilo
     * y evitar condiciones de carrera en la rotación y escritura.
     * * @param mensaje Contenido a registrar.
     * @param nivel Nivel del log (INFO, WARNING, ERROR).
     * @throws IOException si hay error al escribir.
     */
    public synchronized void escribirLog(String mensaje, NivelLog nivel) throws IOException {
        // 1. Verificar y rotar si es necesario antes de escribir
        rotarSiNecesario();

        // 2. Formato de la línea de log
        String timestamp = LocalDateTime.now().format(formatoFecha);
        String lineaLog = String.format("[%s] [%s] %s", timestamp, nivel.name(), mensaje);

        // 3. Escritura eficiente usando BufferedWriter y try-with-resources
        // El 'true' en FileWriter indica que se debe escribir en modo "append" (añadir al final)
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

    /**
     * Obtiene el tamaño actual del archivo de log.
     * * @return Tamaño en bytes.
     */
    private long obtenerTamanoLog() {
        return this.logFile.length();
    }

    /**
     * Verifica si el archivo debe rotarse y ejecuta la rotación.
     * Implementa la rotación simple: app.log -> app.log.1
     * * @return true si se realizó la rotación.
     * @throws IOException si hay error en el movimiento/renombrado del archivo.
     */
    private boolean rotarSiNecesario() throws IOException {
        if (obtenerTamanoLog() >= tamanoMaximo) {

            // 1. Definir rutas
            String archivoRotadoNombre = archivoLogNombre + "." + numeroRotacionBase;
            Path fuente = logFile.toPath();
            Path destino = Paths.get(archivoRotadoNombre);

            // 2. Renombrar/mover el archivo actual a la versión rotada
            // Se usa REPLACE_EXISTING para sobrescribir app.log.1 si ya existe (rotación simple)
            Files.move(fuente, destino, StandardCopyOption.REPLACE_EXISTING);

            // 3. Imprimir mensaje en consola (para el caso de uso)
            System.out.printf("ROTACIÓN: %s renombrado a %s%n", archivoLogNombre, archivoRotadoNombre);

            return true;
        }
        return false;
    }

    public static void main(String[] args) {
        final String NOMBRE_LOG = "app.log";
        // Tamaño máximo de 1024 bytes (1 KB) para la prueba
        final long TAMANO_MAXIMO = 1024;

        try {
            // Crear instancia del sistema de log
            SistemaLog log = new SistemaLog(NOMBRE_LOG, TAMANO_MAXIMO);

            System.out.println("--- DEMOSTRACIÓN DE LOGGING CON ROTACIÓN ---");
            System.out.println("Archivo de log: " + NOMBRE_LOG + " | Tamaño máximo: " + TAMANO_MAXIMO + " bytes.");
            System.out.println("------------------------------------------");

            // Mensajes iniciales que caben en el log
            log.escribirLog("Aplicación iniciada", NivelLog.INFO);
            log.escribirLog("Usuario conectado: admin", NivelLog.INFO);

            // Escribir un mensaje grande para forzar la rotación
            String mensajeGrande = "==========================================";
            // Aproximadamente 55 caracteres por línea * 20 líneas = ~1100 bytes
            // Esto forzará la rotación si el límite es 1024 bytes
            for (int i = 0; i < 20; i++) {
                log.escribirLog(mensajeGrande + " Mensaje de carga " + i, NivelLog.WARNING);
            }

            // Este log se escribirá en el NUEVO archivo 'app.log' después de la rotación
            log.escribirLog("Error de conexión a la base de datos (Post-Rotación)", NivelLog.ERROR);
            log.escribirLog("Sistema normalizado", NivelLog.INFO);

            System.out.println("------------------------------------------");
            System.out.println("Prueba finalizada. Verifique los archivos:");
            System.out.println("- '" + NOMBRE_LOG + "' (Debe contener solo los últimos mensajes).");
            System.out.println("- '" + NOMBRE_LOG + ".1' (Debe contener el contenido anterior).");

        } catch (IllegalArgumentException e) {
            System.err.println("Error de configuración: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Error de I/O en la operación: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
