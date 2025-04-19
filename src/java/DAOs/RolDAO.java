package DAOs;

import Models.Rol;
import Models.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase DAO para gestionar las operaciones de base de datos relacionadas con los roles
 */
public class RolDAO {
    private final Conexion conexion;
    
    public RolDAO() {
        this.conexion = new Conexion();
    }
    
    /**
     * Obtiene todos los roles del sistema
     * @return Lista de roles
     * @throws SQLException Si ocurre un error en la base de datos
     */
    public List<Rol> obtenerTodos() throws SQLException {
        List<Rol> roles = new ArrayList<>();
        String sql = "SELECT * FROM ROL ORDER BY id_rol";
        
        try (Connection conn = conexion.crearConexion();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Rol rol = new Rol();
                rol.setId(rs.getInt("id_rol"));
                rol.setNombre(rs.getString("nombre"));
                roles.add(rol);
            }
        }
        
        return roles;
    }
    
    /**
     * Obtiene un rol por su ID
     * @param id ID del rol a buscar
     * @return Rol encontrado o null si no existe
     * @throws SQLException Si ocurre un error en la base de datos
     */
    public Rol obtenerPorId(int id) throws SQLException {
        String sql = "SELECT * FROM ROL WHERE id_rol = ?";
        
        try (Connection conn = conexion.crearConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Rol rol = new Rol();
                    rol.setId(rs.getInt("id_rol"));
                    rol.setNombre(rs.getString("nombre"));
                    return rol;
                }
            }
        }
        
        return null;
    }
}