package services;

import database.GuildDatabase;
import models.Room;
import java.util.List;

public class RoomService {
    private final GuildDatabase DB = GuildDatabase.getInstance();

    public List<Object[]> getAllRoomRequests()               { return DB.getAllRoomRequests(); }
    public List<Object[]> getPendingRoomRequests()           { return DB.getPendingRoomRequests(); }
    public List<Room> getAllRooms()                          { return DB.getAllRooms(); }
    public Room getRoomById(String roomId)                   { return DB.getRoomById(roomId); }
    public boolean checkoutRoom(String roomId)               { return DB.checkoutRoom(roomId); }
    public boolean setRoomMaintenance(String roomId)         { return DB.setRoomMaintenance(roomId); }
    public boolean setRoomAvailable(String roomId)           { return DB.setRoomAvailable(roomId); }
    public int autoCheckoutExpired()                         { return DB.autoCheckoutExpired(); }
    public int getRoomCountByStatus(String status)           { return DB.getRoomCountByStatus(status); }

    public boolean addRoomRequest(String name, String roomType, String checkIn, String checkOut) {
        return DB.insertRoomRequest(name, roomType, checkIn, checkOut);
    }

   // MODIFIED CODE — clerk-driven discount, full receipt, PaymentResult used for DB
    public boolean approveRoomRequest(int requestId) {
        try {
            Object[] req = DB.getRoomRequestById(requestId);
            if (req == null || !req[5].equals("PENDING")) {
                System.out.println(" [X] Request not found or not pending.");
                return false;
            }
 
            String name     = safeStr(req[1]);
            String roomType = safeStr(req[2]);
            String checkIn  = safeStr(req[3]);
            String checkOut = safeStr(req[4]);
 
            // Clerk enters discount via room-type guide
            PaymentInputHandler payInput = new PaymentInputHandler(new java.util.Scanner(System.in));
            RoomPayment payment = payInput.buildRoomPayment(roomType);
 
            // Process payment
            SafePaymentHandler.PaymentResult result =
                SafePaymentHandler.processRoomPayment(
                    payment, name, roomType, checkIn, checkOut,
                    String.valueOf(requestId), new java.util.Scanner(System.in));
 
            if (result == null) {
                System.out.println(" [X] Room approval blocked: payment was not completed.");
                return false;
            }
 
            // Save to payment ledger
            String description = name + " (" + roomType + ")";
            DB.insertPayment(result.txnId, "ROOM", String.valueOf(requestId),
                             description, result.finalAmount, "SUCCESS");
 
            // Assign room
            String roomToBook = DB.findAvailableRoom(roomType);
            if (roomToBook == null) {
                System.out.println(" [X] No available " + roomType + " for that period.");
                return false;
            }
 
            if (!DB.occupyRoom(roomToBook, name, checkOut)) return false;
            return DB.updateRoomRequestStatus(requestId, "APPROVED");
 
        } catch (Exception e) {
            System.err.println("[ERROR] Unexpected error in approveRoomRequest: " + e.getMessage());
            return false;
        }
    }
 

    private String safeStr(Object o) {
        if (o == null) return "";
        return o.toString().trim();
    }

    public boolean denyRoomRequest(int requestId) {
        Object[] req = DB.getRoomRequestById(requestId);
        if (req == null) {
            System.out.println(" [X] Request not found.");
            return false;
        }
        return DB.updateRoomRequestStatus(requestId, "DENIED");
    }
}
