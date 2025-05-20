package DAOs;

import Interfaces.CRUD;
import Models.Conexion;
import Models.Estudiante;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EstudianteDAO implements CRUD<Estudiante> {
    private final Conexion conexion;
    private Connection conn;
    private PreparedStatement ps;
    private ResultSet rs;
    
    public EstudianteDAO() {
        this.conexion = new Conexion();
    }
    
    @Override
    public boolean create(Estudiante estudiante) {
        System.out.println("[EstudianteDAO] Iniciando creación de nuevo estudiante: " + estudiante.getNombre());
        String sqlUsuario = "INSERT INTO USUARIO (nombre, correo, contraseña, id_rol, fecha_creacion) VALUES (?, ?, ?, ?, NOW())";
        String sqlEstudiante = "INSERT INTO ESTUDIANTE (idUsuario, fecha_nacimiento, direccion, telefono, numero_identificacion, estado, promedio_academico) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try {
            conn = conexion.crearConexion();
            conn.setAutoCommit(false);
            System.out.println("[EstudianteDAO] Iniciando transacción...");
            
            // Insertar en la tabla USUARIO
            ps = conn.prepareStatement(sqlUsuario, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, estudiante.getNombre());
            ps.setString(2, estudiante.getCorreo());
            ps.setString(3, estudiante.getContraseña());
            ps.setInt(4, estudiante.getIdRol());
            ps.executeUpdate();
            
            // Obtener el ID generado para el usuario
            rs = ps.getGeneratedKeys();
            if (rs.next()) {
                int idUsuario = rs.getInt(1);
                System.out.println("[EstudianteDAO] Usuario creado con ID: " + idUsuario);
                
                // Insertar en la tabla ESTUDIANTE
                ps = conn.prepareStatement(sqlEstudiante);
                System.out.println("[EstudianteDAO] Insertando en tabla ESTUDIANTE...");
                ps.setInt(1, idUsuario);
                ps.setDate(2, estudiante.getFechaNacimiento() != null ? new java.sql.Date(estudiante.getFechaNacimiento().getTime()) : null);
                ps.setString(3, estudiante.getDireccion());
                ps.setString(4, estudiante.getTelefono());
                ps.setString(5, estudiante.getNumeroIdentificacion());
                ps.setString(6, estudiante.getEstado());
                ps.setFloat(7, estudiante.getPromedioAcademico());
                ps.executeUpdate();
                
                conn.commit();
                return true;
            }
            
            System.out.println("[EstudianteDAO] Error: No se pudo obtener el ID de usuario generado");
            conn.rollback();
            return false;
            
        } catch (SQLException e) {
            try {
                if (conn != null) {
                    conn.rollback();
                    System.out.println("[EstudianteDAO] Transacción revertida");
                }
            } catch (SQLException ex) {
                System.err.println("[EstudianteDAO] Error en rollback - " + ex.getMessage());
                ex.printStackTrace();
            }
            System.err.println("[EstudianteDAO] Error al crear estudiante - Código: " + e.getErrorCode());
            System.err.println("[EstudianteDAO] Mensaje: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (conn != null) {
                    conn.close();
                    System.out.println("[EstudianteDAO] Conexiones cerradas");
                }
            } catch (SQLException e) {
                System.err.println("[EstudianteDAO] Error al cerrar conexiones - " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
    
    @Override
    public Estudiante read(int id) {
        String sql = "SELECT u.*, e.* FROM USUARIO u INNER JOIN ESTUDIANTE e ON u.id_usu = e.idUsuario WHERE e.id_estudiante = ?";
        Estudiante estudiante = null;
        
        try {
            conn = conexion.crearConexion();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            
            if (rs.next()) {
                estudiante = new Estudiante();
                estudiante.setId(rs.getInt("id_estudiante"));
                estudiante.setIdUsuario(rs.getInt("id_usu"));
                estudiante.setNombre(rs.getString("nombre"));
                estudiante.setCorreo(rs.getString("correo"));
                estudiante.setIdRol(rs.getInt("id_rol"));
                estudiante.setFechaCreacion(rs.getDate("fecha_creacion"));
                estudiante.setUltimaConexion(rs.getDate("ultima_conexion"));
                estudiante.setFechaNacimiento(rs.getDate("fecha_nacimiento"));
                estudiante.setDireccion(rs.getString("direccion"));
                estudiante.setTelefono(rs.getString("telefono"));
                estudiante.setNumeroIdentificacion(rs.getString("numero_identificacion"));
                estudiante.setEstado(rs.getString("estado"));
                estudiante.setPromedioAcademico(rs.getFloat("promedio_academico"));
            }
            
        } catch (SQLException e) {
            System.out.println("Error al leer estudiante - " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (conn != null) {
                    conn.close();
                    System.out.println("[EstudianteDAO] Conexiones cerradas");
                }
            } catch (SQLException e) {
                System.err.println("[EstudianteDAO] Error al cerrar conexiones - " + e.getMessage());
                e.printStackTrace();
            }
        }
        
        return estudiante;
    }
    
    @Override
    public List<Estudiante> readAll() {
        System.out.println("[EstudianteDAO] Iniciando lectura de todos los estudiantes");
        String sql = "SELECT u.*, e.* FROM USUARIO u " +
                    "INNER JOIN ESTUDIANTE e ON u.id_usu = e.idUsuario " +
                    "ORDER BY e.id_estudiante";
        List<Estudiante> estudiantes = new ArrayList<>();

        try {
            conn = conexion.crearConexion();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            System.out.println("[EstudianteDAO] Ejecutando consulta SQL");

            while (rs.next()) {
                Estudiante estudiante = new Estudiante();
                // Datos de Estudiante
                estudiante.setId(rs.getInt("id_estudiante"));
                estudiante.setFechaNacimiento(rs.getDate("fecha_nacimiento"));
                estudiante.setDireccion(rs.getString("direccion"));
                estudiante.setTelefono(rs.getString("telefono"));
                estudiante.setNumeroIdentificacion(rs.getString("numero_identificacion"));
                estudiante.setEstado(rs.getString("estado"));
                estudiante.setPromedioAcademico(rs.getFloat("promedio_academico"));
                
                // Datos de Usuario
                estudiante.setIdUsuario(rs.getInt("id_usu"));
                estudiante.setNombre(rs.getString("nombre"));
                estudiante.setCorreo(rs.getString("correo"));
                estudiante.setIdRol(rs.getInt("id_rol"));
                estudiante.setFechaCreacion(rs.getDate("fecha_creacion"));
                estudiante.setUltimaConexion(rs.getDate("ultima_conexion"));
                
                estudiantes.add(estudiante);
            }
            System.out.println("[EstudianteDAO] Estudiantes encontrados: " + estudiantes.size());

        } catch (SQLException e) {
            System.err.println("[EstudianteDAO] Error al leer todos los estudiantes: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (conn != null) {
                    conn.close();
                    System.out.println("[EstudianteDAO] Conexiones cerradas");
                }
            } catch (SQLException e) {
                System.err.println("[EstudianteDAO] Error al cerrar conexiones: " + e.getMessage());
                e.printStackTrace();
            }
        }

        return estudiantes;
    }
    
    @Override
    public boolean update(Estudiante estudiante) {
        System.out.println("[EstudianteDAO] Iniciando actualización del estudiante ID: " + estudiante.getId());
        
        // SQL condicional para actualizar o no la contraseña
        String sqlUsuarioSinClave = "UPDATE USUARIO SET nombre = ?, correo = ?, id_rol = ? WHERE id_usu = ?";
        String sqlUsuarioConClave = "UPDATE USUARIO SET nombre = ?, correo = ?, contraseña = ?, id_rol = ? WHERE id_usu = ?";
        
        String sqlEstudiante = "UPDATE ESTUDIANTE SET fecha_nacimiento = ?, direccion = ?, telefono = ?, " +
                              "numero_identificacion = ?, estado = ?, promedio_academico = ? WHERE id_estudiante = ?";
        
        try {
            conn = conexion.crearConexion();
            conn.setAutoCommit(false);
            System.out.println("[EstudianteDAO] Iniciando transacción de actualización...");
            
            // Verificar si se actualiza o no la contraseña
            boolean actualizarClave = estudiante.getContraseña() != null && !estudiante.getContraseña().isEmpty();
            System.out.println("[EstudianteDAO] ¿Actualizar contraseña?: " + actualizarClave);
            
            // Actualizar tabla USUARIO (con o sin contraseña)
            if (actualizarClave) {
                ps = conn.prepareStatement(sqlUsuarioConClave);
                ps.setString(1, estudiante.getNombre());
                ps.setString(2, estudiante.getCorreo());
                ps.setString(3, estudiante.getContraseña());
                ps.setInt(4, estudiante.getIdRol());
                ps.setInt(5, estudiante.getIdUsuario());
                System.out.println("[EstudianteDAO] Actualizando usuario con nueva contraseña");
            } else {
                ps = conn.prepareStatement(sqlUsuarioSinClave);
                ps.setString(1, estudiante.getNombre());
                ps.setString(2, estudiante.getCorreo());
                ps.setInt(3, estudiante.getIdRol());
                ps.setInt(4, estudiante.getIdUsuario());
                System.out.println("[EstudianteDAO] Actualizando usuario sin cambiar contraseña");
            }
            
            int filasUsuario = ps.executeUpdate();
            System.out.println("[EstudianteDAO] Filas actualizadas en USUARIO: " + filasUsuario);
            
            // Actualizar tabla ESTUDIANTE
            ps = conn.prepareStatement(sqlEstudiante);
            ps.setDate(1, estudiante.getFechaNacimiento() != null ? new java.sql.Date(estudiante.getFechaNacimiento().getTime()) : null);
            ps.setString(2, estudiante.getDireccion());
            ps.setString(3, estudiante.getTelefono());
            ps.setString(4, estudiante.getNumeroIdentificacion());
            ps.setString(5, estudiante.getEstado());
            ps.setFloat(6, estudiante.getPromedioAcademico());
            ps.setInt(7, estudiante.getId());
            
            int filasEstudiante = ps.executeUpdate();
            System.out.println("[EstudianteDAO] Filas actualizadas en ESTUDIANTE: " + filasEstudiante);
            
            if (filasUsuario > 0 && filasEstudiante > 0) {
                conn.commit();
                System.out.println("[EstudianteDAO] Actualización completada con éxito para ID: " + estudiante.getId());
                return true;
            } else {
                conn.rollback();
                System.err.println("[EstudianteDAO] Error: No se actualizaron registros, realizando rollback");
                return false;
            }
            
        } catch (SQLException e) {
            try {
                if (conn != null) {
                    conn.rollback();
                    System.err.println("[EstudianteDAO] Transacción revertida debido a excepción");
                }
            } catch (SQLException ex) {
                System.err.println("[EstudianteDAO] Error en rollback - " + ex.getMessage());
                ex.printStackTrace();
            }
            System.err.println("[EstudianteDAO] Error al actualizar estudiante - Código: " + e.getErrorCode());
            System.err.println("[EstudianteDAO] Mensaje: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (ps != null) ps.close();
                if (conn != null) {
                    conn.setAutoCommit(true); // Restaurar auto-commit
                    conn.close();
                    System.out.println("[EstudianteDAO] Conexiones cerradas");
                }
            } catch (SQLException e) {
                System.err.println("[EstudianteDAO] Error al cerrar conexiones - " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
    
    @Override
    public boolean delete(int id) {
        System.out.println("[EstudianteDAO] Iniciando proceso de eliminación para estudiante ID: " + id);
        
        String sqlGetUsuarioId = "SELECT idUsuario FROM ESTUDIANTE WHERE id_estudiante = ?";
        String sqlDeleteEstudiante = "DELETE FROM ESTUDIANTE WHERE id_estudiante = ?";
        String sqlDeleteUsuario = "DELETE FROM USUARIO WHERE id_usu = ?";
        
        try {
            conn = conexion.crearConexion();
            conn.setAutoCommit(false); // Iniciar transacción
            System.out.println("[EstudianteDAO] Iniciando transacción de eliminación...");
            
            // Primero obtener el ID del usuario asociado
            int idUsuario = -1;
            ps = conn.prepareStatement(sqlGetUsuarioId);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            
            if (rs.next()) {
                idUsuario = rs.getInt("idUsuario");
                System.out.println("[EstudianteDAO] ID de usuario asociado encontrado: " + idUsuario);
                
                // Luego eliminar el registro de ESTUDIANTE
                ps = conn.prepareStatement(sqlDeleteEstudiante);
                ps.setInt(1, id);
                int estudianteEliminado = ps.executeUpdate();
                System.out.println("[EstudianteDAO] Filas eliminadas de ESTUDIANTE: " + estudianteEliminado);
                
                if (estudianteEliminado > 0 && idUsuario > 0) {
                    // Finalmente eliminar el usuario
                    ps = conn.prepareStatement(sqlDeleteUsuario);
                    ps.setInt(1, idUsuario);
                    int usuarioEliminado = ps.executeUpdate();
                    System.out.println("[EstudianteDAO] Filas eliminadas de USUARIO: " + usuarioEliminado);
                    
                    if (usuarioEliminado > 0) {
                        conn.commit(); // Confirmar transacción
                        System.out.println("[EstudianteDAO] Eliminación completada con éxito para ID: " + id);
                        return true;
                    } else {
                        conn.rollback(); // Revertir si no se pudo eliminar el usuario
                        System.err.println("[EstudianteDAO] Error: No se pudo eliminar el usuario asociado.");
                        return false;
                    }
                } else {
                    conn.rollback(); // Revertir si no se pudo eliminar el estudiante o no se encontró idUsuario
                    System.err.println("[EstudianteDAO] Error: No se pudo eliminar el registro de estudiante.");
                    return false;
                }
            } else {
                conn.rollback(); // Revertir si no se encontró el estudiante
                System.err.println("[EstudianteDAO] Error: No se encontró estudiante con ID: " + id);
                return false;
            }
            
        } catch (SQLException e) {
            try {
                if (conn != null) {
                    conn.rollback(); // Revertir en caso de excepción
                    System.err.println("[EstudianteDAO] Transacción revertida debido a excepción.");
                }
            } catch (SQLException ex) {
                System.err.println("[EstudianteDAO] Error al intentar revertir transacción: " + ex.getMessage());
                ex.printStackTrace();
            }
            
            System.err.println("[EstudianteDAO] Error al eliminar estudiante - Código: " + e.getErrorCode());
            System.err.println("[EstudianteDAO] Mensaje: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (conn != null) {
                    conn.setAutoCommit(true); // Restaurar el auto-commit
                    conn.close();
                    System.out.println("[EstudianteDAO] Conexiones cerradas y auto-commit restaurado");
                }
            } catch (SQLException e) {
                System.err.println("[EstudianteDAO] Error al cerrar conexiones: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Obtiene la lista de estudiantes que no están asignados a un curso específico
     */
    public List<Estudiante> getNoAsignados(int cursoId) {
        String sql = "SELECT e.*, u.nombre, u.correo FROM ESTUDIANTE e " +
                    "INNER JOIN USUARIO u ON e.idUsuario = u.id_usu " +
                    "WHERE e.id_estudiante NOT IN " +
                    "(SELECT ce.id_estudiante FROM CURSO_ESTUDIANTE ce WHERE ce.id_curso = ?)";
        List<Estudiante> estudiantes = new ArrayList<>();
        
        try {
            conn = conexion.crearConexion();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, cursoId);
            rs = ps.executeQuery();
            
            while (rs.next()) {
                Estudiante estudiante = new Estudiante();
                estudiante.setId(rs.getInt("id_estudiante"));
                estudiante.setNombre(rs.getString("nombre"));
                estudiante.setCorreo(rs.getString("correo"));
                estudiante.setNumeroIdentificacion(rs.getString("numero_identificacion"));
                estudiantes.add(estudiante);
            }
            
        } catch (SQLException e) {
            System.out.println("[EstudianteDAO] Error al obtener estudiantes no asignados: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.out.println("[EstudianteDAO] Error al cerrar conexiones: " + e.getMessage());
            }
        }
        return estudiantes;
    }

    /**
     * Calcula el promedio de todas las notas de un estudiante.
     */
    public double calcularPromedio(int idEstudiante) throws SQLException {
        String sql = "SELECT AVG(nota) AS prom FROM nota_tarea WHERE id_estudiante = ?";
        try (Connection con = new Conexion().crearConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idEstudiante);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("prom");
                }
            }
        }
        return 0.0;
    }

    /**
     * Actualiza el campo promedio_academico del estudiante.
     */
    public boolean updatePromedioAcademico(int idEstudiante, double promedio) throws SQLException {
        String sql = "UPDATE estudiante SET promedio_academico = ? WHERE id_estudiante = ?";
        try (Connection con = new Conexion().crearConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDouble(1, promedio);
            ps.setInt(2, idEstudiante);
            return ps.executeUpdate() > 0;
        }
    }
    
    /**
     * Obtiene la lista de estudiantes asignados al curso.
     */
    public List<Models.Estudiante> getAsignados(int cursoId) {
        List<Models.Estudiante> asignados = new ArrayList<>();
        String sql = "SELECT u.id_usu, u.nombre, u.correo, e.* FROM estudiante e " +
                     "JOIN usuario u ON e.idUsuario = u.id_usu " +
                     "JOIN curso_estudiante ce ON ce.id_estudiante = e.id_estudiante " +
                     "WHERE ce.id_curso = ?";
        try (Connection conn = conexion.crearConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, cursoId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Models.Estudiante est = new Models.Estudiante();
                    est.setId(rs.getInt("id_estudiante"));
                    est.setNombre(rs.getString("nombre"));
                    est.setCorreo(rs.getString("correo"));
                    est.setNumeroIdentificacion(rs.getString("numero_identificacion"));
                    // puedes setear otros campos si es necesario
                    asignados.add(est);
                }
            }
        } catch (Exception e) {
            System.err.println("Error en getAsignados: " + e.getMessage());
            e.printStackTrace();
        }
        return asignados;
    }

    /**
     * Obtiene el ID del estudiante a partir del ID de usuario.
     * @param userId ID del usuario
     * @return ID del estudiante o null si no existe
     */    public Integer getEstudianteIdByUserId(int userId) {
        String sql = "SELECT id_estudiante FROM estudiante WHERE idUsuario = ?";
        try (Connection conexionLocal = conexion.crearConexion();
             PreparedStatement statement = conexionLocal.prepareStatement(sql)) {
            statement.setInt(1, userId);
            System.out.println("[EstudianteDAO] Verificando estudiante para userId: " + userId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    int idEstudiante = resultSet.getInt("id_estudiante");
                    System.out.println("[EstudianteDAO] ID de estudiante encontrado: " + idEstudiante);
                    
                    // Verificar si este estudiante tiene cursos asignados
                    String sqlCursos = "SELECT COUNT(*) FROM curso_estudiante WHERE id_estudiante = ?";
                    try (PreparedStatement stmtCursos = conexionLocal.prepareStatement(sqlCursos)) {
                        stmtCursos.setInt(1, idEstudiante);
                        try (ResultSet rsCursos = stmtCursos.executeQuery()) {
                            if (rsCursos.next()) {
                                int cursosCount = rsCursos.getInt(1);
                                System.out.println("[EstudianteDAO] El estudiante " + idEstudiante + " tiene " + cursosCount + " cursos asignados");
                                
                                // Si tiene cursos, verificar si tiene tareas
                                if (cursosCount > 0) {
                                    String sqlTareas = "SELECT COUNT(*) FROM tarea t " +
                                                      "INNER JOIN curso c ON t.id_curso = c.id_curso " +
                                                      "INNER JOIN curso_estudiante ce ON ce.id_curso = c.id_curso " +
                                                      "WHERE ce.id_estudiante = ?";
                                    try (PreparedStatement stmtTareas = conexionLocal.prepareStatement(sqlTareas)) {
                                        stmtTareas.setInt(1, idEstudiante);
                                        try (ResultSet rsTareas = stmtTareas.executeQuery()) {
                                            if (rsTareas.next()) {
                                                int tareasCount = rsTareas.getInt(1);
                                                System.out.println("[EstudianteDAO] El estudiante " + idEstudiante + " tiene acceso a " + tareasCount + " tareas");
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    
                    return idEstudiante;
                } else {
                    System.out.println("[EstudianteDAO] No se encontró estudiante para el userId: " + userId);
                }
            }
        } catch (SQLException e) {
            System.out.println("[EstudianteDAO] Error al obtener ID del estudiante por ID de usuario: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Obtiene los cursos en los que está matriculado un estudiante
     * @param idEstudiante ID del estudiante
     * @return Lista de cursos en formato Map para facilitar su uso en JSP
     */
    public List<java.util.Map<String, Object>> getCursosEstudiante(int idEstudiante) {
        List<java.util.Map<String, Object>> cursos = new ArrayList<>();
        String sql = "SELECT c.*, u.nombre as profesor_nombre FROM curso c " +
                     "INNER JOIN curso_estudiante ce ON c.id_curso = ce.id_curso " +
                     "LEFT JOIN profesor p ON c.idProfesor = p.id_profesor " +
                     "LEFT JOIN usuario u ON p.idUsuario = u.id_usu " +
                     "WHERE ce.id_estudiante = ?";
        
        try (Connection conexionLocal = conexion.crearConexion();
             PreparedStatement statement = conexionLocal.prepareStatement(sql)) {
            statement.setInt(1, idEstudiante);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    java.util.Map<String, Object> curso = new java.util.HashMap<>();
                    curso.put("id", resultSet.getInt("id_curso"));
                    curso.put("nombre", resultSet.getString("nombre"));
                    curso.put("codigo", resultSet.getString("codigo"));
                    curso.put("profesor", resultSet.getString("profesor_nombre"));
                    curso.put("descripcion", resultSet.getString("descripcion"));
                    cursos.add(curso);
                }
            }
        } catch (SQLException e) {
            System.out.println("[EstudianteDAO] Error al obtener cursos del estudiante: " + e.getMessage());
            e.printStackTrace();
        }
        return cursos;
    }
    
    /**
     * Obtiene las tareas asignadas a un estudiante, opcionalmente filtradas por curso y/o estado
     * @param idEstudiante ID del estudiante
     * @param idCurso ID del curso (opcional, puede ser null)
     * @param estado Estado de la tarea (opcional, puede ser null): "Pendiente", "Entregado", "Todos"
     * @return Lista de tareas en formato Map para facilitar su uso en JSP
     */    public List<java.util.Map<String, Object>> getTareasEstudiante(int idEstudiante, Integer idCurso, String estado) {
        List<java.util.Map<String, Object>> tareas = new ArrayList<>();
        
        StringBuilder sqlBuilder = new StringBuilder(
            "SELECT t.id_tarea, t.titulo, t.descripcion, t.fecha_entrega as fecha_limite, " +
            "c.id_curso, c.nombre as curso_nombre, " +
            "nt.id_nota, nt.nota, nt.comentario, nt.fecha_evaluacion " +
            "FROM tarea t " +
            "INNER JOIN curso c ON t.id_curso = c.id_curso " +
            "INNER JOIN curso_estudiante ce ON ce.id_curso = c.id_curso " +
            "LEFT JOIN nota_tarea nt ON nt.id_tarea = t.id_tarea AND nt.id_estudiante = ? " +
            "WHERE ce.id_estudiante = ? ");
            
        if (idCurso != null) {
            sqlBuilder.append("AND c.id_curso = ? ");
        }
        
        if (estado != null && !estado.equals("Todos")) {
            if (estado.equals("Pendiente")) {
                sqlBuilder.append("AND nt.id_nota IS NULL ");
            } else if (estado.equals("En revisión")) {
                sqlBuilder.append("AND nt.id_nota IS NOT NULL AND nt.nota IS NULL ");
            } else if (estado.equals("Calificado")) {
                sqlBuilder.append("AND nt.nota IS NOT NULL ");
            }
        }
        
        sqlBuilder.append("ORDER BY t.fecha_entrega DESC");
        
        try (Connection conexionLocal = conexion.crearConexion();
             PreparedStatement statement = conexionLocal.prepareStatement(sqlBuilder.toString())) {
            
            statement.setInt(1, idEstudiante);
            statement.setInt(2, idEstudiante);
            
            if (idCurso != null) {
                statement.setInt(3, idCurso);
            }
            
            System.out.println("[EstudianteDAO] Ejecutando consulta SQL para tareas: " + statement.toString());
            System.out.println("[EstudianteDAO] SQL completo: " + sqlBuilder.toString());
            System.out.println("[EstudianteDAO] Parámetros: idEstudiante=" + idEstudiante + 
                              ", idCurso=" + idCurso + ", estado=" + estado);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    java.util.Map<String, Object> tarea = new java.util.HashMap<>();
                    int tareaId = resultSet.getInt("id_tarea");
                    String titulo = resultSet.getString("titulo");
                    
                    tarea.put("id", tareaId);
                    tarea.put("titulo", titulo);
                    tarea.put("descripcion", resultSet.getString("descripcion"));
                    tarea.put("curso", resultSet.getString("curso_nombre"));
                    tarea.put("curso_id", resultSet.getInt("id_curso"));
                    tarea.put("fecha_entrega", resultSet.getDate("fecha_limite"));
                    
                    Object notaObj = resultSet.getObject("nota");
                    Object idNotaObj = resultSet.getObject("id_nota");
                    
                    tarea.put("nota", notaObj);
                    tarea.put("comentario", resultSet.getString("comentario"));
                    tarea.put("fecha_evaluacion", resultSet.getDate("fecha_evaluacion"));
                    
                    // Determinar el estado de la tarea
                    String estadoTarea;
                    if (notaObj != null) {
                        estadoTarea = "Calificado";
                    } else if (idNotaObj != null) {
                        estadoTarea = "En revisión";
                    } else {
                        estadoTarea = "Pendiente";
                    }
                    tarea.put("estado", estadoTarea);
                    
                    System.out.println("[EstudianteDAO] Tarea encontrada - ID: " + tareaId + 
                                      ", Título: " + titulo + 
                                      ", Estado: " + estadoTarea + 
                                      ", idNota: " + idNotaObj + 
                                      ", nota: " + notaObj);
                    
                    tareas.add(tarea);
                }
            }
            
            System.out.println("[EstudianteDAO] Total tareas encontradas: " + tareas.size());
            
        } catch (SQLException e) {
            System.out.println("[EstudianteDAO] Error al obtener tareas del estudiante: " + e.getMessage());
            e.printStackTrace();
        }
        return tareas;
    }
    
    /**
     * Obtiene datos para el dashboard del estudiante: cuenta de cursos, 
     * tareas pendientes y promedio general
     * @param idEstudiante ID del estudiante
     * @return Mapa con los datos del dashboard
     */
    public java.util.Map<String, Object> getDatosDashboard(int idEstudiante) {
        java.util.Map<String, Object> datos = new java.util.HashMap<>();
        int cursosCount = 0;
        int tareasPendientes = 0;
        double promedio = 0.0;
        int totalNotas = 0;
        
        // Contar cursos
        String sqlCursos = "SELECT COUNT(*) FROM curso_estudiante WHERE id_estudiante = ?";
        
        // Contar tareas pendientes y calcular promedio
        String sqlTareas = "SELECT nt.nota FROM tarea t " +
                           "INNER JOIN curso_estudiante ce ON t.id_curso = ce.id_curso " +
                           "LEFT JOIN nota_tarea nt ON nt.id_tarea = t.id_tarea AND nt.id_estudiante = ? " +
                           "WHERE ce.id_estudiante = ?";
        
        try (Connection conexionLocal = conexion.crearConexion()) {
            // Contar cursos
            try (PreparedStatement statement1 = conexionLocal.prepareStatement(sqlCursos)) {
                statement1.setInt(1, idEstudiante);
                try (ResultSet resultSet1 = statement1.executeQuery()) {
                    if (resultSet1.next()) {
                        cursosCount = resultSet1.getInt(1);
                    }
                }
            }
            
            // Contar tareas pendientes y calcular promedio
            try (PreparedStatement statement2 = conexionLocal.prepareStatement(sqlTareas)) {
                statement2.setInt(1, idEstudiante);
                statement2.setInt(2, idEstudiante);
                try (ResultSet resultSet2 = statement2.executeQuery()) {                    while (resultSet2.next()) {
                        Object notaObj = resultSet2.getObject("nota");
                        if (notaObj == null) {
                            tareasPendientes++;
                        } else {
                            // Convertir a double independientemente de si es BigDecimal o Double
                            double notaValue;
                            if (notaObj instanceof java.math.BigDecimal) {
                                notaValue = ((java.math.BigDecimal) notaObj).doubleValue();
                            } else if (notaObj instanceof Double) {
                                notaValue = (Double) notaObj;
                            } else {
                                // Para otros tipos, intentar conversión genérica
                                notaValue = Double.parseDouble(notaObj.toString());
                            }
                            promedio += notaValue;
                            totalNotas++;
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("[EstudianteDAO] Error al obtener datos del dashboard: " + e.getMessage());
            e.printStackTrace();
        }
        
        if (totalNotas > 0) {
            promedio = promedio / totalNotas;
        }
        
        datos.put("cursosCount", cursosCount);
        datos.put("tareasPendientes", tareasPendientes);
        datos.put("promedio", Math.round(promedio * 100.0) / 100.0); // Redondear a 2 decimales
        
        return datos;
    }

    /**
     * Registra la entrega de una tarea por parte del estudiante
     * @param idTarea ID de la tarea
     * @param idEstudiante ID del estudiante
     * @param comentarios Comentarios del estudiante sobre la entrega
     * @return true si la entrega se registró correctamente, false en caso contrario
     */    public boolean registrarEntregaTarea(int idTarea, int idEstudiante, String comentarios) {
        String sqlCheck = "SELECT id_nota FROM nota_tarea WHERE id_tarea = ? AND id_estudiante = ?";
        String sqlInsert = "INSERT INTO nota_tarea (id_tarea, id_estudiante, comentario) VALUES (?, ?, ?)";
        String sqlUpdate = "UPDATE nota_tarea SET comentario = ? WHERE id_tarea = ? AND id_estudiante = ?";
        
        try (Connection conexionLocal = conexion.crearConexion()) {
            // Verificar si ya existe una entrega para esta tarea
            boolean existeEntrega = false;
            try (PreparedStatement checkStmt = conexionLocal.prepareStatement(sqlCheck)) {
                checkStmt.setInt(1, idTarea);
                checkStmt.setInt(2, idEstudiante);
                try (ResultSet rs = checkStmt.executeQuery()) {
                    existeEntrega = rs.next();
                }
            }
            
            // Insertar o actualizar según corresponda
            if (existeEntrega) {
                try (PreparedStatement updateStmt = conexionLocal.prepareStatement(sqlUpdate)) {
                    updateStmt.setString(1, comentarios);
                    updateStmt.setInt(2, idTarea);
                    updateStmt.setInt(3, idEstudiante);
                    return updateStmt.executeUpdate() > 0;
                }
            } else {
                try (PreparedStatement insertStmt = conexionLocal.prepareStatement(sqlInsert)) {
                    insertStmt.setInt(1, idTarea);
                    insertStmt.setInt(2, idEstudiante);
                    insertStmt.setString(3, comentarios);
                    return insertStmt.executeUpdate() > 0;
                }
            }
        } catch (SQLException e) {
            System.out.println("[EstudianteDAO] Error al registrar entrega de tarea: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

}