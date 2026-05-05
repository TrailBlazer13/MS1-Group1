package ui;

import models.*;
import services.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class Menu {

    private final Scanner        SCANNER        = new Scanner(System.in);
    private final InputHandler   INPUT          = new InputHandler(SCANNER);
    private final ApplicationService APP_SERVICE  = new ApplicationService();
    private final AdventurerService  ADV_SERVICE  = new AdventurerService();
    private final MissionService     MSN_SERVICE  = new MissionService();
    private final RoomService        ROOM_SERVICE = new RoomService();
    private final PaymentService     PAYMENT_SERVICE = new PaymentService();

    public void start() {
        printBanner();
        int choice;
        do {
            try {
                printMainMenu();
                choice = INPUT.readInt();
                switch (choice) {
                    case 1 -> clerkDeskMenu();
                    case 0 -> System.out.println(
                            "\n May your blades stay sharp, traveler. Farewell!\n");
                    default -> System.out.println(" [X] Invalid option. Try again.");
                }
            } catch (Exception e) {
                System.out.println(" [X] Unexpected error in main menu. Recovering...");
                choice = -999;
            }
        } while (choice != 0);
    }

    private void clerkDeskMenu() {
        int choice;
        do {
            try {
                printClerkMenu();
                choice = INPUT.readInt();
                switch (choice) {
                    case 1 -> checkApplications();
                    case 2 -> viewAdventurers();
                    case 3 -> missionRequests();
                    case 4 -> missionStatus();
                    case 5 -> roomRequests();
                    case 6 -> roomStatus();
                    case 7 -> viewPaymentLedger();
                    case 0 -> System.out.println(" [<] Returning to main hall...");
                    default -> System.out.println(" [X] Unknown command. Try again.");
                }
            } catch (Exception e) {
                System.out.println(" [X] Unexpected error in clerk menu. Recovering...");
                choice = -999;
            }
        } while (choice != 0);
    }

    private void checkApplications() {
        int choice;
        do {
            sectionHeader("CHECK APPLICATIONS");
            List<Application> pending = APP_SERVICE.getPendingApplications();

            if (pending.isEmpty()) {
                System.out.println("  No current applications.");
            } else {
                printApplicationTableHeader();
                for (Application a : pending) {
                    System.out.printf("  [%d] %s%n", a.getId(), a);
                }
                printDivider();
                System.out.println("  Total pending: " + pending.size());
            }

            System.out.println();
            System.out.println("  1. Approve Application");
            System.out.println("  2. Reject Application");
            System.out.println("  3. Submit New Application");
            System.out.println("  4. View All Applications");
            System.out.println("  0. Back");
            System.out.print("\n  > ");
            choice = readInt();

            switch (choice) {
                case 1  -> approveApplication();
                case 2  -> rejectApplication();
                case 3  -> submitNewApplication();
                case 4  -> viewAllApplications();
                case 0  -> {}
                default -> System.out.println("  [X] Invalid choice.");
            }
        } while (choice != 0);
    }

    private void approveApplication() {
        System.out.print("  Enter Application ID to APPROVE: ");
        int id = readInt();
        if (id <= 0) {
            System.out.println("  [X] Invalid ID. Must be a positive number.");
            return;
        }
        Application a = APP_SERVICE.getApplicationById(id);
        if (a == null) {
            System.out.println("  [X] Application not found.");
            return;
        }
        if (!a.getStatus().equals("PENDING")) {
            System.out.println("  [X] Application is already " + a.getStatus() + ".");
            return;
        }
        System.out.printf("  Confirm APPROVE for '%s'? (y/n): ", a.getName());
        String confirm = SCANNER.nextLine().trim().toLowerCase();
        if (!confirm.equals("y")) {
            System.out.println("  [<] Action cancelled.");
            return;
        }
        if (APP_SERVICE.approveApplication(id)) {
            System.out.println("  [OK] Application APPROVED! '"
                + a.getName() + "' has been inducted into the Guild!");
        } else {
            System.out.println("  [X] Approval failed. The adventurer may already be registered.");
        }
    }

    private void rejectApplication() {
        System.out.print("  Enter Application ID to REJECT: ");
        int id = readInt();
        if (id <= 0) {
            System.out.println("  [X] Invalid ID. Must be a positive number.");
            return;
        }
        Application a = APP_SERVICE.getApplicationById(id);
        if (a == null) {
            System.out.println("  [X] Application not found.");
            return;
        }
        if (!a.getStatus().equals("PENDING")) {
            System.out.println("  [X] Application is already " + a.getStatus() + ".");
            return;
        }
        System.out.printf("  Confirm REJECT for '%s'? (y/n): ", a.getName());
        String confirm = SCANNER.nextLine().trim().toLowerCase();
        if (!confirm.equals("y")) {
            System.out.println("  [<] Action cancelled.");
            return;
        }
        if (APP_SERVICE.rejectApplication(id)) {
            System.out.println("  [X] Application REJECTED. Better luck next season.");
        } else {
            System.out.println("  [X] Rejection failed. Please try again.");
        }
    }

    private void submitNewApplication() {
        System.out.println("  -- Submit New Application --");

        String name = readNonEmpty("  Applicant Name       : ");
        if (APP_SERVICE.applicationExists(name)) {
            System.out.println("  [X] An application already exists for '" + name + "'.");
            return;
        }

        String background = readNonEmpty("  Background / Story   : ");
        String rank       = readRank("  Requested Rank");
        String date       = readDate("  Submission Date");

        if (APP_SERVICE.submitApplication(name, background, rank, date)) {
            System.out.println("  [OK] Application submitted successfully for '" + name + "'!");
            System.out.println("  [DB] Record saved to database.");
        } else {
            System.out.println("  [X] Submission failed. Duplicate application detected.");
        }
    }

    private void viewAllApplications() {
        sectionHeader("ALL APPLICATIONS");
        List<Application> all = APP_SERVICE.getAllApplications();
        if (all.isEmpty()) {
            System.out.println("  No applications found.");
            return;
        }
        printApplicationTableHeader();
        for (Application a : all) {
            System.out.printf("  [%d] %s%n", a.getId(), a);
        }
        printDivider();
        System.out.println("  Total: " + all.size());
        pressEnter();
    }

    private void viewAdventurers() {
        int choice;
        do {
            sectionHeader("VIEW REGISTERED ADVENTURERS");
            System.out.println("  1. View All Adventurers");
            System.out.println("  2. Search by Name");
            System.out.println("  3. Filter by Rank");
            System.out.println("  4. Filter by Status");
            System.out.println("  0. Back");
            System.out.print("\n  > ");
            choice = readInt();

            switch (choice) {
                case 1 -> displayAdventurers(ADV_SERVICE.getAllAdventurers());
                case 2 -> {
                    String kw = readNonEmpty("  Enter name keyword: ");
                    displayAdventurers(ADV_SERVICE.searchByName(kw));
                }
                case 3 -> {
                    String rank = readRank("  Filter by Rank");
                    displayAdventurers(ADV_SERVICE.filterByRank(rank));
                }
                case 4 -> {
                    String status = readStatus();
                    displayAdventurers(ADV_SERVICE.filterByStatus(status));
                }
                case 0  -> {}
                default -> System.out.println("  [X] Invalid choice.");
            }
        } while (choice != 0);
    }

    private void displayAdventurers(List<Adventurer> list) {
        if (list.isEmpty()) {
            System.out.println("  No current registered adventurers.");
            return;
        }
        System.out.println();
        for (Adventurer a : list) {
            a.printDetailed();
        }
        System.out.println("  Total count: " + list.size());
        pressEnter();
    }

// MODIFIED CODE - Menu.java - missionRequests() method

private void missionRequests() {
    int choice;
    do {
        sectionHeader("MISSION REQUESTS");
        List<Mission> missions = MSN_SERVICE.getAllMissions();
        printMissionTableHeader();
        for (Mission m : missions) {
            String tag = MSN_SERVICE.isOverdue(m) ? " [OVERDUE]" : "";
            String t = m.getTitle().length() > 37 ? m.getTitle().substring(0, 34) + "..." : m.getTitle();
            System.out.printf("%-8s %-38s %-10s %3d%% %-12s%s\n", 
                m.getId(), t, m.getStatus(), m.getProgress(), m.getDeadline(), tag);
        }
        printDivider();
        
        System.out.println(" 1. Deploy Mission");
        System.out.println(" 2. Add New Mission Request");
        System.out.println(" 3. Deny Mission Request");
        System.out.println(" 0. Back");
        System.out.print("\n > ");
        choice = readInt();
        
        switch (choice) {
            case 1 -> {
                String id = readNonEmpty(" Mission ID to DEPLOY: ").toUpperCase().trim();
                Mission m = MSN_SERVICE.getMissionById(id);
                if (m == null) {
                    System.out.println(" [X] Mission ID " + id + " not found.");
                } else if (!m.getStatus().equals("PENDING") && !m.getStatus().equals("UNPOSTED")) {
                    System.out.println(" [X] Mission " + id + " cannot be deployed.");
                    System.out.println("     Current status: " + m.getStatus());
                    System.out.println("     Mission must be PENDING or UNPOSTED to deploy.");
                } else if (MSN_SERVICE.deployMission(id)) {
                    System.out.println(" [OK] Mission " + id + " DEPLOYED!");
                    System.out.println(" [OK] Status set to: POSTED");
                    System.out.println(" [DB] Mission is now available for adventurer assignment.");
                } else {
                    System.out.println(" [X] Deployment failed. Check payment or try again.");
                }
            }
            case 2 -> addNewMissionRequest();
            case 3 -> {
                String id = readNonEmpty(" Mission ID to DENY: ").toUpperCase().trim();
                if (MSN_SERVICE.getMissionById(id) == null) {
                    System.out.println(" [X] Mission ID " + id + " not found.");
                } else if (MSN_SERVICE.denyMission(id)) {
                    System.out.println(" [X] Mission " + id + " DENIED.");
                    System.out.println(" [DB] Status updated in database.");
                } else {
                    System.out.println(" [X] Could not deny. Mission must be in PENDING state.");
                }
            }
            case 0 -> {}
            default -> System.out.println(" [X] Invalid choice.");
        }
    } while (choice != 0);
}  // <-- THIS CLOSES missionRequests() method

// ADD THIS METHOD AFTER THE CLOSING BRACE (outside missionRequests)
private void addNewMissionRequest() {
    try {
        System.out.println(" -- Register New Mission Request --");
        String title    = readNonEmpty(" Mission Title  : ");
        String deadline = readDate(" Deadline Date");
        double reward   = readPositiveDouble(" Mission Reward (gold coins)");

        if (MSN_SERVICE.addMission(title, deadline, reward)) {
            System.out.println(" [OK] Mission request registered successfully!");
            System.out.println(" [DB] New mission saved to database.");
        } else {
            System.out.println(" [X] Failed to register mission. Please try again.");
        }
    } catch (Exception e) {
        System.out.println(" [X] Error registering mission. Returning to menu.");
    }
}

    private void missionStatus() {
        int choice;
        do {
            sectionHeader("MISSION STATUS");
            System.out.println("  1. View Specific Mission Details");
            System.out.println("  2. Update Mission Status");
            System.out.println("  3. Assign Adventurer to Mission");
            System.out.println("  4. View Overdue Missions");
            System.out.println("  0. Back");
            System.out.print("\n  > ");
            choice = readInt();

            switch (choice) {
                case 1  -> viewMissionDetails();
                case 2  -> updateMissionStatus();
                case 3  -> assignToMission();
                case 4  -> viewOverdueMissions();
                case 0  -> {}
                default -> System.out.println("  [X] Invalid choice.");
            }
        } while (choice != 0);
    }

    private void viewMissionDetails() {
        String id = readNonEmpty("  Enter Mission ID: ").toUpperCase().trim();
        Mission m = MSN_SERVICE.getMissionById(id);
        if (m == null) {
            System.out.println("  [X] Mission not found or expired.");
            return;
        }

        System.out.println();
        m.printDetailed();

        if (MSN_SERVICE.isOverdue(m)) {
            System.out.println("  [!] WARNING: This mission is OVERDUE!");
        }

        System.out.println();
        System.out.println("  -- Team Contributions --");
        if (m.getAssigned().equals("Unassigned")) {
            System.out.println("  No adventurers assigned yet.");
        } else {
            String[] members = m.getAssigned().split(",");
            int share = members.length > 0 ? m.getProgress() / members.length : 0;
            for (String member : members) {
                System.out.printf("  %-25s -> ~%d%% contribution%n", member.trim(), share);
            }
        }
        pressEnter();
    }

    private void updateMissionStatus() {
        String id = readNonEmpty("  Mission ID to update: ").toUpperCase().trim();
        Mission m = MSN_SERVICE.getMissionById(id);
        if (m == null) {
            System.out.println("  [X] Mission not found or expired.");
            return;
        }

        System.out.println("  Current Status  : " + m.getStatus());
        System.out.println("  Current Progress: " + m.getProgress() + "%");
        System.out.println();
        System.out.println("  1. Mark as COMPLETED (100%)");
        System.out.println("  2. Mark as FAILED");
        System.out.println("  3. Update Progress %");
        System.out.print("  > ");
        int sub = readInt();

        switch (sub) {
            case 1 -> {
                if (MSN_SERVICE.updateMissionStatus(id, "COMPLETED", 100)) {
                    if (!m.getAssigned().equals("Unassigned")) {
                        for (String name : m.getAssigned().split(",")) {
                            ADV_SERVICE.appendHistory(
                                name.trim(), "Completed: " + m.getTitle());
                        }
                    }
                    System.out.println("  [OK] Mission marked COMPLETED! The guild rejoices!");
                    System.out.println("  [DB] Status and adventurer histories updated.");
                } else {
                    System.out.println("  [X] Update failed. Please try again.");
                }
            }
            case 2 -> {
                if (MSN_SERVICE.updateMissionStatus(id, "FAILED", m.getProgress())) {
                    System.out.println("  [X] Mission marked FAILED. The guild mourns.");
                    System.out.println("  [DB] Status updated in database.");
                } else {
                    System.out.println("  [X] Update failed. Please try again.");
                }
            }
            case 3 -> {
                System.out.print("  Enter new progress (0-100): ");
                int progress = readInt();
                if (progress < 0 || progress > 100) {
                    System.out.println("  [X] Invalid progress value. Must be between 0 and 100.");
                    return;
                }
                if (MSN_SERVICE.updateMissionStatus(id, m.getStatus(), progress)) {
                    System.out.println("  [OK] Progress updated to " + progress + "%.");
                    System.out.println("  [DB] Progress saved to database.");
                } else {
                    System.out.println("  [X] Update failed. Please try again.");
                }
            }
            default -> System.out.println("  [<] Cancelled.");
        }
    }

    private void assignToMission() {
        String missionId = readNonEmpty("  Mission ID       : ").toUpperCase().trim();
        Mission m = MSN_SERVICE.getMissionById(missionId);
        if (m == null) {
            System.out.println("  [X] Mission not found or expired.");
            return;
        }

        String advName = readNonEmpty("  Adventurer Name  : ");
        if (!ADV_SERVICE.adventurerExists(advName)) {
            System.out.println("  [X] No adventurer found with the name '" + advName + "'.");
            System.out.println("  [!] Make sure the name matches exactly as registered.");
            return;
        }

        if (MSN_SERVICE.assignAdventurer(missionId, advName)) {
            System.out.println("  [OK] " + advName
                + " has been assigned to mission " + missionId + "!");
            System.out.println("  [DB] Assignment saved to database.");
        } else {
            System.out.println("  [X] Assignment failed. Please try again.");
        }
    }

    private void viewOverdueMissions() {
        sectionHeader("OVERDUE MISSIONS");
        List<Mission> all = MSN_SERVICE.getAllMissions();
        boolean found = false;
        printMissionTableHeader();
        for (Mission m : all) {
            if (MSN_SERVICE.isOverdue(m)) {
                String t = m.getTitle().length() > 37
                    ? m.getTitle().substring(0, 34) + "..."
                    : m.getTitle();
                System.out.printf("  %-8s %-38s %-10s %3d%%  %-12s [!]%n",
                    m.getId(), t, m.getStatus(), m.getProgress(), m.getDeadline());
                found = true;
            }
        }
        if (!found) {
            System.out.println("  No overdue missions. The guild is on schedule!");
        }
        printDivider();
        pressEnter();
    }

    private void roomRequests() {
        int choice;
        do {
            sectionHeader("ROOM REQUESTS");
            List<Object[]> requests = ROOM_SERVICE.getAllRoomRequests();

            if (requests.isEmpty()) {
                System.out.println("  No pending requests.");
            } else {
                printRoomRequestHeader();
                for (Object[] r : requests) {
                    System.out.printf("  [%d] %-22s %-18s %-12s %-12s %-10s%n",
                        r[0], r[1], r[2], r[3], r[4], r[5]);
                }
                printDivider();
            }

            System.out.println();
            System.out.println("  1. Approve Request");
            System.out.println("  2. Deny Request");
            System.out.println("  3. View Only Pending Requests");
            System.out.println("  4. Sort by Status");
            System.out.println("  5. Submit New Room Request");
            System.out.println("  0. Back");
            System.out.print("\n  > ");
            choice = readInt();

            switch (choice) {
                case 1 -> {
                    System.out.print("  Request ID to APPROVE: ");
                    int id = readInt();
                    if (id <= 0) {
                        System.out.println("  [X] Invalid ID.");
                    } else if (ROOM_SERVICE.approveRoomRequest(id)) {
                        System.out.println("  [OK] Room request approved and room assigned!");
                        System.out.println("  [DB] Room status updated in database.");
                    }
                }
                case 2 -> {
                    System.out.print("  Request ID to DENY: ");
                    int id = readInt();
                    if (id <= 0) {
                        System.out.println("  [X] Invalid ID.");
                    } else if (ROOM_SERVICE.denyRoomRequest(id)) {
                        System.out.println("  [X] Room request denied.");
                        System.out.println("  [DB] Status updated in database.");
                    } else {
                        System.out.println("  [X] Could not deny. Request not found or already processed.");
                    }
                }
                case 3 -> {
                    List<Object[]> pending = ROOM_SERVICE.getPendingRoomRequests();
                    if (pending.isEmpty()) {
                        System.out.println("  No pending requests.");
                    } else {
                        printRoomRequestHeader();
                        for (Object[] r : pending) {
                            System.out.printf("  [%d] %-22s %-18s %-12s %-12s %-10s%n",
                                r[0], r[1], r[2], r[3], r[4], r[5]);
                        }
                        printDivider();
                    }
                    pressEnter();
                }
                case 4 -> {
                    requests.sort((a, b) -> ((String) a[5]).compareTo((String) b[5]));
                    printRoomRequestHeader();
                    for (Object[] r : requests) {
                        System.out.printf("  [%d] %-22s %-18s %-12s %-12s %-10s%n",
                            r[0], r[1], r[2], r[3], r[4], r[5]);
                    }
                    printDivider();
                    pressEnter();
                }
                case 5  -> submitNewRoomRequest();
                case 0  -> {}
                default -> System.out.println("  [X] Invalid choice.");
            }
        } while (choice != 0);
    }

    private void submitNewRoomRequest() {
        System.out.println("  -- New Room Request --");

        String name     = readNonEmpty("  Guest Name      : ");
        String roomType = readRoomType();
        String checkIn  = readDate("  Check-in Date");
        String checkOut = readDateAfter("  Check-out Date", checkIn);
        if (checkOut == null) return;

        if (ROOM_SERVICE.addRoomRequest(name, roomType, checkIn, checkOut)) {
            System.out.println("  [OK] Room request submitted for '" + name + "'!");
            System.out.println("  [DB] Request saved to database.");
        } else {
            System.out.println("  [X] Failed to submit room request. Please try again.");
        }
    }

    private void roomStatus() {
        int choice;
        do {
            sectionHeader("ROOM STATUS");

            int autoChecked = ROOM_SERVICE.autoCheckoutExpired();
            if (autoChecked > 0) {
                System.out.println("  [Auto] " + autoChecked
                    + " room(s) released after checkout date passed.");
                System.out.println("  [DB] Room statuses updated automatically.");
            }

            int available   = ROOM_SERVICE.getRoomCountByStatus("AVAILABLE");
            int occupied    = ROOM_SERVICE.getRoomCountByStatus("OCCUPIED");
            int maintenance = ROOM_SERVICE.getRoomCountByStatus("MAINTENANCE");
            System.out.printf("  Available: %d  |  Occupied: %d  |  Maintenance: %d%n%n",
                available, occupied, maintenance);

            List<Room> rooms = ROOM_SERVICE.getAllRooms();
            System.out.printf("  %-6s | %-18s | %-12s | %-22s | %s%n",
                "Room", "Type", "Status", "Occupant", "Checkout");
            printDivider();
            for (Room r : rooms) System.out.println(r.toString());
            printDivider();

            System.out.println();
            System.out.println("  1. Checkout Room (release to available)");
            System.out.println("  2. Set Room to Maintenance");
            System.out.println("  3. Set Room to Available");
            System.out.println("  4. View Room Details");
            System.out.println("  0. Back");
            System.out.print("\n  > ");
            choice = readInt();

            switch (choice) {
                case 1 -> {
                    String rid = readNonEmpty("  Room ID to checkout: ").toUpperCase().trim();
                    if (ROOM_SERVICE.checkoutRoom(rid)) {
                        System.out.println("  [OK] Room " + rid + " checked out and is now AVAILABLE.");
                        System.out.println("  [DB] Room status updated in database.");
                    }
                }
                case 2 -> {
                    String rid = readNonEmpty("  Room ID to set MAINTENANCE: ").toUpperCase().trim();
                    if (ROOM_SERVICE.setRoomMaintenance(rid)) {
                        System.out.println("  [OK] Room " + rid + " set to MAINTENANCE.");
                        System.out.println("  [DB] Room status updated in database.");
                    } else {
                        System.out.println("  [X] Cannot set to maintenance. Room may be OCCUPIED.");
                    }
                }
                case 3 -> {
                    String rid = readNonEmpty("  Room ID to set AVAILABLE: ").toUpperCase().trim();
                    if (ROOM_SERVICE.setRoomAvailable(rid)) {
                        System.out.println("  [OK] Room " + rid + " set to AVAILABLE.");
                        System.out.println("  [DB] Room status updated in database.");
                    } else {
                        System.out.println("  [X] Failed to update room. Room ID not found.");
                    }
                }
                case 4 -> {
                    String rid = readNonEmpty("  Room ID to view: ").toUpperCase().trim();
                    Room r = ROOM_SERVICE.getRoomById(rid);
                    if (r == null) {
                        System.out.println("  [X] Room not found.");
                    } else {
                        System.out.println();
                        System.out.println("  +---------------------------------------+");
                        System.out.printf( "  | Room ID    : %-22s |%n", r.getRoomId());
                        System.out.printf( "  | Type       : %-22s |%n", r.getRoomType());
                        System.out.printf( "  | Status     : %-22s |%n", r.getStatus());
                        System.out.printf( "  | Occupant   : %-22s |%n", r.getOccupant());
                        System.out.printf( "  | Checkout   : %-22s |%n", r.getCheckoutDate());
                        System.out.println("  +---------------------------------------+");
                        pressEnter();
                    }
                }
                case 0  -> {}
                default -> System.out.println("  [X] Invalid choice.");
            }
        } while (choice != 0);
    }

    private void viewPaymentLedger() {
        int choice;
        do {
            sectionHeader("PAYMENT LEDGER");
            System.out.println(" 1. All Transactions");
            System.out.println(" 2. Mission Payments Only");
            System.out.println(" 3. Room Payments Only");
            System.out.println(" 0. Back");
            System.out.print("\n > ");
            choice = readInt();

            switch (choice) {
                case 1 -> displayPayments(PAYMENT_SERVICE.getAllPayments(), "ALL");
                case 2 -> displayPayments(PAYMENT_SERVICE.getPaymentsByCategory("MISSION"), "MISSION");
                case 3 -> displayPayments(PAYMENT_SERVICE.getPaymentsByCategory("ROOM"), "ROOM");
                case 0 -> {}
                default -> System.out.println(" [X] Invalid choice.");
            }
        } while (choice != 0);
    }

    private void displayPayments(java.util.List<Object[]> payments, String label) {
        if (payments.isEmpty()) {
            System.out.println(" No " + label.toLowerCase() + " payment records found.");
            pressEnter();
            return;
        }
        printDivider();
        System.out.printf(" %-20s %-8s %-10s %-35s %10s %-10s%n",
                "Transaction ID", "Cat.", "Ref ID", "Description", "Amount", "Date");
        printDivider();
        double total = 0;
        for (Object[] p : payments) {
            String txn  = safeStr(p[1]);
            String cat  = safeStr(p[2]);
            String ref  = safeStr(p[3]);
            String desc = safeStr(p[4]);
            double amt  = safeDouble(p[5]);
            String date = safeStr(p[7]);

            String shortDesc = desc.length() > 34 ? desc.substring(0, 31) + "..." : desc;
            String shortTxn  = txn.length()  > 19 ? txn.substring(0, 16)  + "..." : txn;

            System.out.printf(" %-20s %-8s %-10s %-35s %10.2f %-10s%n",
                    shortTxn, cat, ref, shortDesc, amt, date);
            total += amt;
        }
        printDivider();
        System.out.printf(" Total collected: %.2f gold coins | Records: %d%n",
                total, payments.size());
        pressEnter();
    }

    // ══════════════════════════════════════════════════════════════
    //  PRINT HELPERS
    // ══════════════════════════════════════════════════════════════

    private void printBanner() {
        System.out.println();
        System.out.println("  +============================================================+");
        System.out.println("  |      ***  FAIRYTALE'S GUILD HALL ADMIN SYSTEM  ***         |");
        System.out.println("  |           Where Heroes Are Forged & Legends Born           |");
        System.out.println("  +============================================================+");
        System.out.println();
    }

    private void printMainMenu() {
        System.out.println();
        System.out.println("  =============================================");
        System.out.println("   Welcome to Fairytale's Guild Hall Admin Interface!");
        System.out.println("  =============================================");
        System.out.println("   1. Clerk Desk");
        System.out.println("   0. Exit");
        System.out.print("\n   > ");
    }

    private void printClerkMenu() {
        System.out.println();
        System.out.println("  =============================================");
        System.out.println("               CLERK DESK MENU");
        System.out.println("  =============================================");
        System.out.println("   1. Check Applications");
        System.out.println("   2. View Adventurers");
        System.out.println("   3. Mission Requests");
        System.out.println("   4. Mission Status");
        System.out.println("   5. Room Requests");
        System.out.println("   6. Room Status");
        System.out.println("   7. View Payment Ledger");
        System.out.println("   0. Back");
        System.out.print("\n   > ");
    }

    private void sectionHeader(String title) {
        System.out.println();
        System.out.println("  +============================================================+");
        System.out.printf( "  |  [*]  %-52s|%n", title);
        System.out.println("  +============================================================+");
    }

    private void printDivider() {
        System.out.println("  ------------------------------------------------------------");
    }

    private void printApplicationTableHeader() {
        printDivider();
        System.out.printf("  %-4s %-25s %-35s %-8s %-12s %-10s%n",
            "ID", "Name", "Background", "Rank", "Date", "Status");
        printDivider();
    }

    private void printMissionTableHeader() {
        printDivider();
        System.out.printf("  %-8s %-38s %-10s %-5s %-12s%n",
            "ID", "Title", "Status", "Prog", "Deadline");
        printDivider();
    }

    private void printRoomRequestHeader() {
        printDivider();
        System.out.printf("  %-4s %-22s %-18s %-12s %-12s %-10s%n",
            "ID", "Name", "Room Type", "Check-in", "Check-out", "Status");
        printDivider();
    }

    // ══════════════════════════════════════════════════════════════
    //  INPUT DELEGATES
    // ══════════════════════════════════════════════════════════════

    private int readInt()                                    { return INPUT.readInt(); }
    private String readNonEmpty(String prompt)               { return INPUT.readNonEmpty(prompt); }
    private String readDate(String label)                    { return INPUT.readDate(label); }
    private String readDateAfter(String label, String ci)    { return INPUT.readDateAfter(label, ci); }
    private String readRank(String label)                    { return INPUT.readRank(label); }
    private String readStatus()                              { return INPUT.readStatus(); }
    private String readRoomType()                            { return INPUT.readRoomType(); }
    private double readPositiveDouble(String prompt)         { return INPUT.readPositiveDouble(prompt); }
    private void   pressEnter()                              { INPUT.pressEnter(); }

    private String safeStr(Object o) {
        if (o == null) return "N/A";
        try {
            String s = o.toString().trim();
            return s.isEmpty() ? "N/A" : s;
        } catch (Exception e) {
            return "N/A";
        }
    }

    private double safeDouble(Object o) {
        if (o == null) return 0.0;
        try {
            return Double.parseDouble(o.toString().trim());
        } catch (Exception e) {
            return 0.0;
        }
    }
}
