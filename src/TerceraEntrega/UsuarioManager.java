import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// Clase requerida: Usuario
class Usuario {
    private int id;
    private String nombre;
    private String email;
    private int edad;

    public Usuario(int id, String nombre, String email, int edad) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.edad = edad;
    }

    // Getters
    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public String getEmail() { return email; }
    public int getEdad() { return edad; }

    @Override
    public String toString() {
        // Formato de salida requerido
        return " ID: " + id + ", Nombre: " + nombre + ", Email: " + email + ", Edad: " + edad;
    }
}

public class UsuarioManager {

    // --- Funciones de Gestión CRUD ---

    public static void crearTabla(Connection conn) throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS usuarios ("
                + "id INT AUTO_INCREMENT PRIMARY KEY,"
                + "nombre VARCHAR(100) NOT NULL,"
                + "email VARCHAR(100) UNIQUE NOT NULL,"
                + "edad INT"
                + ")";

        // Uso de try-with-resources para PreparedStatement
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Tabla 'usuarios' creada");
        }
    }


    public static int insertarUsuario(Connection conn, String nombre, String email, int edad) throws SQLException {
        String sql = "INSERT INTO usuarios (nombre, email, edad) VALUES (?, ?, ?)";
        int idGenerado = -1;

        // Requisito: Usar PreparedStatement
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            // Validar/bindear parámetros
            pstmt.setString(1, nombre);
            pstmt.setString(2, email);
            pstmt.setInt(3, edad);

            int filasAfectadas = pstmt.executeUpdate();

            if (filasAfectadas > 0) {
                // Obtener el ID generado (auto_increment)
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        idGenerado = rs.getInt(1);
                        System.out.println("Usuario insertado con ID: " + idGenerado);
                    }
                }
            }
        }
        return idGenerado;
    }


    public static List<Usuario> buscarPorNombre(Connection conn, String nombre) throws SQLException {
        // Búsqueda parcial con LIKE y % (PreparedStatement protege contra la inyección)
        String sql = "SELECT id, nombre, email, edad FROM usuarios WHERE nombre LIKE ?";
        List<Usuario> usuarios = new ArrayList<>();

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "%" + nombre + "%"); // El LIKE se bindea de forma segura

            try (ResultSet rs = pstmt.executeQuery()) {
                System.out.println("Usuarios encontrados:");
                while (rs.next()) {
                    Usuario u = new Usuario(
                            rs.getInt("id"),
                            rs.getString("nombre"),
                            rs.getString("email"),
                            rs.getInt("edad")
                    );
                    usuarios.add(u);
                }
            }
        }
        return usuarios;
    }


    public static boolean actualizarEmail(Connection conn, int id, String nuevoEmail) throws SQLException {
        String sql = "UPDATE usuarios SET email = ? WHERE id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nuevoEmail);
            pstmt.setInt(2, id);

            int filasAfectadas = pstmt.executeUpdate();

            if (filasAfectadas > 0) {
                System.out.println("Email actualizado para usuario ID: " + id);
                return true;
            } else {
                return false;
            }
        }
    }


     // Elimina un usuario por ID.

    public static boolean eliminarUsuario(Connection conn, int id) throws SQLException {
        String sql = "DELETE FROM usuarios WHERE id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);

            int filasAfectadas = pstmt.executeUpdate();

            if (filasAfectadas > 0) {
                System.out.println("Usuario eliminado: ID " + id);
                return true;
            } else {
                return false;
            }
        }
    }

    // Caso de Uso de Prueba (main)

    public static void main(String[] args) {
        // IMPORTANTE: Cambia estos valores por tu configuración local
        String url = "jdbc:mysql://localhost:3306/mi_base_datos";
        String user = "root";
        String password = "mysql";

        Connection conn = null;

        try {
            // 1. Establecer Conexión
            conn = DriverManager.getConnection(url, user, password);
            conn.setAutoCommit(false); // Buena práctica: Usar transacciones

            // 2. Crear Tabla
            crearTabla(conn);

            // 3. Insertar Usuarios
            int id1 = insertarUsuario(conn, "Juan Pérez", "juan@email.com", 25);
            int id2 = insertarUsuario(conn, "María García", "maria@email.com", 30);

            // 4. Buscar Usuarios
            List<Usuario> usuariosEncontrados = buscarPorNombre(conn, "Juan");
            for (Usuario u : usuariosEncontrados) {
                System.out.println(u);
            }

            // 5. Actualizar Email
            actualizarEmail(conn, id1, "juan.nuevo@email.com");

            // 6. Eliminar Usuario
            eliminarUsuario(conn, id2);

            // 7. Confirmar Transacción
            conn.commit();

        } catch (SQLException e) {
            System.err.println("ERROR SQL. Rollback de la transacción.");
            System.err.println("Mensaje de error: " + e.getMessage());
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
        } finally {
            // 8. Cerrar Conexión
            try {
                if (conn != null) {
                    conn.close();
                    System.out.println("\nConexión a la base de datos cerrada.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}