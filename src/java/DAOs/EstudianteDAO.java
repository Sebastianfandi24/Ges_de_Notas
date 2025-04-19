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
        String sqlUsuario = "INSERT INTO USUARIO (nombre, correo, id_rol, fecha_creacion) VALUES (?, ?, ?, NOW())";
        String sqlEstudiante = "INSERT INTO ESTUDIANTE (idUsuario, fecha_nacimiento, direccion, telefono, numero_identificacion, estado, promedio_academico) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try {
            conn = conexion.crearConexion();
            conn.setAutoCommit(false);
            System.out.println("[EstudianteDAO] Iniciando transacción...");
            
            // Insertar en la tabla USUARIO
            ps = conn.prepareStatement(sqlUsuario, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, estudiante.getNombre());
            ps.setString(2, estudiante.getCorreo());
            ps.setInt(3, estudiante.getIdRol());
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
        String sql = "SELECT u.*, e.* FROM USUARIO u INNER JOIN ESTUDIANTE e ON u.id_usu = e.idUsuario";
        List<Estudiante> estudiantes = new ArrayList<>();
        
        try {
            conn = conexion.crearConexion();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            
            while (rs.next()) {
                Estudiante estudiante = new Estudiante();
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
                estudiantes.add(estudiante);
            }
            
        } catch (SQLException e) {
            System.out.println("Error al leer todos los estudiantes - " + e.getMessage());
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
        
        return estudiantes;
    }
    
    @Override
    public boolean update(Estudiante estudiante) {
        String sqlUsuario = "UPDATE USUARIO SET nombre = ?, correo = ? WHERE id_usu = ?";
        String sqlEstudiante = "UPDATE ESTUDIANTE SET fecha_nacimiento = ?, direccion = ?, telefono = ?, numero_identificacion = ?, estado = ?, promedio_academico = ? WHERE id_estudiante = ?";
        
        try {
            conn = conexion.crearConexion();
            conn.setAutoCommit(false);
            
            // Actualizar tabla USUARIO
            ps = conn.prepareStatement(sqlUsuario);
            ps.setString(1, estudiante.getNombre());
            ps.setString(2, estudiante.getCorreo());
            ps.setInt(3, estudiante.getIdUsuario());
            ps.executeUpdate();
            
            // Actualizar tabla ESTUDIANTE
            ps = conn.prepareStatement(sqlEstudiante);
            ps.setDate(1, estudiante.getFechaNacimiento() != null ? new java.sql.Date(estudiante.getFechaNacimiento().getTime()) : null);
            ps.setString(2, estudiante.getDireccion());
            ps.setString(3, estudiante.getTelefono());
            ps.setString(4, estudiante.getNumeroIdentificacion());
            ps.setString(5, estudiante.getEstado());
            ps.setFloat(6, estudiante.getPromedioAcademico());
            ps.setInt(7, estudiante.getId());
            ps.executeUpdate();
            
            conn.commit();
            return true;
            
        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                System.out.println("Error en rollback - " + ex.getMessage());
            }
            System.out.println("Error al actualizar estudiante - " + e.getMessage());
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
        String sql = "DELETE FROM USUARIO WHERE id_usu = (SELECT idUsuario FROM ESTUDIANTE WHERE id_estudiante = ?)";
        
        try {
            conn = conexion.crearConexion();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            int resultado = ps.executeUpdate();
            return resultado > 0;
            
        } catch (SQLException e) {
            System.out.println("Error al eliminar estudiante - " + e.getMessage());
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