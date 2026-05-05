
package services;
 
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class RoomPayment extends PaymentFramework {
 
    private static final double RATE_COMMON  = 20.00;
    private static final double RATE_PRIVATE = 50.00;
    private static final double RATE_NOBLE   = 120.00;
 
    public RoomPayment(double creditAmount, boolean validPaymentMethod, double discountRate) {
        super(creditAmount, validPaymentMethod, discountRate);
    }
 
    // NEW CODE — exposes protected field from PaymentFramework without modifying it
    public double getDiscountRate() {
        return discountRate;
    }
 
    public static double calculateStayCost(String roomType, String checkIn, String checkOut) {
        try {
            if (roomType == null || checkIn == null || checkOut == null) return RATE_COMMON;
            LocalDate ci = LocalDate.parse(checkIn.trim());
            LocalDate co = LocalDate.parse(checkOut.trim());
            long nights  = ChronoUnit.DAYS.between(ci, co);
            if (nights <= 0) nights = 1;
 
            double nightly = switch (roomType.trim().toLowerCase()) {
                case "private chamber" -> RATE_PRIVATE;
                case "noble suite"     -> RATE_NOBLE;
                default                -> RATE_COMMON;
            };
            return nightly * nights;
        } catch (Exception e) {
            System.err.println("[ERROR] Could not calculate stay cost: " + e.getMessage());
            return RATE_COMMON;
        }
    }
 
    public static double getRateCommon()  { return RATE_COMMON; }
    public static double getRatePrivate() { return RATE_PRIVATE; }
    public static double getRateNoble()   { return RATE_NOBLE; }
}
