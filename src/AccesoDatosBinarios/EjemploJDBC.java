package AccesoDatosBinarios;
import java.sql.*;
public class EjemploJDBC {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/mi_base_datos";
        String usuario = "root";
        String password = "mysql";

        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = DriverManager.getConnection(url, usuario, password);
            System.out.println("Conexion establecida a MySQL");

            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT id, nombre, edad FROM usuarios");

            //Procesar Resultados
            while (rs.next()) {
                int id = rs.getInt("id");
                String nombre = rs.getString("nombre");
                int edad = rs.getInt("edad");

                //mostrar en consola el resultado fila a fila
                System.out.println(id + "-" + nombre + "(" + edad + " años)");
            }
        }catch (Exception e){
            System.err.println("Error SQL: "+e.getMessage());
        }finally {
            try {if (rs != null)  rs.close(); } catch (SQLException e) {}
            try {if (rs != null)  rs.close(); } catch (SQLException e) {}
            try {if (rs != null)  rs.close(); } catch (SQLException e) {}
        }
    }
}