package at.wifi.notenmanager.service;

import at.wifi.notenmanager.dao.GradingComponentDAO;
import at.wifi.notenmanager.model.Grades;
import at.wifi.notenmanager.model.GradingComponent;

import java.sql.SQLException;
import java.util.List;

public class GradingComponentServiceImpl implements GradingComponentService{

    private final GradingComponentDAO gradingComponentDAO = new GradingComponentDAO();

    @Override
    public void save(GradingComponent gradingComponent) throws SQLException {
        gradingComponentDAO.save(gradingComponent);
    }

    @Override
    public void update(GradingComponent gradingComponent) throws SQLException {
        gradingComponentDAO.update(gradingComponent);
    }

    @Override
    public void delete(int id) throws SQLException {
        gradingComponentDAO.delete(id);
    }

    @Override
    public List<GradingComponent> findBySubjectId(int subjectId) throws SQLException {
        return gradingComponentDAO.findBySubjectId(subjectId);
    }
}
