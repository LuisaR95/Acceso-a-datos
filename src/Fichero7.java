import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class Fichero7 {
    public static void main(String[] args) {


        System.out.println("=== ORGANIZADOR DE BIBLIOTECA ===");

        organizarBiblioteca();

        System.out.println();

        verificarBiblioteca();

    }

    static void organizarBiblioteca() {


        {
            Scanner scanner = new Scanner(System.in);
            String basePath = "C:\\Users\\AlumnoAfternoon\\Documents\\biblioteca";
            System.out.print("Introduce el nombre de la categoría: ");
            String categoria = scanner.nextLine();
            File categoriaFolder = new File(basePath, categoria);

            if (!categoriaFolder.exists()) {
                if (categoriaFolder.mkdir()) {
                    System.out.println("✔ Categoría '" + categoria + "' creada exitosamente.");
                } else {
                    System.out.println("✖ Error al crear la categoría.");
                    return;
                }
            } else {
                System.out.println("✔ La categoría '" + categoria + "' ya existe.");
            }


            File catalogo = new File(categoriaFolder, "Catalogo.txt");
            try {
                if (catalogo.createNewFile()) {
                    System.out.println("✔ Catálogo creado: " + catalogo.getAbsolutePath());
                } else {
                    System.out.println("X El catálogo ya existe: " + catalogo.getAbsolutePath());
                }
            } catch (IOException e) {
                System.out.println("✖ Error al crear el catálogo: " + e.getMessage());
                return;
            }
        }
    }

    static void verificarBiblioteca() {



            Scanner scanner = new Scanner(System.in);
            String basePath = "C:\\Users\\AlumnoAfternoon\\Documents\\biblioteca";

            System.out.print("\nIntroduce la categoria del libro: ");
            String nombreCategoria = scanner.nextLine();
            File categoriaFolder1 = new File(basePath, nombreCategoria);
            if (!categoriaFolder1.exists())
                categoriaFolder1.mkdir();
            System.out.print("Introduce el nombre del libro: ");
            String nombreLibro = scanner.nextLine();

        File Libro = new File(basePath, nombreCategoria + "\\" + nombreLibro);
            System.out.println(Libro.toString());

            if (Libro.exists()) {
                System.out.println("✔ El libro existe en: " + Libro.getAbsolutePath());
                System.out.println("Tamaño: " + categoriaFolder1.length() + " Bytes");
            } else {
                   System.out.print("X El libro no existe en " + categoriaFolder1.getAbsolutePath());
                    System.out.print("\n¿Quieres crear el libro? (s/n): ");
                    String opcion = scanner.nextLine();

                    if (opcion.equalsIgnoreCase("s")) {
                        try {
                            if (Libro.createNewFile()) {
                                System.out.println("✔ Libro creado exitosamente en: " + Libro.getAbsolutePath());
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


            scanner.close();
        }
    }




