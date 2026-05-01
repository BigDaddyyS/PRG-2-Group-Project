package greenpasteures.models;

public class Staff {

    // attributes
    private int staffId;
    private String firstName;
    private String lastName;
    private String role;
    private String phoneNumber;
    private String userName;

    // constructor
    public Staff(int staffId, String firstName, String lastName,
                 String role, String phoneNumber, String userName) {
        this.staffId = staffId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
        this.phoneNumber = phoneNumber;
        this.userName = userName;
    }

    // getters
    public int getStaffId() {
        return staffId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getRole() {
        return role;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getUserName() {
        return userName;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    // setter - only role can be changed
    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return getFullName() + " (" + role + ")";
    }
}