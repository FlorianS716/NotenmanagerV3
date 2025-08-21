package at.wifi.notenmanager.controller;

import at.wifi.notenmanager.dao.BehaviorDAO;
import at.wifi.notenmanager.model.*;
import at.wifi.notenmanager.service.*;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.SQLException;
import java.time.LocalDate;

public class BehaviorController {

    @FXML private ComboBox<Students> studentComboBox;
    @FXML private ComboBox<Subject> subjectComboBox;
    @FXML private DatePicker datePicker;
    @FXML private ComboBox<Integer> ratingComboBox;
    @FXML private TextArea commentArea;

    @FXML private TableView<Behavior> behaviorTable;
    @FXML private TableColumn<Behavior, String> dateColumn;
    @FXML private TableColumn<Behavior, Integer> ratingColumn;
//    @FXML private TableColumn<Behavior, String> studentColumn;
//    @FXML private TableColumn<Behavior, String> subjectColumn;
    @FXML private TableColumn<Behavior, String> commentColumn;

    @FXML private Label averageLabel;

    private final BehaviorService behaviorService = new BehaviorServiceImpl(new BehaviorDAO());
    private final StudentsServiceImpl studentsService = new StudentsServiceImpl();
    private final SubjectService subjectService = new SubjectServiceImpl();

    @FXML
    public void initialize() throws SQLException {

        studentComboBox.setItems(FXCollections.observableArrayList(studentsService.getAllStudents()));
        subjectComboBox.setItems(FXCollections.observableArrayList(subjectService.findAll()));
        ratingComboBox.setItems(FXCollections.observableArrayList(1, 2, 3, 4, 5));
        datePicker.setValue(java.time.LocalDate.now());
        loadBehaviorEntries();


        dateColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getDate().toString()));
        ratingColumn.setCellValueFactory(cell -> new SimpleIntegerProperty(cell.getValue().getRating()).asObject());
        commentColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getComment()));
    }

    @FXML
    public void onFilterChanged(ActionEvent event) throws SQLException {
        loadBehaviorEntries();
    }

    @FXML
    public void onSave(ActionEvent event) throws SQLException {
        Students selectedStudent = studentComboBox.getValue();
        Subject selectedSubject = subjectComboBox.getValue();
        LocalDate selectedDate = datePicker.getValue();
        Integer selectedRating = ratingComboBox.getValue();
        String comment = commentArea.getText();

        if (selectedStudent == null || selectedSubject == null || selectedDate == null || selectedRating == null) {
            showAlert("Bitte alle Felder ausfüllen.");
            return;
        }

        Behavior behavior = new Behavior();
        behavior.setStudentId(selectedStudent.getId());
        behavior.setSubjectId(selectedSubject.getId());
        behavior.setDate(selectedDate);
        behavior.setRating(selectedRating);
        behavior.setComment(comment);

        behaviorService.createBehavior(behavior);

        clearForm();
        loadBehaviorEntries();
    }

    @FXML
    public void onDelete(ActionEvent event) throws SQLException {
        Behavior selected = behaviorTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            behaviorService.deleteBehavior(selected.getId());
            loadBehaviorEntries();
        } else {
            showAlert("Bitte wähle einen Eintrag zum Löschen aus.");
        }
    }

    private void loadBehaviorEntries() throws SQLException {
        Students selectedStudent = studentComboBox.getValue();
        Subject selectedSubject = subjectComboBox.getValue();

        if (selectedStudent != null && selectedSubject != null) {
            behaviorTable.setItems(FXCollections.observableArrayList(
                    behaviorService.getBehaviorByStudentAndSubject(
                            selectedStudent.getId(), selectedSubject.getId()
                    )
            ));
        }
    }

    private void clearForm() {
        datePicker.setValue(LocalDate.now());
        ratingComboBox.getSelectionModel().clearSelection();
        commentArea.clear();
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warnung");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void onEdit(ActionEvent actionEvent) throws SQLException {
        Behavior selected = behaviorTable.getSelectionModel().getSelectedItem();
        if (selected == null){
            showAlert("Bitte wähle einen Eintrag zum Bearbeiten aus.");
            return;
        }

        Students selectedStudent = studentComboBox.getValue();
        Subject selectedSubject = subjectComboBox.getValue();
        LocalDate selectedDate = datePicker.getValue();
        Integer selectedRating = ratingComboBox.getValue();
        String selectedComment = commentArea.getText();

        if (selectedStudent == null || selectedSubject == null || selectedDate == null || selectedRating == null){
            showAlert("Bitte alle Felder korrekt ausfüllen.");
            return;
        }

        selected.setStudentId(selectedStudent.getId());
        selected.setSubjectId(selectedSubject.getId());
        selected.setDate(selectedDate);
        selected.setRating(selectedRating);
        selected.setComment(selectedComment);

        behaviorService.updateBehavior(selected);
        clearForm();
        loadBehaviorEntries();
        behaviorTable.refresh();

    }
}
