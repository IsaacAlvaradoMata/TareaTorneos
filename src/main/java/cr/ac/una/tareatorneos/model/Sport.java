package cr.ac.una.tareatorneos.model;

import java.time.LocalDate;

public class Sport {

    private String nombre;
    private String ballImage;
    private LocalDate fechaCreacion;

    public Sport() { }

    public Sport(String nombre, String ballImage) {
        this.nombre = nombre;
        this.ballImage = ballImage;
        this.fechaCreacion = LocalDate.now();
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getBallImage() {
        return ballImage;
    }

    public void setBallImage(String ballImage) {
        this.ballImage = ballImage;
    }

    public LocalDate getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDate fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    @Override
    public String toString() {
        return "Sport{" +
                "nombre=" + nombre +
                ", ballImage=" + ballImage +
                ", fechaCreacion=" + fechaCreacion +
                '}';
    }
}
