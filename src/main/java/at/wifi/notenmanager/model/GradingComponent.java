package at.wifi.notenmanager.model;

public class GradingComponent {
    private int id;
    private int subjectId;
    private String name;
    private double weight;

    public GradingComponent() {
    }

    public GradingComponent(int id, int subjectId, String name, double weight) {
        this.id = id;
        this.subjectId = subjectId;
        this.name = name;
        this.weight = weight;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(int subjectId) {
        this.subjectId = subjectId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }
}
