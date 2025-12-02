package TerceraEntrega;

import java.io.*;
import java.sql.*;

public class DBExporter {

    // CONFIGURACIÓN DE LA BASE DE DATOS

    private static final String URL = "jdbc:mysql://localhost:3306/mi_base_datos";
    private static final String USER = "root";
    private static final String PASSWORD = "mysql";
    private static final String ARCHIVO_BACKUP = "backup_productos.dat";

    // FUNCIONES DE BASE DE DATOS Y ARCHIVO (IMPLEMENTACIÓN ANTERIOR)


    public static int exportarProductos(Connection conn, String archivo)
            throws SQLException, IOException {

        final String SQL_SELECT = "SELECT id, nombre FROM productos";
        int contador = 0;

        System.out.println("\nExportando productos...");

        try (DataOutputStream dos = new DataOutputStream(
                new FileOutputStream(archivo));
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SQL_SELECT)) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String nombre = rs.getString("nombre");

                // Escribir los datos en el archivo binario
                dos.writeInt(id);
                dos.writeUTF(nombre);

                System.out.println("Producto exportado: ID=" + id + ", Nombre=" + nombre);
                contador++;
            }
        }
        return contador;
    }


    public static int importarProductos(Connection conn, String archivo)
            throws SQLException, IOException {

        final String SQL_INSERT = "INSERT INTO productos (id, nombre) VALUES (?, ?)";
        int contador = 0;

        System.out.println("\nImportando productos...");

        try (DataInputStream dis = new DataInputStream(
                new FileInputStream(archivo));
             PreparedStatement ps = conn.prepareStatement(SQL_INSERT)) {

            // Desactivamos el autocommit para hacer la importación como una única transacción
            conn.setAutoCommit(false);

            while (true) {
                try {
                    // Leer los datos del archivo binario
                    int id = dis.readInt();
                    String nombre = dis.readUTF();

                    // Insertar en la base de datos
                    ps.setInt(1, id);
                    ps.setString(2, nombre);
                    ps.executeUpdate();

                    System.out.println("Producto importado: ID=" + id + ", Nombre=" + nombre);
                    contador++;

                } catch (EOFException e) {
                    // Fin del archivo
                    break;
                }
            }
            conn.commit(); // Confirmamos todas las inserciones
        } catch (SQLException | IOException e) {
            // Si algo falla, hacemos un rollback
            if (conn != null) {
                conn.rollback();
            }
            throw e; // Relanzamos la excepción
        } finally {
            // Restauramos el autocommit
            if (conn != null) {
                conn.setAutoCommit(true);
            }
        }

        return contador;
    }

    //  MÉTODOS DE SOPORTE PARA EL EJEMPLO DE USO


    private static void setupDatabase(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            // 1. Crear la tabla
            String sqlCreate = "CREATE TABLE IF NOT EXISTS productos (" +
                    "id INT PRIMARY KEY, " +
                    "nombre VARCHAR(100) NOT NULL)";
            stmt.executeUpdate(sqlCreate);
            System.out.println("Tabla 'productos' verificada/creada.");

            // 2. Limpiar la tabla e insertar datos iniciales para la prueba
            stmt.executeUpdate("DELETE FROM productos");
            stmt.executeUpdate("INSERT INTO productos (id, nombre) VALUES (1, 'Laptop')");
            stmt.executeUpdate("INSERT INTO productos (id, nombre) VALUES (2, 'Mouse')");
            stmt.executeUpdate("INSERT INTO productos (id, nombre) VALUES (3, 'Teclado')");
            System.out.println("Datos iniciales insertados para la prueba.");
        }
    }


    // --- MÉTODO PRINCIPAL (MAIN) ---

    public static void main(String[] args) {
        Connection conn = null;

        try {
            // 1. Establecer la conexión con la base de datos
            conn = DriverManager.getConnection(URL, USER, PASSWORD);

            // 2. Configurar la base de datos con datos de prueba
            setupDatabase(conn);

            // EJEMPLO DE USO (Exportar)
            int exportados = exportarProductos(conn, ARCHIVO_BACKUP);
            System.out.println("\n*** Productos exportados: " + exportados + " ***");

            // 3. Limpiar tabla (simulación de pérdida de datos)
            System.out.println("\n--- Simulación de pérdida de datos: Limpiando tabla... ---");
            try (Statement stmt = conn.createStatement()) {
                stmt.executeUpdate("DELETE FROM productos");
            }
            System.out.println("Registros en 'productos' después de limpiar: " + getRowCount(conn));

            // EJEMPLO DE USO (Importar)
            int importados = importarProductos(conn, ARCHIVO_BACKUP);
            System.out.println("\n*** Productos importados: " + importados + " ***");
            System.out.println("Registros en 'productos' después de importar: " + getRowCount(conn));

        } catch (SQLException e) {
            System.err.println("\n[ERROR de BD] Fallo en la operación SQL: " + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("\n[ERROR de Archivo] Fallo en la operación de E/S: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // 4. Cerrar la conexión
            if (conn != null) {
                try {
                    conn.close();
                    System.out.println("\nConexión cerrada.");
                } catch (SQLException e) {
                    System.err.println("Error al cerrar la conexión: " + e.getMessage());
                }
            }
        }
    }

    // Función auxiliar para verificar el número de filas
    private static int getRowCount(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM productos")) {
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        }
    }
}