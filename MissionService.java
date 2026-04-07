package services;

import database.DatabaseManager;
import models.Mission;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MissionService {
    private final Connection conn;

    public MissionService() {
        this.conn = DatabaseManager.getInstance().getConnection();
    }

    public List<Mission> getAllMissions() {
        List<Mission> list = new ArrayList<>();
        String sql = "SELECT * FROM missions ORDER BY deadline";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapMission(rs));
        } catch (SQLException e) {
            System.err.println("[ERROR] Fetching missions: " + e.getMessage());
        }
        return list;
    }

    public List<Mission> getMissionsByStatus(String status) {
        List<Mission> list = new ArrayList<>();
        String sql = "SELECT * FROM missions WHERE UPPER(status) = UPPER(?) ORDER BY deadline";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapMission(rs));
        } catch (SQLException e) {
            System.err.println("[ERROR] Filtering missions: " + e.getMessage());
        }
        return list;
    }

    public Mission getMissionById(String id) {
        String sql = "SELECT * FROM missions WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id.toUpperCase().trim());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapMission(rs);
        } catch (SQLException e) {
            System.err.println("[ERROR] Getting mission: " + e.getMessage());
        }
        return null;
    }

    public String generateMissionId() {
        String sql = "SELECT COUNT(*) FROM missions";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            int count = rs.getInt(1) + 1;
            return String.format("QST-%02d", count);
        } catch (SQLException e) {
            return String.format("QST-%02d", new Random().nextInt(99) + 1);
        }
    }

    public boolean idExists(String id) {
        String sql = "SELECT COUNT(*) FROM missions WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            return rs.getInt(1) > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean addMission(String title, String deadline) {
        String id = generateMissionId();
        while (idExists(id)) {
            id = "QST-" + String.format("%02d", new Random().nextInt(99) + 1);
        }
        String sql = "INSERT INTO missions (id, title, status, progress, assigned, deadline) VALUES (?,?,?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            ps.setString(2, title.trim());
            ps.setString(3, "PENDING");
            ps.setInt(4, 0);
            ps.setString(5, "Unassigned");
            ps.setString(6, deadline.trim());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("[ERROR] Adding mission: " + e.getMessage());
            return false;
        }
    }

    public boolean postMission(String id) {
        String today = LocalDate.now().toString();
        String sql = "UPDATE missions SET status = 'POSTED', posted_date = ? " +
                     "WHERE id = ? AND status IN ('PENDING','UNPOSTED')";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, today);
            ps.setString(2, id.toUpperCase().trim());
            int rows = ps.executeUpdate();
            if (rows > 0) {
                System.out.printf("%n  >> Mission posted! ID %s on [%s]%n", id.toUpperCase(), today);
                return true;
            }
            System.out.println("  >> Mission request post failed!");
            return false;
        } catch (SQLException e) {
            System.out.println("  >> Mission request post failed!");
            return false;
        }
    }

    public boolean unpostMission(String id) {
        String sql = "UPDATE missions SET status = 'UNPOSTED', posted_date = NULL " +
                     "WHERE id = ? AND status = 'POSTED'";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id.toUpperCase().trim());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[ERROR] Unposting mission: " + e.getMessage());
            return false;
        }
    }

    public boolean approveMission(String id) {
        String sql = "UPDATE missions SET status = 'APPROVED' WHERE id = ? AND status = 'PENDING'";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id.toUpperCase().trim());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean denyMission(String id) {
        String sql = "UPDATE missions SET status = 'DENIED' WHERE id = ? AND status = 'PENDING'";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id.toUpperCase().trim());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean updateMissionStatus(String id, String status, int progress) {
        String sql = "UPDATE missions SET status = ?, progress = ? WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status.toUpperCase());
            ps.setInt(2, progress);
            ps.setString(3, id.toUpperCase().trim());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[ERROR] Updating mission status: " + e.getMessage());
            return false;
        }
    }

    public boolean assignAdventurer(String missionId, String adventurerName) {
        Mission m = getMissionById(missionId);
        if (m == null) return false;
        String current = m.getAssigned();
        String updated = current.equals("Unassigned") ? adventurerName : current + ", " + adventurerName;
        String sql = "UPDATE missions SET assigned = ? WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, updated);
            ps.setString(2, missionId.toUpperCase().trim());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
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
            return false;
        }
    }

    private Mission mapMission(ResultSet rs) throws SQLException {
        return new Mission(
            rs.getString("id"),
            rs.getString("title"),
            rs.getString("status"),
            rs.getInt("progress"),
            rs.getString("assigned"),
            rs.getString("deadline"),
            rs.getString("posted_date")
        );
    }
}
