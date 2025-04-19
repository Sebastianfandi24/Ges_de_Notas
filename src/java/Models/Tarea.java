package Models;

import java.util.Date;

public class Tarea {
    private int id_tarea;
    private String titulo;
    private String descripcion;
    private Date fecha_asignacion;
    private Date fecha_entrega;
    private int id_curso;
    private String curso_nombre; // Campo auxiliar para mostrar el nombre del curso
    private Curso curso; // Relación con la clase Curso
    
    public Tarea() {
        super();
        this.curso = null;
    }
    
    public Tarea(int id_tarea, String titulo, String descripcion, Date fecha_asignacion,
            Date fecha_entrega, int id_curso) {
        this.id_tarea = id_tarea;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.fecha_asignacion = fecha_asignacion;
        this.fecha_entrega = fecha_entrega;
        this.id_curso = id_curso;
    }

    public int getId() {
        return id_tarea;
    }

    public void setId(int id_tarea) {
        this.id_tarea = id_tarea;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Date getFecha_asignacion() {
        return fecha_asignacion;
    }

    public void setFecha_asignacion(Date fecha_asignacion) {
        this.fecha_asignacion = fecha_asignacion;
    }

    public Date getFecha_entrega() {
        return fecha_entrega;
    }

    public void setFecha_entrega(Date fecha_entrega) {
        this.fecha_entrega = fecha_entrega;
    }

    public int getIdCurso() {
        return id_curso;
    }

    public void setId_curso(int id_curso) {
        this.id_curso = id_curso;
    }
    public void setCurso_nombre(String curso_nombre) {
        this.curso_nombre = curso_nombre;
    }
    
    public String getCurso_nombre() {
        return curso_nombre;
    }

    public Curso getCurso() {
        return curso;
    }
}