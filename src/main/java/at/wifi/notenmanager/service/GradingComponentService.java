package at.wifi.notenmanager.service;

import at.wifi.notenmanager.model.GradingComponent;

import java.sql.SQLException;
import java.util.List;

public interface GradingComponentService {

    void save (GradingComponent gradingComponent) throws  SQLException;
    void update(GradingComponent gradingComponent) throws  SQLException;
    void delete(int id) throws SQLException;
    List<GradingComponent> findBySubjectId(int subjectId) throws SQLException;

}
