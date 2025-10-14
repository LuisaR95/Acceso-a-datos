import java.io.File;
import java.io.IOException;

public class Fichero5 {

    public static void main(String[] args) throws IOException {
        String directorio = "C:\\Users\\AlumnoAfternoon\\Documents\\Puebas Java\\padre";
        String archivo = "C:\\Users\\AlumnoAfternoon\\Documents\\Puebas Java\\hijo.txt";

        // Craer instancias File utilizando su constructor
        File directorioPadre = new File(directorio);
        File archivoHijo = new File(archivo);

        // Verificar si el archivo y directorio existen
        boolean fin = false;

        do {
        if (!directorioPadre.exists()){
            System.out.println("El directorio no existe");
            directorioPadre.mkdir();
            System.out.println("Directorio creado correctamente");
        } else if (!archivoHijo.exists()){
            System.out.println("El archivo no existe");
            archivoHijo.createNewFile();
            System.out.println("Archivo creado correctamente");
        }else {
            System.out.println("El directorio y el archivo ya existen");
            fin = true;
        }
        } while(!fin);


    }
}
