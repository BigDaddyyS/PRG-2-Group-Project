package greenpasteures.models;

import greenpasteures.enums.AnimalStatus;

public class Sheep extends Animal {

    // sheep specific attributes
    private double wool;
    private boolean isBreeder;

    // constructor
    public Sheep(String tagNumber, String breed, String gender, AnimalStatus status,
                 String dateRegistered, double wool, boolean isBreeder) {
        super(tagNumber, breed, gender, status, dateRegistered);
        this.wool = wool;
        this.isBreeder = isBreeder;
    }

    // implements the abstract method from Animal
    @Override
    public String getAnimalType() {
        return "SHEEP";
    }

    // implements the abstract method from Animal
    @Override
    public String getDetails() {
        String details = "";
        details = details + "Wool           : " + wool + " kg\n";
        details = details + "Is Breeder     : " + (isBreeder ? "Yes" : "No") + "\n";
        return details;
    }

    // getters
    public double getWool() {
        return wool;
    }

    public boolean isBreeder() {
        return isBreeder;
    }

    // setters
    public void setWool(double wool) {
        this.wool = wool;
    }

    public void setBreeder(boolean isBreeder) {
        this.isBreeder = isBreeder;
    }
}