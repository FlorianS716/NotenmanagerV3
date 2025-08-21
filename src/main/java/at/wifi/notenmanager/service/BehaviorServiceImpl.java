package at.wifi.notenmanager.service;

import at.wifi.notenmanager.dao.BehaviorDAO;
import at.wifi.notenmanager.model.Behavior;

import java.sql.SQLException;
import java.util.List;

public class BehaviorServiceImpl implements BehaviorService{

    private final BehaviorDAO behaviorDAO;

    public BehaviorServiceImpl(BehaviorDAO behaviorDAO) {
        this.behaviorDAO = behaviorDAO;
    }

    @Override
    public void createBehavior(Behavior behavior) throws SQLException {
        behaviorDAO.createBehavior(behavior);
    }

    @Override
    public void deleteBehavior(int behaviorId) throws SQLException {
        behaviorDAO.deleteBehavior(behaviorId);
    }

    @Override
    public List<Behavior> getBehaviorByStudentId(int studentId) throws SQLException {
        return behaviorDAO.getBehaviorByStudentId(studentId);
    }

    @Override
    public void updateBehavior(Behavior behavior) throws SQLException {
        behaviorDAO.updateBehavior(behavior);
    }

    @Override
    public List<Behavior> getBehaviorByStudentAndSubject(int studentId, int subjectId) throws SQLException {
        return behaviorDAO.getBehaviorByStudentAndSubject(studentId, subjectId);
    }

    @Override
    public double getAverageRating(int studentId, int subjectId) throws SQLException {
        return behaviorDAO.getAverageRating(studentId, subjectId);
    }
}
