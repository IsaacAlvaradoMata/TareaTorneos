package cr.ac.una.tareatorneos.service;

import cr.ac.una.tareatorneos.model.BracketGenerator;
import cr.ac.una.tareatorneos.model.Tournament;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BracketGeneratorService {

    public List<BracketGenerator> getBracketTeamsByTournament(String tournamentName) {
        List<BracketGenerator> visualTeams = new ArrayList<>();
        TournamentService ts = new TournamentService();
        Tournament torneo = ts.getTournamentByName(tournamentName);

        if (torneo != null) {
            // 🌀 Mezclar si es la primera vez
            if ("Por comenzar".equalsIgnoreCase(torneo.getEstado())) {
                Collections.shuffle(torneo.getEquiposParticipantes()); // 🎲 Randomiza los equipos
                torneo.setEstado("Iniciado"); // 🚩 Cambia el estado
                ts.updateTournament(torneo.getNombre(), torneo); // 💾 Guarda los cambios
            }

            // 🧱 Arma los visuales
            for (String teamName : torneo.getEquiposParticipantes()) {
                String imagePath = "file:teamsPhotos/" + new TeamService().getTeamByName(teamName).getTeamImage();
                visualTeams.add(new BracketGenerator(teamName, imagePath));
            }
        }

        return visualTeams;
    }
}
