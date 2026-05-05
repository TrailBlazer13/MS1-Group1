package services;
 
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import java.util.UUID;
 
/**
 * SafePaymentHandler — crash-proof wrapper around PaymentFramework subclasses.
 * Now collects amount tendered, shows receipt, and returns full PaymentResult.
 * PaymentFramework.java is NOT modified.
 */
public class SafePaymentHandler {
 
    // NEW CODE — result object carrying everything needed for DB insert + receipt
    public static class PaymentResult {
        public final String  txnId;
        public final double  originalAmount;
        public final double  discountRate;
        public final double  finalAmount;
        public final double  amountTendered;
        public final String  date;
 
        public PaymentResult(String txnId, double originalAmount, double discountRate,
                             double finalAmount, double amountTendered, String date) {
            this.txnId          = txnId;
            this.originalAmount = originalAmount;
            this.discountRate   = discountRate;
            this.finalAmount    = finalAmount;
            this.amountTendered = amountTendered;
            this.date           = date;
        }
    }
 
    // NEW CODE — generate unique transaction ID
    public static String generateTransactionId() {
        try {
            String timestamp = LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
            String shortUUID = UUID.randomUUID().toString()
                    .substring(0, 8).toUpperCase();
            return "TXN-" + timestamp + "-" + shortUUID;
        } catch (Exception e) {
            return "TXN-FALLBACK-" + System.currentTimeMillis();
        }
    }
 
    // MODIFIED CODE — mission payment with receipt + amount tendered
    public static PaymentResult processMissionPayment(MissionPayment payment,
                                                       String missionId,
                                                       String title,
                                                       double reward,
                                                       Scanner scanner) {
        try {
            if (reward <= 0) {
                System.out.println(" [X] Payment aborted: reward must be greater than zero.");
                return null;
            }
 
            System.out.println("\n ----------------------------------------");
            System.out.println(" [PAY] Mission Reward Prepayment");
            System.out.println(" [PAY] Mission : " + missionId + " - " + safeDisplay(title));
            System.out.printf( " [PAY] Reward  : %.2f gold coins%n", reward);
            System.out.printf( " [PAY] Discount: %.1f%%%n", payment.getDiscountRate() * 100);
 
            // Calculate final amount for tendering
            double afterDiscount = reward * (1 - payment.getDiscountRate());
            double finalAmount   = afterDiscount * 1.12;
            System.out.printf( " [PAY] Total Due (after discount + VAT): %.2f gp%n", finalAmount);
 
            // Collect amount tendered
            PaymentInputHandler inputHandler = new PaymentInputHandler(scanner);
            double tendered = inputHandler.askAmountTendered(finalAmount);
 
            // Call professor's PaymentFramework (void, unchanged)
            payment.processInvoice(reward);
 
            String txnId = generateTransactionId();
            String date  = LocalDate.now().toString();
 
            // Print receipt
            inputHandler.printReceipt(txnId, "MISSION", missionId,
                    title, reward, payment.getDiscountRate(), tendered, date);
 
            System.out.println(" [OK] Transaction ID: " + txnId);
            System.out.println(" ----------------------------------------");
 
            return new PaymentResult(txnId, reward, payment.getDiscountRate(),
                                     finalAmount, tendered, date);
 
        } catch (Exception e) {
            System.out.println(" [X] Payment processing error: " + e.getMessage());
            System.out.println(" [X] Transaction aborted.");
            System.out.println(" ----------------------------------------");
            return null;
        }
    }
 
    // MODIFIED CODE — room payment with receipt + amount tendered
    public static PaymentResult processRoomPayment(RoomPayment payment,
                                                    String guestName,
                                                    String roomType,
                                                    String checkIn,
                                                    String checkOut,
                                                    String requestId,
                                                    Scanner scanner) {
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
            System.out.printf( " [PAY] Discount  : %.1f%%%n", payment.getDiscountRate() * 100);
 
            double afterDiscount = baseCost * (1 - payment.getDiscountRate());
            double finalAmount   = afterDiscount * 1.12;
            System.out.printf( " [PAY] Total Due (after discount + VAT): %.2f gp%n", finalAmount);
 
            PaymentInputHandler inputHandler = new PaymentInputHandler(scanner);
            double tendered = inputHandler.askAmountTendered(finalAmount);
 
            // Call  PaymentFramework (void, unchanged)
            payment.processInvoice(baseCost);
 
            String txnId = generateTransactionId();
            String date  = LocalDate.now().toString();
 
            String description = guestName + " (" + roomType + ")";
            inputHandler.printReceipt(txnId, "ROOM", requestId,
                    description, baseCost, payment.getDiscountRate(), tendered, date);
 
            System.out.println(" [OK] Transaction ID: " + txnId);
            System.out.println(" ----------------------------------------");
 
            return new PaymentResult(txnId, baseCost, payment.getDiscountRate(),
                                     finalAmount, tendered, date);
 
        } catch (Exception e) {
            System.out.println(" [X] Payment processing error: " + e.getMessage());
            System.out.println(" [X] Transaction aborted.");
            System.out.println(" ----------------------------------------");
            return null;
        }
    }
     // NEW CODE — process posting fee (discount allowed, receipt labeled MISSION_POST_FEE)
    public static PaymentResult processMissionPostFee(MissionPostPayment payment,
                                                       String missionId,
                                                       String title,
                                                       double postingFee,
                                                       Scanner scanner) {
        try {
            if (postingFee <= 0) {
                System.out.println(" [X] Posting fee aborted: invalid fee amount.");
                return null;
            }
 
            double afterDiscount = postingFee * (1 - payment.getDiscountRate());
            double finalAmount   = afterDiscount * 1.12;
 
            System.out.println("\n ----------------------------------------");
            System.out.println(" [PAY] Mission Posting Fee");
            System.out.println(" [PAY] Mission  : " + missionId + " - " + safeDisplay(title));
            System.out.printf( " [PAY] Post Fee : %.2f gold coins%n", postingFee);
            System.out.printf( " [PAY] Discount : %.1f%%%n", payment.getDiscountRate() * 100);
            System.out.printf( " [PAY] Total Due: %.2f gp%n", finalAmount);
 
            PaymentInputHandler inputHandler = new PaymentInputHandler(scanner);
            double tendered = inputHandler.askAmountTendered(finalAmount);
 
            // Call professor's PaymentFramework — untouched
            payment.processInvoice(postingFee);
 
            String txnId = generateTransactionId();
            String date  = LocalDate.now().toString();
 
            inputHandler.printReceipt(txnId, "MISSION_POST_FEE", missionId,
                    "Posting Fee: " + title, postingFee,
                    payment.getDiscountRate(), tendered, date);
 
            System.out.println(" [OK] Posting Fee Paid! Transaction ID: " + txnId);
            System.out.println(" ----------------------------------------");
 
            return new PaymentResult(txnId, postingFee, payment.getDiscountRate(),
                                     finalAmount, tendered, date);
 
        } catch (Exception e) {
            System.out.println(" [X] Posting fee error: " + e.getMessage());
            System.out.println(" [X] Transaction aborted.");
            return null;
        }
    }
 
    // NEW CODE — process mission reward (NO discount, receipt labeled MISSION_REWARD)
    public static PaymentResult processMissionReward(MissionRewardPayment payment,
                                                      String missionId,
                                                      String title,
                                                      double reward,
                                                      Scanner scanner) {
        try {
            if (reward <= 0) {
                System.out.println(" [X] Reward payment aborted: invalid reward amount.");
                return null;
            }
 
            // NO discount — full reward always
            double finalAmount = reward * 1.12;
 
            System.out.println("\n ----------------------------------------");
            System.out.println(" [PAY] Mission Reward Prepayment");
            System.out.println(" [PAY] Mission  : " + missionId + " - " + safeDisplay(title));
            System.out.printf( " [PAY] Reward   : %.2f gold coins%n", reward);
            System.out.println(" [PAY] Discount : NONE (adventurer reward is full amount)");
            System.out.printf( " [PAY] Total Due: %.2f gp%n", finalAmount);
 
            PaymentInputHandler inputHandler = new PaymentInputHandler(scanner);
            double tendered = inputHandler.askAmountTendered(finalAmount);
 
            // Call professor's PaymentFramework — untouched
            payment.processInvoice(reward);
 
            String txnId = generateTransactionId();
            String date  = LocalDate.now().toString();
 
            inputHandler.printReceipt(txnId, "MISSION_REWARD", missionId,
                    "Reward Prepayment: " + title, reward,
                    0.0,   // NO discount on receipt
                    tendered, date);
 
            System.out.println(" [OK] Reward Prepaid! Transaction ID: " + txnId);
            System.out.println(" ----------------------------------------");
 
            return new PaymentResult(txnId, reward, 0.0,
                                     finalAmount, tendered, date);
 
        } catch (Exception e) {
            System.out.println(" [X] Reward payment error: " + e.getMessage());
            System.out.println(" [X] Transaction aborted.");
            return null;
        }
    }
 
 
    private static String safeDisplay(String s) {
        if (s == null) return "N/A";
        String trimmed = s.trim();
        return trimmed.isEmpty() ? "N/A" : trimmed;
    }
}
 
