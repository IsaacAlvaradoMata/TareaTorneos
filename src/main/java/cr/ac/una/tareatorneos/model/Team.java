package cr.ac.una.tareatorneos.model;

public class Team {
    private String nombre;
    private String deporte;
    private String foto;

    public Team() {
    }

    public Team(String nombre, String deporte) {
        this.nombre = nombre;
        this.deporte = deporte;
    }

    public Team(String nombre, String deporte, String foto) {
        this.nombre = nombre;
        this.deporte = deporte;
        this.foto = foto;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDeporte() {
        return deporte;
    }

    public void setDeporte(String deporte) {
        this.deporte = deporte;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    @Override
    public String toString() {
        return "Team{" +
                "nombre=" + nombre +
                ", deporte=" + deporte +
                ", foto=" + foto +
                '}';
    }
}
