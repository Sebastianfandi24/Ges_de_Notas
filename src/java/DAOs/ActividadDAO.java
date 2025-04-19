package DAOs;

import Models.Actividad;
import Models.Rol;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import Models.Conexion;

public class ActividadDAO {
    private final Conexion conexion;
    
    public ActividadDAO() {
        this.conexion = new Conexion();
    }
    
    public Actividad obtenerPorId(int id) throws SQLException {
        Actividad actividad = null;
        String sql = "SELECT a.*, r.id_rol, r.nombre as rol_nombre FROM ACTIVIDAD a "
                + "LEFT JOIN GESTION_ACTIVIDADES ga ON a.id_actividad = ga.id_actividad "
                + "LEFT JOIN ROL r ON ga.id_rol = r.id_rol "
                + "WHERE a.id_actividad = ?";
        
        try (Connection conn = conexion.crearConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                if (actividad == null) {
                    actividad = new Actividad();
                    actividad.setId(rs.getInt("id_actividad"));
                    actividad.setNombre(rs.getString("nombre"));
                    actividad.setEnlace(rs.getString("enlace"));
                }
                
                int idRol = rs.getInt("id_rol");
                if (!rs.wasNull()) {
                    Rol rol = new Rol(idRol, rs.getString("rol_nombre"));
                    actividad.addRol(rol);
                }
            }
        }
        
        return actividad;
    }
    
    public List<Actividad> obtenerTodas() throws SQLException {
        List<Actividad> actividades = new ArrayList<>();
        String sql = "SELECT a.*, r.id_rol, r.nombre as rol_nombre FROM ACTIVIDAD a "
                + "LEFT JOIN GESTION_ACTIVIDADES ga ON a.id_actividad = ga.id_actividad "
                + "LEFT JOIN ROL r ON ga.id_rol = r.id_rol "
                + "ORDER BY a.id_actividad";
        
        try (Connection conn = conexion.crearConexion();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            int currentActividadId = -1;
            Actividad currentActividad = null;
            
            while (rs.next()) {
                int actividadId = rs.getInt("id_actividad");
                
                if (currentActividadId != actividadId) {
                    currentActividad = new Actividad();
                    currentActividad.setId(actividadId);
                    currentActividad.setNombre(rs.getString("nombre"));
                    currentActividad.setEnlace(rs.getString("enlace"));
                    actividades.add(currentActividad);
                    currentActividadId = actividadId;
                }
                
                int idRol = rs.getInt("id_rol");
                if (!rs.wasNull()) {
                    Rol rol = new Rol(idRol, rs.getString("rol_nombre"));
                    currentActividad.addRol(rol);
                }
            }
        }
        
        return actividades;
    }
    
    public boolean crear(Actividad actividad, List<Integer> rolesIds) throws SQLException {
        String sqlActividad = "INSERT INTO ACTIVIDAD (nombre, enlace) VALUES (?, ?)";
        String sqlRoles = "INSERT INTO GESTION_ACTIVIDADES (id_rol, id_actividad) VALUES (?, ?)";
        
        try (Connection conn = conexion.crearConexion()) {
            conn.setAutoCommit(false);
            
            try (PreparedStatement ps = conn.prepareStatement(sqlActividad, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, actividad.getNombre());
                ps.setString(2, actividad.getEnlace());
                ps.executeUpdate();
                
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        actividad.setId(generatedKeys.getInt(1));
                    } else {
                        throw new SQLException("No se pudo obtener el ID generado para la actividad.");
                    }
                }
            }
            
            try (PreparedStatement ps = conn.prepareStatement(sqlRoles)) {
                for (Integer idRol : rolesIds) {
                    ps.setInt(1, idRol);
                    ps.setInt(2, actividad.getId());
                    ps.addBatch();
                }
                ps.executeBatch();
            }
            
            conn.commit();
            return true;
        } catch (SQLException e) {
            throw new SQLException("Error al crear la actividad: " + e.getMessage());
        }
    }
    
    public boolean actualizar(Actividad actividad, List<Integer> rolesIds) throws SQLException {
        String sqlActividad = "UPDATE ACTIVIDAD SET nombre = ?, enlace = ? WHERE id_actividad = ?";
        String sqlDeleteRoles = "DELETE FROM GESTION_ACTIVIDADES WHERE id_actividad = ?";
        String sqlInsertRoles = "INSERT INTO GESTION_ACTIVIDADES (id_rol, id_actividad) VALUES (?, ?)";
        
        try (Connection conn = conexion.crearConexion()) {
            conn.setAutoCommit(false);
            
            try {
                // Actualizar actividad
                try (PreparedStatement ps = conn.prepareStatement(sqlActividad)) {
                    ps.setString(1, actividad.getNombre());
                    ps.setString(2, actividad.getEnlace());
                    ps.setInt(3, actividad.getId());
                    ps.executeUpdate();
                }
                
                // Eliminar roles antiguos
                try (PreparedStatement ps = conn.prepareStatement(sqlDeleteRoles)) {
                    ps.setInt(1, actividad.getId());
                    ps.executeUpdate();
                }
                
                // Insertar nuevos roles
                try (PreparedStatement ps = conn.prepareStatement(sqlInsertRoles)) {
                    for (Integer idRol : rolesIds) {
                        ps.setInt(1, idRol);
                        ps.setInt(2, actividad.getId());
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }
                
                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }
    
    public boolean eliminar(int id) throws SQLException {
        String sql = "DELETE FROM ACTIVIDAD WHERE id_actividad = ?";
        
        try (Connection conn = conexion.crearConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, id);
            int result = ps.executeUpdate();
            return result > 0;
        }
    }
}