package cr.ac.una.tareatorneos.model;

public class BracketGenerator {
    private String teamName;
    private String logoPath;

    public BracketGenerator(String teamName, String logoPath) {
        this.teamName = teamName;
        this.logoPath = logoPath;
    }

    public String getTeamName() {
        return teamName;
    }

    public String getLogoPath() {
        return logoPath;
    }
}
