package at.wifi.notenmanager.model;

import java.time.LocalDate;

public class Notes {
    private int id;
    private LocalDate date;
    private String text;

    public Notes() {
    }

    public Notes(int id, LocalDate date, String text) {
        this.id = id;
        this.date = date;
        this.text = text;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "Datum: " + date + " | Notiz: " + text;
    }
}
