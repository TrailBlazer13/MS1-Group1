package services;
 
public class MissionPayment extends PaymentFramework {
 
    public MissionPayment(double creditAmount, boolean validPaymentMethod, double discountRate) {
        super(creditAmount, validPaymentMethod, discountRate);
    }
 
    // NEW CODE — exposes protected field from PaymentFramework without modifying it
    public double getDiscountRate() {
        return discountRate;
    }
}
