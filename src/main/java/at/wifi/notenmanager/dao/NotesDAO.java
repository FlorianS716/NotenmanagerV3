package at.wifi.notenmanager.dao;

import at.wifi.notenmanager.model.Notes;
import at.wifi.notenmanager.util.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class NotesDAO {

    public void createNote(Notes note) throws SQLException {
        String sql = "INSERT INTO note (date, text) VALUES (?, ?)";

        try (Connection connection = Database.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, note.getDate().toString());
            stmt.setString(2, note.getText());

            stmt.executeUpdate();
        }
    }
    public boolean deleteNoteById(int id) throws SQLException {
        String sql = "DELETE FROM note WHERE id = ?";

        try (Connection connection = Database.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }
    public void updateNote(Notes note) throws SQLException {
        String sql = "UPDATE note SET text = ?, date = ? WHERE id = ?";

        try (Connection connection = Database.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, note.getText());
            stmt.setString(2, note.getDate().toString());
            stmt.setInt(3, note.getId());

            stmt.executeUpdate();
        }
    }
    public List<Notes> findAll() throws SQLException {
        List<Notes> notesList = new ArrayList<>();
        String sql = "SELECT * FROM note ORDER BY date DESC";

        try (Connection connection = Database.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                notesList.add(mapResultSetToNote(rs));
            }
        }

        return notesList;
    }

    // Hilfsmethode zur Objekt-Erstellung aus ResultSet
    private Notes mapResultSetToNote(ResultSet rs) throws SQLException {
        Notes note = new Notes();
        note.setId(rs.getInt("id"));
        note.setDate(LocalDate.parse(rs.getString("date"))); // stored as TEXT
        note.setText(rs.getString("text"));
        return note;
    }
}
