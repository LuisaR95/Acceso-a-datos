import java.io.*;
import java.util.Properties;

public class ConfigManager {

    // --- Definición de Valores por Defecto ---

    // Variables requeridas: Configuración de base de datos
    private static final String DEFAULT_DB_HOST = "localhost";
    private static final int DEFAULT_DB_PORT = 3306;
    private static final String DEFAULT_DB_NAME = "mi_base_datos";
    private static final String DEFAULT_DB_USER = "admin";

    // Variables requeridas: Configuración de aplicación
    private static final String DEFAULT_APP_TITULO = "Mi Aplicación";
    private static final String DEFAULT_APP_VERSION = "1.0.0";
    private static final boolean DEFAULT_APP_DEBUG = false;
    private static final String DEFAULT_APP_IDIOMA = "en";

    // Variables requeridas: Configuración de interfaz
    private static final String DEFAULT_UI_TEMA = "claro";
    private static final int DEFAULT_UI_TAMANO_FUENTE = 12;

    /**
     * Define y carga la configuración por defecto.
     */
    private static void inicializarValoresDefecto(Properties props) {
        // Base de Datos
        props.setProperty("db.host", DEFAULT_DB_HOST);
        props.setProperty("db.port", String.valueOf(DEFAULT_DB_PORT));
        props.setProperty("db.name", DEFAULT_DB_NAME);
        props.setProperty("db.user", DEFAULT_DB_USER);

        // Aplicación
        props.setProperty("app.titulo", DEFAULT_APP_TITULO);
        props.setProperty("app.version", DEFAULT_APP_VERSION);
        props.setProperty("app.debug", String.valueOf(DEFAULT_APP_DEBUG));
        props.setProperty("app.idioma", DEFAULT_APP_IDIOMA);

        // Interfaz
        props.setProperty("ui.tema", DEFAULT_UI_TEMA);
        props.setProperty("ui.tamano_fuente", String.valueOf(DEFAULT_UI_TAMANO_FUENTE));

        // La clave 'db.password' se omite por seguridad, pero se podría incluir
        // props.setProperty("db.password", "secure_default"); 
    }

    /**
     * Carga la configuración desde archivo o crea una por defecto.
     * @param archivo ruta del archivo de configuración
     * @return objeto Properties cargado
     */
    public static Properties cargarConfiguracion(String archivo) throws IOException {
        Properties props = new Properties();
        File configFile = new File(archivo);

        // 1. Inicializar con valores por defecto
        inicializarValoresDefecto(props);

        // 2. Intentar cargar el archivo si existe
        if (configFile.exists()) {
            // Usar try-with-resources
            try (FileInputStream fis = new FileInputStream(configFile)) {
                props.load(fis);
                System.out.println("Configuración cargada: " + archivo);
            } catch (IOException e) {
                // Manejar excepción de lectura
                throw new IOException("Error al leer el archivo de configuración: " + archivo, e);
            }
        } else {
            System.out.println("Archivo no encontrado. Usando configuración por defecto.");
            // Si el archivo no existe, guardamos la configuración por defecto para crear el archivo
            guardarConfiguracion(props, archivo, "Configuración inicial por defecto.");
        }
        return props;
    }

    /**
     * Obtiene una propiedad como String con valor por defecto.
     */
    public static String getString(Properties props, String clave, String valorDefecto) {
        return props.getProperty(clave, valorDefecto);
    }

    /**
     * Obtiene una propiedad como int con validación.
     */
    public static int getInt(Properties props, String clave, int valorDefecto) {
        String valor = props.getProperty(clave);
        if (valor == null) {
            return valorDefecto;
        }
        // Validar tipos al convertir (NumberFormatException)
        try {
            return Integer.parseInt(valor.trim());
        } catch (NumberFormatException e) {
            System.err.println("⚠️ Advertencia: Valor inválido para '" + clave + "'. Usando valor por defecto: " + valorDefecto);
            return valorDefecto;
        }
    }

    /**
     * Obtiene una propiedad como boolean.
     */
    public static boolean getBoolean(Properties props, String clave, boolean valorDefecto) {
        String valor = props.getProperty(clave);
        if (valor == null) {
            return valorDefecto;
        }
        // Utiliza el método de parseo de Java, que maneja "true" (ignorando mayúsculas/minúsculas)
        return Boolean.parseBoolean(valor.trim());
    }

    /**
     * Guarda la configuración en archivo.
     */
    public static void guardarConfiguracion(Properties props, String archivo, String comentario)
            throws IOException {
        // Usar try-with-resources
        try (FileOutputStream fos = new FileOutputStream(archivo)) {
            // Documentar el archivo con comentarios claros
            props.store(fos, comentario);
            System.out.println("Configuración guardada: " + archivo);
        } catch (IOException e) {
            // Manejar excepción de escritura
            throw new IOException("Error al escribir el archivo de configuración: " + archivo, e);
        }
    }

    /**
     * Muestra todas las propiedades por consola.
     */
    public static void mostrarConfiguracion(Properties props) {
        // La clase Properties tiene un método list() útil para esto, aunque es simple
        // También podemos iterar para un formato más limpio:
        props.stringPropertyNames().forEach(key -> {
            System.out.println(key + " = " + props.getProperty(key));
        });
    }

    // --- Caso de Uso de Prueba (main) ---

    public static void main(String[] args) {
        String archivoConfig = "app.properties";

        // Limpiar el archivo anterior para una demostración limpia (opcional)
        // new File(archivoConfig).delete();

        try {
            // 1. Cargar la configuración (usará valores por defecto la primera vez)
            Properties config = cargarConfiguracion(archivoConfig);

            // 2. Leer configuración y aplicar valores por defecto
            String dbHost = getString(config, "db.host", "localhost");
            int dbPort = getInt(config, "db.port", 3306);
            boolean debug = getBoolean(config, "app.debug", false);

            // Simular lectura de una clave que no existe para probar el valor por defecto
            int dbTimeout = getInt(config, "db.timeout", 5000);

            System.out.println("\n=== Configuración Actual ===");
            mostrarConfiguracion(config);

            // 3. Modificar configuración
            System.out.println("\n--- Modificando valores ---");
            config.setProperty("app.idioma", "es");
            config.setProperty("ui.tema", "oscuro");
            // Cambiar el puerto (se guardará como String "3307")
            config.setProperty("db.port", "3307");
            // Añadir una propiedad que antes no existía
            config.setProperty("app.log_level", "INFO");

            // 4. Guardar configuración
            guardarConfiguracion(config, archivoConfig, "Configuración de Mi Aplicación");

            // 5. Demostrar la lectura del valor modificado
            System.out.println("\n--- Lectura después de Modificar ---");
            String nuevoIdioma = getString(config, "app.idioma", "en");
            int nuevoPuerto = getInt(config, "db.port", 3306);
            System.out.println("Nuevo idioma: " + nuevoIdioma);
            System.out.println("Nuevo puerto: " + nuevoPuerto);

            // Mostrar cómo se ve el archivo final
            System.out.println("\n--- Contenido Final del Archivo ---");
            // Recargamos el archivo para simular un nuevo inicio de aplicación
            Properties finalConfig = cargarConfiguracion(archivoConfig);
            mostrarConfiguracion(finalConfig);


        } catch (IOException e) {
            System.err.println("❌ Error en la gestión del archivo de configuración: " + e.getMessage());
        }
    }
}