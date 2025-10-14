import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Scanner;

public class Fichero9 {

    static Scanner scanner = new Scanner(System.in);
    static final String BASEBATH = "C:\\Users\\AlumnoAfternoon\\Documents\\biblioteca";

    public static void main(String[] args) throws URISyntaxException {

        System.out.println("==========================");

        System.out.println("MI ASISTENTE DE ARCHIVOS");

        System.out.println("==========================");


        int opcion;
        do {
            mostrarMenu();
            System.out.print("\nSeleccione una opción: ");
            while (!scanner.hasNextInt()) {
                System.out.println("Por favor, ingrese un número válido.");
                scanner.next(); // limpiar entrada inválida
            }
            opcion = scanner.nextInt();
            scanner.nextLine(); // limpiar el buffer

            switch (opcion) {
                case 1:
                    verificarArchivo(scanner);
                    break;
                case 2:
                    explorarDirectorio(scanner);
                    break;
                case 3:
                    crearCarpeta(scanner);
                    break;
                case 4:
                    crearArchivo(scanner);
                    break;
                case 5:
                    trabajarConURI(scanner);
                    break;
                case 6:
                    System.out.println("Saliendo del programa...");
                    break;
                default:
                    System.out.println("Opción no válida. Intente nuevamente.");
            }
            System.out.println();

        } while (opcion != 6);

        scanner.close();

        System.out.println();

    }

    static void mostrarMenu() {


        System.out.println("1. Verificar archivo");
        System.out.println("2. Explorar carpeta");
        System.out.println("3. Crear carpeta");
        System.out.println("4. Crear archivo");
        System.out.println("5. Trabajar con URIs");
        System.out.println("6. Salir");
        System.out.print("============================ ");
    }

    public static void verificarArchivo(Scanner scanner) {

        System.out.print("\n-- VERIFICAR ARCHIVO -- ");
        System.out.print("\nIngrese la ruta del archivo: ");
        String ruta = scanner.nextLine();
        System.out.print("Introduce la categoria del libro: ");
        String nombreCategoria = scanner.nextLine();
        File categoriaFolder = new File(ruta + File.separator + nombreCategoria);
        if (!categoriaFolder.exists())
            categoriaFolder.mkdir();
        System.out.print("Introduce el nombre del libro: ");
        String nombreLibro = scanner.nextLine();

        File Libro = new File(ruta + File.separator + nombreCategoria + "\\" + nombreLibro);
        System.out.println(Libro.toString());

        if (Libro.exists()) {
            System.out.println("✔ El libro existe en: " + Libro.getAbsolutePath());
            System.out.println("Tamaño: " + Libro.length() + " Bytes");

        } else {
            System.out.print("X El libro no existe en " + categoriaFolder.getAbsolutePath());
            System.out.print("\n¿Quieres crear el libro? (s/n): ");
            String opcion = scanner.nextLine();

            if (opcion.equalsIgnoreCase("s")) {
                try {
                    if (Libro.createNewFile()) {
                        System.out.println("✔ Libro creado exitosamente en: " + Libro.getAbsolutePath());
                        System.out.println("Tamaño: " + Libro.length() + " Bytes");
                    } else {
                        System.out.println("⚠ No se pudo crear el libro.");
                    }
                } catch (IOException e) {
                    System.out.println("X Error al crear el libro: " + e.getMessage());
                }
            } else {
                System.out.println("ℹ No se creó el libro.");
            }
        }
    }

    public static void explorarDirectorio(Scanner scanner) {
        System.out.print("\n-- EXPLORAR DIRECTORIO-- ");
        System.out.print("\nIngrese la ruta de la carpeta: ");
        String ruta = scanner.nextLine();

        File carpeta = new File(ruta);
        if (carpeta.exists() && carpeta.isDirectory()) {
            System.out.println("Contenido del directorio:");
            File[] archivos = carpeta.listFiles();
            if (archivos != null && archivos.length > 0) {
                int contador = 1;
                for (File f : archivos) {
                    String tipo = f.isDirectory() ? "[Carpeta]" : "[Archivo]";
                    System.out.println(contador + ". " + " " + f.getName());
                }
            } else {
                System.out.println("La carpeta está vacía.");
            }
        } else {
            System.out.println("La ruta no existe o no es una carpeta.");
        }
    }

    public static void crearCarpeta(Scanner scanner) {
        System.out.print("\n-- CREAR CARPETA-- ");
        System.out.print("\nIngrese la ruta de la nueva carpeta: ");
        String ruta = scanner.nextLine();

        File nuevaCarpeta = new File(ruta);
        if (!nuevaCarpeta.exists()) {
            if (nuevaCarpeta.mkdirs()) {
                System.out.println("✔ Carpeta creada exitosamante: " + nuevaCarpeta.getAbsolutePath());
            } else {
                System.out.println("No se pudo crear la carpeta.");
            }
        } else {
            System.out.println("X La carpeta ya existe.");
        }
    }

    public static void crearArchivo(Scanner scanner) {
        System.out.print("\nIngrese la ruta del nuevo archivo: ");
        String ruta = scanner.nextLine();

        File nuevoArchivo = new File(ruta);

        if (!nuevoArchivo.exists()) {
            try {
                // Crear directorios padres si no existen
                File carpetaPadre = nuevoArchivo.getParentFile();
                if (carpetaPadre != null && !carpetaPadre.exists()) {
                    if (carpetaPadre.mkdirs()) {
                        System.out.println("Carpetas creadas: " + carpetaPadre.getPath());
                    } else {
                        System.out.println("No se pudo crear las carpetas necesarias.");
                        return;
                    }
                }

                // Crear el archivo
                if (nuevoArchivo.createNewFile()) {
                    System.out.println("✔ Archivo creado exitosamente: " + nuevoArchivo.getPath());
                } else {
                    System.out.println("X No se pudo crear el archivo.");
                }
            } catch (IOException e) {
                System.out.println("X Error al crear el archivo: " + e.getMessage());
            }
        } else {
            System.out.println("✔ El archivo ya existe.");
        }
    }

    public static void trabajarConURI(Scanner scanner) {
        System.out.println("\n-- TRABAJAR CON URI --");
        System.out.println("1. Verificar URI");
        System.out.println("2. Convertir ruta a URI");
        System.out.print("Seleccione una opción: ");

        int opcion1;
        while (!scanner.hasNextInt()) {
            System.out.print("Por favor, ingrese un número válido: ");
            scanner.next();
        }
        opcion1 = scanner.nextInt();
        scanner.nextLine(); // limpiar buffer

        switch (opcion1) {
            case 1:
                verificarUri(scanner);
                break;
            case 2:
                convertirUri(scanner);
                break;
            default:
                System.out.println("Opción no válida.");
        }
    }

    public static void verificarUri(Scanner scanner) {
        System.out.print("Ingrese una URI: ");
        String uriStr = scanner.nextLine();

        try {
            URI uri = new URI(uriStr);
            File archivo = new File(uri);
            if (archivo.exists()) {
                if (archivo.isDirectory()) {
                    System.out.println("✔ La URI representa un directorio en " + archivo.getPath());
                } else if (archivo.isFile()) {
                    System.out.println("✔ La URI representa un archivo en " + archivo.getPath());
                }
            } else {
                System.out.println("X La URI es válida, pero no apunta a un archivo o carpeta existente.");
            }
        } catch (URISyntaxException e) {
            System.out.println("X La URI ingresada no es válida: " + e.getMessage());
        }
    }

    public static void convertirUri(Scanner scanner) {
        System.out.print("Introduce la ruta a convertir: ");
        String ruta = scanner.nextLine();

        File archivo = new File(ruta);
        URI uri = archivo.toURI();

        if (archivo.exists()) {

            System.out.println("Ruta original: " + archivo.getAbsolutePath());
            System.out.println("URI generada: " + uri.toString());
            System.out.println("✔ La ruta existe y la URI es válida");
        } else {
            System.out.println("X La ruta no existe.");
            System.out.println("URI convertida igualmente: " + uri.toString());
        }
    }
}
