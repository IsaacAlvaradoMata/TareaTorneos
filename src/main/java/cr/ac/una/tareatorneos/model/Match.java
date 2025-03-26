package cr.ac.una.tareatorneos.model;

public class Match {
    private String torneoNombre;
    private String equipoA;
    private String equipoB;
    private int puntajeA;
    private int puntajeB;
    private boolean finalizado;

    public Match() {
    }

    public Match(String torneoNombre, String equipoA, String equipoB) {
        this.torneoNombre = torneoNombre;
        this.equipoA = equipoA;
        this.equipoB = equipoB;
        this.puntajeA = 0;
        this.puntajeB = 0;
        this.finalizado = false;
    }

    public String getTorneoNombre() {
        return torneoNombre;
    }

    public void setTorneoNombre(String torneoNombre) {
        this.torneoNombre = torneoNombre;
    }

    public String getEquipoA() {
        return equipoA;
    }

    public void setEquipoA(String equipoA) {
        this.equipoA = equipoA;
    }

    public String getEquipoB() {
        return equipoB;
    }

    public void setEquipoB(String equipoB) {
        this.equipoB = equipoB;
    }

    public int getPuntajeA() {
        return puntajeA;
    }

    public void setPuntajeA(int puntajeA) {
        this.puntajeA = puntajeA;
    }

    public int getPuntajeB() {
        return puntajeB;
    }

    public void setPuntajeB(int puntajeB) {
        this.puntajeB = puntajeB;
    }

    public boolean isFinalizado() {
        return finalizado;
    }

    public void setFinalizado(boolean finalizado) {
        this.finalizado = finalizado;
    }

    @Override
    public String toString() {
        return "Match{" +
                "torneoNombre='" + torneoNombre + '\'' +
                ", equipoA='" + equipoA + '\'' +
                ", equipoB='" + equipoB + '\'' +
                ", puntajeA=" + puntajeA +
                ", puntajeB=" + puntajeB +
                ", finalizado=" + finalizado +
                '}';
    }
}
