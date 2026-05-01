package greenpasteures.models;

import greenpasteures.enums.AnimalStatus;

public class Cattle extends Animal {

    // cattle specific attributes
    private String purpose;
    private double milkCollected;
    private double weight;

    // constructor
    public Cattle(String tagNumber, String breed, String gender, AnimalStatus status,
                  String dateRegistered, String purpose, double milkCollected, double weight) {
        super(tagNumber, breed, gender, status, dateRegistered);
        this.purpose = purpose;
        this.milkCollected = milkCollected;
        this.weight = weight;
    }

    // implements the abstract method from Animal
    @Override
    public String getAnimalType() {
        return "CATTLE";
    }

    // implements the abstract method from Animal
    @Override
    public String getDetails() {
        String details = "";
        details = details + "Purpose        : " + purpose + "\n";
        details = details + "Milk Collected : " + milkCollected + " L\n";
        details = details + "Weight         : " + weight + " kg\n";
        return details;
    }

    // getters
    public String getPurpose() {
        return purpose;
    }

    public double getMilkCollected() {
        return milkCollected;
    }

    public double getWeight() {
        return weight;
    }

    // setters
    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public void setMilkCollected(double milkCollected) {
        this.milkCollected = milkCollected;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }
}