
package services;
 
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
 
/**
 * SafePaymentHandler — crash-proof wrapper around PaymentFramework subclasses.
 *
 * PURPOSE:
 *   PaymentFramework.processInvoice() returns void and may throw exceptions.
 *   This class wraps those calls so the rest of the system gets a safe
 *   result (transaction ID or null) without any unhandled exceptions.
 *
 * IMPORTANT: PaymentFramework.java is NOT modified. This class only
 *   calls it externally and wraps the result.
 */
public class SafePaymentHandler {  // NEW CODE
 
    // NEW CODE — generate a unique transaction ID (moved here from framework)
    public static String generateTransactionId() {
        try {
            String timestamp = LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
            String shortUUID = UUID.randomUUID().toString()
                    .substring(0, 8).toUpperCase();
            return "TXN-" + timestamp + "-" + shortUUID;
        } catch (Exception e) {
            // ultra-safe fallback
            return "TXN-FALLBACK-" + System.currentTimeMillis();
        }
    }
 
    // NEW CODE — safely execute a mission payment; returns txnId or null
    public static String processMissionPayment(MissionPayment payment,
                                                String missionId,
                                                String title,
                                                double reward) {
        try {
            if (reward <= 0) {
                System.out.println(" [X] Payment aborted: reward must be greater than zero.");
                return null;
            }
 
            System.out.println("\n ----------------------------------------");
            System.out.println(" [PAY] Mission Reward Prepayment");
            System.out.println(" [PAY] Mission : " + safeDisplay(missionId)
                    + " - " + safeDisplay(title));
            System.out.printf( " [PAY] Reward  : %.2f gold coins%n", reward);
 
            // Call the professor's framework method (void, cannot return ID)
            payment.processInvoice(reward);
 
            // Generate transaction ID externally after framework runs
            String txnId = generateTransactionId();
            System.out.println(" [OK] Transaction ID: " + txnId);
            System.out.println(" ----------------------------------------");
            return txnId;
 
        } catch (Exception e) {
            System.out.println(" [X] Payment processing error: " + e.getMessage());
            System.out.println(" [X] Transaction aborted.");
            System.out.println(" ----------------------------------------");
            return null;
        }
    }
 
    // NEW CODE — safely execute a room payment; returns txnId or null
    public static String processRoomPayment(RoomPayment payment,
                                             String guestName,
                                             String roomType,
                                             String checkIn,
                                             String checkOut) {
        try {
            double baseCost = RoomPayment.calculateStayCost(roomType, checkIn, checkOut);
            if (baseCost <= 0) {
                System.out.println(" [X] Payment aborted: calculated cost is invalid.");
                return null;
            }
 
            System.out.println("\n ----------------------------------------");
            System.out.println(" [PAY] Room Reservation Fee");
            System.out.println(" [PAY] Guest     : " + safeDisplay(guestName));
            System.out.println(" [PAY] Room Type : " + safeDisplay(roomType));
            System.out.println(" [PAY] Period    : " + safeDisplay(checkIn)
                    + " -> " + safeDisplay(checkOut));
            System.out.printf( " [PAY] Base Cost : %.2f gold coins%n", baseCost);
 
            // Call the professor's framework method
            payment.processInvoice(baseCost);
 
            String txnId = generateTransactionId();
            System.out.println(" [OK] Transaction ID: " + txnId);
            System.out.println(" ----------------------------------------");
            return txnId;
 
        } catch (Exception e) {
            System.out.println(" [X] Payment processing error: " + e.getMessage());
            System.out.println(" [X] Transaction aborted.");
            System.out.println(" ----------------------------------------");
            return null;
        }
    }
 
    // NEW CODE — null-safe string display helper
    private static String safeDisplay(String s) {
        if (s == null) return "N/A";
        String trimmed = s.trim();
        return trimmed.isEmpty() ? "N/A" : trimmed;
    }
}
