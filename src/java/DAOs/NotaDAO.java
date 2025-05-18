package DAOs;

import Interfaces.CRUD;
import Models.Conexion;
import Models.Nota;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class NotaDAO implements CRUD<Nota> {
    private final Conexion conexion;
    private Connection conn;
    private PreparedStatement ps;
    private ResultSet rs;
    private static final Logger logger = Logger.getLogger(NotaDAO.class.getName());

    public NotaDAO() {
        this.conexion = new Conexion();
    }

    @Override
    public boolean create(Nota nota) {
        String sql = "INSERT INTO nota_tarea (id_tarea, id_estudiante, nota, comentario, fecha_evaluacion) VALUES (?, ?, ?, ?, ?)";
        try {
            conn = conexion.crearConexion();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, nota.getIdTarea());
            ps.setInt(2, nota.getIdEstudiante());
            ps.setDouble(3, nota.getNota());
            ps.setString(4, nota.getComentario());
            ps.setDate(5, nota.getFechaEvaluacion() != null ? new java.sql.Date(nota.getFechaEvaluacion().getTime()) : null);
            int rows = ps.executeUpdate();
            logger.info("Nota insertada para tarea=" + nota.getIdTarea() + ", estudiante=" + nota.getIdEstudiante());
            return rows > 0;
        } catch (SQLException e) {
            logger.severe("Error al crear nota: " + e.getMessage());
            return false;
        } finally {
            try { if (ps != null) ps.close(); if (conn != null) conn.close(); } catch (SQLException ex) { logger.severe(ex.getMessage()); }
        }
    }

    @Override
    public Nota read(int id) {
        String sql = "SELECT * FROM nota_tarea WHERE id_nota = ?";
        Nota nota = null;
        try {
            conn = conexion.crearConexion();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (rs.next()) {
                nota = new Nota();
                nota.setIdNota(rs.getInt("id_nota"));
                nota.setIdTarea(rs.getInt("id_tarea"));
                nota.setIdEstudiante(rs.getInt("id_estudiante"));
                nota.setNota(rs.getDouble("nota"));
                nota.setComentario(rs.getString("comentario"));
                nota.setFechaEvaluacion(rs.getDate("fecha_evaluacion"));
            }
        } catch (SQLException e) {
            logger.severe("Error al leer nota: " + e.getMessage());
        } finally {
            try { if (rs != null) rs.close(); if (ps != null) ps.close(); if (conn != null) conn.close(); } catch (SQLException ex) { logger.severe(ex.getMessage()); }
        }
        return nota;
    }

    @Override
    public List<Nota> readAll() {
        String sql = "SELECT * FROM nota_tarea";
        List<Nota> list = new ArrayList<>();
        try {
            conn = conexion.crearConexion();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                Nota nota = new Nota();
                nota.setIdNota(rs.getInt("id_nota"));
                nota.setIdTarea(rs.getInt("id_tarea"));
                nota.setIdEstudiante(rs.getInt("id_estudiante"));
                nota.setNota(rs.getDouble("nota"));
                nota.setComentario(rs.getString("comentario"));
                nota.setFechaEvaluacion(rs.getDate("fecha_evaluacion"));
                list.add(nota);
            }
        } catch (SQLException e) {
            logger.severe("Error al leer todas las notas: " + e.getMessage());
        } finally {
            try { if (rs != null) rs.close(); if (ps != null) ps.close(); if (conn != null) conn.close(); } catch (SQLException ex) { logger.severe(ex.getMessage()); }
        }
        return list;
    }

    @Override
    public boolean update(Nota nota) {
        String sql = "UPDATE nota_tarea SET nota = ?, comentario = ?, fecha_evaluacion = ? WHERE id_nota = ?";
        try {
            conn = conexion.crearConexion();
            ps = conn.prepareStatement(sql);
            ps.setDouble(1, nota.getNota());
            ps.setString(2, nota.getComentario());
            ps.setDate(3, nota.getFechaEvaluacion() != null ? new java.sql.Date(nota.getFechaEvaluacion().getTime()) : null);
            ps.setInt(4, nota.getIdNota());
            int rows = ps.executeUpdate();
            logger.info("Nota actualizada: id_nota=" + nota.getIdNota());
            return rows > 0;
        } catch (SQLException e) {
            logger.severe("Error al actualizar nota: " + e.getMessage());
            return false;
        } finally {
            try { if (ps != null) ps.close(); if (conn != null) conn.close(); } catch (SQLException ex) { logger.severe(ex.getMessage()); }
        }
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM nota_tarea WHERE id_nota = ?";
        try {
            conn = conexion.crearConexion();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            int rows = ps.executeUpdate();
            logger.info("Nota eliminada: id_nota=" + id);
            return rows > 0;
        } catch (SQLException e) {
            logger.severe("Error al eliminar nota: " + e.getMessage());
            return false;
        } finally {
            try { if (ps != null) ps.close(); if (conn != null) conn.close(); } catch (SQLException ex) { logger.severe(ex.getMessage()); }
        }
    }

    /**
     * Método para insertar una nueva nota o actualizar una existente.
     * Usa la sintaxis ON DUPLICATE KEY UPDATE de MySQL.
     */
    public boolean createOrUpdate(Nota nota) {
        String sql = "INSERT INTO nota_tarea (id_tarea, id_estudiante, nota, comentario, fecha_evaluacion) " +
                     "VALUES (?, ?, ?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE nota = ?, comentario = ?, fecha_evaluacion = ?";
        
        try {
            conn = conexion.crearConexion();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, nota.getIdTarea());
            ps.setInt(2, nota.getIdEstudiante());
            ps.setDouble(3, nota.getNota());
            ps.setString(4, nota.getComentario());
            ps.setDate(5, nota.getFechaEvaluacion() != null ? new java.sql.Date(nota.getFechaEvaluacion().getTime()) : null);
            
            // Parámetros para la parte UPDATE
            ps.setDouble(6, nota.getNota());
            ps.setString(7, nota.getComentario());
            ps.setDate(8, nota.getFechaEvaluacion() != null ? new java.sql.Date(nota.getFechaEvaluacion().getTime()) : null);
            
            ps.executeUpdate();
            logger.info("Nota insertada/actualizada para tarea=" + nota.getIdTarea() + ", estudiante=" + nota.getIdEstudiante());
            return true; // Consideramos éxito incluso si no hay cambios
            
        } catch (SQLException e) {
            logger.severe("Error al crear/actualizar nota: " + e.getMessage());
            return false;
        } finally {
            try { if (ps != null) ps.close(); if (conn != null) conn.close(); } catch (SQLException ex) { logger.severe(ex.getMessage()); }
        }
    }    /**
     * Obtiene todas las notas asociadas a una tarea específica.
     */
    public List<Nota> getNotasPorTarea(int idTarea) {
        String sql = "SELECT n.* " +
                   "FROM nota_tarea n " +
                   "WHERE n.id_tarea = ?";
        List<Nota> notas = new ArrayList<>();
        
        try {
            conn = conexion.crearConexion();
            if (conn == null) {
                logger.severe("No se pudo establecer conexión a la base de datos en getNotasPorTarea");
                return notas; // Retornar lista vacía en lugar de null
            }
            
            ps = conn.prepareStatement(sql);
            ps.setInt(1, idTarea);
            rs = ps.executeQuery();
            
            while (rs.next()) {
                Nota nota = new Nota();
                nota.setIdNota(rs.getInt("id_nota"));
                nota.setIdTarea(rs.getInt("id_tarea"));
                nota.setIdEstudiante(rs.getInt("id_estudiante"));
                
                // Manejar explícitamente posibles nulos en la nota
                double valorNota = rs.getDouble("nota");
                if (!rs.wasNull()) {
                    nota.setNota(valorNota);
                } else {
                    // Si la nota es NULL en la base de datos, asignar un valor predeterminado o indicador
                    nota.setNota(0.0); // O cualquier otro valor que indique "sin calificación"
                }
                
                String comentario = rs.getString("comentario");
                nota.setComentario(comentario); // getString ya maneja nulos (devuelve null)
                
                java.sql.Date fechaSQL = rs.getDate("fecha_evaluacion");
                if (fechaSQL != null) {
                    nota.setFechaEvaluacion(new Date(fechaSQL.getTime()));
                }
                
                notas.add(nota);
            }
            
        } catch (SQLException e) {
            logger.severe("Error al obtener notas por tarea: " + e.getMessage());
            e.printStackTrace(); // Añadir stack trace para mejor diagnóstico
        } finally {
            try { 
                if (rs != null) rs.close(); 
                if (ps != null) ps.close(); 
                if (conn != null) conn.close(); 
            } catch (SQLException ex) { 
                logger.severe("Error cerrando recursos: " + ex.getMessage()); 
            }
        }
        
        logger.info("getNotasPorTarea(" + idTarea + ") - Notas recuperadas: " + notas.size());
        return notas;
    }

    /**
     * Obtiene todas las notas de un estudiante específico
     */
    public List<Nota> getNotasPorEstudiante(int idEstudiante) {
        String sql = "SELECT * FROM nota_tarea WHERE id_estudiante = ?";
        List<Nota> list = new ArrayList<>();
        try {
            conn = conexion.crearConexion();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, idEstudiante);
            rs = ps.executeQuery();
            while (rs.next()) {
                Nota nota = new Nota();
                nota.setIdNota(rs.getInt("id_nota"));
                nota.setIdTarea(rs.getInt("id_tarea"));
                nota.setIdEstudiante(idEstudiante);
                nota.setNota(rs.getDouble("nota"));
                nota.setComentario(rs.getString("comentario"));
                nota.setFechaEvaluacion(rs.getDate("fecha_evaluacion"));
                list.add(nota);
            }
        } catch (SQLException e) {
            logger.severe("Error al obtener notas por estudiante: " + e.getMessage());
        } finally {
            try { if (rs != null) rs.close(); if (ps != null) ps.close(); if (conn != null) conn.close(); } catch (SQLException ex) { logger.severe(ex.getMessage()); }
        }
        return list;
    }
}