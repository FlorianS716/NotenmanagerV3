package at.wifi.notenmanager.service;

import at.wifi.notenmanager.model.Notes;

import java.sql.SQLException;
import java.util.List;

public interface NotesService {

    void createNote(Notes note) throws SQLException;
    boolean deleteNoteById(int id) throws SQLException;
    void updateNote(Notes note) throws SQLException;
    List<Notes> findAll() throws SQLException;
}
