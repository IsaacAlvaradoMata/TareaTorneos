package cr.ac.una.tareatorneos.service;

import cr.ac.una.tareatorneos.model.TieBreaker;

public class TieBreakerService {
    private TieBreaker tieBreaker;

    public TieBreakerService(String equipoA, String equipoB) {
        tieBreaker = new TieBreaker(equipoA, equipoB);
    }

    public TieBreaker getTieBreaker() {
        return tieBreaker;
    }

    public void declararGanador(String equipo) {
        tieBreaker.setGanador(equipo);
    }

    public boolean isFinalizado() {
        return tieBreaker.isFinalizado();
    }
}
