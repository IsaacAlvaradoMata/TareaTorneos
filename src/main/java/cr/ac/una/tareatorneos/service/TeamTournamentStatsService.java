package cr.ac.una.tareatorneos.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import cr.ac.una.tareatorneos.model.Match;
import cr.ac.una.tareatorneos.model.TeamTournamentStats;
import cr.ac.una.tareatorneos.model.TeamTournamentStats.MatchStat;
import cr.ac.una.tareatorneos.model.TeamTournamentStats.TournamentStat;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TeamTournamentStatsService {

    private static final File archivo = new File("data/teamTournamentStats.json");
    private final ObjectMapper mapper = new ObjectMapper();

    public List<TeamTournamentStats> getAllStats() {
        try {
            if (archivo.exists()) {
                return mapper.readValue(archivo, new TypeReference<List<TeamTournamentStats>>() {
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public void guardarEstadisticaDelPartido(Match match) {
        List<TeamTournamentStats> stats = getAllStats();

        actualizarStatsEquipo(stats, match.getEquipoA(), match.getEquipoB(), match.getTorneoNombre(),
                match.getPuntajeA(), match.getPuntajeB());

        actualizarStatsEquipo(stats, match.getEquipoB(), match.getEquipoA(), match.getTorneoNombre(),
                match.getPuntajeB(), match.getPuntajeA());

        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(archivo, stats);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void actualizarStatsEquipo(List<TeamTournamentStats> stats, String equipo, String rival,
                                       String torneo, int anotaciones, int enContra) {

        TeamTournamentStats equipoStats = stats.stream()
                .filter(e -> e.getNombreEquipo().equalsIgnoreCase(equipo))
                .findFirst()
                .orElseGet(() -> {
                    TeamTournamentStats nuevo = new TeamTournamentStats();
                    nuevo.setNombreEquipo(equipo);
                    stats.add(nuevo);
                    return nuevo;
                });

        TournamentStat torneoStat = equipoStats.getTorneos().stream()
                .filter(t -> t.getNombreTorneo().equalsIgnoreCase(torneo))
                .findFirst()
                .orElseGet(() -> {
                    TournamentStat nuevoTorneo = new TournamentStat();
                    nuevoTorneo.setNombreTorneo(torneo);
                    equipoStats.getTorneos().add(nuevoTorneo);
                    return nuevoTorneo;
                });

        MatchStat nuevoPartido = new MatchStat();
        nuevoPartido.setNumeroPartido(torneoStat.getPartidos().size() + 1);
        nuevoPartido.setRival(rival);
        nuevoPartido.setAnotaciones(anotaciones);
        nuevoPartido.setAnotacionesEnContra(enContra);
        nuevoPartido.setResultado(anotaciones > enContra ? "Ganado" :
                (anotaciones < enContra ? "Perdido" : "Empatado"));

        torneoStat.getPartidos().add(nuevoPartido);
    }

    public void asignarResultadoFinalTorneo(String equipo, String torneo, String resultado) {
        List<TeamTournamentStats> stats = getAllStats();
        for (TeamTournamentStats equipoStats : stats) {
            if (equipoStats.getNombreEquipo().equalsIgnoreCase(equipo)) {
                for (TournamentStat torneoStat : equipoStats.getTorneos()) {
                    if (torneoStat.getNombreTorneo().equalsIgnoreCase(torneo)) {
                        torneoStat.setResultadoTorneo(resultado);
                        break;
                    }
                }
            }
        }
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(archivo, stats);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
