package DAOs;

import Interfaces.CRUD;
import Models.Conexion;
import Models.Tarea;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;

public class TareaDAO implements CRUD<Tarea> {
    private final Conexion conexion;
    private Connection conn;
    private PreparedStatement ps;
    private ResultSet rs;
    private static final Logger logger = Logger.getLogger(TareaDAO.class.getName());
    
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
            ps.setDate(3, tarea.getFechaAsignacion() != null ? new java.sql.Date(tarea.getFechaAsignacion().getTime()) : null);
            ps.setDate(4, tarea.getFechaEntrega() != null ? new java.sql.Date(tarea.getFechaEntrega().getTime()) : null);
            ps.setInt(5, tarea.getIdCurso());
            
            int resultado = ps.executeUpdate();
            return resultado > 0;
            
        } catch (SQLException e) {
            logger.severe("Error al crear tarea - " + e.getMessage());
            return false;
        } finally {
            try {
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                logger.warning("Error al cerrar conexiones - " + e.getMessage());
            }
        }
    }
    
    /** Crea una tarea y devuelve el ID generado */
    public int createAndReturnId(Tarea tarea) {
        String sql = "INSERT INTO TAREA (titulo, descripcion, fecha_asignacion, fecha_entrega, id_curso) VALUES (?, ?, ?, ?, ?)";
        try {
            conn = conexion.crearConexion();
            ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, tarea.getTitulo());
            ps.setString(2, tarea.getDescripcion());
            ps.setDate(3, tarea.getFechaAsignacion() != null ? new java.sql.Date(tarea.getFechaAsignacion().getTime()) : null);
            ps.setDate(4, tarea.getFechaEntrega() != null ? new java.sql.Date(tarea.getFechaEntrega().getTime()) : null);
            ps.setInt(5, tarea.getIdCurso());

            int affected = ps.executeUpdate();
            if (affected == 0) {
                return -1;
            }
            
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        } catch (SQLException e) {
            logger.severe("Error al crear tarea y obtener ID - " + e.getMessage());
        } finally {
            try {
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (SQLException ignored) {}
        }
        return -1;
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
                tarea.setFechaAsignacion(rs.getDate("fecha_asignacion"));
                tarea.setFechaEntrega(rs.getDate("fecha_entrega"));
                tarea.setIdCurso(rs.getInt("id_curso"));
                tarea.setCursoNombre(rs.getString("curso_nombre"));
            }
            
        } catch (SQLException e) {
            logger.severe("Error al leer tarea - " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                logger.warning("Error al cerrar conexiones - " + e.getMessage());
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
                tarea.setFechaAsignacion(rs.getDate("fecha_asignacion"));
                tarea.setFechaEntrega(rs.getDate("fecha_entrega"));
                tarea.setIdCurso(rs.getInt("id_curso"));
                tarea.setCursoNombre(rs.getString("curso_nombre"));
                tareas.add(tarea);
            }
            
        } catch (SQLException e) {
            logger.severe("Error al leer todas las tareas - " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                logger.warning("Error al cerrar conexiones - " + e.getMessage());
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
            ps.setDate(3, tarea.getFechaAsignacion() != null ? new java.sql.Date(tarea.getFechaAsignacion().getTime()) : null);
            ps.setDate(4, tarea.getFechaEntrega() != null ? new java.sql.Date(tarea.getFechaEntrega().getTime()) : null);
            ps.setInt(5, tarea.getIdCurso());
            ps.setInt(6, tarea.getId());
            
            int resultado = ps.executeUpdate();
            return true;
            
        } catch (SQLException e) {
            logger.severe("Error al actualizar tarea - " + e.getMessage());
            return false;
        } finally {
            try {
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                logger.warning("Error al cerrar conexiones - " + e.getMessage());
            }
        }
    }
    
    @Override
    public boolean delete(int id) {
        // Eliminar notas asociadas antes de eliminar la tarea
        String sqlChild = "DELETE FROM nota_tarea WHERE id_tarea = ?";
        String sql = "DELETE FROM TAREA WHERE id_tarea = ?";
        
        try {
            conn = conexion.crearConexion();
            // Eliminar registros de nota_tarea
            ps = conn.prepareStatement(sqlChild);
            ps.setInt(1, id);
            ps.executeUpdate();
            ps.close();
            // Eliminar la tarea
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            int resultado = ps.executeUpdate();
            return resultado > 0;
            
        } catch (SQLException e) {
            logger.severe("Error al eliminar tarea - " + e.getMessage());
            return false;
        } finally {
            try {
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                logger.warning("Error al cerrar conexiones - " + e.getMessage());
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
                tarea.setFechaAsignacion(rs.getDate("fecha_asignacion"));
                tarea.setFechaEntrega(rs.getDate("fecha_entrega"));
                tarea.setIdCurso(rs.getInt("id_curso"));
                tarea.setCursoNombre(rs.getString("curso_nombre"));
                tareas.add(tarea);
            }
            
        } catch (SQLException e) {
            logger.severe("Error al obtener tareas del curso - " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                logger.warning("Error al cerrar conexiones - " + e.getMessage());
            }
        }
        
        return tareas;
    }
    
    /**
     * Obtiene todas las tareas de un profesor según su ID, uniéndose con cursos
     */
    public List<Tarea> getTareasPorProfesor(int idProfesor) {
        logger.info("============= INICIANDO BÚSQUEDA DE TAREAS =============");
        logger.info("Buscando tareas para profesor ID: " + idProfesor);
        
        // Consulta principal que devuelve todas las tareas de los cursos del profesor
        String sql = "SELECT t.id_tarea, t.titulo, t.descripcion, " +
                    "t.fecha_asignacion, t.fecha_entrega, " +
                    "t.id_curso, c.nombre as curso_nombre " +
                    "FROM tarea t " +
                    "INNER JOIN curso c ON t.id_curso = c.id_curso " +
                    "WHERE c.idProfesor = ? " +
                    "ORDER BY t.fecha_entrega DESC";
        
        List<Tarea> tareas = new ArrayList<>();
        try {
            conn = conexion.crearConexion();
            if (conn == null) {
                logger.severe("Error crítico: No se pudo establecer conexión a la base de datos");
                return tareas;
            }
            
            // Verificar los cursos asignados al profesor y registrar información de diagnóstico
            String checkProfesorSql = "SELECT c.id_curso, c.nombre " +
                                    "FROM curso c " +
                                    "WHERE c.idProfesor = ?";
            
            try (PreparedStatement checkPs = conn.prepareStatement(checkProfesorSql)) {
                checkPs.setInt(1, idProfesor);
                try (ResultSet checkRs = checkPs.executeQuery()) {
                    boolean tieneCursos = false;
                    logger.info("Cursos del profesor ID " + idProfesor + ":");
                    while (checkRs.next()) {
                        tieneCursos = true;
                        logger.info("  - Curso ID: " + checkRs.getInt("id_curso") + 
                                    ", Nombre: " + checkRs.getString("nombre"));
                    }
                    
                    if (!tieneCursos) {
                        logger.warning("El profesor no tiene cursos asignados");
                        return tareas;
                    }
                }
            }
              // Verificar si existen tareas en la base de datos
            String checkTareasSql = "SELECT COUNT(*) as total_tareas FROM tarea";
            try (PreparedStatement checkTareasPs = conn.prepareStatement(checkTareasSql)) {
                try (ResultSet checkTareasRs = checkTareasPs.executeQuery()) {
                    if (checkTareasRs.next()) {
                        int totalTareas = checkTareasRs.getInt("total_tareas");
                        logger.info("Total de tareas en la base de datos: " + totalTareas);
                    }
                }
            }
            
            // Verificar las tareas específicas del profesor
            String checkTareasProfesorSql = "SELECT t.id_tarea, t.titulo, c.nombre as curso_nombre " +
                                          "FROM tarea t " +
                                          "INNER JOIN curso c ON t.id_curso = c.id_curso " +
                                          "WHERE c.idProfesor = ?";
                                          
            try (PreparedStatement checkTareasProfesorPs = conn.prepareStatement(checkTareasProfesorSql)) {
                checkTareasProfesorPs.setInt(1, idProfesor);
                try (ResultSet checkTareasProfesorRs = checkTareasProfesorPs.executeQuery()) {
                    boolean tieneTareas = false;
                    logger.info("Verificando tareas específicas del profesor ID " + idProfesor + ":");
                    while (checkTareasProfesorRs.next()) {
                        tieneTareas = true;
                        logger.info("  - Tarea ID: " + checkTareasProfesorRs.getInt("id_tarea") + 
                                    ", Título: " + checkTareasProfesorRs.getString("titulo") +
                                    ", Curso: " + checkTareasProfesorRs.getString("curso_nombre"));
                    }
                    
                    if (!tieneTareas) {
                        logger.warning("No se encontraron tareas para los cursos del profesor");
                    }
                }
            }
              // Obtener las tareas
            ps = conn.prepareStatement(sql);
            ps.setInt(1, idProfesor);
            logger.info("Ejecutando consulta: " + sql + " con parámetro idProfesor = " + idProfesor);
            rs = ps.executeQuery();
            int count = 0;
            
            while (rs.next()) {
                count++;
                Tarea tarea = new Tarea();
                int idTarea = rs.getInt("id_tarea");
                tarea.setId(idTarea);
                tarea.setTitulo(rs.getString("titulo"));
                tarea.setDescripcion(rs.getString("descripcion"));
                tarea.setFechaAsignacion(rs.getTimestamp("fecha_asignacion"));
                tarea.setFechaEntrega(rs.getTimestamp("fecha_entrega"));
                tarea.setIdCurso(rs.getInt("id_curso"));
                tarea.setCursoNombre(rs.getString("curso_nombre"));
                
                // Calcular el estado de la tarea
                java.util.Date fechaEntrega = tarea.getFechaEntrega();
                java.util.Date ahora = new java.util.Date();
                
                if (fechaEntrega == null) {
                    tarea.setEstado("Pendiente");
                } else if (fechaEntrega.before(ahora)) {
                    tarea.setEstado("Vencida");
                } else if (tarea.getFechaAsignacion() != null && tarea.getFechaAsignacion().after(ahora)) {
                    tarea.setEstado("Programada");
                } else {
                    tarea.setEstado("Activa");
                }
                
                tareas.add(tarea);
                logger.info(String.format(
                    "Tarea encontrada:\n" +
                    " - ID: %d\n" +
                    " - Título: %s\n" +
                    " - Curso: %s\n" +
                    " - Fecha Asignación: %s\n" +
                    " - Fecha Entrega: %s\n" +
                    " - Estado: %s",
                    tarea.getId(),
                    tarea.getTitulo(),
                    tarea.getCursoNombre(),
                    tarea.getFechaAsignacion(),
                    tarea.getFechaEntrega(),
                    tarea.getEstado()
                ));
            }
            
            logger.info("Total de tareas encontradas: " + count);
            
        } catch (SQLException e) {
            logger.severe("Error al obtener tareas por profesor: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (conn != null) conn.close();
                logger.info("Conexiones cerradas correctamente");
            } catch (SQLException e) {
                logger.warning("Error al cerrar conexiones: " + e.getMessage());
            }
        }
        
        logger.info("============= FIN BÚSQUEDA DE TAREAS =============");
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
            logger.severe("Error al asignar nota - " + e.getMessage());
            return false;
        } finally {
            try {
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                logger.warning("Error al cerrar conexiones - " + e.getMessage());
            }
        }
    }
}