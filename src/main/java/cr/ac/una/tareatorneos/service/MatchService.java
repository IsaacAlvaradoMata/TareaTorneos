package cr.ac.una.tareatorneos.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import cr.ac.una.tareatorneos.model.*;
import javafx.scene.image.Image;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MatchService {

    private Tournament torneo;
    private Team equipoA;
    private Team equipoB;
    private Sport deporte;
    private Match match;

    // 🔹 Constructor normal (modificado)
    public MatchService(Tournament torneo, Team equipoA, Team equipoB) {
        this.torneo = torneo;
        this.equipoA = equipoA;
        this.equipoB = equipoB;
        this.deporte = new SportService().getSportByName(torneo.getDeporte());

        String nombreA = equipoA != null ? equipoA.getNombre() : "Equipo A";
        String nombreB = equipoB != null ? equipoB.getNombre() : null;

        this.match = new Match(torneo.getNombre(), nombreA, nombreB, torneo.getDeporte());
    }

    // 🔹 Constructor para BracketMatch (igual pero con null-safe)
    public MatchService(BracketMatch matchData) {
        this.torneo = new TournamentService().getTournamentByName(matchData.getTorneo());
        this.equipoA = new TeamService().getTeamByName(matchData.getEquipo1());
        this.equipoB = matchData.getEquipo2() != null ? new TeamService().getTeamByName(matchData.getEquipo2()) : null;
        this.deporte = new SportService().getSportByName(torneo.getDeporte());

        String nombreA = equipoA != null ? equipoA.getNombre() : "Equipo A";
        String nombreB = equipoB != null ? equipoB.getNombre() : null;

        this.match = new Match(torneo.getNombre(), nombreA, nombreB, torneo.getDeporte());
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
        return cargarImagen("teamsPhotos/" + equipoA.getTeamImage(), "Equipo A");
    }

    public Image getImagenEquipoB() {
        if (equipoB == null) return null;
        return cargarImagen("teamsPhotos/" + equipoB.getTeamImage(), "Equipo B");
    }

    public Image getImagenBalon() {
        return cargarImagen("sportsPhotos/" + deporte.getBallImage(), "Balón/Deporte");
    }

    private Image cargarImagen(String path, String tipo) {
        File file = new File(path);
        if (!file.exists()) {
            System.err.println("⚠️ Imagen no encontrada para " + tipo + ": " + path);
            return null;
        }
        System.out.println("✅ Imagen cargada para " + tipo + ": " + path);
        return new Image(file.toURI().toString());
    }

    // ============================================
    // NUEVA IMPLEMENTACIÓN: MÉTODO PRIVADO para PROCESAR ESTADÍSTICAS
    // ============================================
    private void procesarEstadisticas(String ganadorDesempate) {
        if (match.isStatsProcesadas()) {
            return;
        }

        TeamTournamentStatsService statsService = new TeamTournamentStatsService();
        if (ganadorDesempate != null) {
            statsService.guardarEstadisticaDelPartido(match, ganadorDesempate);
        } else {
            statsService.guardarEstadisticaDelPartido(match);
        }
        statsService.actualizarPuntosDeTodosLosTorneos();

        TeamService teamService = new TeamService();
        Team equipo1 = teamService.getTeamByName(match.getEquipoA());
        if (equipo1 != null) {
            teamService.actualizarLogrosDeEquipo(equipo1);
        }

        Team equipo2 = teamService.getTeamByName(match.getEquipoB());
        if (equipo2 != null) {
            teamService.actualizarLogrosDeEquipo(equipo2);
        }

        match.setStatsProcesadas(true);
        guardarMatchEnJson(match);
    }

    public void finalizarPartido() {
        match.setFinalizado(true);
        procesarEstadisticas(null);
    }

    // Método modificado para finalizar partido con desempate.
    public void finalizarPartidoConDesempate(String ganadorDesempate) {
        match.setFinalizado(true);
        // Se procesa la actualización de estadísticas de forma única.
        procesarEstadisticas(ganadorDesempate);

        // Luego, el flujo para actualizar el bracket se mantiene (sin modificar la actualización de stats).
        BracketMatchService bracketService = new BracketMatchService();
        bracketService.cargarPartidosDesdeArchivo(match.getTorneoNombre()); // Asegura que se tenga la lista actualizada.

        List<BracketMatch> partidos = bracketService.getTodosLosPartidos();

        for (BracketMatch bm : partidos) {
            System.out.println("🔎 Comparando contra BM: " + bm.getTorneo() + " | " + bm.getEquipo1() + " vs " + bm.getEquipo2());

            if (bm.getTorneo().equals(match.getTorneoNombre())
                    && Objects.equals(bm.getEquipo1(), match.getEquipoA())
                    && Objects.equals(bm.getEquipo2(), match.getEquipoB())
                    && !bm.isJugado()) {

                System.out.println("🎯 ¡Match encontrado!");
                bm.setGanador(ganadorDesempate);
                bm.setJugado(true);
                bracketService.registrarGanador(bm, ganadorDesempate);
                break;
            }
        }
        bracketService.guardarPartidosEnArchivo(match.getTorneoNombre());
    }

    public String getGanador() {
        if (match.getPuntajeA() > match.getPuntajeB()) {
            return equipoA.getNombre();
        } else {
            return equipoB != null ? equipoB.getNombre() : "Desconocido";
        }
    }

    public String getNombreEquipoA() {
        return equipoA.getNombre();
    }

    public String getNombreEquipoB() {
        return equipoB != null ? equipoB.getNombre() : "(BYE)";
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
            System.out.println("💾 Partido guardado exitosamente en matches.json");

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("❌ Error al guardar partido en JSON");
        }
    }

    private boolean torneoFinalizadoParaEquipo(String nombreEquipo, String torneoNombre) {
        TeamTournamentStatsService statsService = new TeamTournamentStatsService();
        TeamTournamentStats stats = statsService.getAllStats().stream()
                .filter(s -> s.getNombreEquipo() != null && s.getNombreEquipo().equalsIgnoreCase(nombreEquipo))
                .findFirst()
                .orElse(null);

        if (stats == null) return false;

        TeamTournamentStats.TournamentStat torneo = stats.getTorneos().stream()
                .filter(t -> t.getNombreTorneo().equalsIgnoreCase(torneoNombre))
                .findFirst()
                .orElse(null);

        if (torneo == null) return false;

        return "Ganador".equalsIgnoreCase(torneo.getResultadoTorneo());
    }

}
