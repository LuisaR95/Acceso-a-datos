package AccesoDatosBinarios;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class TestMySGLConnection {
    static void main(String[] args) {
        String url="jdbc:mysql://localhost:3306/mi_base_datos";
        String usuario="root";
        String password="mysql";

        try(Connection conn = DriverManager.getConnection(url, usuario, password)){
            System.out.println("Conexión existosa");
        } catch(SQLException e){
            System.out.println("Error al conectar: " + e.getMessage());
        }
    }
}
