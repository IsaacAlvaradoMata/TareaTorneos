package cr.ac.una.tareatorneos.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import cr.ac.una.tareatorneos.model.*;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class TeamService {

    private final Path filePath = Paths.get("data/teams.json");
    private final ObjectMapper mapper = new ObjectMapper();

    public List<Team> getAllTeams() {
        try {
            if (filePath.toFile().exists()) {
                List<Team> equipos = mapper.readValue(filePath.toFile(), new TypeReference<List<Team>>() {
                });
                for (Team t : equipos) {
                    if (t.getEstadisticas() == null) {
                        t.setEstadisticas(new TeamStats());
                    }
                    if (t.getEstado() == null || t.getEstado().isBlank()) {
                        t.setEstado("disponible");
                    }
                }
                return equipos;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    private boolean saveTeams(List<Team> teams) {
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(filePath.toFile(), teams);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean addTeam(Team newTeam) {
        List<Team> teams = getAllTeams();

        if (newTeam.getEstadisticas() == null) {
            newTeam.setEstadisticas(new TeamStats());
        }
        if (newTeam.getEstado() == null || newTeam.getEstado().isBlank()) {
            newTeam.setEstado("disponible");
        }

        teams.add(newTeam);
        return saveTeams(teams);
    }

    public boolean updateTeam(String oldNombre, Team updatedTeam) {
        List<Team> teams = getAllTeams();
        boolean modified = false;

        for (int i = 0; i < teams.size(); i++) {
            Team existingTeam = teams.get(i);
            if (existingTeam.getNombre().equalsIgnoreCase(oldNombre)) {

                if (updatedTeam.getEstadisticas() == null) {
                    updatedTeam.setEstadisticas(existingTeam.getEstadisticas());
                }

                if (updatedTeam.getEstado() == null || updatedTeam.getEstado().isBlank()) {
                    updatedTeam.setEstado(existingTeam.getEstado());
                }

                teams.set(i, updatedTeam);
                modified = true;
                break;
            }
        }

        return modified && saveTeams(teams);
    }

    public boolean deleteTeam(String teamName) {
        List<Team> teams = getAllTeams();
        boolean removed = teams.removeIf(team -> team.getNombre().equals(teamName));
        return removed && saveTeams(teams);
    }

    public Team getTeamByName(String nombre) {
        return getAllTeams().stream()
                .filter(team -> team.getNombre().equalsIgnoreCase(nombre))
                .findFirst()
                .orElse(null);
    }

    public void actualizarLogrosDeEquipo(Team equipo) {
        AchievementService achievementService = new AchievementService();

        List<Achievement> logrosAnteriores = equipo.getLogros() != null
                ? new ArrayList<>(equipo.getLogros())
                : new ArrayList<>();

        List<Achievement> logrosCompletos = achievementService.calcularLogrosParaEquipo(equipo.getNombre());

        TeamTournamentStatsService statsService = new TeamTournamentStatsService();
        TeamTournamentStats stats = statsService.getAllStats().stream()
                .filter(s -> s.getNombreEquipo() != null && s.getNombreEquipo().equalsIgnoreCase(equipo.getNombre()))
                .findFirst()
                .orElse(null);

        boolean tieneTorneoGanado = false;
        if (stats != null) {
            tieneTorneoGanado = stats.getTorneos().stream()
                    .anyMatch(t -> "Ganador".equalsIgnoreCase(t.getResultadoTorneo()));
        }

        for (Achievement logro : logrosCompletos) {
            if (List.of("Dominador Supremo", "Leyenda Plateada", "Tricampeón", "Campeón Inaugural", "Regreso Triunfal").contains(logro.getNombre())
                    && !tieneTorneoGanado) {
                logro.setObtenido(false);
            }
        }

        equipo.setLogros(logrosCompletos);

        boolean actualizado = updateTeam(equipo.getNombre(), equipo);
        if (actualizado) {
            List<Achievement> desbloqueados = new ArrayList<>();
            for (Achievement nuevo : logrosCompletos) {
                boolean fueObtenidoAhora = nuevo.isObtenido() &&
                        logrosAnteriores.stream()
                                .filter(l -> l.getNombre().equalsIgnoreCase(nuevo.getNombre()))
                                .noneMatch(Achievement::isObtenido);
                if (fueObtenidoAhora) {
                    desbloqueados.add(nuevo);
                }
            }

            if (!desbloqueados.isEmpty()) {
            }
        }
    }

    public void actualizarLogrosDeTodosLosEquipos() {
        List<Team> equipos = getAllTeams();
        for (Team equipo : equipos) {
            actualizarLogrosDeEquipo(equipo);
        }
    }

    public void liberarEquiposSiNoParticipanEnOtros(String nombreTorneoFinalizado, List<String> equiposDelTorneo) {
        TournamentService tournamentService = new TournamentService();
        List<Tournament> todosLosTorneos = tournamentService.getAllTournaments();

        for (String nombreEquipo : equiposDelTorneo) {
            boolean participaEnOtroTorneo = todosLosTorneos.stream()
                    .filter(t -> !t.getNombre().equalsIgnoreCase(nombreTorneoFinalizado))
                    .filter(t -> t.getEstado().equalsIgnoreCase("iniciado") || t.getEstado().equalsIgnoreCase("por comenzar"))
                    .anyMatch(t -> t.getEquiposParticipantes().contains(nombreEquipo));

            if (!participaEnOtroTorneo) {
                Team equipo = getTeamByName(nombreEquipo);
                if (equipo != null && "participante".equalsIgnoreCase(equipo.getEstado())) {
                    equipo.setEstado("disponible");
                    updateTeam(equipo.getNombre(), equipo);
                }
            }
        }
    }


}
