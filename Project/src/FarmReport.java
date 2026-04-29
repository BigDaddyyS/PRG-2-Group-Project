

public class FarmReport {
    private int totalAnimals;
    private int dead;
    private int lost;
    private int stolen;

    public FarmReport(int totalAnimals, int dead, int lost, int stolen) {
        this.totalAnimals = totalAnimals;
        this.dead = dead;
        this.lost = lost;
        this.stolen = stolen;
    }
}