package cr.ac.una.tareatorneos.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import cr.ac.una.tareatorneos.model.Tournament;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class TournamentService {
    private final ObjectMapper mapper = new ObjectMapper();
    private final Path filePath = Path.of("data/tournaments.json");

    public List<Tournament> getAllTournaments() {
        try {
            if (filePath.toFile().exists()) {
                return mapper.readValue(filePath.toFile(), new TypeReference<List<Tournament>>() {
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public boolean addTournament(Tournament newTournament) {
        List<Tournament> tournaments = getAllTournaments();
        tournaments.add(newTournament);
        return saveTournaments(tournaments);
    }

    public boolean deleteTournament(String nombre) {
        List<Tournament> tournaments = getAllTournaments();
        boolean removed = tournaments.removeIf(t -> t.getNombre().equalsIgnoreCase(nombre));
        return removed && saveTournaments(tournaments);
    }

    public boolean updateTournament(String oldNombre, Tournament updatedTournament) {
        List<Tournament> tournaments = getAllTournaments();
        boolean modified = false;
        for (int i = 0; i < tournaments.size(); i++) {
            if (tournaments.get(i).getNombre().equalsIgnoreCase(oldNombre)) {
                tournaments.set(i, updatedTournament);
                modified = true;
                break;
            }
        }
        return modified && saveTournaments(tournaments);
    }

    private boolean saveTournaments(List<Tournament> tournaments) {
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(filePath.toFile(), tournaments);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Tournament getTournamentByName(String nombre) {
        return getAllTournaments().stream()
                .filter(tournament -> tournament.getNombre().equalsIgnoreCase(nombre))
                .findFirst()
                .orElse(null);
    }

    public boolean tournamentExists(String nombre) {
        return getAllTournaments().stream()
                .anyMatch(t -> t.getNombre().equalsIgnoreCase(nombre));
    }

    public List<Tournament> getTournamentsBySport(String deporte) {
        return getAllTournaments().stream()
                .filter(t -> t.getDeporte().equalsIgnoreCase(deporte))
                .toList();
    }

}
