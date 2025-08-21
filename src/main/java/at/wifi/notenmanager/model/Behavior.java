package at.wifi.notenmanager.model;

import java.time.LocalDate;

public class Behavior {
    private int id;
    private int studentId;
    private int subjectId;
    private int rating;
    private String comment;
    private LocalDate date;

    public Behavior() {
    }

    public Behavior(int id, int studentId, int subjectId, int rating, String comment, LocalDate date) {
        this.id = id;
        this.studentId = studentId;
        this.subjectId = subjectId;
        this.rating = rating;
        this.comment = comment;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public int getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(int subjectId) {
        this.subjectId = subjectId;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "Verhalten: " +
                "Bewertung:" + rating +
                ", Kommentar: '" + comment + '\'' +
                ", Datum:" + date;
    }
}
