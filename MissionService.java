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
    
    public boolean deployMission(String id) {
    try {
        Mission m = DB.getMissionById(id);
        if (m == null) {
            System.out.println(" [X] Mission not found: " + id);
            return false;
        }
        
        if (m.getReward() <= 0) {
            System.out.println(" [X] Cannot deploy mission: reward amount is invalid (must be > 0).");
            return false;
        }
        
        if (!m.getStatus().equals("PENDING") && !m.getStatus().equals("UNPOSTED")) {
            System.out.println(" [X] Cannot deploy mission in current status: " + m.getStatus());
            return false;
        }
        
        System.out.println("\n *** DEPLOYMENT PAYMENT REQUIRED ***");
        
        // STEP 1: Posting Fee Payment
        double postingFee = MissionPostPayment.resolvePostingFee(m.getReward());
        java.util.Scanner sc = new java.util.Scanner(System.in);
        PaymentInputHandler payInput = new PaymentInputHandler(sc);
        
        MissionPostPayment postPayment = payInput.buildMissionPostPayment(m.getReward(), m.getTitle());
        SafePaymentHandler.PaymentResult postResult = SafePaymentHandler.processMissionPostFee(
            postPayment, id, m.getTitle(), postingFee, sc);
        
        if (postResult == null) {
            System.out.println(" [X] Mission NOT deployed: posting fee payment failed.");
            return false;
        }
        
        DB.insertPayment(postResult.txnId, "MISSION_POST", id,
            "Post Fee: " + m.getTitle(), postResult.finalAmount, "SUCCESS");
        
        // STEP 2: Mission Reward Prepayment
        MissionRewardPayment rewardPayment = payInput.buildMissionRewardPayment();
        SafePaymentHandler.PaymentResult rewardResult = SafePaymentHandler.processMissionReward(
            rewardPayment, id, m.getTitle(), m.getReward(), sc);
        
        if (rewardResult == null) {
            System.out.println(" [X] Mission NOT deployed: reward prepayment failed.");
            System.out.println(" [!] Posting fee was already collected (non-refundable).");
            return false;
        }
        
        DB.insertPayment(rewardResult.txnId, "MISSION_REWARD", id,
            "Reward: " + m.getTitle(), rewardResult.finalAmount, "SUCCESS");
        
        System.out.println("\n [OK] Both payments successful! Deploying mission...");
        return DB.deployMission(id);
        
    } catch (Exception e) {
        System.err.println("[ERROR] Unexpected error in deployMission: " + e.getMessage());
        return false;
    }
}

    public boolean unpostMission(String id)                  { return DB.unpostMission(id); }
    public boolean denyMission(String id)                    { return DB.denyMission(id); }
    public boolean assignAdventurer(String mId, String name) { return DB.assignAdventurerToMission(mId, name); }

    public boolean updateMissionStatus(String id, String status, int progress) {
        return DB.updateMissionStatus(id, status, progress);
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
