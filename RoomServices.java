package services;

import database.DatabaseManager;
import models.Room;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class RoomService {
    private final Connection conn;

    public RoomService() {
        this.conn = DatabaseManager.getInstance().getConnection();
    }

    // ── ROOM REQUESTS ────────────────────────────────────────────────

    public List<Object[]> getAllRoomRequests() {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT * FROM room_requests ORDER BY check_in";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(extractRequest(rs));
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
            while (rs.next()) list.add(extractRequest(rs));
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
            if (rs.next()) return extractRequest(rs);
        } catch (SQLException e) {
            System.err.println("[ERROR] Getting room request: " + e.getMessage());
        }
        return null;
    }

    public boolean addRoomRequest(String name, String roomType,
                                   String checkIn, String checkOut) {
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
            System.err.println("[ERROR] Adding room request: " + e.getMessage());
            return false;
        }
    }

    public boolean approveRoomRequest(int requestId) {
        Object[] req = getRoomRequestById(requestId);
        if (req == null || !req[5].equals("PENDING")) {
            System.out.println("  [X] Request not found or not pending.");
            return false;
        }
        String name     = (String) req[1];
        String roomType = (String) req[2];
        String checkIn  = (String) req[3];
        String checkOut = (String) req[4];

        String roomToBook = findAvailableRoom(roomType);
        if (roomToBook == null) {
            System.out.println("  [X] Room conflict detected! No available " + roomType + " for that period.");
            return false;
        }

        String bookSql = "UPDATE rooms SET status = 'OCCUPIED', occupant = ?, checkout_date = ? WHERE room_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(bookSql)) {
            ps.setString(1, name);
            ps.setString(2, checkOut);
            ps.setString(3, roomToBook);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("[ERROR] Booking room: " + e.getMessage());
            return false;
        }
        return updateRequestStatus(requestId, "APPROVED");
    }

    public boolean denyRoomRequest(int requestId) {
        return updateRequestStatus(requestId, "DENIED");
    }

    private boolean updateRequestStatus(int id, String status) {
        String sql = "UPDATE room_requests SET status = ? WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[ERROR] Updating request status: " + e.getMessage());
            return false;
        }
    }

    // ── ROOMS ────────────────────────────────────────────────────────

    public List<Room> getAllRooms() {
        List<Room> list = new ArrayList<>();
        String sql = "SELECT * FROM rooms ORDER BY room_id";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Room(
                    rs.getString("room_id"),
                    rs.getString("room_type"),
                    rs.getString("status"),
                    rs.getString("occupant"),
                    rs.getString("checkout_date")
                ));
            }
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
            if (rs.next()) {
                return new Room(
                    rs.getString("room_id"),
                    rs.getString("room_type"),
                    rs.getString("status"),
                    rs.getString("occupant"),
                    rs.getString("checkout_date")
                );
            }
        } catch (SQLException e) {
            System.err.println("[ERROR] Getting room: " + e.getMessage());
        }
        return null;
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
            return false;
        }
    }

    public boolean setRoomAvailable(String roomId) {
        String sql = "UPDATE rooms SET status = 'AVAILABLE', occupant = 'None', checkout_date = 'N/A' WHERE room_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, roomId.toUpperCase().trim());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
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
            return 0;
        }
    }

    private String findAvailableRoom(String roomType) {
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

    private Object[] extractRequest(ResultSet rs) throws SQLException {
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

