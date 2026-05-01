package services;

import models.Mission;
import repository.GuildRepository;

import java.time.LocalDate;
import java.util.List;

public class MissionService {
    private final GuildRepository repo = GuildRepository.getInstance();

    public List<Mission> getAllMissions() {
        return repo.getAllMissions();
    }

    public List<Mission> getMissionsByStatus(String status) {
        return repo.getMissionsByStatus(status);
    }

    public Mission getMissionById(String id) {
        return repo.getMissionById(id);
    }

    public boolean addMission(String title, String deadline) {
        return repo.insertMission(title, deadline);
    }

    public boolean postMission(String id) {
        return repo.postMission(id);
    }

    public boolean unpostMission(String id) {
        return repo.unpostMission(id);
    }

    public boolean approveMission(String id) {
        return repo.approveMission(id);
    }

    public boolean denyMission(String id) {
        return repo.denyMission(id);
    }

    public boolean updateMissionStatus(String id, String status, int progress) {
        return repo.updateMissionStatus(id, status, progress);
    }

    public boolean assignAdventurer(String missionId, String adventurerName) {
        return repo.assignAdventurerToMission(missionId, adventurerName);
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
