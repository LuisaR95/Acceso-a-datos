import java.io.*;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.Date;

enum NivelLog {
    INFO, WARNING, ERROR
}

public class SistemaLog {

    private String archivoLog;
    private long tamanoMaximo; // en bytes
    private int numeroRotacion;

    public SistemaLog(String archivoLog, long tamanoMaximo) {
        if (archivoLog == null || archivoLog.isEmpty()) {
            throw new IllegalArgumentException("El nombre del archivo no puede ser nulo o vacío.");
        }
        if (tamanoMaximo <= 0) {
            throw new IllegalArgumentException("El tamaño máximo debe ser mayor a 0.");
        }
        this.archivoLog = archivoLog;
        this.tamanoMaximo = tamanoMaximo;
        this.numeroRotacion = 1;
    }


    public synchronized void escribirLog(String mensaje, NivelLog nivel) throws IOException {
        if (mensaje == null) mensaje = "";
        if (nivel == null) nivel = NivelLog.INFO;

        // Formato ISO 8601 para la fecha
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String lineaLog = String.format("[%s] [%s] %s", timestamp, nivel, mensaje);

        // Rotar si el archivo supera el tamaño
        if (rotarSiNecesario()) {
            System.out.println("ROTACIÓN: " + archivoLog + " renombrado a " + archivoLog + ".1");
        }

        // Escribir la línea
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(archivoLog, true))) {
            writer.write(lineaLog);
            writer.newLine();
            writer.flush();
        }

        System.out.println("Log escrito: " + mensaje);
    }


    private boolean rotarSiNecesario() throws IOException {
        File logFile = new File(archivoLog);
        if (logFile.exists() && obtenerTamanoLog() >= tamanoMaximo) {
            File archivoRotado = new File(archivoLog + ".1");

            // Eliminar archivo rotado anterior si existe
            if (archivoRotado.exists()) {
                archivoRotado.delete();
            }

            // Renombrar archivo actual
            if (!logFile.renameTo(archivoRotado)) {
                throw new IOException("No se pudo rotar el archivo de log.");
            }

            numeroRotacion++;
            return true;
        }
        return false;
    }


    private long obtenerTamanoLog() {
        File logFile = new File(archivoLog);
        if (logFile.exists()) {
            return logFile.length();
        }
        return 0;
    }

    // Método de prueba
    public static void main(String[] args) {
        try {
            SistemaLog log = new SistemaLog("app.log", 1024); // 1KB máximo

            log.escribirLog("Aplicación iniciada", NivelLog.INFO);
            log.escribirLog("Usuario conectado", NivelLog.INFO);
            log.escribirLog("Error de conexión", NivelLog.ERROR);

            // Agregamos muchos mensajes para forzar rotación
            for (int i = 0; i < 50; i++) {
                log.escribirLog("Mensaje de prueba " + i, NivelLog.INFO);
            }

        } catch (IOException e) {
            System.err.println("Error en logging: " + e.getMessage());
        }
    }
}

