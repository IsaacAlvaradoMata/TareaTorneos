package cr.ac.una.tareatorneos.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import cr.ac.una.tareatorneos.model.Sport;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Servicio para la gestión de deportes.
 * Se utiliza para leer y escribir la información de deportes en un archivo JSON.
 */
public class SportService {

    private final Path filePath = Paths.get("data/sports.json");
    private ObjectMapper mapper = new ObjectMapper();

    /**
     * Obtiene la lista de deportes desde el archivo JSON.
     * Si el archivo no existe o ocurre un error, se devuelve una lista vacía.
     *
     * @return Lista de deportes.
     */
    public List<Sport> getAllSports() {
        try {
            if (filePath.toFile().exists()) {
                return mapper.readValue(filePath.toFile(), new TypeReference<List<Sport>>() {});
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    /**
     * Guarda la lista de deportes en el archivo JSON.
     *
     * @param sports Lista de deportes a guardar.
     * @return true si la operación fue exitosa; false de lo contrario.
     */
    public boolean saveSports(List<Sport> sports) {
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(filePath.toFile(), sports);
            return true;
        } catch(IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Agrega un nuevo deporte a la lista y lo guarda en el archivo JSON.
     *
     * @param sport Deporte a agregar.
     * @return true si se agregó exitosamente; false de lo contrario.
     */
    public boolean addSport(Sport sport) {
        List<Sport> sports = getAllSports();
        sports.add(sport);
        return saveSports(sports);
    }

    /**
     * Actualiza un deporte existente identificándolo por su nombre.
     *
     * @param updatedSport Deporte con los datos actualizados.
     * @return true si la actualización fue exitosa; false de lo contrario.
     */
    public boolean updateSport(Sport updatedSport) {
        List<Sport> sports = getAllSports();
        for (int i = 0; i < sports.size(); i++) {
            if (sports.get(i).getNombre().equals(updatedSport.getNombre())) {
                sports.set(i, updatedSport);
                return saveSports(sports);
            }
        }
        return false;
    }

    /**
     * Elimina un deporte de la lista basándose en su nombre.
     *
     * @param sportName Nombre del deporte a eliminar.
     * @return true si la eliminación fue exitosa; false de lo contrario.
     */
    public boolean deleteSport(String sportName) {
        List<Sport> sports = getAllSports();
        sports.removeIf(s -> s.getNombre().equals(sportName));
        return saveSports(sports);
    }
}
