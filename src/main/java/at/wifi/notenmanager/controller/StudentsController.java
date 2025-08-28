package at.wifi.notenmanager.controller;

import at.wifi.notenmanager.model.Students;
import at.wifi.notenmanager.service.StudentsService;
import at.wifi.notenmanager.service.StudentsServiceImpl;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.SQLException;
import java.util.List;

public class StudentsController {

    public TableView<Students> studentsTable;
    public TableColumn<Students, Number> idColumn;
    public TableColumn<Students, String> lastNameColumn;
    public TableColumn<Students, String> firstNameColumn;
    public TableColumn<Students, String> classIdColumn;
    public TableColumn<Students, String> dobColumn;
    public TableColumn<Students, String> parentNameColumn;
    public TableColumn<Students, String > strengthsColumn;
    public TableColumn<Students, String> weaknessesColumn;
    public TableColumn<Students, String> healthInfoColumn;
    public TableColumn<Students, String> phoneNumberColumn;
    public TextField firstNameField;
    public TextField lastNameField;
    public TextField classIdField;
    public DatePicker dobPicker;
    public TextField parentNameField;
    public TextField strengthsField;
    public TextField weaknessesField;
    public TextField healthInfoField;
    public TextField phoneNumberField;
    public Label statusLabel;
    public Button saveButton;
    public Button deleteButton;
    public Button reloadButton;

    private final StudentsService studentsService = new StudentsServiceImpl();
    private Students selectedForEdit;


    @FXML
    public void initialize() {

        idColumn.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getId()));
        firstNameColumn.setCellValueFactory(c -> new SimpleStringProperty((c.getValue().getFirstName())));
        lastNameColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getLastName()));
        classIdColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getClassId()));
        dobColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDob()));
        parentNameColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getParentName()));
        strengthsColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStrengths()));
        weaknessesColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getWeaknesses()));
        healthInfoColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getHealthInfo()));
        phoneNumberColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getPhoneNumber()));
        dobPicker.setValue(null);
        refreshTable();
    }

    private void fillFormFrom(Students s) {
        if (s == null) return;

        firstNameField.setText(s.getFirstName());
        lastNameField.setText(s.getLastName());
        classIdField.setText(s.getClassId());
        parentNameField.setText(s.getParentName());
        strengthsField.setText(s.getStrengths());
        weaknessesField.setText(s.getWeaknesses());
        healthInfoField.setText(s.getHealthInfo());
        phoneNumberField.setText(s.getPhoneNumber());
    }



    public void onSave(ActionEvent actionEvent) throws SQLException {

        String firstName = firstNameField.getText() != null ? firstNameField.getText().trim() : "";
        String lastName = lastNameField.getText() != null ? lastNameField.getText().trim() : "";
        String classId = classIdField.getText() != null ? classIdField.getText().trim() : "";
        String parentName = parentNameField.getText();
        String strengths = strengthsField.getText();
        String weaknesses = weaknessesField.getText();
        String healthInfo = healthInfoField.getText();
        String phoneNumber = phoneNumberField.getText();
        String dob = (dobPicker.getValue() == null) ? null : dobPicker.getValue().toString();

        if (firstName.isEmpty() || lastName.isEmpty()) {
            statusLabel.setText("Vor- und Nachname sind Pflichtfelder.");
            return;
        }

        try {
            Students s = new Students();
            if (selectedForEdit != null) {
                s.setId(selectedForEdit.getId());   // -> Update
            }
            s.setFirstName(firstName);
            s.setLastName(lastName);
            s.setClassId(classId);
            s.setDob(dob);
            s.setParentName(parentName);
            s.setStrengths(strengths);
            s.setWeaknesses(weaknesses);
            s.setHealthInfo(healthInfo);
            s.setPhoneNumber(phoneNumber);

            if (selectedForEdit == null) {
                studentsService.createStudents(s);     // ggf. Methode an dein Service anpassen
                statusLabel.setText("Schüler angelegt.");
            } else {
                studentsService.updateStudent(s);     // ggf. Methode an dein Service anpassen
                statusLabel.setText("Schüler aktualisiert.");
            }

            clearForm();
            selectedForEdit = null;
            studentsTable.getSelectionModel().clearSelection();
            refreshTable();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void onDelete(ActionEvent actionEvent) {
        Students students = studentsTable.getSelectionModel().getSelectedItem();
        if (students == null) {
            setStatus("Bitte zuerst einen Schüler in der Tabelle auswählen.");
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Schüler wirklich löschen?");
        confirm.setHeaderText(null);
        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.OK) {
                try {
                    studentsService.deleteById(students.getId());
                    setStatus("Schüler gelöscht.");
                    clearForm();
                    refreshTable();
                } catch (SQLException e) {
                    setStatus("Löschen fehlgeschlagen: " + e.getMessage());
                }
            }
        });
    }

    public void onReload(ActionEvent actionEvent) {

        Students student = studentsTable.getSelectionModel().getSelectedItem();
        if (student == null){
            setStatus("Bitte zuerst einen Schüler aus der Tabelle auswählen.");
            return;
        }
        selectedForEdit = student;
        fillFormFrom(student);
        setStatus("Daten von: " + student.getFirstName() + " " + student.getLastName() + " bearbeiten....");
    }

    private void refreshTable() {
        try {
            List<Students> list = studentsService.getAllStudents();
            studentsTable.setItems(FXCollections.observableArrayList(list));
        } catch (SQLException e) {
            setStatus("Fehler beim Laden: " + e.getMessage());
        }
    }

    private void setStatus(String msg) {
        if (statusLabel != null) statusLabel.setText(msg);
    }

    private void clearForm() {
        studentsTable.getSelectionModel().clearSelection();
        selectedForEdit = null;
        firstNameField.clear();
        lastNameField.clear();
        classIdField.clear();
        if (dobPicker != null) dobPicker.setValue(null);
        parentNameField.clear();
        strengthsField.clear();
        weaknessesField.clear();
        healthInfoField.clear();
        phoneNumberField.clear();
    }

}
