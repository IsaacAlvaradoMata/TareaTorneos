package cr.ac.una.tareatorneos.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import cr.ac.una.tareatorneos.model.Match;
import cr.ac.una.tareatorneos.model.Sport;
import cr.ac.una.tareatorneos.model.Team;
import cr.ac.una.tareatorneos.model.Tournament;
import javafx.scene.image.Image;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MatchService {

    private Tournament torneo;
    private Team equipoA;
    private Team equipoB;
    private Sport deporte;

    private Match match;

    public MatchService(Tournament torneo, Team equipoA, Team equipoB) {
        this.torneo = torneo;
        this.equipoA = equipoA;
        this.equipoB = equipoB;
        this.deporte = new SportService().getSportByName(torneo.getDeporte());
        this.match = new Match(torneo.getNombre(), equipoA.getNombre(), equipoB.getNombre());
    }

    public Match getMatch() {
        return match;
    }

    public void sumarPuntoA() {
        match.setPuntajeA(match.getPuntajeA() + 1);
    }

    public void sumarPuntoB() {
        match.setPuntajeB(match.getPuntajeB() + 1);
    }

    public int getPuntajeA() {
        return match.getPuntajeA();
    }

    public int getPuntajeB() {
        return match.getPuntajeB();
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

    public void finalizarPartido() {
        match.setFinalizado(true);
        guardarMatchEnJson(match);
    }

    private void guardarMatchEnJson(Match matchFinalizado) {
        ObjectMapper mapper = new ObjectMapper();
        File archivo = new File("data/matches.json");
        List<Match> partidos = new ArrayList<>();

        try {
            if (archivo.exists()) {
                partidos = mapper.readValue(archivo, new TypeReference<List<Match>>() {
                });
            }

            partidos.add(matchFinalizado);
            mapper.writerWithDefaultPrettyPrinter().writeValue(archivo, partidos);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
