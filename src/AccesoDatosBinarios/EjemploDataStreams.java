package AccesoDatosBinarios;

import java.io.*;
import java.io.IOException;

public class EjemploDataStreams {
    public static void main(String[] args) {
        String nombreArchivo = "primitivos.dat";

        // === 1. Escritura de Datos Primitivos (DataOutputStream) ===
        try (FileOutputStream fileOut = new FileOutputStream(nombreArchivo); // Conecta al archivo
             DataOutputStream dataOut = new DataOutputStream(fileOut)) { // Permite escribir primitivos

            // Escribir datos en un orden específico: int, double, String
            dataOut.writeInt(12345);
            dataOut.writeDouble(1.234);
            dataOut.writeUTF("Producto de ejemplo");

            System.out.println("Datos escritos correctamente en " + nombreArchivo);

        } catch (IOException e) {
            System.err.println("Error al escribir el archivo: " + e.getMessage());
        }

        // === 2. Lectura de Datos Primitivos (DataInputStream) ===
        // ¡El orden de lectura DEBE ser idéntico al orden de escritura!
        try (FileInputStream fileIn = new FileInputStream(nombreArchivo); // Conecta al archivo
             DataInputStream dataIn = new DataInputStream(fileIn)) { // Permite leer primitivos

            // Leer datos en el mismo orden: int, double, String
            int numero = dataIn.readInt();
            double precio = dataIn.readDouble(); // CORREGIDO: Usar readDouble()
            String nombre = dataIn.readUTF();

            // La variable 'activo' no se escribió, se elimina la lectura extra.

            System.out.println("\nDatos leídos:");
            System.out.println("Numero: " + numero);
            System.out.println("Precio: " + precio);
            System.out.println("Nombre: " + nombre);

        } catch (IOException e) {
            // El mensaje de error estaba incorrecto en la sección de lectura
            System.err.println("Error al leer el archivo: " + e.getMessage());
        }
    }
}