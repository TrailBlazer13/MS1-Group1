package services;

import database.GuildDatabase;
import models.Mission;
import java.time.LocalDate;
import java.util.List;

public class MissionService {
    private final GuildDatabase DB = GuildDatabase.getInstance();

    public List<Mission> getAllMissions()                     { return DB.getAllMissions(); }
    public List<Mission> getMissionsByStatus(String status)  { return DB.getMissionsByStatus(status); }
    public Mission getMissionById(String id)                 { return DB.getMissionById(id); }
    public boolean addMission(String title, String deadline, double reward) {
        return DB.insertMission(title, deadline, reward);
    }
    public boolean postMission(String id)                    { return DB.postMission(id); }
    public boolean unpostMission(String id)                  { return DB.unpostMission(id); }
    public boolean denyMission(String id)                    { return DB.denyMission(id); }
    public boolean assignAdventurer(String mId, String name) { return DB.assignAdventurerToMission(mId, name); }

    public boolean updateMissionStatus(String id, String status, int progress) {
        return DB.updateMissionStatus(id, status, progress);
    }

    public boolean approveMission(String id) {
        try {
            Mission m = DB.getMissionById(id);
            if (m == null) return false;

            if (m.getReward() <= 0) {
                System.out.println(" [X] Mission approval blocked: reward is invalid (must be > 0).");
                return false;
            }

            MissionPayment payment = MissionPayment.defaultPayment();

            String txnId = SafePaymentHandler.processMissionPayment(
                    payment, id, m.getTitle(), m.getReward());

            if (txnId == null) {
                System.out.println(" [X] Mission approval blocked: payment was not completed.");
                return false;
            }

            double finalAmount = m.getReward() * 1.12;
            DB.insertPayment(txnId, "MISSION", id, m.getTitle(), finalAmount, "SUCCESS");

            return DB.approveMission(id);

        } catch (Exception e) {
            System.err.println("[ERROR] Unexpected error in approveMission: " + e.getMessage());
            return false;
        }
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
