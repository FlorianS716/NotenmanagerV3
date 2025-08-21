package at.wifi.notenmanager.controller;

import at.wifi.notenmanager.model.Notes;
import at.wifi.notenmanager.service.NotesService;
import at.wifi.notenmanager.service.NotesServiceImpl;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class NotesController {

    @FXML public ListView<Notes> notesListView;
    @FXML public TextArea noteTextArea;
    @FXML public Button saveButton;
    @FXML public Button deleteButton;

    private final NotesService notesService = new NotesServiceImpl();
    private Notes selectedNote;


    @FXML
    public void initialize() {
        loadNotes();

        notesListView.getSelectionModel().selectedItemProperty().addListener((obs, oldNote, newNote) -> {
            selectedNote = newNote;
            if (newNote != null) {
                noteTextArea.setText(newNote.getText());
            } else {
                noteTextArea.clear();
            }
        });
    }


    private void loadNotes() {
        try {
            List<Notes> notes = notesService.findAll();
            notesListView.getItems().setAll(notes);
        } catch (SQLException e) {
            showError("Fehler beim Laden der Notizen: " + e.getMessage());
        }
    }


    public void handleSaveNote(ActionEvent actionEvent) {
        String text = noteTextArea.getText().trim();
        if (text.isEmpty()) {
            showError("Bitte eine Notiz eingeben.");
            return;
        }

        try {
            if (selectedNote == null) {
                Notes newNote = new Notes();
                newNote.setDate(LocalDate.from(LocalDateTime.now()));
                newNote.setText(text);
                notesService.createNote(newNote);
            } else {
                selectedNote.setText(text);
                notesService.updateNote(selectedNote);
            }

            noteTextArea.clear();
            selectedNote = null;
            loadNotes();
        } catch (SQLException e) {
            showError("Fehler beim Speichern: " + e.getMessage());
        }
    }

    public void handleDeleteNote(ActionEvent actionEvent) {
        if (selectedNote == null) {
            showError("Bitte zuerst eine Notiz auswählen.");
            return;
        }

        try {
            notesService.deleteNoteById(selectedNote.getId());
            selectedNote = null;
            noteTextArea.clear();
            loadNotes();
        } catch (SQLException e) {
            showError("Fehler beim Löschen: " + e.getMessage());
        }
    }


    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Fehler");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
