package at.wifi.notenmanager.controller;

import at.wifi.notenmanager.dao.GradesDAO;
import at.wifi.notenmanager.dao.SubjectDAO;
import at.wifi.notenmanager.model.Grades;
import at.wifi.notenmanager.model.GradingComponent;
import at.wifi.notenmanager.model.Students;
import at.wifi.notenmanager.model.Subject;
import at.wifi.notenmanager.service.GradesService;
import at.wifi.notenmanager.service.GradesServiceImpl;
import at.wifi.notenmanager.service.GradingComponentServiceImpl;
import at.wifi.notenmanager.service.StudentsServiceImpl;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.StringConverter;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class GradesController {
    @FXML public ComboBox<Students> studentComboBox;
    @FXML public ComboBox<Subject> subjectComboBox;
    @FXML  public ComboBox<GradingComponent> componentComboBox;
    @FXML  public Spinner<Integer> noteSpinner;
    @FXML  public DatePicker gradeDatePicker;
    @FXML  public Label statusLabel;
    @FXML  public Label avgLabel;
    @FXML  public TableView<Grades> gradeTable;
    @FXML  public TableColumn<Grades, String> componentCol;
    @FXML  public TableColumn<Grades, String> dateCol;
    @FXML  public TableColumn<Grades, String> noteCol;


    private final SubjectDAO subjectDAO = new SubjectDAO();
    private final StudentsServiceImpl studentService = new StudentsServiceImpl();
    private final GradingComponentServiceImpl componentService = new GradingComponentServiceImpl();
    private final GradesService gradesService = new GradesServiceImpl(new GradesDAO());
    private Grades selectedForEdit;



    @FXML
    public void initialize() throws SQLException {
        try {
            studentComboBox.setItems(FXCollections.observableArrayList(studentService.getAllStudents()));
            subjectComboBox.setItems(FXCollections.observableArrayList(subjectDAO.findAll()));
            noteSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1,5,3));
            gradeDatePicker.setValue(LocalDate.now());

            dateCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDate().toString()));
            noteCol.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getRating())));
            componentCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getType()));

            subjectComboBox.setOnAction(e -> {
                loadComponents();
                refreshGrades();
                try {
                    updateAverage();
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            });
            studentComboBox.setOnAction(e -> {
                refreshGrades();
                try {
                    updateAverage();
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            });
            gradeTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
                selectedForEdit = newVal;
                if (newVal != null) {
                    componentComboBox.getSelectionModel().select(getComponentByName(newVal.getType()));
                    noteSpinner.getValueFactory().setValue(newVal.getRating());
                    gradeDatePicker.setValue(newVal.getDate());
                }
            });

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void updateAverage() throws SQLException {
        Students student = studentComboBox.getValue();
        Subject subject = subjectComboBox.getValue();
        if (student == null || subject == null) {
            avgLabel.setText("");
            return;
        }

        double avg = gradesService.getAverageGradeByType(student.getId(), subject.getId());
        avgLabel.setText(Float.isNaN((float) avg) ? "Durchschnitt: –" : String.format("Durchschnitt: %.2f", avg));

    }
    private void refreshGrades() {
        Students student = studentComboBox.getValue();
        Subject subject = subjectComboBox.getValue();
        if (student == null || subject == null) return;

        try {
            List<Grades> all = gradesService.getGradesByStudentId(student.getId());
            List<Grades> filtered = all.stream()
                    .filter(g -> g.getSubjectId() == subject.getId())
                    .collect(Collectors.toList());

            gradeTable.setItems(FXCollections.observableArrayList(filtered));
        } catch (SQLException e) {
            showError("Fehler beim Laden der Noten.");
        }
    }
    private void loadComponents() {
        Subject subject = subjectComboBox.getValue();
        if (subject == null) return;

        try {
            List<GradingComponent> comps = componentService.findBySubjectId(subject.getId());
            componentComboBox.setItems(FXCollections.observableArrayList(comps));
        } catch (SQLException e) {
            showError("Komponenten konnten nicht geladen werden.");
        }

        componentComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(GradingComponent comp) {
                return comp != null ? comp.getName() : "";
            }

            @Override
            public GradingComponent fromString(String string) {
                return componentComboBox.getItems().stream()
                        .filter(c -> c.getName().equals(string))
                        .findFirst().orElse(null);
            }
        });

    }

    private GradingComponent getComponentByName(String name) {
        return componentComboBox.getItems().stream()
                .filter(c -> c.getName().equalsIgnoreCase(name))
                .findFirst().orElse(null);
    }

    public void handleSave(ActionEvent actionEvent) {
        Students student = studentComboBox.getValue();
        Subject subject = subjectComboBox.getValue();
        GradingComponent component = componentComboBox.getValue();
        LocalDate date = gradeDatePicker.getValue();
        int note = noteSpinner.getValue();

        if (student == null || subject == null || component == null || date == null) {
            showError("Bitte alle Felder ausfüllen.");
            return;
        }

        try {
            if (selectedForEdit == null) {
                Grades grade = new Grades();
                grade.setStudentId(student.getId());
                grade.setSubjectId(subject.getId());
                grade.setRating(note);
                grade.setType(component.getName());
                grade.setDate(date);
                gradesService.createGrade(grade);
                showInfo("Note gespeichert.");
            } else {
                selectedForEdit.setStudentId(student.getId());
                selectedForEdit.setSubjectId(subject.getId());
                selectedForEdit.setRating(note);
                selectedForEdit.setType(component.getName());
                selectedForEdit.setDate(date);
                gradesService.updateGrade(selectedForEdit);
                showInfo("Note aktualisiert.");
            }

            clearForm();
            refreshGrades();
            updateAverage();
        } catch (SQLException e) {
            showError("Fehler beim Speichern: " + e.getMessage());
        }

    }
    public void handleDelete(ActionEvent actionEvent) {
        Grades grades = gradeTable.getSelectionModel().getSelectedItem();
        if (grades == null) {
            showError("Bitte zuerst eine Note auswählen.");
            return;
        }

        try {
            if (gradesService.deleteGrade(grades.getId())) {
                showInfo("Note gelöscht.");
                clearForm();
                refreshGrades();
                updateAverage();
            } else {
                showError("Löschen fehlgeschlagen.");
            }
        } catch (SQLException e) {
            showError("Fehler beim Löschen.");
        }
    }

    private void showError(String msg) {
        if (statusLabel != null) statusLabel.setText("Fehler: " + msg);
    }
    private void showInfo(String msg) {
        if (statusLabel != null) statusLabel.setText(msg);
    }
    private void clearForm() {
        selectedForEdit = null;
        gradeTable.getSelectionModel().clearSelection();
        componentComboBox.getSelectionModel().clearSelection();
        noteSpinner.getValueFactory().setValue(3);
        gradeDatePicker.setValue(LocalDate.now());
    }



}
