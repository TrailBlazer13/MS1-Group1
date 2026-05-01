package services;

import database.GuildDatabase;
import models.Mission;

import java.time.LocalDate;
import java.util.List;

public class MissionService {
    private final GuildDatabase db = GuildDatabase.getInstance();

    public List<Mission> getAllMissions()                          { return db.getAllMissions(); }
    public List<Mission> getMissionsByStatus(String status)       { return db.getMissionsByStatus(status); }
    public Mission getMissionById(String id)                      { return db.getMissionById(id); }
    public boolean addMission(String title, String deadline)      { return db.insertMission(title, deadline); }
    public boolean postMission(String id)                         { return db.postMission(id); }
    public boolean unpostMission(String id)                       { return db.unpostMission(id); }
    public boolean approveMission(String id)                      { return db.approveMission(id); }
    public boolean denyMission(String id)                         { return db.denyMission(id); }
    public boolean assignAdventurer(String mId, String name)      { return db.assignAdventurerToMission(mId, name); }

    public boolean updateMissionStatus(String id, String status, int progress) {
        return db.updateMissionStatus(id, status, progress);
    }

    public boolean isOverdue(Mission m) {
        try {
            LocalDate deadline = LocalDate.parse(m.getDeadline());
            return LocalDate.now().isAfter(deadline)
                && !m.getStatus().equals("COMPLETED")
                && !m.getStatus().equals("FAILED");
        } catch (Exception e) {
            System.err.println("[ERROR] Parsing deadline for overdue check: " + e.getMessage());
            return false;
        }
    }
}
