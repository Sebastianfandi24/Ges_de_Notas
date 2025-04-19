package Models;

public class Curso {
    private int id_curso;
    private String nombre;
    private String codigo;
    private String descripcion;
    private int idProfesor;
    private String profesor_nombre; // Campo auxiliar para mostrar el nombre del profesor
    private int cantidadEstudiantes; // Campo auxiliar para mostrar la cantidad de estudiantes
    
    public Curso() {}
    
    public Curso(int id_curso, String nombre, String codigo, String descripcion, int idProfesor) {
        this.id_curso = id_curso;
        this.nombre = nombre;
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.idProfesor = idProfesor;
    }

    public int getId() {
        return id_curso;
    }

    public void setId(int id_curso) {
        this.id_curso = id_curso;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getIdProfesor() {
        return idProfesor;
    }

    public void setIdProfesor(int idProfesor) {
        this.idProfesor = idProfesor;
    }
    
    public String getProfesor_nombre() {
        return profesor_nombre;
    }

    public void setProfesor_nombre(String profesor_nombre) {
        this.profesor_nombre = profesor_nombre;
    }
    
    // Método adicional para compatibilidad con los controladores
    public String getNombreProfesor() {
        return profesor_nombre;
    }

    public void setNombreProfesor(String nombreProfesor) {
        this.profesor_nombre = nombreProfesor;
    }
    
    // Getters y setters para cantidadEstudiantes
    public int getCantidadEstudiantes() {
        return cantidadEstudiantes;
    }
    
    public void setCantidadEstudiantes(int cantidadEstudiantes) {
        this.cantidadEstudiantes = cantidadEstudiantes;
    }
}