import java.io.*;
import java.util.ArrayList;
import java.util.List;

// Clase Producto
class Producto {
    private int id;
    private String nombre;
    private double precio;
    private int stock;

    public Producto(int id, String nombre, double precio, int stock) {
        this.id = id;
        this.nombre = nombre;
        this.precio = precio;
        this.stock = stock;
    }

    // Getters
    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public double getPrecio() { return precio; }
    public int getStock() { return stock; }

    @Override
    public String toString() {
        // Garantiza la salida formateada al leer el archivo
        return "ID: " + id + ", Nombre: " + nombre + ", Precio: " + String.format("%.2f", precio) + ", Stock: " + stock;
    }
}

// Clase Principal
public class InventarioManager {

    private static final String ARCHIVO_INVENTARIO = "inventario.dat";

    /**
     * Escribe un producto en el archivo binario (Sobreescribe el archivo).
     */
    public static void escribirProducto(String archivo, Producto producto) throws IOException {
        // Usa try-with-resources y DataOutputStream
        try (
                FileOutputStream fos = new FileOutputStream(archivo);
                DataOutputStream dos = new DataOutputStream(fos)
        ) {
            // Orden de escritura
            dos.writeInt(producto.getId());
            dos.writeUTF(producto.getNombre());
            dos.writeDouble(producto.getPrecio());
            dos.writeInt(producto.getStock());

            // Salida de consola requerida
            System.out.println("Producto guardado: " + producto.getNombre());
        }
    }

    /**
     * Añade un producto al final del archivo (modo append).
     */
    public static void agregarProducto(String archivo, Producto producto) throws IOException {
        // Usa 'true' en FileOutputStream para el modo append
        try (
                FileOutputStream fos = new FileOutputStream(archivo, true);
                DataOutputStream dos = new DataOutputStream(fos)
        ) {
            // Orden de escritura
            dos.writeInt(producto.getId());
            dos.writeUTF(producto.getNombre());
            dos.writeDouble(producto.getPrecio());
            dos.writeInt(producto.getStock());

            // Salida de consola requerida
            System.out.println("Producto añadido: " + producto.getNombre());
        }
    }

    /**
     * Lee todos los productos del archivo binario.
     */
    public static List<Producto> leerProductos(String archivo) throws IOException {
        List<Producto> productos = new ArrayList<>();
        File file = new File(archivo);

        if (!file.exists() || file.length() == 0) {
            return productos;
        }

        try (
                FileInputStream fis = new FileInputStream(file);
                DataInputStream dis = new DataInputStream(fis)
        ) {
            while (true) {
                try {
                    // Orden de lectura (DEBE ser el mismo que el de escritura)
                    int id = dis.readInt();
                    String nombre = dis.readUTF();
                    double precio = dis.readDouble();
                    int stock = dis.readInt();

                    productos.add(new Producto(id, nombre, precio, stock));
                } catch (EOFException e) {
                    break; // Fin del archivo
                }
            }
        }
        return productos;
    }

    public static void main(String[] args) {
        // Borra el archivo anterior para un resultado limpio
        new File(ARCHIVO_INVENTARIO).delete();

        try {
            // --- PASO 1: Escribir ---
            Producto p1 = new Producto(1, "Laptop", 999.99, 10);
            Producto p2 = new Producto(2, "Mouse", 19.99, 50);

            escribirProducto(ARCHIVO_INVENTARIO, p1);
            agregarProducto(ARCHIVO_INVENTARIO, p2);

            // --- PASO 2: Leer e Imprimir ---
            System.out.println("\n--- Lectura del Inventario ---");
            List<Producto> productosLeidos = leerProductos(ARCHIVO_INVENTARIO);

            for (Producto p : productosLeidos) {
                // Esto llama automáticamente al método toString() y produce la salida requerida
                System.out.println(p);
            }

        } catch (IOException e) {
            System.err.println(" Error: " + e.getMessage());
        }
    }
}