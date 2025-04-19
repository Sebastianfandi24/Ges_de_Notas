package DAOs;

import Interfaces.CRUD;
import Models.Conexion;
import Models.Curso;
import Models.Estudiante;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CursoDAO implements CRUD<Curso> {
    private final Conexion conexion;
    private Connection conn;
    private PreparedStatement ps;
    private ResultSet rs;
    
    public CursoDAO() {
        this.conexion = new Conexion();
    }
    
    @Override
    public boolean create(Curso curso) {
        String sql = "INSERT INTO CURSO (nombre, codigo, descripcion, idProfesor) VALUES (?, ?, ?, ?)";
        
        try {
            conn = conexion.crearConexion();
            ps = conn.prepareStatement(sql);
            ps.setString(1, curso.getNombre());
            ps.setString(2, curso.getCodigo());
            ps.setString(3, curso.getDescripcion());
            ps.setInt(4, curso.getIdProfesor());
            
            int resultado = ps.executeUpdate();
            return resultado > 0;
            
        } catch (SQLException e) {
            System.out.println("Error al crear curso - " + e.getMessage());
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
    public Curso read(int id) {
        String sql = "SELECT c.*, u.nombre as profesor_nombre FROM CURSO c "
                + "LEFT JOIN PROFESOR p ON c.idProfesor = p.id_profesor "
                + "LEFT JOIN USUARIO u ON p.idUsuario = u.id_usu "
                + "WHERE c.id_curso = ?";
        Curso curso = null;
        
        try {
            conn = conexion.crearConexion();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            
            if (rs.next()) {
                curso = new Curso();
                curso.setId(rs.getInt("id_curso"));
                curso.setNombre(rs.getString("nombre"));
                curso.setCodigo(rs.getString("codigo"));
                curso.setDescripcion(rs.getString("descripcion"));
                curso.setIdProfesor(rs.getInt("idProfesor"));
                curso.setProfesor_nombre(rs.getString("profesor_nombre"));
            }
            
        } catch (SQLException e) {
            System.out.println("Error al leer curso - " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.out.println("Error al cerrar conexiones - " + e.getMessage());
            }
        }
        
        return curso;
    }
    
    @Override
    public List<Curso> readAll() {
        String sql = "SELECT c.*, u.nombre as profesor_nombre, " +
                "(SELECT COUNT(*) FROM CURSO_ESTUDIANTE ce WHERE ce.id_curso = c.id_curso) AS cantidad_estudiantes " +
                "FROM CURSO c " +
                "LEFT JOIN PROFESOR p ON c.idProfesor = p.id_profesor " +
                "LEFT JOIN USUARIO u ON p.idUsuario = u.id_usu";
        List<Curso> cursos = new ArrayList<>();
        
        try {
            conn = conexion.crearConexion();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            
            while (rs.next()) {
                Curso curso = new Curso();
                curso.setId(rs.getInt("id_curso"));
                curso.setNombre(rs.getString("nombre"));
                curso.setCodigo(rs.getString("codigo"));
                curso.setDescripcion(rs.getString("descripcion"));
                curso.setIdProfesor(rs.getInt("idProfesor"));
                curso.setProfesor_nombre(rs.getString("profesor_nombre"));
                curso.setCantidadEstudiantes(rs.getInt("cantidad_estudiantes"));
                cursos.add(curso);
            }
            
        } catch (SQLException e) {
            System.out.println("Error al leer todos los cursos - " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.out.println("Error al cerrar conexiones - " + e.getMessage());
            }
        }
        
        return cursos;
    }
    
    @Override
    public boolean update(Curso curso) {
        String sql = "UPDATE CURSO SET nombre = ?, codigo = ?, descripcion = ?, idProfesor = ? WHERE id_curso = ?";
        
        try {
            conn = conexion.crearConexion();
            ps = conn.prepareStatement(sql);
            ps.setString(1, curso.getNombre());
            ps.setString(2, curso.getCodigo());
            ps.setString(3, curso.getDescripcion());
            ps.setInt(4, curso.getIdProfesor());
            ps.setInt(5, curso.getId());
            
            int resultado = ps.executeUpdate();
            return resultado > 0;
            
        } catch (SQLException e) {
            System.out.println("Error al actualizar curso - " + e.getMessage());
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
        String sql = "DELETE FROM CURSO WHERE id_curso = ?";
        
        try {
            conn = conexion.crearConexion();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            
            int resultado = ps.executeUpdate();
            return resultado > 0;
            
        } catch (SQLException e) {
            System.out.println("Error al eliminar curso - " + e.getMessage());
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
    
    public boolean asignarEstudiantes(int idCurso, List<Integer> idEstudiantes) {
        String sql = "INSERT INTO CURSO_ESTUDIANTE (id_curso, id_estudiante) VALUES (?, ?)";
        
        try {
            conn = conexion.crearConexion();
            conn.setAutoCommit(false);
            ps = conn.prepareStatement(sql);
            
            for (Integer idEstudiante : idEstudiantes) {
                ps.setInt(1, idCurso);
                ps.setInt(2, idEstudiante);
                ps.addBatch();
            }
            
            int[] resultados = ps.executeBatch();
            conn.commit();
            
            // Verificar si todas las inserciones fueron exitosas
            for (int resultado : resultados) {
                if (resultado <= 0) return false;
            }
            
            return true;
            
        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                System.out.println("Error en rollback - " + ex.getMessage());
            }
            System.out.println("Error al asignar estudiantes al curso - " + e.getMessage());
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
    
    public boolean removerEstudiante(int idCurso, int idEstudiante) {
        String sql = "DELETE FROM CURSO_ESTUDIANTE WHERE id_curso = ? AND id_estudiante = ?";
        
        try {
            conn = conexion.crearConexion();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, idCurso);
            ps.setInt(2, idEstudiante);
            
            int resultado = ps.executeUpdate();
            return resultado > 0;
            
        } catch (SQLException e) {
            System.out.println("Error al remover estudiante del curso - " + e.getMessage());
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
    
    public List<Curso> getCursosPorProfesor(int idProfesor) {
        String sql = "SELECT c.*, u.nombre as profesor_nombre, " +
                "(SELECT COUNT(*) FROM CURSO_ESTUDIANTE ce WHERE ce.id_curso = c.id_curso) AS cantidad_estudiantes " +
                "FROM CURSO c " +
                "LEFT JOIN PROFESOR p ON c.idProfesor = p.id_profesor " +
                "LEFT JOIN USUARIO u ON p.idUsuario = u.id_usu " +
                "WHERE c.idProfesor = ?";
        List<Curso> cursos = new ArrayList<>();
        
        try {
            conn = conexion.crearConexion();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, idProfesor);
            rs = ps.executeQuery();
            
            while (rs.next()) {
                Curso curso = new Curso();
                curso.setId(rs.getInt("id_curso"));
                curso.setNombre(rs.getString("nombre"));
                curso.setCodigo(rs.getString("codigo"));
                curso.setDescripcion(rs.getString("descripcion"));
                curso.setIdProfesor(rs.getInt("idProfesor"));
                curso.setProfesor_nombre(rs.getString("profesor_nombre"));
                curso.setCantidadEstudiantes(rs.getInt("cantidad_estudiantes"));
                cursos.add(curso);
            }
            
        } catch (SQLException e) {
            System.out.println("Error al obtener cursos del profesor - " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.out.println("Error al cerrar conexiones - " + e.getMessage());
            }
        }
        
        return cursos;
    }
    
    public List<Curso> getCursosPorEstudiante(int idEstudiante) {
        String sql = "SELECT c.*, u.nombre as profesor_nombre FROM CURSO c "
                + "INNER JOIN CURSO_ESTUDIANTE ce ON c.id_curso = ce.id_curso "
                + "LEFT JOIN PROFESOR p ON c.idProfesor = p.id_profesor "
                + "LEFT JOIN USUARIO u ON p.idUsuario = u.id_usu "
                + "WHERE ce.id_estudiante = ?";
        List<Curso> cursos = new ArrayList<>();
        
        try {
            conn = conexion.crearConexion();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, idEstudiante);
            rs = ps.executeQuery();
            
            while (rs.next()) {
                Curso curso = new Curso();
                curso.setId(rs.getInt("id_curso"));
                curso.setNombre(rs.getString("nombre"));
                curso.setCodigo(rs.getString("codigo"));
                curso.setDescripcion(rs.getString("descripcion"));
                curso.setIdProfesor(rs.getInt("idProfesor"));
                curso.setProfesor_nombre(rs.getString("profesor_nombre"));
                cursos.add(curso);
            }
            
        } catch (SQLException e) {
            System.out.println("Error al obtener cursos del estudiante - " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.out.println("Error al cerrar conexiones - " + e.getMessage());
            }
        }
        
        return cursos;
    }
    
    public List<Estudiante> getEstudiantesPorCurso(int idCurso) {
        String sql = "SELECT e.*, u.nombre, u.correo FROM ESTUDIANTE e "
                + "INNER JOIN CURSO_ESTUDIANTE ce ON e.id_estudiante = ce.id_estudiante "
                + "INNER JOIN USUARIO u ON e.idUsuario = u.id_usu "
                + "WHERE ce.id_curso = ?";
        List<Estudiante> estudiantes = new ArrayList<>();
        
        try {
            conn = conexion.crearConexion();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, idCurso);
            rs = ps.executeQuery();
            
            while (rs.next()) {
                Estudiante estudiante = new Estudiante();
                estudiante.setId(rs.getInt("id_estudiante"));
                estudiante.setNombre(rs.getString("nombre"));
                estudiante.setCorreo(rs.getString("correo"));
                estudiante.setTelefono(rs.getString("telefono"));
                estudiante.setNumeroIdentificacion(rs.getString("numero_identificacion"));
                estudiante.setEstado(rs.getString("estado"));
                estudiantes.add(estudiante);
            }
            
        } catch (SQLException e) {
            System.out.println("Error al obtener estudiantes del curso - " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.out.println("Error al cerrar conexiones - " + e.getMessage());
            }
        }
        
        return estudiantes;
    }
}