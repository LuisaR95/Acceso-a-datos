package AccesoDatosBinarios;

import java.io.FileInputStream;
import java.io.FileOutputStream; // Necesaria para guardar (store)
import java.io.IOException;
import java.util.Properties;

public class EjemploProperties {
    public static void main(String[] args) {
        // 1. Inicialización del objeto Properties
        Properties prop = new Properties();
        String nombreArchivo = "Config.properties";

        // === Bloque 1: Carga o Creación de la Configuración ===
        try(FileInputStream fis = new FileInputStream(nombreArchivo)) {
            prop.load(fis);
            System.out.println("Configuracion cargada desde el archivo");

        } catch (IOException e)  {
            // Si el archivo no existe o hay error, se crea la configuración por defecto
            System.out.println("Creando configuracion por defecto");
            prop.setProperty("db.host","localhost");
            prop.setProperty("db.port","3306");
            prop.setProperty("db.name","mi_base_datos");
            prop.setProperty("db.debug","false");
        }

        // === Bloque 2: Lectura (Para verificar) ===
        String host = prop.getProperty("db.host");
        String port = prop.getProperty("db.port");
        boolean debug = Boolean.parseBoolean(prop.getProperty("db.debug"));

        System.out.println("=== Configuracion Actual ===");
        System.out.println("Host: " + host);
        System.out.println("Puerto: " + port);
        System.out.println("Modo Debug: " + debug);

        // === Bloque 3: Escritura/Guardado (Usando el código que proporcionaste) ===
        // Esto guarda la configuración (ya sea cargada o por defecto) en el archivo.
        try(FileOutputStream fos = new FileOutputStream(nombreArchivo)){
            // prop.store(outputStream, comentario)
            prop.store(fos, "Configuracion de la aplicacion");
            System.out.println("\nConfiguracion guardada en " + nombreArchivo);

        } catch (IOException e){
            System.err.println("Error al guardar configuracion: " + e.getMessage());
        }
    } // Cierre correcto del método main
} // Cierre correcto de la clase