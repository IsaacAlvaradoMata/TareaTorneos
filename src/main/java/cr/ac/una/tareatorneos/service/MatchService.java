package cr.ac.una.tareatorneos.service;

import cr.ac.una.tareatorneos.model.Sport;
import cr.ac.una.tareatorneos.model.Team;
import cr.ac.una.tareatorneos.model.Tournament;
import javafx.scene.image.Image;

import java.io.File;

public class MatchService {

    private Tournament torneo;
    private Team equipoA;
    private Team equipoB;
    private Sport deporte;

    private int puntajeA = 0;
    private int puntajeB = 0;

    public MatchService(Tournament torneo, Team equipoA, Team equipoB) {
        this.torneo = torneo;
        this.equipoA = equipoA;
        this.equipoB = equipoB;
        this.deporte = new SportService().getSportByName(torneo.getDeporte());
    }

    public Tournament getTorneo() {
        return torneo;
    }

    public Team getEquipoA() {
        return equipoA;
    }

    public Team getEquipoB() {
        return equipoB;
    }

    public Sport getDeporte() {
        return deporte;
    }

    public int getPuntajeA() {
        return puntajeA;
    }

    public int getPuntajeB() {
        return puntajeB;
    }

    public void sumarPuntoA() {
        puntajeA++;
    }

    public void sumarPuntoB() {
        puntajeB++;
    }

    public Image getImagenEquipoA() {
        return cargarImagen("teamsPhotos/" + equipoA.getTeamImage());
    }

    public Image getImagenEquipoB() {
        return cargarImagen("teamsPhotos/" + equipoB.getTeamImage());
    }

    public Image getImagenBalon() {
        return cargarImagen("sportsPhotos/" + deporte.getBallImage());
    }

    private Image cargarImagen(String path) {
        File file = new File(path);
        return file.exists() ? new Image(file.toURI().toString()) : null;
    }
}
