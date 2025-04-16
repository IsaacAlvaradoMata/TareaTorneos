package cr.ac.una.tareatorneos.model;

import java.util.ArrayList;
import java.util.List;

public class TeamTournamentStats {
    private String nombreEquipo;
    private List<TournamentStat> torneos = new ArrayList<>();

    public String getNombreEquipo() {
        return nombreEquipo;
    }

    public void setNombreEquipo(String nombreEquipo) {
        this.nombreEquipo = nombreEquipo;
    }

    public List<TournamentStat> getTorneos() {
        return torneos;
    }

    public void setTorneos(List<TournamentStat> torneos) {
        this.torneos = torneos;
    }

    public static class TournamentStat {
        private String nombreTorneo;
        private List<MatchStat> partidos = new ArrayList<>();
        private String resultadoTorneo;
        private int puntos;

        public String getNombreTorneo() {
            return nombreTorneo;
        }

        public void setNombreTorneo(String nombreTorneo) {
            this.nombreTorneo = nombreTorneo;
        }

        public List<MatchStat> getPartidos() {
            return partidos;
        }

        public void setPartidos(List<MatchStat> partidos) {
            this.partidos = partidos;
        }

        public String getResultadoTorneo() {
            return resultadoTorneo;
        }

        public void setResultadoTorneo(String resultadoTorneo) {
            this.resultadoTorneo = resultadoTorneo;
        }

        public TournamentStat() {
            this.partidos = new ArrayList<>();
            this.puntos = 0;
        }

        public int getPuntos() {
            return puntos;
        }

        public void setPuntos(int puntos) {
            this.puntos = puntos;
        }

    }

    public static class MatchStat {
        private int numeroPartido;
        private String rival;
        private int anotaciones;
        private int anotacionesEnContra;
        private String resultado;
        private boolean conDesempate;
        private String resultadoReal;

        public int getNumeroPartido() {
            return numeroPartido;
        }

        public void setNumeroPartido(int numeroPartido) {
            this.numeroPartido = numeroPartido;
        }

        public String getRival() {
            return rival;
        }

        public void setRival(String rival) {
            this.rival = rival;
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

        public String getResultado() {
            return resultado;
        }

        public void setResultado(String resultado) {
            this.resultado = resultado;
        }

        public boolean isConDesempate() {
            return conDesempate;
        }

        public void setConDesempate(boolean conDesempate) {
            this.conDesempate = conDesempate;
        }

        public String getResultadoReal() {
            return resultadoReal;
        }

        public void setResultadoReal(String resultadoReal) {
            this.resultadoReal = resultadoReal;
        }

    }
}
