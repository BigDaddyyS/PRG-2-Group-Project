package greenpasteures.models;

public class HealthRecord {

    // attributes
    private int recordId;
    private String animalTagNumber;
    private String treatmentDate;
    private String diagnosis;
    private String treatment;
    private String medicineUsed;
    private int attendedBy;

    // constructor
    public HealthRecord(int recordId, String animalTagNumber, String treatmentDate,
                        String diagnosis, String treatment, String medicineUsed, int attendedBy) {
        this.recordId = recordId;
        this.animalTagNumber = animalTagNumber;
        this.treatmentDate = treatmentDate;
        this.diagnosis = diagnosis;
        this.treatment = treatment;
        this.medicineUsed = medicineUsed;
        this.attendedBy = attendedBy;
    }

    // getters
    public int getRecordId() {
        return recordId;
    }

    public String getAnimalTagNumber() {
        return animalTagNumber;
    }

    public String getTreatmentDate() {
        return treatmentDate;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public String getTreatment() {
        return treatment;
    }

    public String getMedicineUsed() {
        return medicineUsed;
    }

    public int getAttendedBy() {
        return attendedBy;
    }

    // get a short summary of this record
    public String getSummary() {
        return "Record " + recordId + " | Tag: " + animalTagNumber +
               " | " + diagnosis + " | " + treatmentDate;
    }
}