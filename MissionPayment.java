package services;
 
/**
 * MissionPayment — concrete PaymentFramework for mission reward prepayment.
 * Extends the professor's PaymentFramework without modifying it.
 * Transaction ID generation is handled by SafePaymentHandler.
 */
public class MissionPayment extends PaymentFramework {  // MODIFIED CODE
 
    public MissionPayment(double creditAmount, boolean validPaymentMethod, double discountRate) {
        super(creditAmount, validPaymentMethod, discountRate);
    }
 
    // Default factory: guild has ample credit, valid payment, no discount
    public static MissionPayment defaultPayment() {
        return new MissionPayment(9999.99, true, 0.0);
    }
 
    // getBaseFee() removed — reward is now dynamic, passed from mission data
}
