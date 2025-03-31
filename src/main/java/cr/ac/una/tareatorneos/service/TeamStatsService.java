package cr.ac.una.tareatorneos.service;

import cr.ac.una.tareatorneos.model.Team;
import cr.ac.una.tareatorneos.model.TeamStats;

public class TeamStatsService {

    // Método para reiniciar estadísticas (ejemplo útil)
    public void resetStats(Team team) {
        team.setEstadisticas(new TeamStats());
    }

    // Métodos para sumar estadísticas
    public void agregarPartidoGanado(Team team) {
        TeamStats stats = team.getEstadisticas();
        stats.setPartidosTotales(stats.getPartidosTotales() + 1);
        stats.setPartidosGanados(stats.getPartidosGanados() + 1);
    }

    public void agregarPartidoPerdido(Team team) {
        TeamStats stats = team.getEstadisticas();
        stats.setPartidosTotales(stats.getPartidosTotales() + 1);
        stats.setPartidosPerdidos(stats.getPartidosPerdidos() + 1);
    }

    public void agregarTorneoGanado(Team team) {
        TeamStats stats = team.getEstadisticas();
        stats.setTorneosTotales(stats.getTorneosTotales() + 1);
        stats.setTorneosGanados(stats.getTorneosGanados() + 1);
    }

    public void agregarPuntos(Team team, int puntos) {
        TeamStats stats = team.getEstadisticas();
        stats.setPuntosGlobales(stats.getPuntosGlobales() + puntos);
    }

    public void agregarAnotacion(Team team, int cantidad) {
        TeamStats stats = team.getEstadisticas();
        stats.setAnotaciones(stats.getAnotaciones() + cantidad);
    }

    public void agregarAnotacionEnContra(Team team, int cantidad) {
        TeamStats stats = team.getEstadisticas();
        stats.setAnotacionesEnContra(stats.getAnotacionesEnContra() + cantidad);
    }

    // Puedes seguir expandiendo según tu lógica de negocio.
}
