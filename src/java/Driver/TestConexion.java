package Driver;

import java.sql.*;

public class TestConexion {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/sistema_academico";
        String username = "root";
        String password = "";

        try {
            // Registrar el driver JDBC
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // Establecer la conexión
            System.out.println("Intentando conectar a la base de datos...");
            Connection conn = DriverManager.getConnection(url, username, password);
            System.out.println("Conexión exitosa!");
            
            // Verificar si existen cursos para el profesor con ID = 1
            String sql = "SELECT * FROM CURSO WHERE idProfesor = 1";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            
            System.out.println("\nCursos para el profesor ID 1:");
            int count = 0;
            while (rs.next()) {
                count++;
                System.out.println("Curso ID: " + rs.getInt("id_curso") + 
                                 ", Nombre: " + rs.getString("nombre") + 
                                 ", Código: " + rs.getString("codigo"));
            }
            
            if (count == 0) {
                System.out.println("No se encontraron cursos para el profesor ID 1.");
                
                // Verificar si hay cursos en general
                sql = "SELECT * FROM CURSO";
                ps = conn.prepareStatement(sql);
                rs = ps.executeQuery();
                
                System.out.println("\nTodos los cursos en la base de datos:");
                count = 0;
                while (rs.next()) {
                    count++;
                    System.out.println("Curso ID: " + rs.getInt("id_curso") + 
                                     ", Nombre: " + rs.getString("nombre") + 
                                     ", idProfesor: " + rs.getInt("idProfesor"));
                }
                
                if (count == 0) {
                    System.out.println("No hay cursos en la base de datos.");
                }
                
                // Verificar la estructura de la tabla CURSO
                DatabaseMetaData metaData = conn.getMetaData();
                rs = metaData.getColumns(null, null, "CURSO", null);
                
                System.out.println("\nEstructura de la tabla CURSO:");
                while (rs.next()) {
                    System.out.println(rs.getString("COLUMN_NAME") + " - " + 
                                     rs.getString("TYPE_NAME") + " - " + 
                                     rs.getString("COLUMN_SIZE"));
                }
            }
            
            // Cerrar conexiones
            rs.close();
            ps.close();
            conn.close();
            
        } catch (ClassNotFoundException e) {
            System.out.println("Error: Driver JDBC no encontrado - " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("Error de SQL: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("Error general: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
