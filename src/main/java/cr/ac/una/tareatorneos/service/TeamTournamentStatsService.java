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

    public TeamTournamentStats getStatsByTeamName(String nombreEquipo) {
        List<TeamTournamentStats> todos = getAllStats();
        return todos.stream()
                .filter(s -> s.getNombreEquipo() != null && s.getNombreEquipo().equalsIgnoreCase(nombreEquipo))
                .findFirst()
                .orElse(null);
    }


    public void guardarEstadisticaDelPartido(Match match, String ganadorDesempate) {
        List<TeamTournamentStats> stats = getAllStats();

        actualizarStatsEquipo(stats, match.getEquipoA(), match.getEquipoB(), match.getTorneoNombre(),
                match.getPuntajeA(), match.getPuntajeB(), ganadorDesempate);

        actualizarStatsEquipo(stats, match.getEquipoB(), match.getEquipoA(), match.getTorneoNombre(),
                match.getPuntajeB(), match.getPuntajeA(), ganadorDesempate);

        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(archivo, stats);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void guardarEstadisticaDelPartido(Match match) {
        guardarEstadisticaDelPartido(match, null);
    }

    private void actualizarStatsEquipo(List<TeamTournamentStats> stats, String equipo, String rival,
                                       String torneo, int anotaciones, int enContra, String ganadorDesempate) {
        TeamTournamentStats equipoStats = stats.stream()
                .filter(e -> e.getNombreEquipo() != null && e.getNombreEquipo().equalsIgnoreCase(equipo))
                .findFirst()
                .orElseGet(() -> {
                    TeamTournamentStats nuevo = new TeamTournamentStats();
                    nuevo.setNombreEquipo(equipo);
                    stats.add(nuevo);
                    return nuevo;
                });

        TournamentStat torneoStat = equipoStats.getTorneos().stream()
                .filter(t -> t.getNombreTorneo() != null && t.getNombreTorneo().equalsIgnoreCase(torneo))
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

        boolean empate = anotaciones == enContra;
        nuevoPartido.setResultado(empate ? "Empatado" : (anotaciones > enContra ? "Ganado" : "Perdido"));

        if (empate && ganadorDesempate != null) {
            nuevoPartido.setConDesempate(true);
            if (equipo.equalsIgnoreCase(ganadorDesempate)) {
                nuevoPartido.setResultadoReal("Ganado (desempate)");
            } else {
                nuevoPartido.setResultadoReal("Perdido (desempate)");
            }
        } else {
            nuevoPartido.setConDesempate(false);
            nuevoPartido.setResultadoReal(nuevoPartido.getResultado());
        }

        torneoStat.getPartidos().add(nuevoPartido);
    }

    public void asignarResultadoFinalTorneo(String equipo, String torneo, String resultado) {
        List<TeamTournamentStats> stats = getAllStats();

        TeamTournamentStats equipoStats = stats.stream()
                .filter(e -> e.getNombreEquipo() != null && e.getNombreEquipo().equalsIgnoreCase(equipo))
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

        torneoStat.setResultadoTorneo(resultado);

        for (TeamTournamentStats stat : stats) {
            if (stat.getNombreEquipo() == null || stat.getNombreEquipo().equalsIgnoreCase(equipo)) continue;

            for (TournamentStat t : stat.getTorneos()) {
                if (torneo.equalsIgnoreCase(t.getNombreTorneo()) && t.getResultadoTorneo() == null) {
                    t.setResultadoTorneo("Perdedor");
                }
            }
        }

        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(archivo, stats);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void actualizarPuntosDeTodosLosTorneos() {
        List<TeamTournamentStats> statsEquipos = getAllStats();
        for (TeamTournamentStats equipo : statsEquipos) {
            for (TeamTournamentStats.TournamentStat torneo : equipo.getTorneos()) {
                int puntos = 0;
                for (TeamTournamentStats.MatchStat partido : torneo.getPartidos()) {
                    switch (partido.getResultadoReal()) {
                        case "Ganado" -> puntos += 3;
                        case "Ganado (desempate)" -> puntos += 2;
                    }

                }
                torneo.setPuntos(puntos);
            }
        }
        saveAllStats(statsEquipos);
    }

    public void saveAllStats(List<TeamTournamentStats> stats) {
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(archivo, stats);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
