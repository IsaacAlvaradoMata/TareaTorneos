package cr.ac.una.tareatorneos.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import cr.ac.una.tareatorneos.model.Team;
import cr.ac.una.tareatorneos.model.TeamStats;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class TeamService {

    private final Path filePath = Paths.get("data/teams.json");
    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * Obtiene la lista de equipos desde el archivo JSON.
     * Si el archivo no existe o ocurre un error, retorna una lista vacía.
     */
    public List<Team> getAllTeams() {
        try {
            if (filePath.toFile().exists()) {
                List<Team> equipos = mapper.readValue(filePath.toFile(), new TypeReference<List<Team>>() {});
                // 🧠 Asegurarse de que todos los equipos tengan estadísticas y estado
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

    /**
     * Guarda la lista de equipos en el archivo JSON.
     */
    private boolean saveTeams(List<Team> teams) {
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(filePath.toFile(), teams);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Agrega un nuevo equipo a la lista y lo guarda en el archivo JSON.
     */
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

    /**
     * Actualiza un equipo existente identificándolo por su nombre.
     */
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

    /**
     * Elimina un equipo de la lista basándose en su nombre.
     */
    public boolean deleteTeam(String teamName) {
        List<Team> teams = getAllTeams();
        boolean removed = teams.removeIf(team -> team.getNombre().equals(teamName));
        return removed && saveTeams(teams);
    }

    /**
     * Obtiene un equipo por nombre (ignora mayúsculas).
     */
    public Team getTeamByName(String nombre) {
        return getAllTeams().stream()
                .filter(team -> team.getNombre().equalsIgnoreCase(nombre))
                .findFirst()
                .orElse(null);
    }
}
