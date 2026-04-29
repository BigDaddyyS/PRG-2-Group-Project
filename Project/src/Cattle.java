
public class Cattle {
   

public abstract class Animal {
    private String tagNumber;
    private String breed;
    private String gender;
    private String status;

    public Animal(String tagNumber, String breed, String gender, String status) {
        this.tagNumber = tagNumber;
        this.breed = breed;
        this.gender = gender;
        this.status = status;
    }

    public String getTagNumber() { return tagNumber; }
    public String getBreed() { return breed; }
    public String getGender() { return gender; }
    public String getStatus() { return status; }

    public abstract String getAnimalType();
}
}
