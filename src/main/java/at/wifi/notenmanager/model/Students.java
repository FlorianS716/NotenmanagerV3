package at.wifi.notenmanager.model;

public class Students {
    private int id;
    private String classId;
    private String firstName;
    private String lastName;
    private String dob;
    private String parentName;
    private String strengths;
    private String weaknesses;
    private String healthInfo;
    private String phoneNumber;

    public Students() {
    }

    public Students(int id, String classId, String firstName, String lastName, String dob, String parentName, String strengths, String weaknesses, String healthInfo, String phoneNumber) {
        this.id = id;
        this.classId = classId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dob = dob;
        this.parentName = parentName;
        this.strengths = strengths;
        this.weaknesses = weaknesses;
        this.healthInfo = healthInfo;
        this.phoneNumber = phoneNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public String getStrengths() {
        return strengths;
    }

    public void setStrengths(String strengths) {
        this.strengths = strengths;
    }

    public String getWeaknesses() {
        return weaknesses;
    }

    public void setWeaknesses(String weaknesses) {
        this.weaknesses = weaknesses;
    }

    public String getHealthInfo() {
        return healthInfo;
    }

    public void setHealthInfo(String healthInfo) {
        this.healthInfo = healthInfo;
    }

    @Override
    public String toString() {
        return firstName  + " "+ lastName;
    }
}

