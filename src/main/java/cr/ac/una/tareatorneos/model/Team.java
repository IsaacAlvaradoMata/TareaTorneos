package cr.ac.una.tareatorneos.model;

public class Team {
    private String nombre;
    private String deporte;
    private String teamImage;

    public Team() {
    }

    public Team(String nombre, String deporte) {
        this.nombre = nombre;
        this.deporte = deporte;
    }

    public Team(String nombre, String deporte, String teamImage) {
        this.nombre = nombre;
        this.deporte = deporte;
        this.teamImage = teamImage;
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

    public String getTeamImage() {
        return teamImage;
    }

    public void setTeamImage(String teamImage) {
        this.teamImage = teamImage;
    }




    @Override
    public String toString() {
        return "Team{" +
                "nombre=" + nombre +
                ", deporte=" + deporte +
                ", foto=" + teamImage +
                '}';
    }
}
