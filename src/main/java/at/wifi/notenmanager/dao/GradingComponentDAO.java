package at.wifi.notenmanager.dao;

import at.wifi.notenmanager.model.GradingComponent;
import at.wifi.notenmanager.util.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GradingComponentDAO {

    public void save(GradingComponent gradingComponent) throws SQLException {
        String sql = "INSERT INTO grading_components (subject_id, name, weight) VALUES (?, ?, ?)";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, gradingComponent.getSubjectId());
            stmt.setString(2, gradingComponent.getName());
            stmt.setDouble(3, gradingComponent.getWeight());
            stmt.executeUpdate();
        }
    }
    public void update(GradingComponent gradingComponent) throws SQLException {
        String sql = "UPDATE grading_components SET name = ?, weight = ? WHERE id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, gradingComponent.getName());
            stmt.setDouble(2, gradingComponent.getWeight());
            stmt.setInt(3, gradingComponent.getId());
            stmt.executeUpdate();
        }
    }
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM grading_components WHERE id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
    public List<GradingComponent> findBySubjectId(int subjectId) throws SQLException {
        List<GradingComponent> components = new ArrayList<>();
        String sql = "SELECT * FROM grading_components WHERE subject_id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, subjectId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                components.add(new GradingComponent(
                        rs.getInt("id"),
                        rs.getInt("subject_id"),
                        rs.getString("name"),
                        rs.getDouble("weight")
                ));
            }
        }

        return components;
    }
}
