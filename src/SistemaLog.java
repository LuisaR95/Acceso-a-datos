import java.io.*;

// Enum para los niveles de log
enum NivelLog {
    INFO, ERROR
}

// Clase principal del sistema de logging
public class SistemaLog {

    private String archivoLog;
    private long tamanoMaximo; // Tamaño máximo en bytes

    public SistemaLog(String archivoLog, long tamanoMaximo) {
        if (archivoLog == null || archivoLog.isEmpty()) {
            throw new IllegalArgumentException("El nombre del archivo no puede ser nulo o vacío.");
        }
        if (tamanoMaximo <= 0) {
            throw new IllegalArgumentException("El tamaño máximo debe ser mayor a 0.");
        }
        this.archivoLog = archivoLog;
        this.tamanoMaximo = tamanoMaximo;
    }

    // Método para escribir un log con fecha fija
    public synchronized void escribirLog(String fecha, String mensaje, NivelLog nivel) throws IOException {
        if (mensaje == null) mensaje = "";
        if (nivel == null) nivel = NivelLog.INFO;
        if (fecha == null) fecha = "2025-10-14 10:30:00";

        // Línea de log con formato
        String lineaLog = String.format("[%s] [%s] %s", fecha, nivel, mensaje);

        // Rotar archivo si supera el tamaño máximo
        if (rotarSiNecesario()) {
            System.out.println("ROTACIÓN: " + archivoLog + " renombrado a " + archivoLog + ".1");
        }

        // Escribir línea en el archivo actual
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(archivoLog, true))) {
            writer.write(lineaLog);
            writer.newLine();
        }

        // Mostrar en consola
        System.out.println("Log escrito: " + mensaje);
    }

    // Método que revisa si es necesario rotar el archivo
    private boolean rotarSiNecesario() throws IOException {
        File logFile = new File(archivoLog);
        if (logFile.exists() && logFile.length() >= tamanoMaximo) {
            File archivoRotado = new File(archivoLog + ".1");

            // Eliminar archivo rotado anterior si existe
            if (archivoRotado.exists()) {
                archivoRotado.delete();
            }

            // Renombrar archivo actual
            if (!logFile.renameTo(archivoRotado)) {
                throw new IOException("No se pudo rotar el archivo de log.");
            }

            // Crear nuevo archivo vacío
            logFile.createNewFile();
            return true;
        }
        return false;
    }

    // Método principal de prueba
    public static void main(String[] args) {
        try {
            // Crear sistema de log con tamaño máximo de 100 bytes para prueba de rotación
            SistemaLog log = new SistemaLog("app.log", 100);

            // Escribir logs con fechas fijas
            log.escribirLog("2025-10-14 10:30:15", "Aplicación iniciada", NivelLog.INFO);
            log.escribirLog("2025-10-14 10:30:16", "Usuario conectado", NivelLog.INFO);
            log.escribirLog("2025-10-14 10:30:17", "Error de conexión", NivelLog.ERROR);

        } catch (IOException e) {
            System.err.println("Error en logging: " + e.getMessage());
        }
    }
}
