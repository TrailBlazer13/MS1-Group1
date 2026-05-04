//updated

package services;

import database.GuildDatabase;
import models.Application;

import java.time.LocalDate;
import java.util.List;

public class ApplicationService {
    private final GuildDatabase db = GuildDatabase.getInstance();

    public List<Application> getAllApplications()      { return db.getAllApplications(); }
    public List<Application> getPendingApplications()  { return db.getPendingApplications(); }
    public Application getApplicationById(int id)      { return db.getApplicationById(id); }
    public boolean applicationExists(String name)      { return db.applicationExists(name); }

    public boolean submitApplication(String name, String background, String rank, String date) {
        if (db.applicationExists(name)) return false;
        return db.insertApplication(name, background, rank, date);
    }

    public boolean approveApplication(int id) {
        Application a = db.getApplicationById(id);
        if (a == null) return false;
        boolean updated = db.updateApplicationStatus(id, "APPROVED");
        if (updated) {
            db.insertAdventurer(a.getName(), a.getRank(),
                LocalDate.now().toString(), "Adventurer", "N/A");
        }
        return updated;
    }

    public boolean rejectApplication(int id) {
        return db.updateApplicationStatus(id, "REJECTED");
    }
}
