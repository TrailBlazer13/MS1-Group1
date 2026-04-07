package services;

import database.DatabaseManager;
import models.Application;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ApplicationService {
    private final Connection conn;
    private final AdventurerService adventurerService;

    public ApplicationService() {
        this.conn             = DatabaseManager.getInstance().getConnection();
        this.adventurerService = new AdventurerService();
    }

    public List<Application> getPendingApplications() {
        List<Application> list = new ArrayList<>();
        String sql = "SELECT * FROM applications WHERE status = 'PENDING' ORDER BY submission_date";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapApplication(rs));
        } catch (SQLException e) {
            System.err.println("[ERROR] Fetching applications: " + e.getMessage());
        }
        return list;
    }

    public List<Application> getAllApplications() {
        List<Application> list = new ArrayList<>();
        String sql = "SELECT * FROM applications ORDER BY submission_date DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapApplication(rs));
        } catch (SQLException e) {
            System.err.println("[ERROR] Fetching all applications: " + e.getMessage());
        }
        return list;
    }

    public boolean applicationExists(String name) {
        String sql = "SELECT COUNT(*) FROM applications WHERE LOWER(name) = LOWER(?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            return rs.getInt(1) > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean submitApplication(String name, String background, String rank, String date) {
        if (applicationExists(name)) return false;
        String sql = "INSERT INTO applications (name, background, rank, submission_date, status) VALUES (?,?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name.trim());
            ps.setString(2, background.trim());
            ps.setString(3, rank.trim().toUpperCase());
            ps.setString(4, date.trim());
            ps.setString(5, "PENDING");
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("[ERROR] Submitting application: " + e.getMessage());
            return false;
        }
    }

    public boolean approveApplication(int id) {
        String fetchSql = "SELECT * FROM applications WHERE id = ? AND status = 'PENDING'";
        try (PreparedStatement ps = conn.prepareStatement(fetchSql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) return false;

            String name       = rs.getString("name");
            String rank       = rs.getString("rank");
            String background = rs.getString("background");
            String classType  = inferClass(background);

            boolean inserted = adventurerService.addAdventurer(
                name, rank, LocalDate.now().toString(), classType, "contact@guild.net"
            );
            if (!inserted) return false;

            return updateStatus(id, "APPROVED");
        } catch (SQLException e) {
            System.err.println("[ERROR] Approving application: " + e.getMessage());
            return false;
        }
    }

    public boolean rejectApplication(int id) {
        return updateStatus(id, "REJECTED");
    }

    public Application getApplicationById(int id) {
        String sql = "SELECT * FROM applications WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapApplication(rs);
        } catch (SQLException e) {
            System.err.println("[ERROR] Getting application: " + e.getMessage());
        }
        return null;
    }

    private boolean updateStatus(int id, String status) {
        String sql = "UPDATE applications SET status = ? WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[ERROR] Updating status: " + e.getMessage());
            return false;
        }
    }

    private Application mapApplication(ResultSet rs) throws SQLException {
        return new Application(
            rs.getInt("id"),
            rs.getString("name"),
            rs.getString("background"),
            rs.getString("rank"),
            rs.getString("submission_date"),
            rs.getString("status")
        );
    }

    private String inferClass(String background) {
        background = background.toLowerCase();
        if (background.contains("mage")   || background.contains("magic")  || background.contains("spell"))  return "Mage";
        if (background.contains("sword")  || background.contains("warrior") || background.contains("fighter")) return "Warrior";
        if (background.contains("rogue")  || background.contains("thief")  || background.contains("scout"))  return "Rogue";
        if (background.contains("heal")   || background.contains("cleric") || background.contains("priest")) return "Cleric";
        if (background.contains("archer") || background.contains("ranger") || background.contains("bow"))    return "Ranger";
        if (background.contains("paladin")|| background.contains("holy")   || background.contains("knight")) return "Paladin";
        return "Adventurer";
    }
}
