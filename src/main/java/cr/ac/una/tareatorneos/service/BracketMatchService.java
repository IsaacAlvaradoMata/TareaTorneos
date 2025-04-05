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

        List<String> equipos = new ArrayList<>(torneo.getEquiposParticipantes());
        allMatches.clear();

        // 1. Calcular la cantidad de rondas necesarias
        int totalEquipos = equipos.size();
        int rondas = (int) Math.ceil(Math.log(totalEquipos) / Math.log(2)); // ej: 8 equipos = 3 rondas

        // 2. Rellenar con "BYEs" si es necesario
        int totalSlots = (int) Math.pow(2, rondas); // ej: 8 → 8, 6 → 8
        while (equipos.size() < totalSlots) {
            equipos.add(null); // espacio vacío
        }

        // 3. Crear partidos por rondas (estructura completa)
        List<String> rondaActual = new ArrayList<>(equipos);
        int matchId = 0;

        for (int ronda = 1; ronda <= rondas; ronda++) {
            List<String> siguienteRonda = new ArrayList<>();

            for (int i = 0; i < rondaActual.size(); i += 2) {
                String equipo1 = rondaActual.get(i);
                String equipo2 = rondaActual.get(i + 1);

                BracketMatch match = new BracketMatch(torneo.getNombre(), ronda, equipo1, equipo2);
                allMatches.add(match);

                // En la siguiente ronda solo agregamos espacios vacíos por ahora
                siguienteRonda.add(null);
            }

            rondaActual = siguienteRonda;
        }

        guardarPartidosEnArchivo(torneo.getNombre());
    }

    /**
     * Marca el resultado de un partido y genera el próximo match si aplica.
     */
    /**
     * Marca el resultado de un partido y coloca el ganador en la siguiente ronda correctamente.
     */
    public void registrarGanador(BracketMatch partido, String equipoGanador) {
        if (!partido.getEquipo1().equals(equipoGanador) &&
                (partido.getEquipo2() == null || !partido.getEquipo2().equals(equipoGanador))) {
            throw new IllegalArgumentException("El equipo ganador no está en este partido.");
        }

        partido.setGanador(equipoGanador);
        partido.setJugado(true);

        // ➕ Avanza a la próxima ronda colocándolo en el primer espacio vacío disponible
        allMatches.stream()
                .filter(p -> p.getRonda() == partido.getRonda() + 1)
                .filter(p -> p.getEquipo1() == null || p.getEquipo2() == null)
                .findFirst()
                .ifPresentOrElse(p -> {
                    if (p.getEquipo1() == null) {
                        p.setEquipo1(equipoGanador);
                    } else {
                        p.setEquipo2(equipoGanador);
                    }
                }, () -> {
                    // Solo se llega aquí si no existía nodo en la siguiente ronda (respaldo)
                    allMatches.add(new BracketMatch(
                            partido.getTorneo(),
                            partido.getRonda() + 1,
                            equipoGanador,
                            null
                    ));
                });

        guardarPartidosEnArchivo(partido.getTorneo());
    }

    /**
     * Retorna el siguiente partido pendiente por jugar.
     */
    public BracketMatch getSiguientePartidoPendiente() {
        List<BracketMatch> partidos = getTodosLosPartidos();

        // Obtener rondas ordenadas
        int maxRonda = partidos.stream().mapToInt(BracketMatch::getRonda).max().orElse(1);

        for (int ronda = 1; ronda <= maxRonda; ronda++) {
            final int rondaActual = ronda;
            List<BracketMatch> rondaPendientes = partidos.stream()
                    .filter(p -> p.getRonda() == rondaActual && !p.isJugado())
                    .toList();

            if (rondaPendientes.isEmpty()) continue;

            long partidosJugables = rondaPendientes.stream()
                    .filter(p -> p.getEquipo1() != null && p.getEquipo2() != null)
                    .count();

            long conBye = rondaPendientes.stream()
                    .filter(p -> (p.getEquipo1() == null || p.getEquipo2() == null))
                    .count();

            // Caso: solo 1 partido con dos equipos -> final
            if (rondaPendientes.size() == 1 && partidosJugables == 1) {
                return rondaPendientes.get(0);
            }

            // Permitir pasar equipos que estén solos
            for (BracketMatch p : rondaPendientes) {
                if ((p.getEquipo1() == null && p.getEquipo2() != null) ||
                        (p.getEquipo2() == null && p.getEquipo1() != null)) {
                    return p; // avanzar este equipo automáticamente
                }
            }

            // Luego retornar partidos normales
            for (BracketMatch p : rondaPendientes) {
                if (p.getEquipo1() != null && p.getEquipo2() != null) {
                    return p;
                }
            }
        }

        return null; // todos jugados
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
