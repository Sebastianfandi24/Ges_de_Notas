package Models;

import java.util.Date;

public class Profesor extends Usuario {
    private int idProfesor;
    private int idUsu;
    private Date fechaNacimiento;
    private String direccion;
    private String telefono;
    private String gradoAcademico;
    private String especializacion;
    private Date fechaContratacion;
    private String estado;
    private Rol rol;
    
    public Profesor() {
        super();
        this.rol = null;
    }
    
    public Profesor(int idProfesor, Date fechaNacimiento, String direccion, String telefono,
            String gradoAcademico, String especializacion, Date fechaContratacion, String estado,
            int idUsu, String nombre, String correo, String contraseña, int idRol, Date fechaCreacion, Date ultimaConexion, Rol rol) {
        super(idUsu, nombre, correo, contraseña, idRol, fechaCreacion, ultimaConexion);
        this.idProfesor = idProfesor;
        this.fechaNacimiento = fechaNacimiento;
        this.direccion = direccion;
        this.telefono = telefono;
        this.gradoAcademico = gradoAcademico;
        this.especializacion = especializacion;
        this.fechaContratacion = fechaContratacion;
        this.estado = estado;
        this.rol = rol;
    }

    public int getId() {
        return idProfesor;
    }

    public int getId_profesor() {
        return idProfesor;
    }

    public void setId_profesor(int idProfesor) {
        this.idProfesor = idProfesor;
    }

    public int getId_usu() {
        return idUsu;
    }

    public void setId_usu(int idUsu) {
        this.idUsu = idUsu;
    }

    public void setIdUsu(int idUsu) {
        this.idUsu = idUsu;
    }

    public void setId(int idProfesor) {
        this.idProfesor = idProfesor;
    }

    public Date getFechaNacimiento() {
        return fechaNacimiento;
    }

    public Date getFecha_nacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(Date fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public void setFecha_nacimiento(Date fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getGradoAcademico() {
        return gradoAcademico;
    }

    public String getGrado_academico() {
        return gradoAcademico;
    }

    public void setGradoAcademico(String gradoAcademico) {
        this.gradoAcademico = gradoAcademico;
    }

    public void setGrado_academico(String gradoAcademico) {
        this.gradoAcademico = gradoAcademico;
    }

    public String getEspecializacion() {
        return especializacion;
    }

    public void setEspecializacion(String especializacion) {
        this.especializacion = especializacion;
    }

    public Date getFechaContratacion() {
        return fechaContratacion;
    }

    public Date getFecha_contratacion() {
        return fechaContratacion;
    }

    public void setFechaContratacion(Date fechaContratacion) {
        this.fechaContratacion = fechaContratacion;
    }

    public void setFecha_contratacion(Date fechaContratacion) {
        this.fechaContratacion = fechaContratacion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    public int getIdRol() {
        return super.getIdRol();
    }
}