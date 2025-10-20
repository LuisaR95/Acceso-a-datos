import java.io.IOException;
import java.io.RandomAccessFile;

public class EjemploRandomAccessFile {
    public static void main(String[] args) {
        //Try - catch envuelve el RandomAccessFile
        try (RandomAccessFile raf = new RandomAccessFile("datos.bin","rw")){
         // Escribimos en diferentes posiciones

            raf.writeBytes("INICIO");

            //Nos vamos a la posicion 20
            raf.seek(20);
            raf.writeBytes("MEDIO");

            //Nos vamos a la posicion 40
            raf.seek(40);
            raf.writeBytes("FINAL");

            // Volver al inicio para leer
            raf.seek(0);
            System.out.println("Posicion 0: " + raf.readLine());

            raf.seek(20);
            System.out.println("Posicion 20: " + raf.readLine());

            raf.seek(40);
            System.out.println("Posicion 40: " + raf.readLine());


            // mostramos la lomgitu total del archivo
            System.out.println("Tamaño del archivo: " + raf.length() + " bytes");
        }catch (IOException e){
            System.err.println("Error al acceder al archivo: " + e.getMessage());
        }
    }
}
