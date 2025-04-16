package cr.ac.una.tareatorneos.model;

import java.util.ArrayList;
import java.util.List;

public class Team {
    private String nombre;
    private String deporte;
    private String teamImage;
    private List<Achievement> logros = new ArrayList<>();
    private TeamStats estadisticas = new TeamStats();

    private String estado = "disponible";

    public Team() {
        this.estadisticas = new TeamStats();
        this.estado = "disponible";
    }

    public Team(String nombre, String deporte) {
        this.nombre = nombre;
        this.deporte = deporte;
        this.estadisticas = new TeamStats();
        this.estado = "disponible";
    }

    public Team(String nombre, String deporte, String teamImage) {
        this.nombre = nombre;
        this.deporte = deporte;
        this.teamImage = teamImage;
        this.estadisticas = new TeamStats();
        this.estado = "disponible";
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

    public TeamStats getEstadisticas() {
        return estadisticas;
    }

    public void setEstadisticas(TeamStats estadisticas) {
        this.estadisticas = estadisticas != null ? estadisticas : new TeamStats();
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        if (estado == null || (!estado.equalsIgnoreCase("participante") && !estado.equalsIgnoreCase("disponible"))) {
            this.estado = "disponible";
        } else {
            this.estado = estado.toLowerCase();
        }
    }

    @Override
    public String toString() {
        return "Team{" +
                "nombre='" + nombre + '\'' +
                ", deporte='" + deporte + '\'' +
                ", teamImage='" + teamImage + '\'' +
                ", logros=" + logros +
                ", estadisticas=" + estadisticas +
                ", estado='" + estado + '\'' +
                '}';
    }
}
