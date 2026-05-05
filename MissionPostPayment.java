package services;
 

public class MissionPostPayment extends PaymentFramework {  // NEW CODE
 
    // Fixed posting fee rates by rank tier (clerk sees these as a guide)
    public static final double FEE_BRONZE   = 30.00;
    public static final double FEE_SILVER   = 50.00;
    public static final double FEE_GOLD     = 80.00;
    public static final double FEE_PLATINUM = 120.00;
    public static final double FEE_DEFAULT  = 50.00;
 
    public MissionPostPayment(double creditAmount, boolean validPaymentMethod,
                               double discountRate) {
        super(creditAmount, validPaymentMethod, discountRate);
    }
 
    // NEW CODE — resolve posting fee based on mission reward tier
    public static double resolvePostingFee(double reward) {
        if      (reward >= 400) return FEE_PLATINUM;
        else if (reward >= 200) return FEE_GOLD;
        else if (reward >= 100) return FEE_SILVER;
        else                    return FEE_BRONZE;
    }
 
    // NEW CODE — exposes protected field without modifying PaymentFramework
    public double getDiscountRate() {
        return discountRate;
    }
}
