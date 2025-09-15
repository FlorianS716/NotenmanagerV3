package at.wifi.notenmanager.dao;

import at.wifi.notenmanager.model.Subject;
import at.wifi.notenmanager.util.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SubjectDAO {

    public void createSubject(Subject subject) throws SQLException {
        String sql = "INSERT INTO subject (name, teacher_id) VALUES (?, ?)";

        try (Connection connection = Database.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, subject.getName());
            preparedStatement.setInt(2, subject.getTeacherId());


            preparedStatement.executeUpdate();
        }
    }
    public boolean deleteSubject(int id) throws SQLException {
        String sql = "DELETE FROM subject WHERE id = ?";
        try (Connection connection = Database.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            int deleteRow = preparedStatement.executeUpdate();
            return deleteRow > 0;
        }
    }
    public void updateSubject(Subject subject) throws SQLException {
        String sql = "UPDATE subject SET name = ?  WHERE id = ?";
        try (Connection connection = Database.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, subject.getName());
            preparedStatement.setInt(2, subject.getId());
            preparedStatement.executeUpdate();
        }
    }
    public List<Subject> findAll() throws SQLException {
        List<Subject> subjects = new ArrayList<>();
        String sql = "SELECT * FROM subject";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Subject subject = new Subject(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("teacher_id")
                );
                subjects.add(subject);
            }
        }

        return subjects;
    }
}
