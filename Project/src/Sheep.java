
public class Sheep extends Animal {

    public Sheep(String tagNumber, String breed, String gender, String status) {
        super(tagNumber, breed, gender, status);
    }

    @Override
    public String getAnimalType() {
        return "Sheep";
    }
}

