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
}