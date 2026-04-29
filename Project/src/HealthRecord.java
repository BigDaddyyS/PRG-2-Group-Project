

import java.util.Date;

public class HealthRecord {
    private int recordId;
    private String treatment;
    private Date date;
    private String animalTagNumber;
    private int staffId;

    public HealthRecord(int recordId, String treatment, Date date, String animalTagNumber, int staffId) {
        this.recordId = recordId;
        this.treatment = treatment;
        this.date = date;
        this.animalTagNumber = animalTagNumber;
        this.staffId = staffId;
    }
}
