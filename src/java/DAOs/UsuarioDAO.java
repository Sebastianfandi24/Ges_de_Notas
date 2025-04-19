package DAOs;

import Driver.Conexion;
import Models.Usuario;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.time.LocalDateTime;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import jakarta.xml.bind.DatatypeConverter;

public class UsuarioDAO {
    
    private final Connection conexion;
    
    public UsuarioDAO() throws SQLException {
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
    }

    public Usuario autenticarUsuario(String correo, String contraseña) throws SQLException {
        System.out.println("[UsuarioDAO] Iniciando proceso de autenticación para usuario: " + correo);
        String sql = "SELECT * FROM USUARIO WHERE correo = ?";
        
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, correo);
            System.out.println("[UsuarioDAO] Ejecutando consulta de autenticación...");
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    System.out.println("[UsuarioDAO] Usuario encontrado, verificando contraseña...");
                    String hashedPasswordFromDB = rs.getString("contraseña");
                    String hashedInputPassword;
                    try {
                        hashedInputPassword = hashPassword(contraseña);
                        // Imprimir los hashes para depuración
                        System.out.println("[UsuarioDAO] Hash de contraseña almacenado: " + hashedPasswordFromDB);
                        System.out.println("[UsuarioDAO] Hash de contraseña ingresada: " + hashedInputPassword);
                    } catch (NoSuchAlgorithmException e) {
                        System.err.println("[UsuarioDAO] Error al procesar la contraseña: " + e.getMessage());
                        e.printStackTrace();
                        throw new SQLException("Error al procesar la contraseña", e);
                    }
                    
                    if (!hashedPasswordFromDB.equals(hashedInputPassword)) {
                        System.out.println("[UsuarioDAO] Las contraseñas no coinciden");
                        return null;
                    }
                    
                    Usuario usuario = new Usuario();
                    usuario.setId(rs.getInt("id_usu"));
                    usuario.setNombre(rs.getString("nombre"));
                    usuario.setCorreo(rs.getString("correo"));
                    usuario.setContraseña(rs.getString("contraseña"));
                    usuario.setIdRol(rs.getInt("id_rol"));
                    usuario.setFechaCreacion(rs.getDate("fecha_creacion"));
                    usuario.setUltimaConexion(new Date(System.currentTimeMillis()));
                    actualizarUltimaConexion(usuario);
                    return usuario;
                }
                return null;
            }
        }
    }
    
    public void actualizarUltimaConexion(Usuario usuario) throws SQLException {
        System.out.println("[UsuarioDAO] Actualizando última conexión para usuario ID: " + usuario.getId());
        String sql = "UPDATE USUARIO SET ultima_conexion = ? WHERE id_usu = ?";
        
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setDate(1, new Date(System.currentTimeMillis()));
            stmt.setInt(2, usuario.getId());
            int filasActualizadas = stmt.executeUpdate();
            System.out.println("[UsuarioDAO] Última conexión actualizada exitosamente. Filas afectadas: " + filasActualizadas);
        } catch (SQLException e) {
            System.err.println("[UsuarioDAO] Error al actualizar última conexión - Código: " + e.getErrorCode());
            System.err.println("[UsuarioDAO] Mensaje: " + e.getMessage());
            e.printStackTrace();
            throw new SQLException("Error al actualizar la última conexión", e);
        }
    }
}