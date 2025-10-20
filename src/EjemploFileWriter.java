import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

public class EjemploFileWriter {
    public static void main(String[] args) {
        // contenido que queremos escribir
        String contenido = "Primera linea \n segunda lines \n Tercera linea";

        //try-catch cierra automaticamente el FileWriter
        //Por defecto sobreescribe el archivo si existe

        try (FileWriter fw = new FileWriter("C:\\Users\\AlumnoAfternoon\\Documents\\salida.txt")) {
            //Escribimos la cadena completa
            fw.write(contenido);

        } catch (IOException e) {
            System.out.println("Error al cargar el archivo: " + e.getMessage());
        }
    }
}
