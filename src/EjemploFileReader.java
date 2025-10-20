import javax.imageio.IIOException;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class EjemploFileReader {
    public static void main(String[] args) {
        //variable para almacenar el caracter leido
        int caracter;

        // try-catch cierra automaticamente el FileReader
        try (FileReader fr = new FileReader("C:\\Users\\AlumnoAfternoon\\Documents\\entrada.txt")){
            //read() retorna -1 cuando llega al final del archivo
            while ((caracter = fr.read()) != -1){
                // convertimos el int a char para mostrar el caracter
                System.out.print((char) caracter);
            }

        } catch (IOException e){
            System.out.println("Error al cargar el archivo: " + e.getMessage());

        }
    }
}
