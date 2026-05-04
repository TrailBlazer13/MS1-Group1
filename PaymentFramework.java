package services;
// Abstract class PaymentFramework

public abstract class PaymentFramework {
    protected double creditAmount;
    protected boolean validPaymentMethod;
    protected double discountRate;

    // Constructor
    public PaymentFramework(double creditAmount, boolean validPaymentMethod, double discountRate) {
        this.creditAmount = creditAmount;
        this.validPaymentMethod = validPaymentMethod;
        this.discountRate = discountRate;
    }

    // 1. Validate if there is enough credit or a valid payment method
    public boolean validatePayment(double amount) {
        if (validPaymentMethod && creditAmount >= amount) {
            System.out.println("Payment validation successful.");
            return true;
        } else {
            System.out.println("Payment validation failed. Insufficient credit or invalid payment method.");
            return false;
        }
    }

    // 2. Apply 12% VAT inclusive tax rate
    public double addVAT(double amount) {
        double vat = amount * 0.12;
        double totalWithVAT = amount + vat;
        System.out.println("Added 12% VAT: " + vat + ". Total with VAT: " + totalWithVAT);
        return totalWithVAT;
    }

    // 3. Apply discount
    public double applyDiscount(double amount) {
        double discountAmount = amount * discountRate;
        double afterDiscount = amount - discountAmount;
        System.out.println("Discount applied: " + discountAmount + ". Total after discount: " + afterDiscount);
        return afterDiscount;
    }

    // 4. Finalizing the transaction
    public void finalizeTransaction(double amount) {
        System.out.println("Transaction finalized for amount: " + amount);
        creditAmount -= amount;
        System.out.println("Remaining credit: " + creditAmount);
    }

    // 5. Concrete method - processInvoice (can be overridden)
    public void processInvoice(double originalAmount) {
        System.out.println("\n--- Processing Invoice ---");
        if (!validatePayment(originalAmount)) {
            System.out.println("Transaction aborted.");
            return;
        }

        double afterDiscount = applyDiscount(originalAmount);
        double finalAmount = addVAT(afterDiscount);
        finalizeTransaction(finalAmount);
        System.out.println("Invoice processed successfully.");
    }
}

