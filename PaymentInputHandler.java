package services;
 
import java.util.Scanner;
 
/**
 * PaymentInputHandler — collects all clerk-driven payment inputs.
 * Handles: credit/tender amount, discount input with rank guide, receipt display.
 * Never modifies PaymentFramework.java.
 */
public class PaymentInputHandler {
 
    private final Scanner SCANNER;
 
    public PaymentInputHandler(Scanner scanner) {
        this.SCANNER = scanner;
    }
 
    // ══════════════════════════════════════════════════════════════
    //  AMOUNT TENDERED INPUT
    // ══════════════════════════════════════════════════════════════
 
    // NEW CODE — ask clerk for amount tendered (credit/cash)
    public double askAmountTendered(double minimumRequired) {
        while (true) {
            try {
                System.out.printf(" Enter amount tendered (minimum %.2f gold coins): ", minimumRequired);
                String raw = SCANNER.nextLine().trim();
                if (raw.isEmpty()) {
                    System.out.println(" [X] Amount cannot be empty. Please try again.");
                    continue;
                }
                double value = Double.parseDouble(raw);
                if (value <= 0) {
                    System.out.println(" [X] Amount must be greater than zero.");
                } else if (value < minimumRequired) {
                    System.out.printf(" [X] Insufficient amount. Minimum required: %.2f%n", minimumRequired);
                } else {
                    return value;
                }
            } catch (NumberFormatException e) {
                System.out.println(" [X] Invalid input. Please enter a valid number (e.g. 500.00).");
            } catch (Exception e) {
                System.out.println(" [X] Unexpected error. Please try again.");
            }
        }
    }
 
    // ══════════════════════════════════════════════════════════════
    //  DISCOUNT INPUT WITH RANK GUIDE
    // ══════════════════════════════════════════════════════════════
 
    // NEW CODE — display rank-based discount guide for missions
    public void printMissionDiscountGuide(String highestRank) {
        System.out.println("\n ┌─────────────────────────────────────────┐");
        System.out.println(" │         DISCOUNT GUIDE (MISSIONS)       │");
        System.out.println(" ├──────────────┬──────────────────────────┤");
        System.out.println(" │ Rank         │ Suggested Discount       │");
        System.out.println(" ├──────────────┼──────────────────────────┤");
        System.out.println(" │ BRONZE       │ 0% – 5%                 │");
        System.out.println(" │ SILVER       │ 5% – 10%                │");
        System.out.println(" │ GOLD         │ 10% – 15%               │");
        System.out.println(" │ PLATINUM     │ 15% – 20%               │");
        System.out.println(" └──────────────┴──────────────────────────┘");
        if (highestRank != null && !highestRank.equals("Unassigned")) {
            System.out.println(" [!] Highest assigned rank: " + highestRank);
        } else {
            System.out.println(" [!] No adventurers assigned. Suggested: 0%");
        }
        System.out.println(" [!] This is a GUIDE only. Clerk decides final discount.");
    }
 
    // NEW CODE — display room-type discount guide
    public void printRoomDiscountGuide(String roomType) {
        System.out.println("\n ┌─────────────────────────────────────────┐");
        System.out.println(" │          DISCOUNT GUIDE (ROOMS)         │");
        System.out.println(" ├──────────────────┬──────────────────────┤");
        System.out.println(" │ Room Type        │ Suggested Discount   │");
        System.out.println(" ├──────────────────┼──────────────────────┤");
        System.out.println(" │ Common Quarters  │ 0% – 5%             │");
        System.out.println(" │ Private Chamber  │ 5% – 10%            │");
        System.out.println(" │ Noble Suite      │ 10% – 20%           │");
        System.out.println(" └──────────────────┴──────────────────────┘");
        System.out.println(" [!] Selected room type: " + roomType);
        System.out.println(" [!] This is a GUIDE only. Clerk decides final discount.");
    }
 
    // NEW CODE — ask clerk to input discount percentage (0–100)
    public double askDiscountPercent() {
        while (true) {
            try {
                System.out.print(" Enter discount percentage to apply (0 to 100): ");
                String raw = SCANNER.nextLine().trim();
                if (raw.isEmpty()) {
                    System.out.println(" [X] Input cannot be empty. Enter 0 for no discount.");
                    continue;
                }
                double percent = Double.parseDouble(raw);
                if (percent < 0) {
                    System.out.println(" [X] Discount cannot be negative.");
                } else if (percent > 100) {
                    System.out.println(" [X] Discount cannot exceed 100%.");
                } else {
                    System.out.printf(" [OK] Discount of %.1f%% will be applied.%n", percent);
                    return percent / 100.0; // return as rate (e.g. 0.10)
                }
            } catch (NumberFormatException e) {
                System.out.println(" [X] Invalid input. Please enter a number (e.g. 10 for 10%).");
            } catch (Exception e) {
                System.out.println(" [X] Unexpected error. Please try again.");
            }
        }
    }
 
    // ══════════════════════════════════════════════════════════════
    //  BUILD PAYMENT OBJECTS
    // ══════════════════════════════════════════════════════════════
 
    // NEW CODE — build MissionPayment with clerk-supplied discount
    public MissionPayment buildMissionPayment(String highestRank) {
        printMissionDiscountGuide(highestRank);
        double discountRate = askDiscountPercent();
        // creditAmount and validPaymentMethod resolved after we know final amount
        // Pass large credit here; actual tender is collected in receipt flow
        return new MissionPayment(999999.99, true, discountRate);
    }
 
    // NEW CODE — build RoomPayment with clerk-supplied discount
    public RoomPayment buildRoomPayment(String roomType) {
        printRoomDiscountGuide(roomType);
        double discountRate = askDiscountPercent();
        return new RoomPayment(999999.99, true, discountRate);
    }
 
    // ══════════════════════════════════════════════════════════════
    //  RECEIPT GENERATION
    // ══════════════════════════════════════════════════════════════
 
    // NEW CODE — print full receipt after successful payment
    public void printReceipt(String txnId, String category, String referenceId,
                              String description, double originalAmount,
                              double discountRate, double amountTendered,
                              String date) {
        double discountAmt  = originalAmount * discountRate;
        double afterDiscount = originalAmount - discountAmt;
        double vatAmt        = afterDiscount * 0.12;
        double finalAmount   = afterDiscount + vatAmt;
        double change        = amountTendered - finalAmount;
 
        System.out.println("\n ╔══════════════════════════════════════════════════════════╗");
        System.out.println(" ║           FAIRYTALE'S GUILD HALL — OFFICIAL RECEIPT      ║");
        System.out.println(" ╠══════════════════════════════════════════════════════════╣");
        System.out.printf( " ║  Transaction ID : %-38s ║%n", safeTrunc(txnId, 38));
        System.out.printf( " ║  Category       : %-38s ║%n", category);
        System.out.printf( " ║  Reference ID   : %-38s ║%n", referenceId);
        System.out.printf( " ║  Description    : %-38s ║%n", safeTrunc(description, 38));
        System.out.printf( " ║  Date           : %-38s ║%n", date);
        System.out.println(" ╠══════════════════════════════════════════════════════════╣");
        System.out.printf( " ║  Original Amount : %35.2f gp ║%n", originalAmount);
        System.out.printf( " ║  Discount (%.1f%%)  : %34.2f gp ║%n", discountRate * 100, -discountAmt);
        System.out.printf( " ║  After Discount  : %35.2f gp ║%n", afterDiscount);
        System.out.printf( " ║  VAT (12%%)       : %35.2f gp ║%n", vatAmt);
        System.out.println(" ╠══════════════════════════════════════════════════════════╣");
        System.out.printf( " ║  TOTAL DUE       : %35.2f gp ║%n", finalAmount);
        System.out.printf( " ║  Amount Tendered : %35.2f gp ║%n", amountTendered);
        System.out.printf( " ║  CHANGE          : %35.2f gp ║%n", change);
        System.out.println(" ╠══════════════════════════════════════════════════════════╣");
        System.out.println(" ║         Thank you! May your quests be legendary.         ║");
        System.out.println(" ╚══════════════════════════════════════════════════════════╝");
    }
     // NEW CODE — print posting fee guide based on reward tier
    public void printPostingFeeGuide(double reward, String missionTitle) {
        double fee = MissionPostPayment.resolvePostingFee(reward);
        System.out.println("\n ┌─────────────────────────────────────────────┐");
        System.out.println(" │           MISSION POSTING FEE GUIDE         │");
        System.out.println(" ├──────────────────────┬──────────────────────┤");
        System.out.println(" │ Reward Tier          │ Posting Fee          │");
        System.out.println(" ├──────────────────────┼──────────────────────┤");
        System.out.printf( " │ Below 100 gp         │ %.2f gp             │%n", MissionPostPayment.FEE_BRONZE);
        System.out.printf( " │ 100 – 199 gp         │ %.2f gp             │%n", MissionPostPayment.FEE_SILVER);
        System.out.printf( " │ 200 – 399 gp         │ %.2f gp             │%n", MissionPostPayment.FEE_GOLD);
        System.out.printf( " │ 400+ gp              │ %.2f gp            │%n", MissionPostPayment.FEE_PLATINUM);
        System.out.println(" └──────────────────────┴──────────────────────┘");
        System.out.printf( " [!] Mission : %s%n", missionTitle);
        System.out.printf( " [!] Reward  : %.2f gp → Posting Fee: %.2f gp%n", reward, fee);
        System.out.println(" [!] Clerk may apply a discount to the POSTING FEE only.");
        System.out.println(" [!] This is a GUIDE only. Clerk decides final discount.");
    }
 
    // NEW CODE — build MissionPostPayment with clerk discount
    public MissionPostPayment buildMissionPostPayment(double reward, String missionTitle) {
        printPostingFeeGuide(reward, missionTitle);
        System.out.println("\n [STEP 1/2] POSTING FEE PAYMENT");
        double discountRate = askDiscountPercent();
        return new MissionPostPayment(999999.99, true, discountRate);
    }
 
// MODIFIED CODE — no discount prompt, no guide, just inform clerk
public MissionRewardPayment buildMissionRewardPayment() {
    System.out.println("\n [STEP 2/2] MISSION REWARD PREPAYMENT");
    System.out.println(" ----------------------------------------");
    System.out.println(" [!] Discount: NOT APPLICABLE");
    System.out.println(" [!] Mission rewards are always paid in FULL.");
    System.out.println(" [!] This guarantees adventurers receive their complete payment.");
    System.out.println(" ----------------------------------------");
    return new MissionRewardPayment(999999.99);  // no discount input — object built directly
}
    
    // NEW CODE — safe string truncator for receipt alignment
    private String safeTrunc(String s, int maxLen) {
        if (s == null) return "N/A";
        return s.length() > maxLen ? s.substring(0, maxLen - 3) + "..." : s;
    }
}
