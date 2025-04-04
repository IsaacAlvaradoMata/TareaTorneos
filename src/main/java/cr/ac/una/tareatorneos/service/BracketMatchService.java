package cr.ac.una.tareatorneos.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import cr.ac.una.tareatorneos.model.BracketGenerator;
import cr.ac.una.tareatorneos.model.BracketMatch;
import cr.ac.una.tareatorneos.model.Tournament;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class BracketMatchService {

    public List<BracketGenerator> getEstadoVisualActual() {
        List<BracketGenerator> visual = new ArrayList<>();
        for (BracketMatch match : allMatches) {
            if (match.getEquipo1() != null) {
                visual.add(new BracketGenerator(match.getEquipo1(), "file:teamsPhotos/" + new TeamService().getTeamByName(match.getEquipo1()).getTeamImage()));
            }
            if (match.getEquipo2() != null && !match.getEquipo2().equals(match.getEquipo1())) {
                visual.add(new BracketGenerator(match.getEquipo2(), "file:teamsPhotos/" + new TeamService().getTeamByName(match.getEquipo2()).getTeamImage()));
            }
        }
        return visual;
    }

    private final List<BracketMatch> allMatches = new ArrayList<>();
    private final ObjectMapper mapper = new ObjectMapper();

    // 📁 Archivo por torneo
    private File getMatchFile(String tournamentName) {
        return new File("data/matches_" + tournamentName + ".json");
    }

    /**
     * Genera los partidos para el torneo solo si no existen previamente.
     */
    public void generarPartidosDesdeEquipos(Tournament torneo) {
        File file = getMatchFile(torneo.getNombre());

        if (file.exists()) {
            cargarPartidosDesdeArchivo(torneo.getNombre());
            return;
        }

        List<String> equipos = torneo.getEquiposParticipantes();
        allMatches.clear();

        int ronda = 1;
        List<String> rondaActual = new ArrayList<>(equipos);

        while (rondaActual.size() > 1) {
            List<String> siguienteRonda = new ArrayList<>();

            for (int i = 0; i < rondaActual.size(); i += 2) {
                String equipo1 = rondaActual.get(i);
                String equipo2 = (i + 1 < rondaActual.size()) ? rondaActual.get(i + 1) : null;

                BracketMatch match = new BracketMatch(torneo.getNombre(), ronda, equipo1, equipo2);
                allMatches.add(match);

                // Si tiene rival automático (BYE), pasa directo
                if (equipo2 == null) {
                    siguienteRonda.add(equipo1);
                }
            }

            ronda++;
            rondaActual = siguienteRonda;
        }

        guardarPartidosEnArchivo(torneo.getNombre());
    }

    /**
     * Retorna el siguiente partido pendiente por jugar.
     */
    public BracketMatch getSiguientePartidoPendiente() {
        return allMatches.stream()
                .filter(p -> !p.isJugado() && p.getEquipo1() != null && p.getEquipo2() != null)
                .findFirst()
                .orElse(null);
    }

    /**
     * Marca el resultado de un partido y genera el próximo match si aplica.
     */
    public void registrarGanador(BracketMatch partido, String equipoGanador) {
        if (!partido.getEquipo1().equals(equipoGanador) &&
                (partido.getEquipo2() == null || !partido.getEquipo2().equals(equipoGanador))) {
            throw new IllegalArgumentException("El equipo ganador no está en este partido.");
        }

        partido.setGanador(equipoGanador);
        partido.setJugado(true);

        // ➕ Avanza a la próxima ronda
        allMatches.stream()
                .filter(p -> p.getRonda() == partido.getRonda() + 1)
                .filter(p -> p.getEquipo2() == null)
                .findFirst()
                .ifPresentOrElse(
                        p -> p.setEquipo2(equipoGanador),
                        () -> allMatches.add(new BracketMatch(
                                partido.getTorneo(),
                                partido.getRonda() + 1,
                                equipoGanador,
                                null
                        ))
                );

        guardarPartidosEnArchivo(partido.getTorneo());
    }

    /**
     * Todos los partidos del torneo actual (ordenados por ronda).
     */
    public List<BracketMatch> getTodosLosPartidos() {
        return allMatches.stream()
                .sorted(Comparator.comparingInt(BracketMatch::getRonda))
                .collect(Collectors.toList());
    }

    /**
     * Retorna todos los partidos de una ronda específica.
     */
    public List<BracketMatch> getPartidosPorRonda(int ronda) {
        return allMatches.stream()
                .filter(p -> p.getRonda() == ronda)
                .collect(Collectors.toList());
    }

    /**
     * Persiste los partidos en JSON.
     */
    public void guardarPartidosEnArchivo(String torneo) {
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(getMatchFile(torneo), allMatches);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Carga los partidos desde JSON.
     */
    public void cargarPartidosDesdeArchivo(String torneo) {
        File file = getMatchFile(torneo);
        if (file.exists()) {
            try {
                List<BracketMatch> partidos = mapper.readValue(file, new TypeReference<>() {
                });
                allMatches.clear();
                allMatches.addAll(partidos);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Reinicia los partidos del torneo (debug o pruebas).
     */
    public void reiniciarPartidos(String torneo) {
        getMatchFile(torneo).delete();
        allMatches.clear();
    }
}
