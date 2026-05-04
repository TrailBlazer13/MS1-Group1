package services;
 
import database.GuildDatabase;
import java.util.List;
 
/**
 * PaymentService — exposes payment record queries to the UI layer.
 * Follows the same pattern as AdventurerService, MissionService, etc.
 * Only GuildDatabase touches SQL.
 */
public class PaymentService {
 
    private final GuildDatabase db = GuildDatabase.getInstance();
 
    public List<Object[]> getAllPayments() {
        return db.getAllPayments();
    }
 
    public List<Object[]> getPaymentsByCategory(String category) {
        return db.getPaymentsByCategory(category);
    }
}

