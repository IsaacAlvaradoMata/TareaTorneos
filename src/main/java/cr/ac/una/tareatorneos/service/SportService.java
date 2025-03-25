package cr.ac.una.tareatorneos.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import cr.ac.una.tareatorneos.model.Sport;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class SportService {

    private final Path filePath = Paths.get("data/sports.json");
    private ObjectMapper mapper;

    public SportService() {
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
    }

    public List<Sport> getAllSports() {
        try {
            if (filePath.toFile().exists()) {
                List<Sport> sports = mapper.readValue(filePath.toFile(), new TypeReference<List<Sport>>() {
                });
                for (Sport sport : sports) {
                    if (sport.getFechaCreacion() == null) {
                        sport.setFechaCreacion(LocalDate.now());
                    }
                    if (sport.getBallImage() != null && !sport.getBallImage().isEmpty()) {
                        sport.setBallImage(sport.getBallImage());
                    }
                }
                return sports;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public boolean saveSports(List<Sport> sports) {
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(filePath.toFile(), sports);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean addSport(Sport sport) {
        List<Sport> sports = getAllSports();
        sport.setFechaCreacion(LocalDate.now());
        sports.add(sport);
        return saveSports(sports);
    }

    public boolean updateSport(String oldNombre, Sport updatedSport) {
        List<Sport> sports = getAllSports();
        boolean modified = false;
        for (int i = 0; i < sports.size(); i++) {
            if (sports.get(i).getNombre().equals(oldNombre)) {
                updatedSport.setFechaCreacion(sports.get(i).getFechaCreacion());
                sports.set(i, updatedSport);
                modified = true;
                break;
            }
        }
        if (modified) {
            return saveSports(sports);
        }
        return false;
    }

    public boolean deleteSport(String sportName) {
        List<Sport> sports = getAllSports();
        Sport sportToRemove = null;

        for (Sport sport : sports) {
            if (sport.getNombre().equals(sportName)) {
                sportToRemove = sport;
                break;
            }
        }

        if (sportToRemove != null) {
            // 📌 Eliminar el archivo de imagen asociado
            String imageFileName = sportToRemove.getBallImage();
            File imageFile = new File(System.getProperty("user.dir") + "/sportsPhotos/" + imageFileName);

            if (imageFile.exists()) {
                if (imageFile.delete()) {
                    System.out.println("✅ Imagen eliminada: " + imageFileName);
                } else {
                    System.out.println("⚠️ No se pudo eliminar la imagen: " + imageFileName);
                }
            } else {
                System.out.println("ℹ️ La imagen no existe en la carpeta.");
            }

            // 📌 Eliminar el deporte del JSON
            boolean removed = sports.remove(sportToRemove);
            if (removed) {
                return saveSports(sports);
            }
        }

        return false;
    }

    public Sport getSportByName(String nombre) {
        return getAllSports().stream()
                .filter(sport -> sport.getNombre().equalsIgnoreCase(nombre))
                .findFirst()
                .orElse(null);
    }

}
