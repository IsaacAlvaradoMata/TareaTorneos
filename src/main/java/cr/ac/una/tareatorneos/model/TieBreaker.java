package cr.ac.una.tareatorneos.model;

public class TieBreaker {
    private String equipoA;
    private String equipoB;
    private String ganador;
    private boolean finalizado;

    public TieBreaker(String equipoA, String equipoB) {
        this.equipoA = equipoA;
        this.equipoB = equipoB;
        this.finalizado = false;
    }

    public String getEquipoA() {
        return equipoA;
    }

    public String getEquipoB() {
        return equipoB;
    }

    public String getGanador() {
        return ganador;
    }

    public boolean isFinalizado() {
        return finalizado;
    }

    public void setGanador(String ganador) {
        this.ganador = ganador;
        this.finalizado = true;
    }
}
