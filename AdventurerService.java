package services;

import models.Adventurer;
import repository.GuildRepository;

import java.util.List;

public class AdventurerService {
    private final GuildRepository repo = GuildRepository.getInstance();

    public List<Adventurer> getAllAdventurers() {
        return repo.getAllAdventurers();
    }

    public List<Adventurer> searchByName(String keyword) {
        return repo.searchAdventurersByName(keyword);
    }

    public List<Adventurer> filterByRank(String rank) {
        return repo.filterAdventurersByRank(rank);
    }

    public List<Adventurer> filterByStatus(String status) {
        return repo.filterAdventurersByStatus(status);
    }

    public boolean addAdventurer(String name, String rank, String joinDate,
                                  String classType, String contact) {
        return repo.insertAdventurer(name, rank, joinDate, classType, contact);
    }

    public boolean adventurerExists(String name) {
        return repo.adventurerExists(name);
    }

    public boolean updateStatus(int id, String status) {
        return repo.updateAdventurerStatus(id, status);
    }

    public boolean appendHistory(String name, String entry) {
        return repo.appendAdventurerHistory(name, entry);
    }

    public Adventurer getByName(String name) {
        return repo.getAdventurerByName(name);
    }

    public Adventurer getById(int id) {
        return repo.getAdventurerById(id);
    }

    public int getTotalCount() {
        return repo.getAdventurerCount();
    }
}
