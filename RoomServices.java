package services;

import database.GuildDatabase;
import models.Room;

import java.util.List;

public class RoomService {
    private final GuildDatabase db = GuildDatabase.getInstance();

    public List<Object[]> getAllRoomRequests()          { return db.getAllRoomRequests(); }
    public List<Object[]> getPendingRoomRequests()      { return db.getPendingRoomRequests(); }
    public List<Room> getAllRooms()                     { return db.getAllRooms(); }
    public Room getRoomById(String roomId)              { return db.getRoomById(roomId); }
    public boolean checkoutRoom(String roomId)          { return db.checkoutRoom(roomId); }
    public boolean setRoomMaintenance(String roomId)    { return db.setRoomMaintenance(roomId); }
    public boolean setRoomAvailable(String roomId)      { return db.setRoomAvailable(roomId); }
    public int autoCheckoutExpired()                    { return db.autoCheckoutExpired(); }
    public int getRoomCountByStatus(String status)      { return db.getRoomCountByStatus(status); }

    public boolean addRoomRequest(String name, String roomType, String checkIn, String checkOut) {
        return db.insertRoomRequest(name, roomType, checkIn, checkOut);
    }

    public boolean approveRoomRequest(int requestId) {
        Object[] req = db.getRoomRequestById(requestId);
        if (req == null || !req[5].equals("PENDING")) {
            System.out.println("  [X] Request not found or not pending.");
            return false;
        }
        String name     = (String) req[1];
        String roomType = (String) req[2];
        String checkOut = (String) req[4];

        String roomToBook = db.findAvailableRoom(roomType);
        if (roomToBook == null) {
            System.out.println("  [X] Room conflict detected! No available " + roomType + " for that period.");
            return false;
        }
        if (!db.occupyRoom(roomToBook, name, checkOut)) return false;
        return db.updateRoomRequestStatus(requestId, "APPROVED");
    }

    public boolean denyRoomRequest(int requestId) {
        Object[] req = db.getRoomRequestById(requestId);
        if (req == null) {
            System.out.println("  [X] Request not found.");
            return false;
        }
        return db.updateRoomRequestStatus(requestId, "DENIED");
    }
}
