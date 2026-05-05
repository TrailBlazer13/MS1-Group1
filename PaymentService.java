package services;

import database.GuildDatabase;
import java.util.List;

public class PaymentService {

    private final GuildDatabase DB = GuildDatabase.getInstance();

    public List<Object[]> getAllPayments() {
        return DB.getAllPayments();
    }

    public List<Object[]> getPaymentsByCategory(String category) {
        return DB.getPaymentsByCategory(category);
    }
}

