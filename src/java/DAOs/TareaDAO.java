package DAOs;

import Interfaces.CRUD;
import Models.Conexion;
import Models.Tarea;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TareaDAO implements CRUD<Tarea> {
    private final Conexion conexion;
    private Connection conn;
    private PreparedStatement ps;
    private ResultSet rs;
    
    public TareaDAO() {
        this.conexion = new Conexion();
    }
    
    @Override
    public boolean create(Tarea tarea) {
        String sql = "INSERT INTO TAREA (titulo, descripcion, fecha_asignacion, fecha_entrega, id_curso) VALUES (?, ?, ?, ?, ?)";
        
        try {
            conn = conexion.crearConexion();
            ps = conn.prepareStatement(sql);
            ps.setString(1, tarea.getTitulo());
            ps.setString(2, tarea.getDescripcion());
            ps.setDate(3, tarea.getFecha_asignacion() != null ? new java.sql.Date(tarea.getFecha_asignacion().getTime()) : null);
            ps.setDate(4, tarea.getFecha_entrega() != null ? new java.sql.Date(tarea.getFecha_entrega().getTime()) : null);
            ps.setInt(5, tarea.getIdCurso());
            
            int resultado = ps.executeUpdate();
            return resultado > 0;
            
        } catch (SQLException e) {
            System.out.println("Error al crear tarea - " + e.getMessage());
            return false;
        } finally {
            try {
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.out.println("Error al cerrar conexiones - " + e.getMessage());
            }
        }
    }
    
    @Override
    public Tarea read(int id) {
        String sql = "SELECT t.*, c.nombre as curso_nombre FROM TAREA t "
                + "LEFT JOIN CURSO c ON t.id_curso = c.id_curso "
                + "WHERE t.id_tarea = ?";
        Tarea tarea = null;
        
        try {
            conn = conexion.crearConexion();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            
            if (rs.next()) {
                tarea = new Tarea();
                tarea.setId(rs.getInt("id_tarea"));
                tarea.setTitulo(rs.getString("titulo"));
                tarea.setDescripcion(rs.getString("descripcion"));
                tarea.setFecha_asignacion(rs.getDate("fecha_asignacion"));
                tarea.setFecha_entrega(rs.getDate("fecha_entrega"));
                tarea.setId_curso(rs.getInt("id_curso"));
                tarea.setCurso_nombre(rs.getString("curso_nombre"));
            }
            
        } catch (SQLException e) {
            System.out.println("Error al leer tarea - " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.out.println("Error al cerrar conexiones - " + e.getMessage());
            }
        }
        
        return tarea;
    }
    
    @Override
    public List<Tarea> readAll() {
        String sql = "SELECT t.*, c.nombre as curso_nombre FROM TAREA t "
                + "LEFT JOIN CURSO c ON t.id_curso = c.id_curso";
        List<Tarea> tareas = new ArrayList<>();
        
        try {
            conn = conexion.crearConexion();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            
            while (rs.next()) {
                Tarea tarea = new Tarea();
                tarea.setId(rs.getInt("id_tarea"));
                tarea.setTitulo(rs.getString("titulo"));
                tarea.setDescripcion(rs.getString("descripcion"));
                tarea.setFecha_asignacion(rs.getDate("fecha_asignacion"));
                tarea.setFecha_entrega(rs.getDate("fecha_entrega"));
                tarea.setId_curso(rs.getInt("id_curso"));
                tarea.setCurso_nombre(rs.getString("curso_nombre"));
                tareas.add(tarea);
            }
            
        } catch (SQLException e) {
            System.out.println("Error al leer todas las tareas - " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.out.println("Error al cerrar conexiones - " + e.getMessage());
            }
        }
        
        return tareas;
    }
    
    @Override
    public boolean update(Tarea tarea) {
        String sql = "UPDATE TAREA SET titulo = ?, descripcion = ?, fecha_asignacion = ?, fecha_entrega = ?, id_curso = ? WHERE id_tarea = ?";
        
        try {
            conn = conexion.crearConexion();
            ps = conn.prepareStatement(sql);
            ps.setString(1, tarea.getTitulo());
            ps.setString(2, tarea.getDescripcion());
            ps.setDate(3, tarea.getFecha_asignacion() != null ? new java.sql.Date(tarea.getFecha_asignacion().getTime()) : null);
            ps.setDate(4, tarea.getFecha_entrega() != null ? new java.sql.Date(tarea.getFecha_entrega().getTime()) : null);
            ps.setInt(5, tarea.getIdCurso());
            ps.setInt(6, tarea.getId());
            
            int resultado = ps.executeUpdate();
            return resultado > 0;
            
        } catch (SQLException e) {
            System.out.println("Error al actualizar tarea - " + e.getMessage());
            return false;
        } finally {
            try {
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.out.println("Error al cerrar conexiones - " + e.getMessage());
            }
        }
    }
    
    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM TAREA WHERE id_tarea = ?";
        
        try {
            conn = conexion.crearConexion();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            
            int resultado = ps.executeUpdate();
            return resultado > 0;
            
        } catch (SQLException e) {
            System.out.println("Error al eliminar tarea - " + e.getMessage());
            return false;
        } finally {
            try {
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.out.println("Error al cerrar conexiones - " + e.getMessage());
            }
        }
    }
    
    public List<Tarea> getTareasPorCurso(int idCurso) {
        String sql = "SELECT t.*, c.nombre as curso_nombre FROM TAREA t "
                + "LEFT JOIN CURSO c ON t.id_curso = c.id_curso "
                + "WHERE t.id_curso = ?";
        List<Tarea> tareas = new ArrayList<>();
        
        try {
            conn = conexion.crearConexion();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, idCurso);
            rs = ps.executeQuery();
            
            while (rs.next()) {
                Tarea tarea = new Tarea();
                tarea.setId(rs.getInt("id_tarea"));
                tarea.setTitulo(rs.getString("titulo"));
                tarea.setDescripcion(rs.getString("descripcion"));
                tarea.setFecha_asignacion(rs.getDate("fecha_asignacion"));
                tarea.setFecha_entrega(rs.getDate("fecha_entrega"));
                tarea.setId_curso(rs.getInt("id_curso"));
                tarea.setCurso_nombre(rs.getString("curso_nombre"));
                tareas.add(tarea);
            }
            
        } catch (SQLException e) {
            System.out.println("Error al obtener tareas del curso - " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.out.println("Error al cerrar conexiones - " + e.getMessage());
            }
        }
        
        return tareas;
    }
    
    public boolean asignarNota(int idTarea, int idEstudiante, float nota, String comentario) {
        String sql = "INSERT INTO NOTA_TAREA (id_tarea, id_estudiante, nota, fecha_evaluacion, comentario) "
                + "VALUES (?, ?, ?, NOW(), ?) "
                + "ON DUPLICATE KEY UPDATE nota = ?, fecha_evaluacion = NOW(), comentario = ?";
        
        try {
            conn = conexion.crearConexion();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, idTarea);
            ps.setInt(2, idEstudiante);
            ps.setFloat(3, nota);
            ps.setString(4, comentario);
            ps.setFloat(5, nota);
            ps.setString(6, comentario);
            
            int resultado = ps.executeUpdate();
            return resultado > 0;
            
        } catch (SQLException e) {
            System.out.println("Error al asignar nota - " + e.getMessage());
            return false;
        } finally {
            try {
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.out.println("Error al cerrar conexiones - " + e.getMessage());
            }
        }
    }
}