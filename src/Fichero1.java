import java.io.File;

public class Fichero1 {
    public static void main(String[] args) {
        // Directorio padre que acabamos de crear en la ruta de documentos
        File dirPadre = new File("C:\\Users\\AlumnoAfternoon\\Documents\\Puebas Java");

        // Nombre o ruta relativa al fichero que acabo de crear
        String nomHijo = "hijo.txt";

        // Creo una instancia File utilizando el constructor y variable de arriba
        File archivo = new File(dirPadre, nomHijo);

        // verificar si el archivo existe
        if(archivo.exists()){
            // si el archivo existe me muestra un mensaje en pantalla de que existe mostrando la ruta especificada
            System.out.println("El archivo existe en la ruta:" + archivo.getAbsolutePath());

        }else {
            // si el archivo no existe me muestra un mensaje en pantalla de que existe mostrando la< ruta completa
            System.out.println("El archivo NO existe en la ruta:" + archivo.getAbsolutePath());

        }

    }
}
