package repository;

import database.DatabaseManager;
import models.*;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * GuildRepository — single point of contact for all SQL operations.
 * All services delegate to this class; no other class holds SQL queries.
 */
public class GuildRepository {

    private static GuildRepository instance;
    private final Connection conn;

    private GuildRepository() {
        this.conn = DatabaseManager.getInstance().getConnection();
    }

    public static GuildRepository getInstance() {
        if (instance == null) instance = new GuildRepository();
        return instance;
    }

    // ══════════════════════════════════════════════════════════════
    //  APPLICATION QUERIES
    // ══════════════════════════════════════════════════════════════

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

    public List<Application> getPendingApplications() {
        List<Application> list = new ArrayList<>();
        String sql = "SELECT * FROM applications WHERE status = 'PENDING' ORDER BY submission_date";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapApplication(rs));
        } catch (SQLException e) {
            System.err.println("[ERROR] Fetching pending applications: " + e.getMessage());
        }
        return list;
    }

    public Application getApplicationById(int id) {
        String sql = "SELECT * FROM applications WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapApplication(rs);
        } catch (SQLException e) {
            System.err.println("[ERROR] Getting application by ID: " + e.getMessage());
        }
        return null;
    }

    public boolean applicationExists(String name) {
        String sql = "SELECT COUNT(*) FROM applications WHERE LOWER(name) = LOWER(?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            return rs.getInt(1) > 0;
        } catch (SQLException e) {
            System.err.println("[ERROR] Checking application existence: " + e.getMessage());
            return false;
        }
    }

    public boolean insertApplication(String name, String background, String rank, String date) {
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
            System.err.println("[ERROR] Inserting application: " + e.getMessage());
            return false;
        }
    }

    public boolean updateApplicationStatus(int id, String status) {
        String sql = "UPDATE applications SET status = ? WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status.toUpperCase());
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[ERROR] Updating application status: " + e.getMessage());
            return false;
        }
    }

    // ══════════════════════════════════════════════════════════════
    //  ADVENTURER QUERIES
    // ══════════════════════════════════════════════════════════════

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

    public List<Adventurer> searchAdventurersByName(String keyword) {
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

    public List<Adventurer> filterAdventurersByRank(String rank) {
        List<Adventurer> list = new ArrayList<>();
        String sql = "SELECT * FROM adventurers WHERE UPPER(rank) = UPPER(?) ORDER BY name";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, rank);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapAdventurer(rs));
        } catch (SQLException e) {
            System.err.println("[ERROR] Filtering adventurers by rank: " + e.getMessage());
        }
        return list;
    }

    public List<Adventurer> filterAdventurersByStatus(String status) {
        List<Adventurer> list = new ArrayList<>();
        String sql = "SELECT * FROM adventurers WHERE UPPER(status) = UPPER(?) ORDER BY name";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapAdventurer(rs));
        } catch (SQLException e) {
            System.err.println("[ERROR] Filtering adventurers by status: " + e.getMessage());
        }
        return list;
    }

    public boolean insertAdventurer(String name, String rank, String joinDate,
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
            System.err.println("[ERROR] Inserting adventurer: " + e.getMessage());
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
            System.err.println("[ERROR] Checking adventurer existence: " + e.getMessage());
            return false;
        }
    }

    public boolean updateAdventurerStatus(int id, String status) {
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

    public boolean appendAdventurerHistory(String name, String entry) {
        Adventurer a = getAdventurerByName(name);
        if (a == null) return false;
        String newHistory = a.getHistory().equals("None") ? entry : a.getHistory() + "; " + entry;
        String sql = "UPDATE adventurers SET history = ? WHERE LOWER(name) = LOWER(?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newHistory);
            ps.setString(2, name);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[ERROR] Appending adventurer history: " + e.getMessage());
            return false;
        }
    }

    public Adventurer getAdventurerByName(String name) {
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

    public Adventurer getAdventurerById(int id) {
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

    public int getAdventurerCount() {
        String sql = "SELECT COUNT(*) FROM adventurers";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("[ERROR] Counting adventurers: " + e.getMessage());
            return 0;
        }
    }

    // ══════════════════════════════════════════════════════════════
    //  MISSION QUERIES
    // ══════════════════════════════════════════════════════════════

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
            System.err.println("[ERROR] Filtering missions by status: " + e.getMessage());
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
            System.err.println("[ERROR] Getting mission by ID: " + e.getMessage());
        }
        return null;
    }

    public boolean missionIdExists(String id) {
        String sql = "SELECT COUNT(*) FROM missions WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            return rs.getInt(1) > 0;
        } catch (SQLException e) {
            System.err.println("[ERROR] Checking mission ID existence: " + e.getMessage());
            return false;
        }
    }

    public String generateMissionId() {
        String sql = "SELECT COUNT(*) FROM missions";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            int count = rs.getInt(1) + 1;
            return String.format("QST-%02d", count);
        } catch (SQLException e) {
            System.err.println("[ERROR] Generating mission ID: " + e.getMessage());
            return String.format("QST-%02d", new Random().nextInt(99) + 1);
        }
    }

    public boolean insertMission(String title, String deadline) {
        String id = generateMissionId();
        while (missionIdExists(id)) {
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
            System.err.println("[ERROR] Inserting mission: " + e.getMessage());
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
            System.err.println("[ERROR] Posting mission: " + e.getMessage());
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
            System.err.println("[ERROR] Approving mission: " + e.getMessage());
            return false;
        }
    }

    public boolean denyMission(String id) {
        String sql = "UPDATE missions SET status = 'DENIED' WHERE id = ? AND status = 'PENDING'";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id.toUpperCase().trim());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[ERROR] Denying mission: " + e.getMessage());
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

    public boolean assignAdventurerToMission(String missionId, String adventurerName) {
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
            System.err.println("[ERROR] Assigning adventurer to mission: " + e.getMessage());
            return false;
        }
    }

    // ══════════════════════════════════════════════════════════════
    //  ROOM REQUEST QUERIES
    // ══════════════════════════════════════════════════════════════

    public List<Object[]> getAllRoomRequests() {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT * FROM room_requests ORDER BY check_in";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(extractRoomRequest(rs));
        } catch (SQLException e) {
            System.err.println("[ERROR] Fetching room requests: " + e.getMessage());
        }
        return list;
    }

    public List<Object[]> getPendingRoomRequests() {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT * FROM room_requests WHERE status = 'PENDING' ORDER BY check_in";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(extractRoomRequest(rs));
        } catch (SQLException e) {
            System.err.println("[ERROR] Fetching pending room requests: " + e.getMessage());
        }
        return list;
    }

    public Object[] getRoomRequestById(int id) {
        String sql = "SELECT * FROM room_requests WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return extractRoomRequest(rs);
        } catch (SQLException e) {
            System.err.println("[ERROR] Getting room request by ID: " + e.getMessage());
        }
        return null;
    }

    public boolean insertRoomRequest(String name, String roomType, String checkIn, String checkOut) {
        String sql = "INSERT INTO room_requests (name, room_type, check_in, check_out, status) VALUES (?,?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name.trim());
            ps.setString(2, roomType.trim());
            ps.setString(3, checkIn.trim());
            ps.setString(4, checkOut.trim());
            ps.setString(5, "PENDING");
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("[ERROR] Inserting room request: " + e.getMessage());
            return false;
        }
    }

    public boolean updateRoomRequestStatus(int id, String status) {
        String sql = "UPDATE room_requests SET status = ? WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[ERROR] Updating room request status: " + e.getMessage());
            return false;
        }
    }

    // ══════════════════════════════════════════════════════════════
    //  ROOM QUERIES
    // ══════════════════════════════════════════════════════════════

    public List<Room> getAllRooms() {
        List<Room> list = new ArrayList<>();
        String sql = "SELECT * FROM rooms ORDER BY room_id";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRoom(rs));
        } catch (SQLException e) {
            System.err.println("[ERROR] Fetching rooms: " + e.getMessage());
        }
        return list;
    }

    public Room getRoomById(String roomId) {
        String sql = "SELECT * FROM rooms WHERE room_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, roomId.toUpperCase().trim());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRoom(rs);
        } catch (SQLException e) {
            System.err.println("[ERROR] Getting room by ID: " + e.getMessage());
        }
        return null;
    }

    public String findAvailableRoom(String roomType) {
        String sql = "SELECT room_id FROM rooms WHERE LOWER(room_type) = LOWER(?) AND status = 'AVAILABLE' LIMIT 1";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, roomType);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getString("room_id");
        } catch (SQLException e) {
            System.err.println("[ERROR] Finding available room: " + e.getMessage());
        }
        return null;
    }

    public boolean occupyRoom(String roomId, String occupant, String checkoutDate) {
        String sql = "UPDATE rooms SET status = 'OCCUPIED', occupant = ?, checkout_date = ? WHERE room_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, occupant);
            ps.setString(2, checkoutDate);
            ps.setString(3, roomId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[ERROR] Occupying room: " + e.getMessage());
            return false;
        }
    }

    public boolean checkoutRoom(String roomId) {
        Room r = getRoomById(roomId);
        if (r == null) {
            System.out.println("  [X] Room not found.");
            return false;
        }
        if (!r.getStatus().equals("OCCUPIED")) {
            System.out.println("  [X] Room is not currently occupied.");
            return false;
        }
        String sql = "UPDATE rooms SET status = 'AVAILABLE', occupant = 'None', checkout_date = 'N/A' WHERE room_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, roomId.toUpperCase().trim());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[ERROR] Checking out room: " + e.getMessage());
            return false;
        }
    }

    public boolean setRoomMaintenance(String roomId) {
        String sql = "UPDATE rooms SET status = 'MAINTENANCE', occupant = 'None', checkout_date = 'N/A' " +
                     "WHERE room_id = ? AND status != 'OCCUPIED'";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, roomId.toUpperCase().trim());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[ERROR] Setting room to maintenance: " + e.getMessage());
            return false;
        }
    }

    public boolean setRoomAvailable(String roomId) {
        String sql = "UPDATE rooms SET status = 'AVAILABLE', occupant = 'None', checkout_date = 'N/A' WHERE room_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, roomId.toUpperCase().trim());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[ERROR] Setting room to available: " + e.getMessage());
            return false;
        }
    }

    public int autoCheckoutExpired() {
        String today = LocalDate.now().toString();
        String sql = "UPDATE rooms SET status = 'AVAILABLE', occupant = 'None', checkout_date = 'N/A' " +
                     "WHERE status = 'OCCUPIED' AND checkout_date != 'N/A' AND checkout_date < ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, today);
            return ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("[ERROR] Auto-checkout expired rooms: " + e.getMessage());
            return 0;
        }
    }

    public int getRoomCountByStatus(String status) {
        String sql = "SELECT COUNT(*) FROM rooms WHERE UPPER(status) = UPPER(?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ResultSet rs = ps.executeQuery();
            return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("[ERROR] Counting rooms by status: " + e.getMessage());
            return 0;
        }
    }

    // ══════════════════════════════════════════════════════════════
    //  MAPPERS
    // ══════════════════════════════════════════════════════════════

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

    private Room mapRoom(ResultSet rs) throws SQLException {
        return new Room(
            rs.getString("room_id"),
            rs.getString("room_type"),
            rs.getString("status"),
            rs.getString("occupant"),
            rs.getString("checkout_date")
        );
    }

    private Object[] extractRoomRequest(ResultSet rs) throws SQLException {
        return new Object[]{
            rs.getInt("id"),
            rs.getString("name"),
            rs.getString("room_type"),
            rs.getString("check_in"),
            rs.getString("check_out"),
            rs.getString("status")
        };
    }
}
