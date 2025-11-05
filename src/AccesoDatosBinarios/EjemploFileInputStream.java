package AccesoDatosBinarios;

import java.io.FileInputStream;
import java.io.IOException;

public class EjemploFileInputStream {
    public static void main(String[] args){
        //Inicializacion de variable para recorrer fichero
        int b;

        // try-catch en el que inicializamnos FileInputStream y se cierra automaticamente
        try (FileInputStream fis = new FileInputStream("Datos.bin")){
           //bucle en el que leemos caracter a caracter
            while((b = fis.read()) != -1){

                System.out.print(b+" ");
            }
        }catch(IOException e){

        }
    }
}
