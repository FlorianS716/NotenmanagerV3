package at.wifi.notenmanager.service;

import at.wifi.notenmanager.dao.NotesDAO;
import at.wifi.notenmanager.model.Notes;

import java.sql.SQLException;
import java.util.List;

public class NotesServiceImpl  implements NotesService{

    private final  NotesDAO notesDAO;

    public NotesServiceImpl() {
        this.notesDAO = new NotesDAO();
    }


    @Override
    public void createNote(Notes note) throws SQLException {
        notesDAO.createNote(note);
    }

    @Override
    public boolean deleteNoteById(int id) throws SQLException {
        return notesDAO.deleteNoteById(id);
    }

    @Override
    public void updateNote(Notes note) throws SQLException {
        notesDAO.updateNote(note);
    }

    @Override
    public List<Notes> findAll() throws SQLException {
        return notesDAO.findAll();
    }
}
