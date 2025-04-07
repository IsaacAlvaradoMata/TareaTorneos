package cr.ac.una.tareatorneos.service;

import cr.ac.una.tareatorneos.model.Achievement;
import cr.ac.una.tareatorneos.model.TeamTournamentStats;
import cr.ac.una.tareatorneos.model.TeamTournamentStats.MatchStat;
import cr.ac.una.tareatorneos.model.TeamTournamentStats.TournamentStat;
import cr.ac.una.tareatorneos.util.AchievementUtils;

import java.util.List;

public class AchievementService {

    private final TeamTournamentStatsService statsService = new TeamTournamentStatsService();

    public List<Achievement> calcularLogrosParaEquipo(String nombreEquipo) {
        List<Achievement> logros = AchievementUtils.generarLogrosIniciales(); // ✅ base con los 9 logros predefinidos

        TeamTournamentStats stats = statsService.getAllStats().stream()
                .filter(e -> e.getNombreEquipo() != null && e.getNombreEquipo().equalsIgnoreCase(nombreEquipo))
                .findFirst()
                .orElse(null);

        if (stats == null) return logros;

        for (Achievement logro : logros) {
            switch (logro.getNombre()) {
                case "Máxima Potencia" -> logro.setObtenido(logroMaximaPotencia(stats));
                case "Muralla Imbatible" -> logro.setObtenido(logroMurallaImbatible(stats));
                case "Imparable" -> logro.setObtenido(logroImparable(stats));
                case "Equilibrio Perfecto" -> logro.setObtenido(logroEquilibrioPerfecto(stats));
                // Otros logros no están implementados aún, se quedan en false
            }
        }

        return logros;
    }

    private boolean logroMaximaPotencia(TeamTournamentStats stats) {
        for (TournamentStat torneo : stats.getTorneos()) {
            int totalGoles = torneo.getPartidos().stream()
                    .mapToInt(MatchStat::getAnotaciones)
                    .sum();
            if (totalGoles > 20) return true;
        }
        return false;
    }

    private boolean logroMurallaImbatible(TeamTournamentStats stats) {
        for (TournamentStat torneo : stats.getTorneos()) {
            boolean sinGolesEnContra = torneo.getPartidos().stream()
                    .allMatch(p -> p.getAnotacionesEnContra() == 0);
            boolean todosGanados = torneo.getPartidos().stream()
                    .allMatch(p -> p.getResultadoReal().startsWith("Ganado"));
            if (sinGolesEnContra && todosGanados) return true;
        }
        return false;
    }

    private boolean logroImparable(TeamTournamentStats stats) {
        for (TournamentStat torneo : stats.getTorneos()) {
            int racha = 0;
            for (MatchStat p : torneo.getPartidos()) {
                if (p.getResultadoReal().startsWith("Ganado")) {
                    racha++;
                    if (racha >= 3) return true;
                } else {
                    racha = 0;
                }
            }
        }
        return false;
    }

    private boolean logroEquilibrioPerfecto(TeamTournamentStats stats) {
        int contador = 0;
        for (TournamentStat torneo : stats.getTorneos()) {
            for (MatchStat p : torneo.getPartidos()) {
                if ("Empatado".equals(p.getResultado()) &&
                        "Ganado (desempate)".equals(p.getResultadoReal())) {
                    contador++;
                }
            }
        }
        return contador >= 5;
    }
}
