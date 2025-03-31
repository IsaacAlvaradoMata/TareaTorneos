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
        // Inicializa todo a 0 por defecto (opcional, Java ya lo hace)
    }

    // 📈 Métodos de actualización interna (NO públicos para usuario)
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

    // ✅ Getters y Setters necesarios para serialización JSON

    public int getPartidosTotales() {
        return partidosTotales;
    }

    public int getPartidosGanados() {
        return partidosGanados;
    }

    public int getPartidosPerdidos() {
        return partidosPerdidos;
    }

    public int getPartidosEmpatados() {
        return partidosEmpatados;
    }

    public int getTorneosTotales() {
        return torneosTotales;
    }

    public int getTorneosGanados() {
        return torneosGanados;
    }

    public int getTorneosPerdidos() {
        return torneosPerdidos;
    }

    public int getPuntosGlobales() {
        return puntosGlobales;
    }

    public int getAnotaciones() {
        return anotaciones;
    }

    public int getAnotacionesEnContra() {
        return anotacionesEnContra;
    }

    // 🔄 Setters solo si necesitas cargar desde JSON
    public void setPartidosTotales(int partidosTotales) {
        this.partidosTotales = partidosTotales;
    }

    public void setPartidosGanados(int partidosGanados) {
        this.partidosGanados = partidosGanados;
    }

    public void setPartidosPerdidos(int partidosPerdidos) {
        this.partidosPerdidos = partidosPerdidos;
    }

    public void setPartidosEmpatados(int partidosEmpatados) {
        this.partidosEmpatados = partidosEmpatados;
    }

    public void setTorneosTotales(int torneosTotales) {
        this.torneosTotales = torneosTotales;
    }

    public void setTorneosGanados(int torneosGanados) {
        this.torneosGanados = torneosGanados;
    }

    public void setTorneosPerdidos(int torneosPerdidos) {
        this.torneosPerdidos = torneosPerdidos;
    }

    public void setPuntosGlobales(int puntosGlobales) {
        this.puntosGlobales = puntosGlobales;
    }

    public void setAnotaciones(int anotaciones) {
        this.anotaciones = anotaciones;
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
