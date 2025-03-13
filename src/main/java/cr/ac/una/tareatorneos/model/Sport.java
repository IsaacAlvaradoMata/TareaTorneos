package cr.ac.una.tareatorneos.model;

public class Sport {

    private String nombre;
    private String ballImage; // Ruta o nombre del archivo de la imagen del balón

    public Sport() {
    }

    public Sport(String nombre, String ballImage) {
        this.nombre = nombre;
        this.ballImage = ballImage;
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

    @Override
    public String toString() {
        return "Sport{" + "nombre=" + nombre + ", ballImage=" + ballImage + '}';
    }
}
