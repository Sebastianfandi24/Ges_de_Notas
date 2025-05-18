package Models;

import java.util.Date;

public class Tarea {
    private int idTarea;
    private String titulo;
    private String descripcion;
    private Date fechaAsignacion;
    private Date fechaEntrega;
    private int idCurso;
    private String cursoNombre; // Campo auxiliar para mostrar el nombre del curso
    private Curso curso; // Relación con la clase Curso
    private String estado; // Estado calculado de la tarea
    
    public Tarea() {
        super();
        this.curso = null;
    }
    
    public Tarea(int idTarea, String titulo, String descripcion, Date fechaAsignacion,
            Date fechaEntrega, int idCurso) {
        this.idTarea = idTarea;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.fechaAsignacion = fechaAsignacion;
        this.fechaEntrega = fechaEntrega;
        this.idCurso = idCurso;
    }

    public int getId() {
        return idTarea;
    }

    public void setId(int idTarea) {
        this.idTarea = idTarea;
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

    public Date getFechaAsignacion() {
        return fechaAsignacion;
    }

    public void setFechaAsignacion(Date fechaAsignacion) {
        this.fechaAsignacion = fechaAsignacion;
    }

    public Date getFechaEntrega() {
        return fechaEntrega;
    }

    public void setFechaEntrega(Date fechaEntrega) {
        this.fechaEntrega = fechaEntrega;
    }

    public int getIdCurso() {
        return idCurso;
    }

    public void setIdCurso(int idCurso) {
        this.idCurso = idCurso;
    }
    
    public String getCursoNombre() {
        return cursoNombre;
    }
    
    public void setCursoNombre(String cursoNombre) {
        this.cursoNombre = cursoNombre;
    }

    public Curso getCurso() {
        return curso;
    }

    public void setCurso(Curso curso) {
        this.curso = curso;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    /**
     * Determina el estado de la tarea basado en las fechas
     * @return Estado de la tarea como cadena
     */
    public String getEstado() {
        if (estado != null) {
            return estado;
        }
        
        Date hoy = new Date();
        
        if (fechaEntrega == null) {
            return "Pendiente";
        }
        
        if (hoy.after(fechaEntrega)) {
            return "Vencida";
        } else {
            return "Activa";
        }
    }
}