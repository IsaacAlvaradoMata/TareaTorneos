package cr.ac.una.tareatorneos.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true) // opcional para proteger de otros errores
public class Match {

    private String torneoNombre;
    private String equipoA;
    private String equipoB;
    private int puntajeA;
    private int puntajeB;
    private boolean finalizado;

    private String deporte; // <--- este es el nuevo campo que se necesita

    public Match() {
    }

    public Match(String torneoNombre, String equipoA, String equipoB, String deporte) {
        this.torneoNombre = torneoNombre;
        this.equipoA = equipoA;
        this.equipoB = equipoB;
        this.deporte = deporte;
        this.puntajeA = 0;
        this.puntajeB = 0;
        this.finalizado = false;
    }

    // Getters y Setters
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

    public String getDeporte() {
        return deporte;
    }

    public void setDeporte(String deporte) {
        this.deporte = deporte;
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
                ", deporte='" + deporte + '\'' +
                '}';
    }
}
