package Models;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {

    public static String usuario = "root";
    public static String clave = "";
    public static String servidor = "localhost:3306";
    public static String BD = "sistema_academico";

    public Connection crearConexion() {
        Connection con = null;
        System.out.println("[Conexion] Intentando establecer conexión a la base de datos...");
        System.out.println("[Conexion] URL: jdbc:mysql://" + servidor + "/" + BD);

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            String URL = "jdbc:mysql://" + servidor + "/" + BD;
            con = DriverManager.getConnection(URL, usuario, clave);
            System.out.println("[Conexion] Conexión establecida exitosamente");
        } catch (ClassNotFoundException ex) {
            System.err.println("[Conexion] Error: Driver MySQL no encontrado - " + ex.getMessage());
            ex.printStackTrace();
        } catch (SQLException e) {
            System.err.println("[Conexion] Error de conexión SQL - Código: " + e.getErrorCode());
            System.err.println("[Conexion] Mensaje: " + e.getMessage());
            e.printStackTrace();
        }

        return con;
    }
}