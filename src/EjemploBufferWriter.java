import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class EjemploBufferWriter {
    public static void main(String[] args) {
        //Array con lineas a escrbir
        String[] lineas = {
                "Encabezado del documento",
                "Esta es la primera linea del contenido",
                "Esta es la segunda linea",
                "Final del docuemnto"
        };

        //BufferedWriter envuelve al objeto FileWriter para añadir Buffering
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("salida.txt"))) {
            //bucle for-eacg que escribe linea a linea con salto incluido
            for (String linea : lineas) {
                //la escritura en el documento
                bw.write(linea);
                //Salto de linea
                bw.newLine();
            }
            // flush() se llama automaticamente al final
        } catch (IOException e) {
            System.err.println("Error al escribor en el archivo: " + e.getMessage());
        }
    }
}
