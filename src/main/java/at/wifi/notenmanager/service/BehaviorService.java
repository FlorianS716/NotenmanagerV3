package at.wifi.notenmanager.service;

import at.wifi.notenmanager.model.Behavior;

import java.sql.SQLException;
import java.util.List;

public interface BehaviorService {
    void createBehavior(Behavior behavior) throws SQLException;
    void deleteBehavior(int behaviorId) throws  SQLException;
    void updateBehavior(Behavior behavior) throws SQLException;
    List<Behavior> getBehaviorByStudentAndSubject(int studentId, int subjectId) throws SQLException;
}
