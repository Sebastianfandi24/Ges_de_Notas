package Controllers;

import DAOs.CursoDAO;
import Models.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

@WebServlet("/profesor/cursos")
public class ProfesorCursosServlet extends HttpServlet {
    private CursoDAO cursoDAO;
    private static final Logger logger = Logger.getLogger(ProfesorCursosServlet.class.getName());
    
    @Override
    public void init() throws ServletException {
        super.init();
        try {
            // Inicializamos el DAO
            cursoDAO = new CursoDAO();
            logger.info("CursoDAO inicializado correctamente");
            
            // También verificamos la conexión directamente
            verificarConexionDirecta();
        } catch (Exception e) {
            logger.severe("Error al inicializar CursoDAO: " + e.getMessage());
            throw new ServletException(e);
        }
    }
    
    /**
     * Método para verificar la conexión directamente sin pasar por el DAO
     */
    private void verificarConexionDirecta() {
        logger.info("===== VERIFICANDO CONEXIÓN DIRECTA =====");
        
        try {
            // Obtener el contexto
            Context initContext = new InitialContext();
            Context envContext = (Context) initContext.lookup("java:comp/env");
            
            // Verificar si el recurso JNDI existe
            logger.info("Intentando buscar el recurso JNDI: jdbc/GesDeNotasDB");
            try {
                Object obj = envContext.lookup("jdbc/GesDeNotasDB");
                if (obj != null) {
                    logger.info("Recurso JNDI 'jdbc/GesDeNotasDB' encontrado");
                } else {
                    logger.severe("Recurso JNDI 'jdbc/GesDeNotasDB' es nulo");
                }
            } catch (Exception e) {
                logger.severe("Error al buscar el recurso JNDI: " + e.getMessage());
            }
            
            // Obtener el dataSource
            DataSource dataSource = (DataSource) envContext.lookup("jdbc/GesDeNotasDB");
            logger.info("DataSource obtenido correctamente");
            
            // Obtener conexión
            try (Connection conn = dataSource.getConnection()) {
                logger.info("Conexión establecida correctamente");
                
                // Verificar si hay cursos para el profesor con ID = 1
                String sql = "SELECT * FROM CURSO WHERE idProfesor = 1";
                try (PreparedStatement ps = conn.prepareStatement(sql);
                     ResultSet rs = ps.executeQuery()) {
                    
                    int count = 0;
                    while (rs.next()) {
                        count++;
                        int id_curso = rs.getInt("id_curso");
                        String nombre = rs.getString("nombre");
                        String codigo = rs.getString("codigo");
                        
                        logger.info("Curso encontrado directamente: ID=" + id_curso + 
                                  ", Nombre=" + nombre + ", Código=" + codigo);
                    }
                    
                    logger.info("Total de cursos encontrados directamente: " + count);
                    
                    if (count == 0) {
                        // Si no encontramos cursos, verificamos si la tabla tiene datos
                        try (PreparedStatement ps2 = conn.prepareStatement("SELECT COUNT(*) FROM CURSO");
                             ResultSet rs2 = ps2.executeQuery()) {
                            
                            if (rs2.next()) {
                                int totalCursos = rs2.getInt(1);
                                logger.info("Total de cursos en la base de datos: " + totalCursos);
                            }
                        }
                        
                        // Y verificamos si existe el profesor con ID 1
                        try (PreparedStatement ps3 = conn.prepareStatement("SELECT COUNT(*) FROM PROFESOR WHERE id_profesor = 1");
                             ResultSet rs3 = ps3.executeQuery()) {
                            
                            if (rs3.next()) {
                                int existeProfesor = rs3.getInt(1);
                                logger.info("¿Existe el profesor con ID=1? " + (existeProfesor > 0 ? "SÍ" : "NO"));
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.severe("Error al verificar conexión directa: " + e.getMessage());
            e.printStackTrace();
        }
        
        logger.info("===== FIN VERIFICACIÓN CONEXIÓN DIRECTA =====");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            logger.info("Iniciando doGet en ProfesorCursosServlet");
            
            // TODO: Obtener el ID del profesor de la sesión
            int idProfesor = 1; // Por ahora hardcodeado para pruebas
            logger.info("Buscando cursos para profesor ID: " + idProfesor);
            
            // Probar recuperando datos de dos formas diferentes
            probarRecuperacionDirecta(idProfesor, request);
            
            // Usar el DAO para obtener los cursos
            List<Curso> cursos = cursoDAO.getCursosPorProfesor(idProfesor);
            logger.info("Cursos recuperados desde DAO: " + (cursos != null ? cursos.size() : "null"));
            
            if (cursos != null && !cursos.isEmpty()) {
                List<CursoInfo> cursosInfo = new ArrayList<>();
                for (Curso curso : cursos) {
                    CursoInfo info = new CursoInfo(curso);
                    cursosInfo.add(info);
                    logger.info("Agregado curso a cursosInfo: ID=" + curso.getId() + ", Nombre=" + curso.getNombre());
                }
                request.setAttribute("cursosInfo", cursosInfo);
                logger.info("cursosInfo agregado al request con " + cursosInfo.size() + " cursos");
            } else {
                logger.warning("No se encontraron cursos para el profesor ID: " + idProfesor);
            }
            
            request.getRequestDispatcher("/profesorcursos.jsp").forward(request, response);
            logger.info("Vista enviada al cliente");
            
        } catch (Exception e) {
            logger.severe("Error en ProfesorCursosServlet: " + e.getMessage());
            e.printStackTrace();
            throw new ServletException(e);
        }
    }
    
    /**
     * Método para probar la recuperación directa de cursos sin usar el DAO
     */
    private void probarRecuperacionDirecta(int idProfesor, HttpServletRequest request) {
        logger.info("===== PROBANDO RECUPERACIÓN DIRECTA =====");
        
        try {
            Context initContext = new InitialContext();
            Context envContext = (Context) initContext.lookup("java:comp/env");
            DataSource dataSource = (DataSource) envContext.lookup("jdbc/GesDeNotasDB");
            
            List<Curso> cursosDirectos = new ArrayList<>();
            String sql = "SELECT * FROM CURSO WHERE idProfesor = ?";
            
            try (Connection conn = dataSource.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                
                ps.setInt(1, idProfesor);
                ResultSet rs = ps.executeQuery();
                
                while (rs.next()) {
                    Curso curso = new Curso();
                    curso.setId(rs.getInt("id_curso"));
                    curso.setNombre(rs.getString("nombre"));
                    curso.setCodigo(rs.getString("codigo"));
                    curso.setDescripcion(rs.getString("descripcion"));
                    curso.setIdProfesor(rs.getInt("idProfesor"));
                    
                    cursosDirectos.add(curso);
                    logger.info("Curso recuperado directamente: ID=" + curso.getId() + 
                              ", Nombre=" + curso.getNombre());
                }
            }
            
            logger.info("Total cursos recuperados directamente: " + cursosDirectos.size());
            
            if (cursosDirectos != null && !cursosDirectos.isEmpty()) {
                List<CursoInfo> cursosInfoDirectos = new ArrayList<>();
                for (Curso curso : cursosDirectos) {
                    CursoInfo info = new CursoInfo(curso);
                    cursosInfoDirectos.add(info);
                }
                request.setAttribute("cursosInfoDirectos", cursosInfoDirectos);
                logger.info("cursosInfoDirectos agregado al request con " + cursosInfoDirectos.size() + " cursos");
            }
        } catch (Exception e) {
            logger.severe("Error en recuperación directa JNDI: " + e.getMessage());
            e.printStackTrace();
            // Fallback usando DriverManager
            try {
                logger.info("Intentando recuperación directa por JDBC fallback...");
                Class.forName("com.mysql.cj.jdbc.Driver");
                try (Connection conn = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/sistema_academico", "root", "")) {
                    List<Curso> cursosFallback = new ArrayList<>();
                    String sql = "SELECT * FROM curso WHERE idProfesor = ?";
                    try (PreparedStatement ps2 = conn.prepareStatement(sql)) {
                        ps2.setInt(1, idProfesor);
                        ResultSet rs2 = ps2.executeQuery();
                        while (rs2.next()) {
                            Curso c = new Curso();
                            c.setId(rs2.getInt("id_curso"));
                            c.setNombre(rs2.getString("nombre"));
                            c.setCodigo(rs2.getString("codigo"));
                            c.setDescripcion(rs2.getString("descripcion"));
                            c.setIdProfesor(rs2.getInt("idProfesor"));
                            cursosFallback.add(c);
                        }
                    }
                    logger.info("Total cursos recuperados fallback JDBC: " + cursosFallback.size());
                    if (!cursosFallback.isEmpty()) {
                        List<CursoInfo> infoFallback = new ArrayList<>();
                        for (Curso cf : cursosFallback) infoFallback.add(new CursoInfo(cf));
                        request.setAttribute("cursosInfoDirectos", infoFallback);
                        logger.info("cursosInfoDirectos (fallback) agregado al request con " + infoFallback.size() + " cursos");
                    }
                }
            } catch (Exception ex2) {
                logger.severe("Fallback JDBC failed: " + ex2.getMessage());
                ex2.printStackTrace();
            }
        }
        
        logger.info("===== FIN PROBANDO RECUPERACIÓN DIRECTA =====");
    }
}
