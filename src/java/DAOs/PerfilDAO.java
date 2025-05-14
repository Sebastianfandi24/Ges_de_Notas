package DAOs;

import Driver.Conexion;
import Models.Estudiante;
import Models.Profesor;
import Models.Usuario;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import jakarta.xml.bind.DatatypeConverter;
import java.sql.Date;
import java.util.HashMap;
import java.util.Map;

public class PerfilDAO {
    
    private final Connection conexion;
    
    public PerfilDAO() throws SQLException {
        Connection conn = Conexion.getConnection();
        if (conn == null) {
            throw new SQLException("No se pudo establecer la conexión a la base de datos");
        }
        this.conexion = conn;
    }
    
    private String hashPassword(String password) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] digest = md.digest(password.getBytes());
        return DatatypeConverter.printHexBinary(digest);
    }    // Obtener información detallada del usuario según su ID y rol
    public Map<String, Object> obtenerPerfilUsuario(int userId, int rolId) throws SQLException {
        Map<String, Object> perfilUsuario = new HashMap<>();
        
        String sql = "SELECT * FROM usuario WHERE id_usu = ?";
        System.out.println("[PerfilDAO] Obteniendo datos básicos del usuario ID: " + userId + ", Rol: " + rolId);
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // Datos comunes para todos los usuarios
                    perfilUsuario.put("id_usu", rs.getInt("id_usu"));
                    perfilUsuario.put("nombre", rs.getString("nombre"));
                    perfilUsuario.put("correo", rs.getString("correo"));
                    perfilUsuario.put("id_rol", rs.getInt("id_rol"));
                    perfilUsuario.put("fecha_creacion", rs.getDate("fecha_creacion"));
                    perfilUsuario.put("ultima_conexion", rs.getDate("ultima_conexion"));
                    
                    System.out.println("[PerfilDAO] Datos básicos recuperados: " + 
                                      "ID: " + rs.getInt("id_usu") + 
                                      ", Nombre: " + rs.getString("nombre") +
                                      ", Correo: " + rs.getString("correo"));
                    
                    // Obtener datos específicos según el rol
                    switch (rolId) {
                        case 1: // Estudiante
                            obtenerDatosEstudiante(userId, perfilUsuario);
                            break;
                        case 2: // Profesor
                            obtenerDatosProfesor(userId, perfilUsuario);
                            break;
                        case 3: // Administrador
                            obtenerDatosAdministrador(userId, perfilUsuario);
                            break;
                    }
                }
            }
        }
        
        return perfilUsuario;
    }    private void obtenerDatosEstudiante(int userId, Map<String, Object> perfilUsuario) throws SQLException {
        String sql = "SELECT e.* FROM estudiante e WHERE e.idUsuario = ?";
        System.out.println("[PerfilDAO] Ejecutando consulta para datos de estudiante: " + sql + " con userId: " + userId);
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    perfilUsuario.put("id_estudiante", rs.getInt("id_estudiante"));
                    perfilUsuario.put("fecha_nacimiento", rs.getDate("fecha_nacimiento"));
                    perfilUsuario.put("direccion", rs.getString("direccion"));
                    perfilUsuario.put("telefono", rs.getString("telefono"));
                    perfilUsuario.put("numero_identificacion", rs.getString("numero_identificacion"));
                    perfilUsuario.put("estado", rs.getString("estado"));
                    perfilUsuario.put("promedio_academico", rs.getFloat("promedio_academico"));
                    
                    System.out.println("[PerfilDAO] Datos de estudiante recuperados: " + 
                                      "ID: " + rs.getInt("id_estudiante") + 
                                      ", Fecha Nacimiento: " + rs.getDate("fecha_nacimiento") +
                                      ", Teléfono: " + rs.getString("telefono") +
                                      ", Número ID: " + rs.getString("numero_identificacion"));
                } else {
                    System.out.println("[PerfilDAO] No se encontraron datos de estudiante para el usuario ID: " + userId);
                }
            }
        } catch (SQLException e) {
            System.err.println("[PerfilDAO] Error al obtener datos de estudiante: " + e.getMessage());
            e.printStackTrace();
            throw e; // Relanzar para manejo adecuado
        }
    }    private void obtenerDatosProfesor(int userId, Map<String, Object> perfilUsuario) throws SQLException {
        String sql = "SELECT p.* FROM profesor p WHERE p.idUsuario = ?";
        System.out.println("[PerfilDAO] Ejecutando consulta para datos de profesor: " + sql + " con userId: " + userId);
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    perfilUsuario.put("id_profesor", rs.getInt("id_profesor"));
                    perfilUsuario.put("fecha_nacimiento", rs.getDate("fecha_nacimiento"));
                    perfilUsuario.put("direccion", rs.getString("direccion"));
                    perfilUsuario.put("telefono", rs.getString("telefono"));
                    perfilUsuario.put("grado_academico", rs.getString("grado_academico"));
                    perfilUsuario.put("especializacion", rs.getString("especializacion"));
                    perfilUsuario.put("fecha_contratacion", rs.getDate("fecha_contratacion"));
                    perfilUsuario.put("estado", rs.getString("estado"));
                    
                    System.out.println("[PerfilDAO] Datos de profesor recuperados: " + 
                                      "ID: " + rs.getInt("id_profesor") + 
                                      ", Grado académico: " + rs.getString("grado_academico") +
                                      ", Especialización: " + rs.getString("especializacion") +
                                      ", Fecha contratación: " + rs.getDate("fecha_contratacion"));
                } else {
                    System.out.println("[PerfilDAO] No se encontraron datos de profesor para el usuario ID: " + userId);
                }
            }
        } catch (SQLException e) {
            System.err.println("[PerfilDAO] Error al obtener datos de profesor: " + e.getMessage());
            e.printStackTrace();
            throw e; // Relanzar para manejo adecuado
        }
    }private void obtenerDatosAdministrador(int userId, Map<String, Object> perfilUsuario) throws SQLException {
        String sql = "SELECT a.* FROM administrador a WHERE a.idUsuario = ?";
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    perfilUsuario.put("id_admin", rs.getInt("id_admin"));
                    perfilUsuario.put("departamento", rs.getString("departamento"));
                    perfilUsuario.put("fecha_ingreso", rs.getDate("fecha_ingreso"));
                    System.out.println("[PerfilDAO] Datos de administrador recuperados para usuario ID: " + userId + 
                                      ", Departamento: " + rs.getString("departamento") +
                                      ", Fecha ingreso: " + rs.getDate("fecha_ingreso"));
                } else {
                    System.out.println("[PerfilDAO] No se encontraron datos de administrador para el usuario ID: " + userId);
                }
            }
        } catch (SQLException e) {
            // Si no existe la tabla o el registro, no es un error crítico
            System.err.println("[PerfilDAO] Error al obtener datos de administrador: " + e.getMessage());
            e.printStackTrace();
        }
    }
      public boolean actualizarPerfilUsuario(Map<String, Object> datosUsuario) throws SQLException {
        boolean actualizacionExitosa = true;
        
        // int idUsuario = (int) datosUsuario.get("id_usu"); // Variable no utilizada
        int rolId = (int) datosUsuario.get("id_rol");
        
        // Actualizar datos básicos del usuario
        actualizacionExitosa &= actualizarDatosBasicosUsuario(datosUsuario);
        
        // Actualizar datos específicos según rol
        switch (rolId) {
            case 1: // Estudiante
                actualizacionExitosa &= actualizarDatosEstudiante(datosUsuario);
                break;
            case 2: // Profesor
                actualizacionExitosa &= actualizarDatosProfesor(datosUsuario);
                break;
            case 3: // Administrador
                actualizacionExitosa &= actualizarDatosAdministrador(datosUsuario);
                break;
        }
        
        return actualizacionExitosa;
    }
    
    private boolean actualizarDatosBasicosUsuario(Map<String, Object> datosUsuario) throws SQLException {
        String sql = "UPDATE USUARIO SET nombre = ?, correo = ? WHERE id_usu = ?";
        
        // Si se incluye una nueva contraseña, actualizarla
        if (datosUsuario.containsKey("nueva_contraseña") && datosUsuario.get("nueva_contraseña") != null && 
            !((String)datosUsuario.get("nueva_contraseña")).isEmpty()) {
            sql = "UPDATE USUARIO SET nombre = ?, correo = ?, contraseña = ? WHERE id_usu = ?";
        }
        
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, (String) datosUsuario.get("nombre"));
            stmt.setString(2, (String) datosUsuario.get("correo"));
            
            if (datosUsuario.containsKey("nueva_contraseña") && datosUsuario.get("nueva_contraseña") != null && 
                !((String)datosUsuario.get("nueva_contraseña")).isEmpty()) {
                try {
                    String hashedPassword = hashPassword((String) datosUsuario.get("nueva_contraseña"));
                    stmt.setString(3, hashedPassword);
                    stmt.setInt(4, (int) datosUsuario.get("id_usu"));
                } catch (NoSuchAlgorithmException e) {
                    throw new SQLException("Error al procesar la contraseña", e);
                }
            } else {
                stmt.setInt(3, (int) datosUsuario.get("id_usu"));
            }
            
            int filasActualizadas = stmt.executeUpdate();
            return filasActualizadas > 0;
        }
    }
      private boolean actualizarDatosEstudiante(Map<String, Object> datosUsuario) throws SQLException {
        String sql = "UPDATE ESTUDIANTE SET fecha_nacimiento = ?, direccion = ?, telefono = ?, numero_identificacion = ? WHERE idUsuario = ?";
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setDate(1, (datosUsuario.get("fecha_nacimiento") != null) ? 
                         (Date) datosUsuario.get("fecha_nacimiento") : null);
            stmt.setString(2, (String) datosUsuario.get("direccion"));
            stmt.setString(3, (String) datosUsuario.get("telefono"));
            stmt.setString(4, (String) datosUsuario.get("numero_identificacion"));
            stmt.setInt(5, (int) datosUsuario.get("id_usu"));
            
            int filasActualizadas = stmt.executeUpdate();
            return filasActualizadas > 0;
        }
    }
      private boolean actualizarDatosProfesor(Map<String, Object> datosUsuario) throws SQLException {
        String sql = "UPDATE PROFESOR SET fecha_nacimiento = ?, direccion = ?, telefono = ?, grado_academico = ?, especializacion = ? WHERE idUsuario = ?";
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setDate(1, (datosUsuario.get("fecha_nacimiento") != null) ?
                         (Date) datosUsuario.get("fecha_nacimiento") : null);
            stmt.setString(2, (String) datosUsuario.get("direccion"));
            stmt.setString(3, (String) datosUsuario.get("telefono"));
            stmt.setString(4, (String) datosUsuario.get("grado_academico"));
            stmt.setString(5, (String) datosUsuario.get("especializacion"));
            stmt.setInt(6, (int) datosUsuario.get("id_usu"));
            
            int filasActualizadas = stmt.executeUpdate();
            return filasActualizadas > 0;
        }
    }
      private boolean actualizarDatosAdministrador(Map<String, Object> datosUsuario) throws SQLException {
        String sql = "UPDATE ADMINISTRADOR SET departamento = ? WHERE idUsuario = ?";
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, (String) datosUsuario.get("departamento"));
            stmt.setInt(2, (int) datosUsuario.get("id_usu"));
            
            int filasActualizadas = stmt.executeUpdate();
            return filasActualizadas > 0;
        } catch (SQLException e) {
            // Si no existe la tabla, crear el registro
            System.err.println("Aviso: No se pudo actualizar datos de administrador: " + e.getMessage());
            return true; // No consideramos un error crítico
        }
    }
}
