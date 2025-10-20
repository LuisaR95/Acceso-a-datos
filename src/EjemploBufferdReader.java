
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class EjemploBufferdReader {
    public static void main(String[] args) {
        //variable para almacenar la linea leida
        String linea;
        //Contador de lineas
        int numLinea = 1;

        //BufferedReader envuelve al objeto FileReader para añadir Buffering
        try (BufferedReader br = new BufferedReader(new FileReader("entradaB.txt"))) {

            //readline() retorna null cuando no hay mas lineas
            while ((linea = br.readLine()) != null) {
                System.out.println(numLinea + ": " + linea);
                numLinea++;
            }
        } catch (IOException e) {
            System.err.println("Error al cargar el archivo: " + e.getMessage());
        }
    }
}