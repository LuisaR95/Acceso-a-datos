import java.io.File;

public class Fichero4 {

    public static void main(String[] args) {
        //Ruta del directorio como cadena de texto
        String dirPadre = "C:\\Users\\AlumnoAfternoon\\Documents\\Puebas Java";

        //Creación de la instancia utilizando el constructor File
        File directorio = new File(dirPadre);

        // Verificar si el archivo existe y si es un directorio
        if (directorio.exists() && directorio.isDirectory()) {
            //Creación de un array del contenido dentro de la carpeta
            String[] contenido = directorio.list();

            for(int i = 0; i < contenido.length; i++){
                System.out.println(contenido[i]);
            }


        } else {
            System.out.println("La siguiente ruta no es un directorio o no existe");
        }
    }
}


