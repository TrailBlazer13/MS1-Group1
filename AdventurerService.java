package services;

import database.DatabaseManager;
import models.Adventurer;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AdventurerService {
    private final Connection conn;

    public AdventurerService() {
        this.conn = DatabaseManager.getInstance().getConnection();
    }

    public List<Adventurer> getAllAdventurers() {
        List<Adventurer> list = new ArrayList<>();
        String sql = "SELECT * FROM adventurers ORDER BY rank, name";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapAdventurer(rs));
        } catch (SQLException e) {
            System.err.println("[ERROR] Fetching adventurers: " + e.getMessage());
        }
        return list;
    }

    public List<Adventurer> searchByName(String keyword) {
        List<Adventurer> list = new ArrayList<>();
        String sql = "SELECT * FROM adventurers WHERE LOWER(name) LIKE LOWER(?) ORDER BY name";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + keyword + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapAdventurer(rs));
        } catch (SQLException e) {
            System.err.println("[ERROR] Searching adventurers: " + e.getMessage());
        }
        return list;
    }

    public List<Adventurer> filterByRank(String rank) {
        List<Adventurer> list = new ArrayList<>();
        String sql = "SELECT * FROM adventurers WHERE UPPER(rank) = UPPER(?) ORDER BY name";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, rank);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapAdventurer(rs));
        } catch (SQLException e) {
            System.err.println("[ERROR] Filtering by rank: " + e.getMessage());
        }
        return list;
    }

    public List<Adventurer> filterByStatus(String status) {
        List<Adventurer> list = new ArrayList<>();
        String sql = "SELECT * FROM adventurers WHERE UPPER(status) = UPPER(?) ORDER BY name";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapAdventurer(rs));
        } catch (SQLException e) {
            System.err.println("[ERROR] Filtering by status: " + e.getMessage());
        }
        return list;
    }

    public boolean addAdventurer(String name, String rank, String joinDate,
                                  String classType, String contact) {
        if (adventurerExists(name)) return false;
        String sql = "INSERT INTO adventurers (name, rank, join_date, status, class_type, contact, history) VALUES (?,?,?,?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name.trim());
            ps.setString(2, rank.trim().toUpperCase());
            ps.setString(3, joinDate);
            ps.setString(4, "ACTIVE");
            ps.setString(5, classType.trim());
            ps.setString(6, contact.trim());
            ps.setString(7, "None");
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("[ERROR] Adding adventurer: " + e.getMessage());
            return false;
        }
    }

    public boolean adventurerExists(String name) {
        String sql = "SELECT COUNT(*) FROM adventurers WHERE LOWER(name) = LOWER(?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            return rs.getInt(1) > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean updateStatus(int id, String status) {
        String sql = "UPDATE adventurers SET status = ? WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status.toUpperCase());
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[ERROR] Updating adventurer status: " + e.getMessage());
            return false;
        }
    }

    public boolean appendHistory(String name, String entry) {
        Adventurer a = getByName(name);
        if (a == null) return false;
        String newHistory = a.getHistory().equals("None") ? entry : a.getHistory() + "; " + entry;
        String sql = "UPDATE adventurers SET history = ? WHERE LOWER(name) = LOWER(?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newHistory);
            ps.setString(2, name);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    public Adventurer getByName(String name) {
        String sql = "SELECT * FROM adventurers WHERE LOWER(name) = LOWER(?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapAdventurer(rs);
        } catch (SQLException e) {
            System.err.println("[ERROR] Getting adventurer by name: " + e.getMessage());
        }
        return null;
    }

    public Adventurer getById(int id) {
        String sql = "SELECT * FROM adventurers WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapAdventurer(rs);
        } catch (SQLException e) {
            System.err.println("[ERROR] Getting adventurer by ID: " + e.getMessage());
        }
        return null;
    }

    public int getTotalCount() {
        String sql = "SELECT COUNT(*) FROM adventurers";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.getInt(1);
        } catch (SQLException e) {
            return 0;
        }
    }

    private Adventurer mapAdventurer(ResultSet rs) throws SQLException {
        return new Adventurer(
            rs.getInt("id"),
            rs.getString("name"),
            rs.getString("rank"),
            rs.getString("join_date"),
            rs.getString("status"),
            rs.getString("class_type"),
            rs.getString("contact"),
            rs.getString("history")
        );
    }
}
