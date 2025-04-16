package cr.ac.una.tareatorneos.util;

import cr.ac.una.tareatorneos.model.Achievement;

import java.util.ArrayList;
import java.util.List;

public class AchievementUtils {

    public static List<Achievement> generarLogrosIniciales() {
        List<Achievement> logros = new ArrayList<>();
        logros.add(new Achievement("Dominador Supremo", false));
        logros.add(new Achievement("Leyenda Plateada", false));
        logros.add(new Achievement("Tricampeón", false));
        logros.add(new Achievement("Máxima Potencia", false));
        logros.add(new Achievement("Muralla Imbatible", false));
        logros.add(new Achievement("Equilibrio Perfecto", false));
        logros.add(new Achievement("Imparable", false));
        logros.add(new Achievement("Regreso Triunfal", false));
        logros.add(new Achievement("Campeón Inaugural", false));
        return logros;
    }

    public static List<Achievement> filtrarNuevosLogros(List<Achievement> antes, List<Achievement> despues) {
        List<Achievement> nuevos = new ArrayList<>();
        for (int i = 0; i < despues.size(); i++) {
            Achievement logroAntes = antes.get(i);
            Achievement logroDespues = despues.get(i);
            if (!logroAntes.isObtenido() && logroDespues.isObtenido()) {
                nuevos.add(logroDespues);
            }
        }
        return nuevos;
    }

}
