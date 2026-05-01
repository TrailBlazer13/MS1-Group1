package services;

import models.Room;
import repository.GuildRepository;

import java.util.List;

public class RoomService {
    private final GuildRepository repo = GuildRepository.getInstance();

    // ── ROOM REQUESTS ────────────────────────────────────────────────

    public List<Object[]> getAllRoomRequests() {
        return repo.getAllRoomRequests();
    }

    public List<Object[]> getPendingRoomRequests() {
        return repo.getPendingRoomRequests();
    }

    public boolean addRoomRequest(String name, String roomType, String checkIn, String checkOut) {
        return repo.insertRoomRequest(name, roomType, checkIn, checkOut);
    }

    public boolean approveRoomRequest(int requestId) {
        Object[] req = repo.getRoomRequestById(requestId);
        if (req == null || !req[5].equals("PENDING")) {
            System.out.println("  [X] Request not found or not pending.");
            return false;
        }
        String name     = (String) req[1];
        String roomType = (String) req[2];
        String checkOut = (String) req[4];

        String roomToBook = repo.findAvailableRoom(roomType);
        if (roomToBook == null) {
            System.out.println("  [X] Room conflict detected! No available " + roomType + " for that period.");
            return false;
        }

        boolean booked = repo.occupyRoom(roomToBook, name, checkOut);
        if (!booked) return false;
        return repo.updateRoomRequestStatus(requestId, "APPROVED");
    }

    public boolean denyRoomRequest(int requestId) {
        Object[] req = repo.getRoomRequestById(requestId);
        if (req == null) {
            System.out.println("  [X] Request not found.");
            return false;
        }
        return repo.updateRoomRequestStatus(requestId, "DENIED");
    }

    // ── ROOMS ────────────────────────────────────────────────────────

    public List<Room> getAllRooms() {
        return repo.getAllRooms();
    }

    public Room getRoomById(String roomId) {
        return repo.getRoomById(roomId);
    }

    public boolean checkoutRoom(String roomId) {
        return repo.checkoutRoom(roomId);
    }

    public boolean setRoomMaintenance(String roomId) {
        return repo.setRoomMaintenance(roomId);
    }

    public boolean setRoomAvailable(String roomId) {
        return repo.setRoomAvailable(roomId);
    }

    public int autoCheckoutExpired() {
        return repo.autoCheckoutExpired();
    }

    public int getRoomCountByStatus(String status) {
        return repo.getRoomCountByStatus(status);
    }
}
