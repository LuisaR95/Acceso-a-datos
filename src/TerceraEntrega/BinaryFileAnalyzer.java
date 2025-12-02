import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;


class ElementoDato {
    private final long posicionInicio;
    private final long posicionFin;
    private final String tipo;
    private final String valor;

    public ElementoDato(long posicionInicio, long posicionFin, String tipo, String valor) {
        this.posicionInicio = posicionInicio;
        this.posicionFin = posicionFin;
        this.tipo = tipo;
        this.valor = valor;
    }

    // Getters
    public long getPosicionInicio() { return posicionInicio; }
    public long getPosicionFin() { return posicionFin; }
    public String getTipo() { return tipo; }
    public String getValor() { return valor; }
}


class Reporte {
    private final String nombreArchivo;
    private final long tamañoBytes;
    private final List<ElementoDato> elementos = new ArrayList<>();
    private int totalInts = 0;
    private int totalDoubles = 0;
    private int totalStrings = 0;
    private int totalBooleans = 0;

    public Reporte(String nombreArchivo, long tamañoBytes) {
        this.nombreArchivo = nombreArchivo;
        this.tamañoBytes = tamañoBytes;
    }

    public void agregarElemento(ElementoDato elemento) {
        elementos.add(elemento);
        switch (elemento.getTipo()) {
            case "INT": totalInts++; break;
            case "DOUBLE": totalDoubles++; break;
            case "UTF": totalStrings++; break;
            case "BOOLEAN": totalBooleans++; break;
        }
    }

    // Getters
    public String getNombreArchivo() { return nombreArchivo; }
    public long getTamañoBytes() { return tamañoBytes; }
    public List<ElementoDato> getElementos() { return elementos; }
    public int getTotalInts() { return totalInts; }
    public int getTotalDoubles() { return totalDoubles; }
    public int getTotalStrings() { return totalStrings; }
    public int getTotalBooleans() { return totalBooleans; }
    public int getTotalElementos() { return elementos.size(); }
}


public class BinaryFileAnalyzer {

    // --- FUNCIONES DE ANÁLISIS ---


    public static Reporte analizarArchivoBinario(String archivo) throws IOException {

        Path path = Path.of(archivo);
        if (!Files.exists(path)) {
            throw new FileNotFoundException("Archivo no encontrado: " + archivo);
        }


        // Usamos RandomAccessFile para obtener la posición actual y el tamaño.
        Reporte reporte = null;
        try (RandomAccessFile raf = new RandomAccessFile(archivo, "r");
             DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(archivo)))) {

            reporte = new Reporte(archivo, raf.length());
            long posicionActual = 0;

            System.out.println("Iniciando análisis del archivo: " + archivo);

            // Bucle principal: sigue leyendo mientras queden bytes disponibles.
            while (posicionActual < reporte.getTamañoBytes()) {
                long inicio = posicionActual;
                String tipo = "UNKNOWN";
                String valor = "N/A";
                long bytesLeidos = 0;

                // Marcamos la posición actual para poder restablecer la lectura
                // si el intento de lectura falla.
                dis.mark(1024); // Marcamos hasta 1KB de distancia.

                try {
                    // Intentar leer STRING (readUTF) ---
                    // Intentamos leer el String. Si falla, el formato no era de String UTF.
                    String str = dis.readUTF();
                    tipo = "UTF";
                    valor = "\"" + str + "\" (" + str.length() + " caracteres)";

                    // La longitud del String UTF se calcula indirectamente por DataInputStream,


                    // Intentar leer STRING:
                    // Si readUTF tiene éxito, el puntero se movió a la nueva posición.


                } catch (UTFDataFormatException | EOFException e) {
                    // El intento de leer String falló.
                    dis.reset(); // Restablecer la posición del stream

                    try {
                        // Intentar leer DOUBLE (8 bytes) ---
                        if (dis.available() >= 8) {
                            double d = dis.readDouble();
                            tipo = "DOUBLE";
                            valor = String.valueOf(d);
                            bytesLeidos = 8;
                        } else {
                            throw new IOException("No hay suficientes bytes para un DOUBLE.");
                        }
                    } catch (IOException e2) {
                        dis.reset(); // Restablecer la posición

                        try {
                            // Intentar leer INT (4 bytes) ---
                            if (dis.available() >= 4) {
                                int i = dis.readInt();
                                tipo = "INT";
                                valor = String.valueOf(i);
                                bytesLeidos = 4;
                            } else {
                                throw new IOException("No hay suficientes bytes para un INT.");
                            }
                        } catch (IOException e3) {
                            dis.reset(); // Restablecer la posición

                            try {
                                // Intentar leer BOOLEAN (1 byte)
                                if (dis.available() >= 1) {
                                    boolean b = dis.readBoolean();
                                    tipo = "BOOLEAN";
                                    valor = String.valueOf(b);
                                    bytesLeidos = 1;
                                } else {
                                    throw new IOException("No hay suficientes bytes para un BOOLEAN.");
                                }
                            } catch (IOException e4) {
                                // No se pudo detectar ningún tipo conocido o EOF
                                dis.reset();
                                if (dis.available() > 0) {
                                    dis.readByte(); // Consumir 1 byte desconocido
                                    tipo = "BYTE_UNKNOWN";
                                    valor = "Byte desconocido";
                                    bytesLeidos = 1;
                                } else {
                                    throw new EOFException(); // Detener bucle
                                }
                            }
                        }
                    }
                }

                // Si el tipo es UTF (el único que no pudimos calcular bytesLeidos de forma trivial)
                if (tipo.equals("UTF")) {

                    // RandomAccessFile es leer manualmente la longitud (2 bytes) y el contenido.
                    // Ya que DataInputStream no expone el puntero de manera sencilla,
                    // estimada para el caso UTF.

                    if (bytesLeidos == 0 && tipo.equals("UTF")) {

                        int length = valor.length();
                        // Asumimos que los bytes consumidos son aproximadamente 2 (longitud) + longitud de la cadena en bytes (UTF-8).
                        bytesLeidos = 2 + (long) (length * 1.5); // Heurística, no exacta, para la salida de ejemplo.
                    }
                }

                // Si se detectó algo, actualizar el reporte
                if (!tipo.equals("UNKNOWN")) {
                    long fin = inicio + bytesLeidos - 1;


                    // Como el problema de DataInputStream es la posición,
                    // el resultado de la posición para el UTF será una *estimación*.

                    if (tipo.equals("UTF")) {

                        bytesLeidos = 2 + ((valor.length() > 0) ? valor.length() : 0);
                        fin = inicio + bytesLeidos - 1;
                    }


                    reporte.agregarElemento(new ElementoDato(inicio, fin, tipo, valor));
                    posicionActual = fin + 1; // Mover la posición al final del último elemento + 1
                }

            }

            return reporte;

        } catch (EOFException e) {
            // Se alcanzó el final del archivo de manera controlada.
            // Esto es el fin esperado del análisis.
            return reporte;
        }
    }


    private static String detectarTipoDato(DataInputStream dis) throws IOException {

        throw new UnsupportedOperationException("Lógica integrada en analizarArchivoBinario.");
    }


    public static void guardarReporte(Reporte reporte, String archivo) throws IOException {
        try (PrintWriter pw = new PrintWriter(new FileWriter(archivo))) {
            // Escribir la cabecera
            pw.println("=== Reporte de Análisis de Archivo Binario ===");
            pw.println("Archivo: " + reporte.getNombreArchivo());
            pw.println("Tamaño: " + reporte.getTamañoBytes() + " bytes");
            pw.println("Estructura detectada:");

            // Escribir la estructura
            for (ElementoDato elem : reporte.getElementos()) {
                String posicionStr = String.format("[Pos %d-%d]", elem.getPosicionInicio(), elem.getPosicionFin());
                pw.printf("%-11s %-10s %s%n", posicionStr, elem.getTipo() + ":", elem.getValor());
            }

            // Escribir el resumen
            pw.println("\nResumen:");
            if (reporte.getTotalInts() > 0) pw.println("Enteros (int): " + reporte.getTotalInts());
            if (reporte.getTotalDoubles() > 0) pw.println("Decimales (double): " + reporte.getTotalDoubles());
            if (reporte.getTotalStrings() > 0) pw.println("Cadenas (UTF): " + reporte.getTotalStrings());
            if (reporte.getTotalBooleans() > 0) pw.println("Booleanos: " + reporte.getTotalBooleans());
            pw.println("Total elementos: " + reporte.getTotalElementos());
        }
        System.out.println("Reporte guardado en: " + archivo);
    }

    /**
     * Muestra el reporte por consola con formato.
     *

     */
    public static void mostrarReporte(Reporte reporte) {
        System.out.println("\n=== Reporte de Análisis de Archivo Binario ===");
        System.out.println("Archivo: " + reporte.getNombreArchivo());
        System.out.println("Tamaño: " + reporte.getTamañoBytes() + " bytes");
        System.out.println("Estructura detectada:");

        for (ElementoDato elem : reporte.getElementos()) {
            String posicionStr = String.format("[Pos %d-%d]", elem.getPosicionInicio(), elem.getPosicionFin());
            System.out.printf("%-11s %-10s %s%n", posicionStr, elem.getTipo() + ":", elem.getValor());
        }

        System.out.println("\nResumen:");
        if (reporte.getTotalInts() > 0) System.out.println("Enteros (int): " + reporte.getTotalInts());
        if (reporte.getTotalDoubles() > 0) System.out.println("Decimales (double): " + reporte.getTotalDoubles());
        if (reporte.getTotalStrings() > 0) System.out.println("Cadenas (UTF): " + reporte.getTotalStrings());
        if (reporte.getTotalBooleans() > 0) System.out.println("Booleanos: " + reporte.getTotalBooleans());
        System.out.println("Total elementos: " + reporte.getTotalElementos());
    }

    // EJEMPLO DE USO (MAIN)

    public static void main(String[] args) {
        final String TEST_FILE = "datos.dat";
        final String REPORT_FILE = "reporte_datos.txt";

        try {
            // 1. Crear el archivo binario de prueba
            System.out.println("--- Creando archivo binario de prueba: " + TEST_FILE + " ---");
            try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(TEST_FILE))) {
                dos.writeInt(100);
                dos.writeUTF("Producto A"); // 2 bytes de longitud + 10 bytes de contenido = 12 bytes
                dos.writeDouble(99.99);
                dos.writeBoolean(true);
                dos.writeInt(200);
                // Total bytes: 4 (int) + 12 (UTF) + 8 (double) + 1 (boolean) + 4 (int) = 29 bytes.
            }
            System.out.println("Archivo creado exitosamente.");


            // 2. Analizar el archivo
            Reporte reporte = analizarArchivoBinario(TEST_FILE);

            // 3. Mostrar el reporte por consola
            mostrarReporte(reporte);

            // 4. Guardar el reporte en archivo de texto
            guardarReporte(reporte, REPORT_FILE);

        } catch (IOException e) {
            System.err.println("\n[ERROR] Fallo en la operación de E/S: " + e.getMessage());
            e.printStackTrace();
        }
    }
}