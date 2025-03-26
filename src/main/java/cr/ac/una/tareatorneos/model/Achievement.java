package cr.ac.una.tareatorneos.model;

public class Achievement {
    private String nombre;
    private boolean obtenido;

    public Achievement() {
    }

    public Achievement(String nombre, boolean obtenido) {
        this.nombre = nombre;
        this.obtenido = obtenido;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public boolean isObtenido() {
        return obtenido;
    }

    public void setObtenido(boolean obtenido) {
        this.obtenido = obtenido;
    }

    @Override
    public String toString() {
        return "Achievement{" +
                "nombre='" + nombre + '\'' +
                ", obtenido=" + obtenido +
                '}';
    }
}
