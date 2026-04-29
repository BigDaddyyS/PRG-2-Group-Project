

public class Poultry extends Animal {

    public Poultry(String tagNumber, String breed, String gender, String status) {
        super(tagNumber, breed, gender, status);
    }

    @Override
    public String getAnimalType() {
        return "Poultry";
    }
}
