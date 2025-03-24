package cr.ac.una.tareatorneos.model;

import java.util.List;

public class Tournament {
    private String nombre;
    private String deporte;
    private int tiempoPorPartido;
    private int cantidadEquipos;
    private List<String> equiposParticipantes;
    private String estado;

    public Tournament() {
    }

    public Tournament(String nombre, String deporte, int tiempoPorPartido,
                      int cantidadEquipos, List<String> equiposParticipantes, String estado) {
        this.nombre = nombre;
        this.deporte = deporte;
        this.tiempoPorPartido = tiempoPorPartido;
        this.cantidadEquipos = cantidadEquipos;
        this.equiposParticipantes = equiposParticipantes;
        this.estado = estado;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDeporte() {
        return deporte;
    }

    public void setDeporte(String deporte) {
        this.deporte = deporte;
    }

    public int getTiempoPorPartido() {
        return tiempoPorPartido;
    }

    public void setTiempoPorPartido(int tiempoPorPartido) {
        this.tiempoPorPartido = tiempoPorPartido;
    }

    public int getCantidadEquipos() {
        return cantidadEquipos;
    }

    public void setCantidadEquipos(int cantidadEquipos) {
        this.cantidadEquipos = cantidadEquipos;
    }

    public List<String> getEquiposParticipantes() {
        return equiposParticipantes;
    }

    public void setEquiposParticipantes(List<String> equiposParticipantes) {
        this.equiposParticipantes = equiposParticipantes;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return "Tournament{" +
                "nombre='" + nombre + '\'' +
                ", deporte='" + deporte + '\'' +
                ", tiempoPorPartido=" + tiempoPorPartido +
                ", cantidadEquipos=" + cantidadEquipos +
                ", equiposParticipantes=" + equiposParticipantes +
                ", estado='" + estado + '\'' +
                '}';
    }
}
