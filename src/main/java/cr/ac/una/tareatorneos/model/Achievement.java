package cr.ac.una.tareatorneos.model;

public class Achievement {
    private String nombre;
    private boolean obtenido;
    private String equipoAsociado;

    public Achievement() {
    }

    public Achievement(String nombre, boolean obtenido) {
        this.nombre = nombre;
        this.obtenido = obtenido;
    }

    public Achievement(String nombre, boolean obtenido, String equipoAsociado) {
        this.nombre = nombre;
        this.obtenido = obtenido;
        this.equipoAsociado = equipoAsociado;
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

    public String getEquipoAsociado() {
        return equipoAsociado;
    }

    public void setEquipoAsociado(String equipoAsociado) {
        this.equipoAsociado = equipoAsociado;
    }

    @Override
    public String toString() {
        return "Achievement{" +
                "nombre='" + nombre + '\'' +
                ", obtenido=" + obtenido +
                ", equipoAsociado='" + equipoAsociado + '\'' +
                '}';
    }
}
