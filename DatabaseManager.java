package database;

import java.sql.*;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:guildhall.db";
    private static DatabaseManager instance;
    private Connection connection;

    private DatabaseManager() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection(DB_URL);
            connection.createStatement().execute("PRAGMA foreign_keys = ON");
        } catch (Exception e) {
            System.err.println("[ERROR] Failed to connect to database: " + e.getMessage());
        }
    }

    public static DatabaseManager getInstance() {
        if (instance == null) instance = new DatabaseManager();
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }

    public void initializeDatabase() {
        String createApplications =
            "CREATE TABLE IF NOT EXISTS applications (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "name TEXT NOT NULL UNIQUE," +
            "background TEXT NOT NULL," +
            "rank TEXT NOT NULL," +
            "submission_date TEXT NOT NULL," +
            "status TEXT NOT NULL DEFAULT 'PENDING'" +
            ")";

        String createAdventurers =
            "CREATE TABLE IF NOT EXISTS adventurers (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "name TEXT NOT NULL UNIQUE," +
            "rank TEXT NOT NULL," +
            "join_date TEXT NOT NULL," +
            "status TEXT NOT NULL DEFAULT 'ACTIVE'," +
            "class_type TEXT NOT NULL," +
            "contact TEXT NOT NULL," +
            "history TEXT DEFAULT 'None'" +
            ")";

        String createMissions =
            "CREATE TABLE IF NOT EXISTS missions (" +
            "id TEXT PRIMARY KEY," +
            "title TEXT NOT NULL," +
            "status TEXT NOT NULL DEFAULT 'PENDING'," +
            "progress INTEGER NOT NULL DEFAULT 0," +
            "assigned TEXT DEFAULT 'Unassigned'," +
            "deadline TEXT NOT NULL," +
            "posted_date TEXT" +
            ")";

        String createRoomRequests =
            "CREATE TABLE IF NOT EXISTS room_requests (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "name TEXT NOT NULL," +
            "room_type TEXT NOT NULL," +
            "check_in TEXT NOT NULL," +
            "check_out TEXT NOT NULL," +
            "status TEXT NOT NULL DEFAULT 'PENDING'" +
            ")";

        String createRooms =
            "CREATE TABLE IF NOT EXISTS rooms (" +
            "room_id TEXT PRIMARY KEY," +
            "room_type TEXT NOT NULL," +
            "status TEXT NOT NULL DEFAULT 'AVAILABLE'," +
            "occupant TEXT DEFAULT 'None'," +
            "checkout_date TEXT DEFAULT 'N/A'" +
            ")";

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createApplications);
            stmt.execute(createAdventurers);
            stmt.execute(createMissions);
            stmt.execute(createRoomRequests);
            stmt.execute(createRooms);
            System.out.println("  [DB] Tables initialized successfully.");
        } catch (SQLException e) {
            System.err.println("[ERROR] Table creation failed: " + e.getMessage());
        }
    }

    public void insertSampleData() {
        insertSampleApplications();
        insertSampleAdventurers();
        insertSampleMissions();
        insertSampleRooms();
        insertSampleRoomRequests();
    }

    private void insertSampleApplications() {
        String sql = "INSERT OR IGNORE INTO applications (name, background, rank, submission_date, status) VALUES (?,?,?,?,?)";
        Object[][] data = {
            {"Elara Moonwhisper",  "Former royal mage, seeks greater purpose",    "GOLD",   "2025-01-10", "PENDING"},
            {"Theron Ashblade",    "Wandering swordsman from the eastern wastes", "SILVER", "2025-01-12", "PENDING"},
            {"Mira Stoneback",     "Dwarf healer from Ironpeak Monastery",        "BRONZE", "2025-01-14", "PENDING"},
            {"Garrick Foxfoot",    "Rogue and ex-thief seeking redemption",       "SILVER", "2025-01-15", "PENDING"},
            {"Sylvara Dawnbrook",  "Elven archer, trained in the Verdant Woods",  "GOLD",   "2025-01-18", "REJECTED"},
        };
        batchInsert(sql, data);
    }

    private void insertSampleAdventurers() {
        String sql = "INSERT OR IGNORE INTO adventurers (name, rank, join_date, status, class_type, contact, history) VALUES (?,?,?,?,?,?,?)";
        Object[][] data = {
            {"Aldric Stormforge", "PLATINUM", "2023-03-01", "ACTIVE",   "Warrior", "aldric@guild.net", "Slew the Basilisk of Greystone; Recovered the Sunken Chalice"},
            {"Lyria Emberveil",   "GOLD",     "2023-07-15", "ACTIVE",   "Mage",    "lyria@guild.net",  "Sealed the Rift of Dusk; Assisted in the Tower Siege"},
            {"Brom Ironfist",     "SILVER",   "2024-01-20", "ACTIVE",   "Paladin", "brom@guild.net",   "Escorted Merchant Caravan; Defended Fort Ashvale"},
            {"Senna Nightveil",   "GOLD",     "2024-02-10", "ACTIVE",   "Rogue",   "senna@guild.net",  "Infiltrated the Shadow Den; Retrieved stolen artifacts"},
            {"Dunric Flamecrest", "BRONZE",   "2024-06-05", "INACTIVE", "Ranger",  "dunric@guild.net", "Scouted Thornwood Pass"},
            {"Vessa Coldwater",   "SILVER",   "2024-08-22", "ACTIVE",   "Cleric",  "vessa@guild.net",  "Cured the Plague of Millhaven; Assisted expedition to Ruins"},
        };
        batchInsert(sql, data);
    }

    private void insertSampleMissions() {
        String sql = "INSERT OR IGNORE INTO missions (id, title, status, progress, assigned, deadline, posted_date) VALUES (?,?,?,?,?,?,?)";
        Object[][] data = {
            {"QST-01", "Retrieve the Dragon's Tear Crystal",  "POSTED",    40,  "Aldric Stormforge, Lyria Emberveil", "2025-03-01", "2025-01-20"},
            {"QST-02", "Eliminate the Bandit Chieftain",      "POSTED",    75,  "Brom Ironfist",                      "2025-02-25", "2025-01-22"},
            {"QST-03", "Escort the Merchant Convoy",          "COMPLETED", 100, "Senna Nightveil, Vessa Coldwater",   "2025-02-10", "2025-01-15"},
            {"QST-04", "Investigate the Haunted Watchtower",  "PENDING",   0,   "Unassigned",                         "2025-03-15", null},
            {"QST-05", "Gather Rare Moonpetal Herbs",         "UNPOSTED",  0,   "Unassigned",                         "2025-03-20", null},
            {"QST-06", "Defend Millhaven from Goblin Raids",  "FAILED",    30,  "Dunric Flamecrest",                  "2025-01-30", "2025-01-10"},
        };
        batchInsert(sql, data);
    }

    private void insertSampleRooms() {
        String sql = "INSERT OR IGNORE INTO rooms (room_id, room_type, status, occupant, checkout_date) VALUES (?,?,?,?,?)";
        Object[][] data = {
            {"R-01", "Common Quarters", "OCCUPIED",    "Aldric Stormforge", "2025-02-28"},
            {"R-02", "Common Quarters", "AVAILABLE",   "None",              "N/A"},
            {"R-03", "Private Chamber", "OCCUPIED",    "Lyria Emberveil",   "2025-03-05"},
            {"R-04", "Private Chamber", "AVAILABLE",   "None",              "N/A"},
            {"R-05", "Noble Suite",     "MAINTENANCE", "None",              "N/A"},
            {"R-06", "Common Quarters", "AVAILABLE",   "None",              "N/A"},
        };
        batchInsert(sql, data);
    }

    private void insertSampleRoomRequests() {
        String sql = "INSERT OR IGNORE INTO room_requests (name, room_type, check_in, check_out, status) VALUES (?,?,?,?,?)";
        Object[][] data = {
            {"Theron Ashblade",   "Common Quarters", "2025-03-01", "2025-03-07", "PENDING"},
            {"Mira Stoneback",    "Private Chamber", "2025-03-02", "2025-03-10", "PENDING"},
            {"Garrick Foxfoot",   "Common Quarters", "2025-03-05", "2025-03-08", "APPROVED"},
            {"Elara Moonwhisper", "Noble Suite",     "2025-03-10", "2025-03-15", "PENDING"},
        };
        batchInsert(sql, data);
    }

    private void batchInsert(String sql, Object[][] data) {
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            for (Object[] row : data) {
                for (int i = 0; i < row.length; i++) {
                    if (row[i] == null)              ps.setNull(i + 1, Types.NULL);
                    else if (row[i] instanceof Integer) ps.setInt(i + 1, (Integer) row[i]);
                    else                             ps.setString(i + 1, row[i].toString());
                }
                ps.executeUpdate();
            }
        } catch (SQLException e) {
           
        }
    }
}
