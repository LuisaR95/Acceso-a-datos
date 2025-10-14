import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Scanner;

public class Fichero8 {
    public static void main(String[] args) throws URISyntaxException {
        System.out.println("=== EXPLORADOR INTELIGENTE ===");

        Scanner scanner = new Scanner(System.in);

        System.out.print("Introduce la ruta a explorar: ");
        String ruta = scanner.nextLine();

        File directorio = new File(ruta);

        if (!directorio.exists()) {
            System.out.println("X La ruta no existe.");
        } else if (!directorio.isDirectory()) {
            System.out.println("x La ruta no es un directorio.");
        } else {
            System.out.println("Explorando: " + ruta);
            String[] contenido = directorio.list();

            int totalElementos = 0;

            if (contenido != null && contenido.length > 0) {
                for (String nombre : contenido) {
                    File elemento = new File(directorio, nombre);
                    if (elemento.isFile()) {
                        long tamaño = elemento.length();
                        System.out.println("-" + nombre + " [ARCHIVO - " + tamaño + " bytes]");
                    } else if (elemento.isDirectory()) {
                        String[] subcontenido = elemento.list();
                        int numElementos = (subcontenido != null) ? subcontenido.length : 0;
                        System.out.println("-" + nombre + " [DIRECTORIO - " + numElementos + " elementos]");
                        totalElementos++;
                    }
                }
            } else {
                System.out.println("El directorio está vacío.");
            }
            System.out.println("\n Total de elementos encontrados: " + totalElementos);

        }
        System.out.println("\n CONVERSIÓN A URI: ");
        System.out.println("Ruta original: " + ruta);
        File archivo = new File(ruta);
        URI uri = archivo.toURI();
        if (archivo.exists()) {

            if (archivo.isDirectory()) {
                System.out.println("URI equivalente: " + uri.toString());
                System.out.println("✔La URI es válida y apunta al mismo elemento");
            }

            else if (archivo.isFile()) {
                System.out.println("La ruta presenta un archivo: " + uri.toString());
            }
        } else {
            System.out.println("La ruta no existe: " + uri.toString());
            System.out.println("X Hay un problema con la conversión a URI");
        }
    }
}
