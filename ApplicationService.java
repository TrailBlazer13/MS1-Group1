package services;

import models.Application;
import repository.GuildRepository;

import java.util.List;

public class ApplicationService {
    private final GuildRepository repo = GuildRepository.getInstance();

    public List<Application> getAllApplications() {
        return repo.getAllApplications();
    }

    public List<Application> getPendingApplications() {
        return repo.getPendingApplications();
    }

    public Application getApplicationById(int id) {
        return repo.getApplicationById(id);
    }

    public boolean applicationExists(String name) {
        return repo.applicationExists(name);
    }

    public boolean submitApplication(String name, String background, String rank, String date) {
        if (repo.applicationExists(name)) return false;
        return repo.insertApplication(name, background, rank, date);
    }

    public boolean approveApplication(int id) {
        Application a = repo.getApplicationById(id);
        if (a == null) return false;
        boolean updated = repo.updateApplicationStatus(id, "APPROVED");
        if (updated) {
            // Induct into adventurers table using background as class placeholder
            repo.insertAdventurer(a.getName(), a.getRank(),
                java.time.LocalDate.now().toString(), "Adventurer", "N/A");
        }
        return updated;
    }

    public boolean rejectApplication(int id) {
        return repo.updateApplicationStatus(id, "REJECTED");
    }
}
