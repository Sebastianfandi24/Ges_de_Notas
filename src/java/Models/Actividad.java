package Models;

import java.util.ArrayList;
import java.util.List;

/**
 * Modelo que representa una actividad en el sistema según la tabla ACTIVIDAD y GESTION_ACTIVIDADES
 */
public class Actividad {
    private int id_actividad;
    private String nombre;
    private String enlace;
    private List<Rol> roles;
    
    public Actividad() {
        this.roles = new ArrayList<>();
    }
    
    public Actividad(int id_actividad, String nombre, String enlace, List<Rol> roles) {
        this.id_actividad = id_actividad;
        this.nombre = nombre;
        this.enlace = enlace;
        this.roles = roles != null ? roles : new ArrayList<>();
    }

    public int getId() {
        return id_actividad;
    }

    public void setId(int id_actividad) {
        this.id_actividad = id_actividad;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEnlace() {
        return enlace;
    }

    public void setEnlace(String enlace) {
        this.enlace = enlace;
    }

    public List<Rol> getRoles() {
        return roles;
    }

    public void setRoles(List<Rol> roles) {
        this.roles = roles != null ? roles : new ArrayList<>();
    }
    
    public void addRol(Rol rol) {
        if (rol != null && !this.roles.contains(rol)) {
            this.roles.add(rol);
        }
    }
    
    public void removeRol(Rol rol) {
        if (rol != null) {
            this.roles.remove(rol);
        }
    }
}