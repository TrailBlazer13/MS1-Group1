package services;

/**
 * MissionRewardPayment — full mission reward prepaid by the guild.
 * NO DISCOUNT ALLOWED. discountRate is permanently locked at 0.0.
 * PaymentFramework.java is NOT modified.
 */
public class MissionRewardPayment extends PaymentFramework {  // MODIFIED CODE

    public MissionRewardPayment(double creditAmount) {
        super(creditAmount, true, 0.0);  // 0.0 is hardcoded — cannot be changed
    }

    // MODIFIED CODE — override applyDiscount to skip it entirely for reward payments
    // This prevents PaymentFramework from printing discount lines for reward payments
    @Override
    public double applyDiscount(double amount) {
        // Intentionally do nothing — no discount on mission rewards
        return amount;  // return original amount untouched, no print
    }

    // MODIFIED CODE — always returns 0.0, no exceptions
    public double getDiscountRate() {
        return 0.0;
    }
}
