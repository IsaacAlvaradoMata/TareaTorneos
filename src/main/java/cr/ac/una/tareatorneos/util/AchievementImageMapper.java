package cr.ac.una.tareatorneos.util;

import java.util.HashMap;
import java.util.Map;

public class AchievementImageMapper {

    private static final Map<String, String> logroToImageMap = new HashMap<>();

    static {
        logroToImageMap.put("Dominador Supremo", "8TournamentsWinnerIcon.png");
        logroToImageMap.put("Leyenda Plateada", "6TournamentsWinnerIcon.png");
        logroToImageMap.put("Tricampeón", "3TournamentsWinnerIcon.png");
        logroToImageMap.put("Máxima Potencia", "20GoalsIcon.png");
        logroToImageMap.put("Muralla Imbatible", "DefenseIcon.png");
        logroToImageMap.put("Equilibrio Perfecto", "TieIcon.png");
        logroToImageMap.put("Imparable", "10GamesConsecutiveIcon.png");
        logroToImageMap.put("Regreso Triunfal", "WinAfterLosing.png");
        logroToImageMap.put("Campeón Inaugural", "TournamentWinnerIcon.png");
    }

    public static String getRutaImagen(String nombreLogro) {
        String fileName = logroToImageMap.get(nombreLogro);
        if (fileName == null) {
            System.err.println("⛔ No hay imagen registrada para el logro: " + nombreLogro);
            return null;
        }
        String ruta = "/cr/ac/una/tareatorneos/resources/" + fileName;
        System.out.println("🔍 Ruta mapeada para logro '" + nombreLogro + "': " + ruta);
        return ruta;
    }
}
