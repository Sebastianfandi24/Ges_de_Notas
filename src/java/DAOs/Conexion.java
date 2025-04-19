package DAOs;

import java.sql.Connection;
import java.sql.SQLException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

public class Conexion {
    private static Connection connection;

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Context initContext = new InitialContext();
                DataSource ds = (DataSource) initContext.lookup("java:comp/env/jdbc/GesDeNotasDB");
                connection = ds.getConnection();
            } catch (Exception e) {
                throw new SQLException("Error al conectar con la base de datos", e);
            }
        }
        return connection;
    }
}
