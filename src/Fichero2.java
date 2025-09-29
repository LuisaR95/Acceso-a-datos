import java.io.File;

public class Fichero2 {
    public static void main(String[] args) {
        // Directorio padre que acabamos de crear en la ruta de documentos
        File ruta = new File("C:\\Users\\AlumnoAfternoon\\Documents\\Puebas Java");

        // Verificar si la ruta existe
        if (ruta.exists()) {
            // Verificar si la ruta especificada es un directorio
            if (ruta.isDirectory()) {
                System.out.println("La ruta presenta un directorio: " + ruta.getAbsolutePath());
            }
            // Verificar si la ruta especificada es un archivo
            else if (ruta.isFile()) {
                System.out.println("La ruta presenta un archivo: " + ruta.getAbsolutePath());
            }
        } else {
            System.out.println("La ruta no existe: " + ruta.getAbsolutePath());
        }
    }
}
