package greenpasteures.models;

import greenpasteures.enums.AnimalStatus;

public class Poultry extends Animal {

    // poultry specific attributes
    private String poultryType;
    private int eggsCollected;
    private String housingUnit;

    // constructor
    public Poultry(String tagNumber, String breed, String gender, AnimalStatus status,
                   String dateRegistered, String poultryType, int eggsCollected, String housingUnit) {
        super(tagNumber, breed, gender, status, dateRegistered);
        this.poultryType = poultryType;
        this.eggsCollected = eggsCollected;
        this.housingUnit = housingUnit;
    }

    // implements the abstract method from Animal
    @Override
    public String getAnimalType() {
        return "POULTRY";
    }

    // implements the abstract method from Animal
    @Override
    public String getDetails() {
        String details = "";
        details = details + "Poultry Type   : " + poultryType + "\n";
        details = details + "Eggs Collected : " + eggsCollected + "\n";
        details = details + "Housing Unit   : " + housingUnit + "\n";
        return details;
    }

    // getters
    public String getPoultryType() {
        return poultryType;
    }

    public int getEggsCollected() {
        return eggsCollected;
    }

    public String getHousingUnit() {
        return housingUnit;
    }

    // setters
    public void setPoultryType(String poultryType) {
        this.poultryType = poultryType;
    }

    public void setEggsCollected(int eggsCollected) {
        this.eggsCollected = eggsCollected;
    }

    public void setHousingUnit(String housingUnit) {
        this.housingUnit = housingUnit;
    }
}