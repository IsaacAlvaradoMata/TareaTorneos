package cr.ac.una.tareatorneos.util;

import java.io.File;

public class DirectoryCreator {

    public static void createRequiredDirectories() {
        String basePath = System.getProperty("user.dir");

        String[] folders = {
                basePath + "/teamsPhotos",
                basePath + "/sportsPhotos",
                basePath + "/data",
                basePath + "/certificados"
        };

        for (String folder : folders) {
            File dir = new File(folder);
            if (!dir.exists()) {
                dir.mkdirs(); // Crear la carpeta
                System.out.println("Carpeta creada: " + folder);
            }
        }
    }
}
