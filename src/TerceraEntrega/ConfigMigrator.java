import java.io.*;
import java.sql.*;
import java.util.Properties;

public class ConfigMigrator {

    // CONFIGURACIÓN DE LA BASE DE DATOS Y ARCHIVOS
    private static final String URL = "jdbc:mysql://localhost:3306/mi_base_datos";
    private static final String USER = "root";
    private static final String PASSWORD = "mysql";
    private static final String ARCHIVO_CONFIG_INICIAL = "config.properties";
    private static final String ARCHIVO_CONFIG_EXPORTADO = "config_exportado.properties";

    // ESTRUCTURA DE LA TABLA
    private static final String TABLE_NAME = "configuracion";
    private static final String COLUMN_KEY = "clave";
    private static final String COLUMN_VALUE = "valor";


    public static int migrarPropertiesABD(String archivo, Connection conn)
            throws IOException, SQLException {

        Properties props = new Properties();
        int contador = 0;

        System.out.println("Migrando propiedades a BD...");

        // Cargar el archivo Properties
        try (FileInputStream fis = new FileInputStream(archivo)) {
            props.load(fis);
        }

        // SQL para la inserción/actualización (UPSERT en MySQL)
        // ON DUPLICATE KEY UPDATE se asegura de que si la clave existe, actualiza el valor
        final String SQL_UPSERT = "INSERT INTO " + TABLE_NAME +
                " (" + COLUMN_KEY + ", " + COLUMN_VALUE + ") VALUES (?, ?) " +
                "ON DUPLICATE KEY UPDATE " + COLUMN_VALUE + " = VALUES(" + COLUMN_VALUE + ")";

        // Preparar la sentencia SQL y ejecutar la migración
        try (PreparedStatement ps = conn.prepareStatement(SQL_UPSERT)) {
            conn.setAutoCommit(false); // Iniciar transacción

            for (String key : props.stringPropertyNames()) {
                String value = props.getProperty(key);

                ps.setString(1, key);
                ps.setString(2, value);
                ps.executeUpdate();

                System.out.println(key + " = " + value);
                contador++;
            }
            conn.commit(); // Confirmar transacción
        } catch (SQLException e) {
            conn.rollback(); // Deshacer en caso de error
            throw e;
        } finally {
            conn.setAutoCommit(true); // Restaurar autocommit
        }

        return contador;
    }


    public static int exportarBDaProperties(Connection conn, String archivo)
            throws SQLException, IOException {

        Properties props = new Properties();
        int contador = 0;

        System.out.println("Exportando configuración de BD a archivo...");

        // SQL para obtener todas las configuraciones
        final String SQL_SELECT = "SELECT " + COLUMN_KEY + ", " + COLUMN_VALUE + " FROM " + TABLE_NAME;

        // Leer los datos de la base de datos
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SQL_SELECT)) {

            while (rs.next()) {
                String key = rs.getString(COLUMN_KEY);
                String value = rs.getString(COLUMN_VALUE);
                props.setProperty(key, value);
                contador++;
            }
        }

        // Guardar las propiedades en el archivo
        try (FileOutputStream fos = new FileOutputStream(archivo)) {
            props.store(fos, "Configuración exportada desde BD");
        }

        System.out.println("Archivo Properties creado: " + archivo);
        return contador;
    }



    public static int sincronizarPropiedades(String archivo, Connection conn)
            throws IOException, SQLException {

        Properties fileProps = new Properties();
        int actualizados = 0;

        System.out.println("\nSincronizando Properties con BD...");

        // Cargar el archivo Properties
        try (FileInputStream fis = new FileInputStream(archivo)) {
            fileProps.load(fis);
        }

        // Preparar sentencias SQL
        // SQL para obtener el valor actual de una clave en la BD
        final String SQL_SELECT_CURRENT = "SELECT " + COLUMN_VALUE + " FROM " + TABLE_NAME + " WHERE " + COLUMN_KEY + " = ?";
        // SQL para actualizar un valor
        final String SQL_UPDATE = "UPDATE " + TABLE_NAME + " SET " + COLUMN_VALUE + " = ? WHERE " + COLUMN_KEY + " = ?";
        // SQL para insertar una nueva clave
        final String SQL_INSERT = "INSERT INTO " + TABLE_NAME + " (" + COLUMN_KEY + ", " + COLUMN_VALUE + ") VALUES (?, ?)";


        try (PreparedStatement psSelect = conn.prepareStatement(SQL_SELECT_CURRENT);
             PreparedStatement psUpdate = conn.prepareStatement(SQL_UPDATE);
             PreparedStatement psInsert = conn.prepareStatement(SQL_INSERT)) {

            conn.setAutoCommit(false); // Iniciar transacción

            for (String key : fileProps.stringPropertyNames()) {
                String fileValue = fileProps.getProperty(key);
                String dbValue = null;

                // A. Buscar valor actual en la BD
                psSelect.setString(1, key);
                try (ResultSet rs = psSelect.executeQuery()) {
                    if (rs.next()) {
                        dbValue = rs.getString(COLUMN_VALUE);
                    }
                }

                // B. Lógica de Sincronización
                if (dbValue == null) {
                    // La clave existe en el archivo pero no en la BD (INSERTAR)
                    psInsert.setString(1, key);
                    psInsert.setString(2, fileValue);
                    psInsert.executeUpdate();
                    System.out.println(" [INSERTADO] " + key + " = " + fileValue);
                    actualizados++;

                } else if (!dbValue.equals(fileValue)) {
                    // El valor en el archivo es diferente al de la BD (ACTUALIZAR BD)
                    psUpdate.setString(1, fileValue);
                    psUpdate.setString(2, key);
                    psUpdate.executeUpdate();
                    System.out.println(" [ACTUALIZADO] " + key + ": '" + dbValue + "' -> '" + fileValue + "'");
                    actualizados++;
                } else {
                    // Los valores son iguales, no se hace nada
                }
            }

            conn.commit(); // Confirmar transacción
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }

        return actualizados;
    }


    // MÉTODOS DE SOPORTE PARA EL EJEMPLO DE USO


    private static void createTestFile(String archivo) throws IOException {
        Properties props = new Properties();
        props.setProperty("db.host", "localhost");
        props.setProperty("db.port", "3306");
        props.setProperty("app.nombre", "Mi App");
        props.setProperty("app.version", "2.0");

        try (FileOutputStream fos = new FileOutputStream(archivo)) {
            props.store(fos, "Configuracion de prueba inicial");
        }
        System.out.println("Archivo de prueba creado: " + archivo);
    }


    private static void setupDatabase(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            // Eliminar tabla anterior para una prueba limpia
            stmt.executeUpdate("DROP TABLE IF EXISTS " + TABLE_NAME);

            // Crear la tabla, clave debe ser PRIMARY KEY para el UPSERT
            String sqlCreate = "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_KEY + " VARCHAR(100) PRIMARY KEY, " +
                    COLUMN_VALUE + " VARCHAR(255) NOT NULL)";
            stmt.executeUpdate(sqlCreate);
            System.out.println("Creando tabla '" + TABLE_NAME + "'...");
        }
    }


    // --- MÉTODO PRINCIPAL (MAIN) ---

    public static void main(String[] args) {
        Connection conn = null;

        try {
            // Crear el archivo de configuración inicial
            createTestFile(ARCHIVO_CONFIG_INICIAL);

            // Establecer la conexión con la base de datos
            conn = DriverManager.getConnection(URL, USER, PASSWORD);

            // 2. Configurar la tabla de base de datos
            setupDatabase(conn);

            // EJEMPLO DE USO 1: Migrar de archivo a BD
            int migradas = migrarPropertiesABD(ARCHIVO_CONFIG_INICIAL, conn);
            System.out.println("\n*** Propiedades migradas a BD: " + migradas + " ***");

            //  EJEMPLO DE USO 2: Modificar en BD
            System.out.println("\n--- Modificando valor 'db.port' en BD (3306 -> 3307) ---");
            final String SQL_UPDATE = "UPDATE " + TABLE_NAME + " SET " + COLUMN_VALUE + " = ? WHERE " + COLUMN_KEY + " = ?";
            try (PreparedStatement ps = conn.prepareStatement(SQL_UPDATE)) {
                ps.setString(1, "3307");
                ps.setString(2, "db.port");
                ps.executeUpdate();
            }

            // EJEMPLO DE USO 3: Exportar de BD a archivo
            int exportadas = exportarBDaProperties(conn, ARCHIVO_CONFIG_EXPORTADO);
            System.out.println("\n*** Propiedades exportadas a archivo: " + exportadas + " ***");
            System.out.println("Verifica el archivo " + ARCHIVO_CONFIG_EXPORTADO + " para el cambio (db.port = 3307).");

            // EJEMPLO DE USO 4: Sincronizar (Cambio de vuelta y adición de nueva)
            System.out.println("\n--- Creando archivo modificado para sincronización... ---");
            // Cargar el archivo exportado y hacer cambios para la prueba de sincronización
            Properties syncProps = new Properties();
            try (FileInputStream fis = new FileInputStream(ARCHIVO_CONFIG_EXPORTADO)) {
                syncProps.load(fis);
            }
            syncProps.setProperty("db.port", "3306"); // Cambio de vuelta (BD: 3307, Archivo: 3306)
            syncProps.setProperty("new.feature", "enabled"); // Nueva propiedad
            try (FileOutputStream fos = new FileOutputStream(ARCHIVO_CONFIG_EXPORTADO)) {
                syncProps.store(fos, "Configuracion para prueba de sincronizacion");
            }
            System.out.println("Archivo de sincronización modificado.");

            int sincronizadas = sincronizarPropiedades(ARCHIVO_CONFIG_EXPORTADO, conn);
            System.out.println("\n*** Propiedades sincronizadas/actualizadas: " + sincronizadas + " ***");


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
}