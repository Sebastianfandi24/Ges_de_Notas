package Models;

import java.util.Date;

public class Usuario {
    private int idUsu;
    private String nombre;
    private String correo;
    private String contraseña;
    private int idRol;
    private Date fechaCreacion;
    private Date ultimaConexion;
    
    public Usuario() {}
    
    public Usuario(int idUsu, String nombre, String correo, String contraseña, int idRol, Date fechaCreacion, Date ultimaConexion) {
        this.idUsu = idUsu;
        this.nombre = nombre;
        this.correo = correo;
        this.contraseña = contraseña;
        this.idRol = idRol;
        this.fechaCreacion = fechaCreacion;
        this.ultimaConexion = ultimaConexion;
    }

    public int getId() {
        return idUsu;
    }

    public void setId(int idUsu) {
        this.idUsu = idUsu;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getContraseña() {
        return contraseña;
    }

    public void setContraseña(String contraseña) {
        this.contraseña = contraseña;
    }

    public int getIdRol() {
        return idRol;
    }

    public void setIdRol(int idRol) {
        this.idRol = idRol;
    }

    public Date getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Date fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public Date getUltimaConexion() {
        return ultimaConexion;
    }

    public void setUltimaConexion(Date ultimaConexion) {
        this.ultimaConexion = ultimaConexion;
    }
}