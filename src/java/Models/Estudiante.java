package Models;

import java.util.Date;

public class Estudiante extends Usuario {
    private int idEstudiante;
    private Date fechaNacimiento;
    private String direccion;
    private String telefono;
    private String numeroIdentificacion;
    private String estado;
    private float promedioAcademico;
    
    public Estudiante() {
        super();
    }
    
    public Estudiante(int idEstudiante, Date fechaNacimiento, String direccion, String telefono,
            String numeroIdentificacion, String estado, float promedioAcademico,
            int idUsu, String nombre, String correo, String contraseña, int idRol, Date fechaCreacion, Date ultimaConexion) {
        super(idUsu, nombre, correo, contraseña, idRol, fechaCreacion, ultimaConexion);
        this.idEstudiante = idEstudiante;
        this.fechaNacimiento = fechaNacimiento;
        this.direccion = direccion;
        this.telefono = telefono;
        this.numeroIdentificacion = numeroIdentificacion;
        this.estado = estado;
        this.promedioAcademico = promedioAcademico;
    }

    public int getId() {
        return idEstudiante;
    }

    public void setId(int idEstudiante) {
        this.idEstudiante = idEstudiante;
    }

    public int getIdUsuario() {
        return super.getId();
    }

    public void setIdUsuario(int idUsuario) {
        super.setId(idUsuario);
    }

    public Date getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(Date fechaNacimiento) {
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

    public String getNumeroIdentificacion() {
        return numeroIdentificacion;
    }

    public void setNumeroIdentificacion(String numeroIdentificacion) {
        this.numeroIdentificacion = numeroIdentificacion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public float getPromedioAcademico() {
        return promedioAcademico;
    }

    public void setPromedioAcademico(float promedioAcademico) {
        this.promedioAcademico = promedioAcademico;
    }

    public String getNombre() {
        return super.getNombre();
    }

    public void setNombre(String nombre) {
        super.setNombre(nombre);
    }

    public String getCorreo() {
        return super.getCorreo();
    }

    public void setCorreo(String correo) {
        super.setCorreo(correo);
    }

    public String getContraseña() {
        return super.getContraseña();
    }

    public void setContraseña(String contraseña) {
        super.setContraseña(contraseña);
    }

    public Date getFechaCreacion() {
        return super.getFechaCreacion();
    }

    public void setFechaCreacion(Date fechaCreacion) {
        super.setFechaCreacion(fechaCreacion);
    }

    public Date getUltimaConexion() {
        return super.getUltimaConexion();
    }

    public void setUltimaConexion(Date ultimaConexion) {
        super.setUltimaConexion(ultimaConexion);
    }
}