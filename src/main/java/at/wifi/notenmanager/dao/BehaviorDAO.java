package at.wifi.notenmanager.dao;

import at.wifi.notenmanager.model.Behavior;
import at.wifi.notenmanager.util.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class BehaviorDAO {

    public void createBehavior(Behavior behavior) throws SQLException {
        String sql = "INSERT INTO behavior (student_id, subject_id, rating, comment, date) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = Database.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, behavior.getStudentId());
            preparedStatement.setInt(2, behavior.getSubjectId());
            preparedStatement.setInt(3, behavior.getRating());
            preparedStatement.setString(4, behavior.getComment());
            preparedStatement.setString(5, String.valueOf(behavior.getDate()));

            preparedStatement.executeUpdate();
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String formattedDate = behavior.getDate().format(formatter);

    }
    public void deleteBehavior(int behaviorId) throws SQLException {
        String sql = "DELETE FROM behavior WHERE id = ?";
        try (Connection connection = Database.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, behaviorId);
            preparedStatement.executeUpdate();
        }
    }
    public List<Behavior> getBehaviorByStudentId(int studentId) throws SQLException {
        List<Behavior> behaviors = new ArrayList<>();
        String sql = "SELECT * FROM behavior WHERE student_id = ? ORDER BY date DESC";

        try (Connection connection = Database.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, studentId);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Behavior behavior = new Behavior();
                behavior.setId(resultSet.getInt("id"));
                behavior.setRating(resultSet.getInt("rating"));
                behavior.setDate(LocalDate.parse(resultSet.getString("date")));
                behavior.setStudentId(resultSet.getInt("student_id"));
                behavior.setSubjectId(resultSet.getInt("subject_id"));
                behavior.setComment(resultSet.getString("comment"));


                behaviors.add(behavior);
            }
        }

        return behaviors;
    }
    public void updateBehavior(Behavior behavior) throws SQLException {
        String sql = "UPDATE behavior SET rating = ?, date = ?, student_id = ?, subject_id = ?, comment = ? WHERE id = ?";
        try (Connection connection = Database.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, behavior.getRating());
            stmt.setString(2, behavior.getDate().toString());
            stmt.setInt(3, behavior.getStudentId());
            stmt.setInt(4, behavior.getSubjectId());
            stmt.setString(5, behavior.getComment());
            stmt.setInt(6, behavior.getId());

            stmt.executeUpdate();
        }
    }
    public List<Behavior> getBehaviorByStudentAndSubject(int studentId, int subjectId) throws SQLException {
        List<Behavior> list = new ArrayList<>();
        String sql = "SELECT * FROM behavior WHERE student_id = ? AND subject_id = ? ORDER BY date DESC";

        try (Connection connection = Database.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, studentId);
            stmt.setInt(2, subjectId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Behavior behavior = new Behavior();
                    behavior.setId(rs.getInt("id"));
                    behavior.setRating(rs.getInt("rating"));
                    behavior.setDate(LocalDate.parse(rs.getString("date")));
                    behavior.setStudentId(rs.getInt("student_id"));
                    behavior.setSubjectId(rs.getInt("subject_id"));
                    behavior.setComment(rs.getString("comment"));
                    list.add(behavior);
                }
            }
        }

        return list;
    }
    public float getAverageRating(int studentId, int subjectId) throws SQLException {
        String sql = "SELECT AVG(rating) FROM behavior WHERE student_id = ? AND subject_id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, studentId);
            stmt.setInt(2, subjectId);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getFloat(1);
            }
        }
        return 0.0f;
    }

}
