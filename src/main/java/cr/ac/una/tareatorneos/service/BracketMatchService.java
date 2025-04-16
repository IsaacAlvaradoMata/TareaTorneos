package cr.ac.una.tareatorneos.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import cr.ac.una.tareatorneos.model.*;
import cr.ac.una.tareatorneos.util.AchievementAnimationQueue;
import cr.ac.una.tareatorneos.util.AchievementUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
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
        if ("Por comenzar".equalsIgnoreCase(torneo.getEstado())) {
            Collections.shuffle(equipos);
        }
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

        // ✅ Actualiza el estado del torneo a "Iniciado" y guarda el cambio
        torneo.setEstado("Iniciado");
        new TournamentService().updateTournament(torneo.getNombre(), torneo);
    }

    /**
     * Marca el resultado de un partido y genera el próximo match si aplica.
     */
    /**
     * Marca el resultado de un partido y coloca el ganador en la siguiente ronda correctamente.
     */
    public void registrarGanador(BracketMatch partido, String equipoGanador, boolean skipStats) {
        if (!equipoGanador.equals(partido.getEquipo1()) &&
                (partido.getEquipo2() == null || !equipoGanador.equals(partido.getEquipo2()))) {
            throw new IllegalArgumentException("El equipo ganador no está en este partido.");
        }

        // Asignar ganador y marcar partido como jugado
        partido.setGanador(equipoGanador);
        partido.setJugado(true);

        if (!skipStats) {
            TournamentService torneoService = new TournamentService();
            Tournament torneo = torneoService.getTournamentByName(partido.getTorneo());
            Team equipoA = new TeamService().getTeamByName(partido.getEquipo1());
            Team equipoB = (partido.getEquipo2() != null) ? new TeamService().getTeamByName(partido.getEquipo2()) : null;

            MatchService matchService;
            if (equipoB == null) {
                matchService = new MatchService(torneo, equipoA, null);
                matchService.getMatch().setPuntajeA(1);
                matchService.getMatch().setPuntajeB(0);
            } else {
                matchService = new MatchService(torneo, equipoA, equipoB);
                matchService.getMatch().setPuntajeA(equipoA.getNombre().equals(equipoGanador) ? 1 : 0);
                matchService.getMatch().setPuntajeB(equipoB.getNombre().equals(equipoGanador) ? 1 : 0);
            }

            // Aquí se actualizan las estadísticas del partido.
            // (En MatchService.finalizarPartido() se utiliza el flag para no duplicar la actualización).
            matchService.finalizarPartido();

            // Si el torneo aún está en estado "Por comenzar", lo pasamos a "Iniciado"
            if (torneo.getEstado().equalsIgnoreCase("Por comenzar")) {
                torneo.setEstado("Iniciado");
                torneoService.updateTournament(torneo.getNombre(), torneo);
            }
        }

        // --------------- Avanzar a la siguiente ronda ---------------
        // Se busca el siguiente partido de la siguiente ronda donde aun falte asignar equipo.
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
                    // Si no hay partido pendiente en la siguiente ronda, se crea uno nuevo.
                    Tournament torneo = new TournamentService().getTournamentByName(partido.getTorneo());

                    int cantidadEquipos = torneo.getCantidadEquipos();
                    int partidosEsperados = cantidadEquipos - 1;
                    long partidosJugados = allMatches.stream().filter(BracketMatch::isJugado).count();

                    // ✅ Solo crear nuevo partido si aún no se han jugado todos
                    if (partidosJugados < partidosEsperados) {
                        allMatches.add(new BracketMatch(
                                partido.getTorneo(),
                                partido.getRonda() + 1,
                                equipoGanador,
                                null
                        ));
                    }
                });

        // Persiste los partidos actualizados y verifica si ya se puede declarar ganador del torneo.
        guardarPartidosEnArchivo(partido.getTorneo());
        verificarYGuardarGanadorDelTorneo();
    }

    // Sobrecarga para compatibilidad cuando no se especifique skipStats (por defecto false)
    public void registrarGanador(BracketMatch partido, String equipoGanador) {
        registrarGanador(partido, equipoGanador, false);
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

            // Permitir pasar equipos que estén solos (BYE) SOLO si es el primero visualmente
            for (BracketMatch p : rondaPendientes) {
                boolean isBye = (p.getEquipo1() != null && p.getEquipo2() == null) ||
                        (p.getEquipo2() != null && p.getEquipo1() == null);

                if (isBye) {
                    // ⚠️ Validar que sea el primero visualmente
                    BracketMatch primerPendienteVisual = rondaPendientes.get(0);
                    if (primerPendienteVisual == p) {
                        return p;
                    }
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
     * Retorna los partidos pendientes (no jugados).
     */
    public List<BracketMatch> getPartidosPendientes() {
        return allMatches.stream()
                .filter(p -> !p.isJugado())
                .collect(Collectors.toList());
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

    public void verificarYGuardarGanadorDelTorneo() {
        BracketMatch finalMatch = getFinalMatch();
        if (finalMatch == null || !finalMatch.isJugado()
                || finalMatch.getEquipo1() == null || finalMatch.getEquipo2() == null
                || finalMatch.getGanador() == null)
            return;

        TournamentService torneoService = new TournamentService();
        Tournament torneo = torneoService.getTournamentByName(finalMatch.getTorneo());

        int cantidadEquipos = torneo.getCantidadEquipos();
        int partidosEsperados = cantidadEquipos - 1;
        long partidosJugados = allMatches.stream().filter(BracketMatch::isJugado).count();

        // Solo si se han jugado todos los partidos esperados
        if (partidosJugados == partidosEsperados) {
            String ganador = finalMatch.getGanador();

            torneo.setGanador(ganador);
            torneo.setEstado("Finalizado");

            torneoService.updateTournament(torneo.getNombre(), torneo);
            new TeamTournamentStatsService().asignarResultadoFinalTorneo(ganador, torneo.getNombre(), "Ganador");

            TeamService teamService = new TeamService();
            Team equipoGanadorAntes = teamService.getTeamByName(ganador);

            if (equipoGanadorAntes != null) {
                System.out.println("🏁 Torneo finalizado. Recalculando logros para el campeón: " + ganador);

                AchievementService achievementService = new AchievementService();
                List<Achievement> antes = achievementService.calcularLogrosParaEquipo(ganador);

                teamService.actualizarLogrosDeEquipo(equipoGanadorAntes); // ✅ actualiza logros en JSON

                Team equipoGanadorDespues = teamService.getTeamByName(ganador);
                List<Achievement> despues = equipoGanadorDespues.getLogros();

                System.out.println("📋 Logros nuevos detectados:");
                List<Achievement> nuevos = AchievementUtils.filtrarNuevosLogros(antes, despues);
                for (Achievement logro : nuevos) {
                    System.out.println("   ✅ " + logro.getNombre());
                    AchievementAnimationQueue.agregarALaCola(logro);
                }

                // ✅ Procesar logros justo después
                if (!nuevos.isEmpty()) {
                    AchievementAnimationQueue.setPermitirMostrar(true);
                    AchievementAnimationQueue.mostrarCuandoPosible(nuevos);
                }
            }
        }
    }


    public BracketMatch getFinalMatch() {
        return allMatches.stream()
                .filter(p -> p.isJugado())
                .filter(p -> p.getEquipo1() != null && p.getEquipo2() != null)
                .max(Comparator.comparingInt(BracketMatch::getRonda))
                .orElse(null);
    }

}
