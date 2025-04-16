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
        List<Achievement> logros = AchievementUtils.generarLogrosIniciales();

        TeamTournamentStats stats = statsService.getAllStats().stream()
                .filter(e -> e.getNombreEquipo() != null && e.getNombreEquipo().equalsIgnoreCase(nombreEquipo))
                .findFirst()
                .orElse(null);

        if (stats == null) return logros;

        for (Achievement logro : logros) {
            logro.setEquipoAsociado(nombreEquipo);

            switch (logro.getNombre()) {
                case "Máxima Potencia" -> logro.setObtenido(logroMaximaPotencia(stats));
                case "Muralla Imbatible" -> logro.setObtenido(logroMurallaImbatible(stats));
                case "Imparable" -> logro.setObtenido(logroImparable(stats));
                case "Equilibrio Perfecto" -> logro.setObtenido(logroEquilibrioPerfecto(stats));
                case "Dominador Supremo" -> logro.setObtenido(logroDominadorSupremo(stats));
                case "Leyenda Plateada" -> logro.setObtenido(logroLeyendaPlateada(stats));
                case "Tricampeón" -> logro.setObtenido(logroTricampeon(stats));
                case "Regreso Triunfal" -> logro.setObtenido(logroRegresoTriunfal(stats));
                case "Campeón Inaugural" -> logro.setObtenido(logroCampeonInaugural(stats));
                default -> {
                }
            }
        }
        return logros;
    }

    private boolean logroMaximaPotencia(TeamTournamentStats stats) {
        for (TournamentStat torneo : stats.getTorneos()) {
            for (MatchStat partido : torneo.getPartidos()) {
                int anotaciones = partido.getAnotaciones();
                String resultadoReal = partido.getResultadoReal();

                if (anotaciones >= 20 && resultadoReal != null && resultadoReal.startsWith("Ganado")) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean logroMurallaImbatible(TeamTournamentStats stats) {
        for (TournamentStat torneo : stats.getTorneos()) {
            if (torneo.getResultadoTorneo() == null || !torneo.getResultadoTorneo().equalsIgnoreCase("Ganador")) {
                continue;
            }
            boolean sinGolesEnContra = torneo.getPartidos().stream()
                    .allMatch(p -> p.getAnotacionesEnContra() == 0);
            boolean todosGanados = torneo.getPartidos().stream()
                    .allMatch(p -> p.getResultadoReal() != null && p.getResultadoReal().startsWith("Ganado"));
            if (sinGolesEnContra && todosGanados) {
                return true;
            }
        }
        return false;
    }

    private boolean logroImparable(TeamTournamentStats stats) {
        for (TournamentStat torneo : stats.getTorneos()) {
            int racha = 0;
            for (MatchStat p : torneo.getPartidos()) {
                boolean rivalValido = p.getRival() != null && !p.getRival().isBlank();
                boolean partidoGanado = p.getResultadoReal() != null && p.getResultadoReal().startsWith("Ganado");

                if (rivalValido && partidoGanado) {
                    racha++;
                    if (racha >= 3) {
                        return true;
                    }
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

    private boolean logroDominadorSupremo(TeamTournamentStats stats) {
        long ganadorCount = stats.getTorneos().stream()
                .filter(torneo -> torneo.getResultadoTorneo() != null &&
                        torneo.getResultadoTorneo().equalsIgnoreCase("Ganador"))
                .count();
        return ganadorCount >= 8;
    }

    private boolean logroLeyendaPlateada(TeamTournamentStats stats) {
        long ganadorCount = stats.getTorneos().stream()
                .filter(torneo -> torneo.getResultadoTorneo() != null &&
                        torneo.getResultadoTorneo().equalsIgnoreCase("Ganador"))
                .count();
        return ganadorCount >= 6;
    }

    private boolean logroTricampeon(TeamTournamentStats stats) {
        long ganadorCount = stats.getTorneos().stream()
                .filter(torneo -> torneo.getResultadoTorneo() != null &&
                        torneo.getResultadoTorneo().equalsIgnoreCase("Ganador"))
                .count();
        return ganadorCount >= 3;
    }

    private boolean logroRegresoTriunfal(TeamTournamentStats stats) {
        List<TournamentStat> torneos = stats.getTorneos();
        if (torneos.size() < 2) {
            return false;
        }
        for (int i = 1; i < torneos.size(); i++) {
            String resultadoAnterior = torneos.get(i - 1).getResultadoTorneo();
            String resultadoActual = torneos.get(i).getResultadoTorneo();
            if (resultadoAnterior != null && resultadoActual != null) {
                if (!resultadoAnterior.equalsIgnoreCase("Ganador") &&
                        resultadoActual.equalsIgnoreCase("Ganador")) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean logroCampeonInaugural(TeamTournamentStats stats) {
        for (TournamentStat torneo : stats.getTorneos()) {
            if ("Ganador".equalsIgnoreCase(torneo.getResultadoTorneo())) {
                return true;
            }
        }
        return false;
    }


}
