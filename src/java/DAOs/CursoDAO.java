package DAOs;

import Interfaces.CRUD;
import Models.Conexion;
import Models.Curso;
import Models.Estudiante;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javax.naming.InitialContext;
import javax.sql.DataSource;

public class CursoDAO implements CRUD<Curso> {
    private DataSource dataSource;
    private static final Logger logger = Logger.getLogger(CursoDAO.class.getName());
    private Connection conn;
    private PreparedStatement ps;
    private ResultSet rs;

    public CursoDAO() throws Exception {
        try {
            InitialContext ctx = new InitialContext();
            dataSource = (DataSource) ctx.lookup("java:comp/env/jdbc/GesDeNotasDB");
            logger.info("DataSource inicializado correctamente");
        } catch (Exception e) {
            logger.severe("Error al inicializar DataSource: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public boolean create(Curso curso) {
        String sql = "INSERT INTO CURSO (nombre, codigo, descripcion, idProfesor) VALUES (?, ?, ?, ?)";
        logger.info("Ejecutando query: " + sql);

        try {
            conn = dataSource.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, curso.getNombre());
            ps.setString(2, curso.getCodigo());
            ps.setString(3, curso.getDescripcion());
            ps.setInt(4, curso.getIdProfesor());

            int resultado = ps.executeUpdate();
            logger.info("Curso creado exitosamente: " + curso.getNombre());
            return resultado > 0;

        } catch (SQLException e) {
            logger.severe("Error al crear curso: " + e.getMessage());
            return false;
        } finally {
            try {
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                logger.severe("Error al cerrar conexiones: " + e.getMessage());
            }
        }
    }

    @Override
    public Curso read(int id) {
        String sql = "SELECT c.*, u.nombre as profesor_nombre FROM CURSO c "
                + "LEFT JOIN PROFESOR p ON c.idProfesor = p.id_profesor "
                + "LEFT JOIN USUARIO u ON p.idUsuario = u.id_usu "
                + "WHERE c.id_curso = ?";
        logger.info("Ejecutando query: " + sql + " con id_curso = " + id);
        Curso curso = null;

        try {
            conn = dataSource.getConnection();
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
                logger.info("Curso encontrado: ID=" + curso.getId() + ", Nombre=" + curso.getNombre());
            }

        } catch (SQLException e) {
            logger.severe("Error al leer curso: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                logger.severe("Error al cerrar conexiones: " + e.getMessage());
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
        logger.info("Ejecutando query: " + sql);
        List<Curso> cursos = new ArrayList<>();

        try {
            conn = dataSource.getConnection();
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
                logger.info("Curso encontrado: ID=" + curso.getId() + ", Nombre=" + curso.getNombre());
            }

        } catch (SQLException e) {
            logger.severe("Error al leer todos los cursos: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                logger.severe("Error al cerrar conexiones: " + e.getMessage());
            }
        }

        return cursos;
    }

    @Override
    public boolean update(Curso curso) {
        String sql = "UPDATE CURSO SET nombre = ?, codigo = ?, descripcion = ?, idProfesor = ? WHERE id_curso = ?";
        logger.info("Ejecutando query: " + sql + " para actualizar curso ID=" + curso.getId());

        try {
            conn = dataSource.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, curso.getNombre());
            ps.setString(2, curso.getCodigo());
            ps.setString(3, curso.getDescripcion());
            ps.setInt(4, curso.getIdProfesor());
            ps.setInt(5, curso.getId());

            int resultado = ps.executeUpdate();
            logger.info("Curso actualizado exitosamente: ID=" + curso.getId());
            return resultado > 0;

        } catch (SQLException e) {
            logger.severe("Error al actualizar curso: " + e.getMessage());
            return false;
        } finally {
            try {
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                logger.severe("Error al cerrar conexiones: " + e.getMessage());
            }
        }
    }

    @Override
    public boolean delete(int id) {
        String sqlNota = "DELETE FROM nota_tarea WHERE id_tarea IN (SELECT id_tarea FROM TAREA WHERE id_curso = ?)";
        String sqlTarea = "DELETE FROM TAREA WHERE id_curso = ?";
        String sqlRel = "DELETE FROM CURSO_ESTUDIANTE WHERE id_curso = ?";
        String sqlCurso = "DELETE FROM CURSO WHERE id_curso = ?";
        logger.info("Ejecutando eliminación de curso ID=" + id);

        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);

            ps = conn.prepareStatement(sqlNota);
            ps.setInt(1, id);
            ps.executeUpdate();
            ps.close();

            ps = conn.prepareStatement(sqlTarea);
            ps.setInt(1, id);
            ps.executeUpdate();
            ps.close();

            ps = conn.prepareStatement(sqlRel);
            ps.setInt(1, id);
            ps.executeUpdate();
            ps.close();

            ps = conn.prepareStatement(sqlCurso);
            ps.setInt(1, id);
            int resultado = ps.executeUpdate();
            conn.commit();
            logger.info("Curso eliminado exitosamente: ID=" + id);
            return resultado > 0;
        } catch (SQLException e) {
            logger.severe("Error al eliminar curso: " + e.getMessage());
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                logger.severe("Error en rollback: " + ex.getMessage());
            }
            return false;
        } finally {
            try {
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                logger.severe("Error al cerrar conexiones: " + e.getMessage());
            }
        }
    }

    public boolean asignarEstudiantes(int idCurso, List<Integer> idEstudiantes) {
        String sql = "INSERT INTO CURSO_ESTUDIANTE (id_curso, id_estudiante) VALUES (?, ?)";
        logger.info("Asignando estudiantes al curso ID=" + idCurso);

        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);
            ps = conn.prepareStatement(sql);

            for (Integer idEstudiante : idEstudiantes) {
                ps.setInt(1, idCurso);
                ps.setInt(2, idEstudiante);
                ps.addBatch();
            }

            int[] resultados = ps.executeBatch();
            conn.commit();

            for (int resultado : resultados) {
                if (resultado <= 0) return false;
            }

            logger.info("Estudiantes asignados exitosamente al curso ID=" + idCurso);
            return true;

        } catch (SQLException e) {
            logger.severe("Error al asignar estudiantes al curso: " + e.getMessage());
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                logger.severe("Error en rollback: " + ex.getMessage());
            }
            return false;
        } finally {
            try {
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                logger.severe("Error al cerrar conexiones: " + e.getMessage());
            }
        }
    }

    public boolean removerEstudiante(int idCurso, int idEstudiante) {
        String sql = "DELETE FROM CURSO_ESTUDIANTE WHERE id_curso = ? AND id_estudiante = ?";
        logger.info("Removiendo estudiante ID=" + idEstudiante + " del curso ID=" + idCurso);

        try {
            conn = dataSource.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, idCurso);
            ps.setInt(2, idEstudiante);

            int resultado = ps.executeUpdate();
            logger.info("Estudiante removido exitosamente del curso ID=" + idCurso);
            return resultado > 0;

        } catch (SQLException e) {
            logger.severe("Error al remover estudiante del curso: " + e.getMessage());
            return false;
        } finally {
            try {
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                logger.severe("Error al cerrar conexiones: " + e.getMessage());
            }
        }
    }

    public List<Curso> getCursosPorProfesor(int idProfesor) {
        List<Curso> cursos = new ArrayList<>();
        String sql = "SELECT c.id_curso, c.nombre, c.codigo, c.descripcion, c.idProfesor, " +
                "COALESCE((SELECT COUNT(*) FROM CURSO_ESTUDIANTE ce WHERE ce.id_curso = c.id_curso), 0) as estudiantes " +
                "FROM CURSO c WHERE c.idProfesor = ?";

        logger.info("========== INICIO CONSULTA DE CURSOS POR PROFESOR ==========");
        logger.info("Profesor ID: " + idProfesor);
        logger.info("SQL: " + sql);

        try {
            // Obtener conexión directamente, sin usar try-with-resources
            logger.info("Obteniendo conexión a la base de datos...");
            Connection conn = dataSource.getConnection();
            logger.info("Conexión obtenida correctamente");
            
            logger.info("Preparando statement...");
            PreparedStatement stmt = conn.prepareStatement(sql);
            logger.info("Statement preparado correctamente");
            
            logger.info("Estableciendo parámetro idProfesor = " + idProfesor);
            stmt.setInt(1, idProfesor);
            
            logger.info("Ejecutando query...");
            ResultSet rs = stmt.executeQuery();
            logger.info("Query ejecutada correctamente");
            
            int count = 0;
            logger.info("Iterando sobre resultados...");
            
            while (rs.next()) {
                count++;
                int id_curso = rs.getInt("id_curso");
                String nombre = rs.getString("nombre");
                String codigo = rs.getString("codigo");
                String descripcion = rs.getString("descripcion");
                int idProf = rs.getInt("idProfesor");
                
                logger.info("Curso encontrado: ID=" + id_curso + 
                          ", Nombre=" + nombre + 
                          ", Código=" + codigo + 
                          ", idProfesor=" + idProf);
                
                Curso curso = new Curso();
                curso.setId(id_curso);
                curso.setNombre(nombre);
                curso.setCodigo(codigo);
                curso.setDescripcion(descripcion);
                curso.setIdProfesor(idProf);
                
                cursos.add(curso);
                logger.info("Curso agregado a la lista");
            }
            
            logger.info("Total de cursos encontrados: " + count);
            
            // Cerrar recursos explícitamente
            logger.info("Cerrando recursos...");
            rs.close();
            stmt.close();
            conn.close();
            logger.info("Recursos cerrados correctamente");

        } catch (SQLException e) {
            logger.severe("ERROR SQL al obtener cursos del profesor " + idProfesor + ": " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            logger.severe("ERROR GENERAL al obtener cursos del profesor " + idProfesor + ": " + e.getMessage());
            e.printStackTrace();
        }
        
        logger.info("Retornando " + cursos.size() + " cursos");
        logger.info("========== FIN CONSULTA DE CURSOS POR PROFESOR ==========");
        return cursos;
    }

    public List<Curso> getCursosPorEstudiante(int idEstudiante) {
        String sql = "SELECT c.*, u.nombre as profesor_nombre FROM CURSO c "
                + "INNER JOIN CURSO_ESTUDIANTE ce ON c.id_curso = ce.id_curso "
                + "LEFT JOIN PROFESOR p ON c.idProfesor = p.id_profesor "
                + "LEFT JOIN USUARIO u ON p.idUsuario = u.id_usu "
                + "WHERE ce.id_estudiante = ?";
        logger.info("Ejecutando query: " + sql + " con idEstudiante = " + idEstudiante);
        List<Curso> cursos = new ArrayList<>();

        try {
            conn = dataSource.getConnection();
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
                logger.info("Curso encontrado: ID=" + curso.getId() + ", Nombre=" + curso.getNombre());
            }

        } catch (SQLException e) {
            logger.severe("Error al obtener cursos del estudiante: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                logger.severe("Error al cerrar conexiones: " + e.getMessage());
            }
        }

        return cursos;
    }

    public List<Estudiante> getEstudiantesPorCurso(int idCurso) {
        String sql = "SELECT e.*, u.nombre, u.correo FROM ESTUDIANTE e "
                + "INNER JOIN CURSO_ESTUDIANTE ce ON e.id_estudiante = ce.id_estudiante "
                + "INNER JOIN USUARIO u ON e.idUsuario = u.id_usu "
                + "WHERE ce.id_curso = ?";
        logger.info("Ejecutando query: " + sql + " con idCurso = " + idCurso);
        List<Estudiante> estudiantes = new ArrayList<>();

        try {
            conn = dataSource.getConnection();
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
                logger.info("Estudiante encontrado: ID=" + estudiante.getId() + ", Nombre=" + estudiante.getNombre());
            }

        } catch (SQLException e) {
            logger.severe("Error al obtener estudiantes del curso: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                logger.severe("Error al cerrar conexiones: " + e.getMessage());
            }
        }

        return estudiantes;
    }

    /**
     * Retorna el número de estudiantes inscritos en un curso.
     */
    public int getStudentCount(int idCurso) {
        String sql = "SELECT COUNT(*) FROM curso_estudiante WHERE id_curso = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idCurso);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            logger.severe("Error al contar estudiantes del curso " + idCurso + ": " + e.getMessage());
        }
        return 0;
    }

    /**
     * Calcula el promedio del curso a partir del promedio académico de los estudiantes y lo ajusta a escala /10.
     */
    public double getCourseAverage(int idCurso) {
        String sql = "SELECT AVG(e.promedio_academico) FROM estudiante e " +
                     "JOIN curso_estudiante ce ON e.id_estudiante = ce.id_estudiante " +
                     "WHERE ce.id_curso = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idCurso);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    double avg = rs.getDouble(1);
                    // Ajuste a escala 0-10
                    return avg / 10.0;
                }
            }
        } catch (SQLException e) {
            logger.severe("Error al calcular promedio del curso " + idCurso + ": " + e.getMessage());
        }
        return 0.0;
    }

    /**
     * Elimina todas las asignaciones de estudiantes para un curso.
     */
    public boolean clearAssignments(int idCurso) {
        String sql = "DELETE FROM curso_estudiante WHERE id_curso = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idCurso);
            int count = ps.executeUpdate();
            logger.info("Se eliminaron " + count + " asignaciones para curso ID=" + idCurso);
            return true;
        } catch (SQLException e) {
            logger.severe("Error al limpiar asignaciones del curso " + idCurso + ": " + e.getMessage());
            return false;
        }
    }

    // Método de prueba para diagnosticar problemas de conexión
    public void testConexion() {
        logger.info("====== INICIANDO PRUEBA DE CONEXIÓN ======");
        
        try {
            // Verificar que el dataSource no sea nulo
            if (dataSource == null) {
                logger.severe("ERROR: dataSource es NULL");
                return;
            }
            
            logger.info("DataSource obtenido correctamente");
            
            // Intentar obtener una conexión
            try (Connection conn = dataSource.getConnection()) {
                logger.info("Conexión establecida correctamente");
                
                // Verificar si hay cursos para el profesor con ID = 1
                String sql = "SELECT * FROM CURSO WHERE idProfesor = 1";
                try (PreparedStatement ps = conn.prepareStatement(sql);
                     ResultSet rs = ps.executeQuery()) {
                    
                    logger.info("Query ejecutada correctamente");
                    int count = 0;
                    StringBuilder result = new StringBuilder();
                    
                    while (rs.next()) {
                        count++;
                        result.append("Curso ID: ").append(rs.getInt("id_curso"))
                              .append(", Nombre: ").append(rs.getString("nombre"))
                              .append(", Código: ").append(rs.getString("codigo"))
                              .append("\n");
                    }
                    
                    if (count > 0) {
                        logger.info("Se encontraron " + count + " cursos para el profesor ID 1:");
                        logger.info(result.toString());
                    } else {
                        logger.warning("No se encontraron cursos para el profesor ID 1");
                        
                        // Verificar si hay cursos en general
                        sql = "SELECT * FROM CURSO";
                        try (PreparedStatement ps2 = conn.prepareStatement(sql);
                             ResultSet rs2 = ps2.executeQuery()) {
                            
                            count = 0;
                            result = new StringBuilder();
                            
                            while (rs2.next()) {
                                count++;
                                result.append("Curso ID: ").append(rs2.getInt("id_curso"))
                                      .append(", Nombre: ").append(rs2.getString("nombre"))
                                      .append(", idProfesor: ").append(rs2.getInt("idProfesor"))
                                      .append("\n");
                            }
                            
                            if (count > 0) {
                                logger.info("Se encontraron " + count + " cursos en total:");
                                logger.info(result.toString());
                            } else {
                                logger.warning("No hay cursos en la base de datos");
                                
                                // Verificar si la tabla CURSO existe
                                DatabaseMetaData metaData = conn.getMetaData();
                                try (ResultSet rs3 = metaData.getTables(null, null, "CURSO", null)) {
                                    if (rs3.next()) {
                                        logger.info("La tabla CURSO existe");
                                    } else {
                                        logger.severe("La tabla CURSO no existe");
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (SQLException e) {
            logger.severe("Error SQL en prueba de conexión: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            logger.severe("Error general en prueba de conexión: " + e.getMessage());
            e.printStackTrace();
        }
        
        logger.info("====== PRUEBA DE CONEXIÓN FINALIZADA ======");
    }
}