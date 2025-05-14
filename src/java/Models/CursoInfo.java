package Models;

public class CursoInfo {
    private Curso curso;
    private int numEstudiantes;
    private double promedio;
    private int progreso;

    public CursoInfo(Curso curso) {
        this.curso = curso;
        this.numEstudiantes = 0; // TODO: Obtener de la base de datos
        this.promedio = 0.0; // TODO: Calcular promedio
        this.progreso = 0; // TODO: Calcular progreso
    }

    public Curso getCurso() {
        return curso;
    }

    public int getNumEstudiantes() {
        return numEstudiantes;
    }

    public double getPromedio() {
        return promedio;
    }

    public int getProgreso() {
        return progreso;
    }
}
