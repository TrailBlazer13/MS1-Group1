package services;
 
import database.GuildDatabase;
import models.Room;
import java.util.List;
 
public class RoomService {
    private final GuildDatabase db = GuildDatabase.getInstance();
 
    public List<Object[]> getAllRoomRequests()               { return db.getAllRoomRequests(); }
    public List<Object[]> getPendingRoomRequests()           { return db.getPendingRoomRequests(); }
    public List<Room> getAllRooms()                          { return db.getAllRooms(); }
    public Room getRoomById(String roomId)                   { return db.getRoomById(roomId); }
    public boolean checkoutRoom(String roomId)               { return db.checkoutRoom(roomId); }
    public boolean setRoomMaintenance(String roomId)         { return db.setRoomMaintenance(roomId); }
    public boolean setRoomAvailable(String roomId)           { return db.setRoomAvailable(roomId); }
    public int autoCheckoutExpired()                         { return db.autoCheckoutExpired(); }
    public int getRoomCountByStatus(String status)           { return db.getRoomCountByStatus(status); }
 
    public boolean addRoomRequest(String name, String roomType, String checkIn, String checkOut) {
        return db.insertRoomRequest(name, roomType, checkIn, checkOut);
    }
 
    // MODIFIED CODE — uses SafePaymentHandler instead of direct payment call
    public boolean approveRoomRequest(int requestId) {
        try {
            Object[] req = db.getRoomRequestById(requestId);
            if (req == null || !req[5].equals("PENDING")) {
                System.out.println(" [X] Request not found or not pending.");
                return false;
            }
 
            String name     = safeStr(req[1]);
            String roomType = safeStr(req[2]);
            String checkIn  = safeStr(req[3]);
            String checkOut = safeStr(req[4]);
 
            RoomPayment payment = RoomPayment.defaultPayment();
 
            // Wrapped call — SafePaymentHandler catches any framework exceptions
            String txnId = SafePaymentHandler.processRoomPayment(
                    payment, name, roomType, checkIn, checkOut);
 
            if (txnId == null) {
                System.out.println(" [X] Room approval blocked: payment was not completed.");
                return false;
            }
 
            double baseCost    = RoomPayment.calculateStayCost(roomType, checkIn, checkOut);
            double finalAmount = baseCost * 1.12;
            db.insertPayment(txnId, "ROOM", String.valueOf(requestId),
                    name + " (" + roomType + ")", finalAmount, "SUCCESS");
 
            String roomToBook = db.findAvailableRoom(roomType);
            if (roomToBook == null) {
                System.out.println(" [X] No available " + roomType + " for that period.");
                return false;
            }
 
            if (!db.occupyRoom(roomToBook, name, checkOut)) return false;
            return db.updateRoomRequestStatus(requestId, "APPROVED");
 
        } catch (Exception e) {
            System.err.println("[ERROR] Unexpected error in approveRoomRequest: " + e.getMessage());
            return false;
        }
    }
 
    // NEW CODE — null-safe string extractor for Object[] room request rows
    private String safeStr(Object o) {
        if (o == null) return "";
        return o.toString().trim();
    }
 
    public boolean denyRoomRequest(int requestId) {
        Object[] req = db.getRoomRequestById(requestId);
        if (req == null) {
            System.out.println(" [X] Request not found.");
            return false;
        }
        return db.updateRoomRequestStatus(requestId, "DENIED");
    }
}


