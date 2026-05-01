package greenpasteures.models;
 
import greenpasteures.enums.AnimalStatus;
 
public abstract class Animal implements Reportable {
 
    // attributes
    private String tagNumber;
    private String breed;
    private String gender;
    private AnimalStatus status;
    private String dateRegistered;
 
    // constructor
    public Animal(String tagNumber, String breed, String gender, AnimalStatus status, String dateRegistered) {
        this.tagNumber = tagNumber;
        this.breed = breed;
        this.gender = gender;
        this.status = status;
        this.dateRegistered = dateRegistered;
    }
 
    // abstract methods that every subclass must implement
    public abstract String getAnimalType();
    public abstract String getDetails();
 
    // generateReport from the Reportable interface
    @Override
    public String generateReport() {
        String report = "";
        report = report + "Tag Number     : " + tagNumber + "\n";
        report = report + "Animal Type    : " + getAnimalType() + "\n";
        report = report + "Breed          : " + breed + "\n";
        report = report + "Gender         : " + gender + "\n";
        report = report + "Status         : " + status + "\n";
        report = report + "Date Registered: " + dateRegistered + "\n";
        report = report + getDetails();
        return report;
    }
 
    // getters
    public String getTagNumber() {
        return tagNumber;
    }
 
    public String getBreed() {
        return breed;
    }
 
    public String getGender() {
        return gender;
    }
 
    public AnimalStatus getStatus() {
        return status;
    }
 
    public String getDateRegistered() {
        return dateRegistered;
    }
 
    // setters
    public void setBreed(String breed) {
        this.breed = breed;
    }
 
    public void setGender(String gender) {
        this.gender = gender;
    }
 
    public void setStatus(AnimalStatus status) {
        this.status = status;
    }
 
    public void setDateRegistered(String dateRegistered) {
        this.dateRegistered = dateRegistered;
    }
 
    @Override
    public String toString() {
        return getAnimalType() + " [" + tagNumber + "] - " + breed + " - " + status;
    }
}
