package cr.ac.una.tareatorneos.model;

public class BracketMatch {

    private String torneo;
    private int ronda;
    private String equipo1;
    private String equipo2;
    private String ganador;
    private boolean jugado;
    private int puntajeEquipo1;
    private int puntajeEquipo2;

    public BracketMatch(String torneo, int ronda, String equipo1, String equipo2) {
        this.torneo = torneo;
        this.ronda = ronda;
        this.equipo1 = equipo1;
        this.equipo2 = equipo2;
        this.ganador = null;
        this.jugado = false;
    }

    public BracketMatch() {
    }

    public String getTorneo() {
        return torneo;
    }

    public void setTorneo(String torneo) {
        this.torneo = torneo;
    }

    public int getRonda() {
        return ronda;
    }

    public void setRonda(int ronda) {
        this.ronda = ronda;
    }

    public String getEquipo1() {
        return equipo1;
    }

    public void setEquipo1(String equipo1) {
        this.equipo1 = equipo1;
    }

    public String getEquipo2() {
        return equipo2;
    }

    public void setEquipo2(String equipo2) {
        this.equipo2 = equipo2;
    }

    public String getGanador() {
        return ganador;
    }

    public void setGanador(String ganador) {
        this.ganador = ganador;
    }

    public boolean isJugado() {
        return jugado;
    }

    public void setJugado(boolean jugado) {
        this.jugado = jugado;
    }

    public int getPuntajeEquipo1() {
        return puntajeEquipo1;
    }

    public void setPuntajeEquipo1(int puntajeEquipo1) {
        this.puntajeEquipo1 = puntajeEquipo1;
    }

    public int getPuntajeEquipo2() {
        return puntajeEquipo2;
    }

    public void setPuntajeEquipo2(int puntajeEquipo2) {
        this.puntajeEquipo2 = puntajeEquipo2;
    }

    public boolean esPartidoReal() {
        return equipo1 != null && equipo2 != null;
    }

    @Override
    public String toString() {
        return "Ronda " + ronda + ": " + equipo1 + " vs " +
                (equipo2 != null ? equipo2 : "(BYE)") +
                (jugado ? " 🏆 Ganador: " + ganador : "");
    }
}
