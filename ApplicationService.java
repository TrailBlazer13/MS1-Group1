package services;

import database.GuildDatabase;
import models.Application;

import java.time.LocalDate;
import java.util.List;

public class ApplicationService {
    private final GuildDatabase DB = GuildDatabase.getInstance();

    public List<Application> getAllApplications()      { return DB.getAllApplications(); }
    public List<Application> getPendingApplications()  { return DB.getPendingApplications(); }
    public Application getApplicationById(int id)      { return DB.getApplicationById(id); }
    public boolean applicationExists(String name)      { return DB.applicationExists(name); }

    public boolean submitApplication(String name, String background, String rank, String date) {
        if (DB.applicationExists(name)) return false;
        return DB.insertApplication(name, background, rank, date);
    }

    public boolean approveApplication(int id) {
        Application a = DB.getApplicationById(id);
        if (a == null) return false;
        boolean updated = DB.updateApplicationStatus(id, "APPROVED");
        if (updated) {
            DB.insertAdventurer(a.getName(), a.getRank(),
                LocalDate.now().toString(), "Adventurer", "N/A");
        }
        return updated;
    }

    public boolean rejectApplication(int id) {
        return DB.updateApplicationStatus(id, "REJECTED");
    }
}
