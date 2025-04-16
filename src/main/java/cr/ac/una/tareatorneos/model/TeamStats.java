package cr.ac.una.tareatorneos.model;

public class TeamStats {
    private int partidosTotales;
    private int partidosGanados;
    private int partidosPerdidos;
    private int partidosEmpatados;

    private int torneosTotales;
    private int torneosGanados;
    private int torneosPerdidos;

    private int puntosGlobales;
    private int anotaciones;
    private int anotacionesEnContra;

    public TeamStats() {
    }

    public void registrarVictoria(int golesAFavor, int golesEnContra) {
        partidosTotales++;
        partidosGanados++;
        puntosGlobales += 3;
        anotaciones += golesAFavor;
        anotacionesEnContra += golesEnContra;
    }

    public void registrarEmpate(int golesAFavor, int golesEnContra) {
        partidosTotales++;
        partidosEmpatados++;
        puntosGlobales += 1;
        anotaciones += golesAFavor;
        anotacionesEnContra += golesEnContra;
    }

    public void registrarDerrota(int golesAFavor, int golesEnContra) {
        partidosTotales++;
        partidosPerdidos++;
        anotaciones += golesAFavor;
        anotacionesEnContra += golesEnContra;
    }

    public void registrarParticipacionTorneo() {
        torneosTotales++;
    }

    public void registrarVictoriaTorneo() {
        torneosTotales++;
        torneosGanados++;
    }

    public void registrarDerrotaTorneo() {
        torneosTotales++;
        torneosPerdidos++;
    }


    public int getPartidosTotales() {
        return partidosTotales;
    }

    public void setPartidosTotales(int partidosTotales) {
        this.partidosTotales = partidosTotales;
    }

    public int getPartidosGanados() {
        return partidosGanados;
    }

    public void setPartidosGanados(int partidosGanados) {
        this.partidosGanados = partidosGanados;
    }

    public int getPartidosPerdidos() {
        return partidosPerdidos;
    }

    public void setPartidosPerdidos(int partidosPerdidos) {
        this.partidosPerdidos = partidosPerdidos;
    }

    public int getPartidosEmpatados() {
        return partidosEmpatados;
    }

    public void setPartidosEmpatados(int partidosEmpatados) {
        this.partidosEmpatados = partidosEmpatados;
    }

    public int getTorneosTotales() {
        return torneosTotales;
    }

    public void setTorneosTotales(int torneosTotales) {
        this.torneosTotales = torneosTotales;
    }

    public int getTorneosGanados() {
        return torneosGanados;
    }

    public void setTorneosGanados(int torneosGanados) {
        this.torneosGanados = torneosGanados;
    }

    public int getTorneosPerdidos() {
        return torneosPerdidos;
    }

    public void setTorneosPerdidos(int torneosPerdidos) {
        this.torneosPerdidos = torneosPerdidos;
    }

    public int getPuntosGlobales() {
        return puntosGlobales;
    }

    public void setPuntosGlobales(int puntosGlobales) {
        this.puntosGlobales = puntosGlobales;
    }

    public int getAnotaciones() {
        return anotaciones;
    }

    public void setAnotaciones(int anotaciones) {
        this.anotaciones = anotaciones;
    }

    public int getAnotacionesEnContra() {
        return anotacionesEnContra;
    }

    public void setAnotacionesEnContra(int anotacionesEnContra) {
        this.anotacionesEnContra = anotacionesEnContra;
    }

    @Override
    public String toString() {
        return "TeamStats{" +
                "partidosTotales=" + partidosTotales +
                ", ganados=" + partidosGanados +
                ", perdidos=" + partidosPerdidos +
                ", empatados=" + partidosEmpatados +
                ", torneosTotales=" + torneosTotales +
                ", torneosGanados=" + torneosGanados +
                ", torneosPerdidos=" + torneosPerdidos +
                ", puntos=" + puntosGlobales +
                ", goles=" + anotaciones +
                ", golesEnContra=" + anotacionesEnContra +
                '}';
    }
}
