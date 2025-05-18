package Models;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Logger;
import java.util.logging.Level;

public class Conexion {
    private static final Logger LOGGER = Logger.getLogger(Conexion.class.getName());
    private static final String URL = "jdbc:mysql://localhost:3306/sistema_academico";
    private static final String USER = "root";
    private static final String PASS = "";
    private static Connection connection;

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USER, PASS);
                LOGGER.info("[Conexion] Conexión establecida exitosamente");
            } catch (ClassNotFoundException e) {
                LOGGER.log(Level.SEVERE, "[Conexion] Error: Driver MySQL no encontrado", e);
                throw new SQLException("Error: Driver MySQL no encontrado", e);
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "[Conexion] Error al conectar con la base de datos", e);
                throw e;
            }
        }
        return connection;
    }

    // Mantenemos crearConexion por compatibilidad
    public Connection crearConexion() {
        try {
            return getConnection();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "[Conexion] Error al crear conexión", e);
            return null;
        }
    }

    public static boolean testConnection() {
        try {
            Connection testConn = getConnection();
            if (testConn != null && !testConn.isClosed()) {
                LOGGER.info("[Conexion] Test de conexión exitoso");
                return true;
            }
            LOGGER.warning("[Conexion] Test de conexión falló - La conexión es nula o está cerrada");
            return false;
        } catch (SQLException e) {
            LOGGER.severe("[Conexion] Test de conexión falló: " + e.getMessage());
            return false;
        }
    }
}