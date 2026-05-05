package services;

import database.GuildDatabase;
import models.Adventurer;

import java.util.List;

public class AdventurerService {
    private final GuildDatabase DB = GuildDatabase.getInstance();

    public List<Adventurer> getAllAdventurers()              { return DB.getAllAdventurers(); }
    public List<Adventurer> searchByName(String keyword)    { return DB.searchAdventurersByName(keyword); }
    public List<Adventurer> filterByRank(String rank)       { return DB.filterAdventurersByRank(rank); }
    public List<Adventurer> filterByStatus(String status)   { return DB.filterAdventurersByStatus(status); }
    public boolean adventurerExists(String name)            { return DB.adventurerExists(name); }
    public boolean updateStatus(int id, String status)      { return DB.updateAdventurerStatus(id, status); }
    public boolean appendHistory(String name, String entry) { return DB.appendAdventurerHistory(name, entry); }
    public Adventurer getByName(String name)                { return DB.getAdventurerByName(name); }
    public Adventurer getById(int id)                       { return DB.getAdventurerById(id); }
    public int getTotalCount()                              { return DB.getAdventurerCount(); }

    public boolean addAdventurer(String name, String rank, String joinDate,
                                  String classType, String contact) {
        return DB.insertAdventurer(name, rank, joinDate, classType, contact);
    }
}
