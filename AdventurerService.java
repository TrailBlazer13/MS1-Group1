//updated
package services;

import database.GuildDatabase;
import models.Adventurer;

import java.util.List;

public class AdventurerService {
    private final GuildDatabase db = GuildDatabase.getInstance();

    public List<Adventurer> getAllAdventurers()              { return db.getAllAdventurers(); }
    public List<Adventurer> searchByName(String keyword)    { return db.searchAdventurersByName(keyword); }
    public List<Adventurer> filterByRank(String rank)       { return db.filterAdventurersByRank(rank); }
    public List<Adventurer> filterByStatus(String status)   { return db.filterAdventurersByStatus(status); }
    public boolean adventurerExists(String name)            { return db.adventurerExists(name); }
    public boolean updateStatus(int id, String status)      { return db.updateAdventurerStatus(id, status); }
    public boolean appendHistory(String name, String entry) { return db.appendAdventurerHistory(name, entry); }
    public Adventurer getByName(String name)                { return db.getAdventurerByName(name); }
    public Adventurer getById(int id)                       { return db.getAdventurerById(id); }
    public int getTotalCount()                              { return db.getAdventurerCount(); }

    public boolean addAdventurer(String name, String rank, String joinDate,
                                  String classType, String contact) {
        return db.insertAdventurer(name, rank, joinDate, classType, contact);
    }
}
