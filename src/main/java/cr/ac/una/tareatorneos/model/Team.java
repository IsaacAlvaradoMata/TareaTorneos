package cr.ac.una.tareatorneos.model;

import java.util.ArrayList;
import java.util.List;

public class Team {
    private String nombre;
    private String deporte;
    private String teamImage;
    private List<Achievement> logros = new ArrayList<>();

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

    public List<Achievement> getLogros() {
        return logros;
    }

    public void setLogros(List<Achievement> logros) {
        this.logros = logros;
    }

    @Override
    public String toString() {
        return "Team{" +
                "nombre=" + nombre +
                ", deporte=" + deporte +
                ", foto=" + teamImage +
                ", logros=" + logros +
                '}';
    }
}
