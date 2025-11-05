package AccesoDatosBinarios;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
public class EjemploFileOutPutStream {
    public static void main(String[] args) {
        byte[] datos = {72, 111, 108, 97, 32, 109, 117, 110, 100, 111 };
        // try-catch en el que inicializamnos FileInputStream y se cierra automaticamente
        try (FileOutputStream fis = new FileOutputStream("Salida.bin")){
            fis.write(datos);



        }catch(IOException e){
            System.err.println("Error al escribir el archivo: " + e.getMessage() );

        }
    }
}