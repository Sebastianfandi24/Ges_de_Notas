package Models;

import java.util.ArrayList;
import java.util.List;

/**
 * Modelo que representa un rol en el sistema según la tabla ROL
 */
public class Rol {
    private int id_rol;
    private String nombre;
    private List<Actividad> actividades;
    
    public Rol() {
        this.actividades = new ArrayList<>();
    }
    
    public Rol(int id_rol, String nombre) {
        this.id_rol = id_rol;
        this.nombre = nombre;
        this.actividades = new ArrayList<>();
    }

    public int getId() {
        return id_rol;
    }

    public void setId(int id_rol) {
        this.id_rol = id_rol;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    public List<Actividad> getActividades() {
        return actividades;
    }
    
    public void setActividades(List<Actividad> actividades) {
        this.actividades = actividades != null ? actividades : new ArrayList<>();
    }
    
    public void addActividad(Actividad actividad) {
        if (actividad != null && !this.actividades.contains(actividad)) {
            this.actividades.add(actividad);
        }
    }
    
    public void removeActividad(Actividad actividad) {
        if (actividad != null) {
            this.actividades.remove(actividad);
        }
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Rol rol = (Rol) obj;
        return id_rol == rol.id_rol;
    }
    
    @Override
    public int hashCode() {
        return id_rol;
    }
}