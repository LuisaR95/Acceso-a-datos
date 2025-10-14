import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

public class Fichero3 {
    public static void main(String[] args) {
      // Utilizamos try-catch porque el objeto URI tiende a dar errores
        try{
            String uriString = "file:///C:/Users/AlumnoAfternoon/Documents/Puebas%20Java";
            URI uri = new URI(uriString);

            File ruta = new File(uri);

            if (ruta.exists()) {
                // Verificar si la ruta especificada es un directorio
                if (ruta.isDirectory()) {
                    System.out.println("La ruta presenta un directorio: " + uri.toString());
                }
                // Verificar si la ruta especificada es un archivo
                else if (ruta.isFile()) {
                    System.out.println("La ruta presenta un archivo: " + uri.toString());
                }
            } else {
                System.out.println("La ruta no existe: " + uri.toString());
            }
        } catch (URISyntaxException e){
            e.printStackTrace();
        }
    }
}
