package ui;

import models.*;
import services.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class Menu {

    private final Scanner            scanner     = new Scanner(System.in);
    private final ApplicationService appService  = new ApplicationService();
    private final AdventurerService  advService  = new AdventurerService();
    private final MissionService     msnService  = new MissionService();
    private final RoomService        roomService = new RoomService();

    public void start() {
        printBanner();
        int choice;
        do {
            printMainMenu();
            choice = readInt();
            switch (choice) {
                case 1  -> clerkDeskMenu();
                case 0  -> System.out.println(
                    "\n  May your blades stay sharp, traveler. Farewell!\n");
                default -> System.out.println("  [X] Invalid option. Try again.");
            }
        } while (choice != 0);
    }

    private void clerkDeskMenu() {
        int choice;
        do {
            printClerkMenu();
            choice = readInt();
            switch (choice) {
                case 1  -> checkApplications();
                case 2  -> viewAdventurers();
                case 3  -> missionRequests();
                case 4  -> missionStatus();
                case 5  -> roomRequests();
                case 6  -> roomStatus();
                case 0  -> System.out.println("  [<] Returning to main hall...");
                default -> System.out.println("  [X] Unknown command. Try again.");
            }
        } while (choice != 0);
    }

    private void checkApplications() {
        int choice;
        do {
            sectionHeader("CHECK APPLICATIONS");
            List<Application> pending = appService.getPendingApplications();

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
        Application a = appService.getApplicationById(id);
        if (a == null) {
            System.out.println("  [X] Application not found.");
            return;
        }
        if (!a.getStatus().equals("PENDING")) {
            System.out.println("  [X] Application is already " + a.getStatus() + ".");
            return;
        }
        System.out.printf("  Confirm APPROVE for '%s'? (y/n): ", a.getName());
        String confirm = scanner.nextLine().trim().toLowerCase();
        if (!confirm.equals("y")) {
            System.out.println("  [<] Action cancelled.");
            return;
        }
        if (appService.approveApplication(id)) {
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
        Application a = appService.getApplicationById(id);
        if (a == null) {
            System.out.println("  [X] Application not found.");
            return;
        }
        if (!a.getStatus().equals("PENDING")) {
            System.out.println("  [X] Application is already " + a.getStatus() + ".");
            return;
        }
        System.out.printf("  Confirm REJECT for '%s'? (y/n): ", a.getName());
        String confirm = scanner.nextLine().trim().toLowerCase();
        if (!confirm.equals("y")) {
            System.out.println("  [<] Action cancelled.");
            return;
        }
        if (appService.rejectApplication(id)) {
            System.out.println("  [X] Application REJECTED. Better luck next season.");
        } else {
            System.out.println("  [X] Rejection failed. Please try again.");
        }
    }

    private void submitNewApplication() {
        System.out.println("  -- Submit New Application --");

        String name = readNonEmpty("  Applicant Name       : ");
        if (appService.applicationExists(name)) {
            System.out.println("  [X] An application already exists for '" + name + "'.");
            return;
        }

        String background = readNonEmpty("  Background / Story   : ");

        String rank = readRank("  Requested Rank");

        String date = readDate("  Submission Date");

        if (appService.submitApplication(name, background, rank, date)) {
            System.out.println("  [OK] Application submitted successfully for '" + name + "'!");
            System.out.println("  [DB] Record saved to database.");
        } else {
            System.out.println("  [X] Submission failed. Duplicate application detected.");
        }
    }

    private void viewAllApplications() {
        sectionHeader("ALL APPLICATIONS");
        List<Application> all = appService.getAllApplications();
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
                case 1 -> displayAdventurers(advService.getAllAdventurers());
                case 2 -> {
                    String kw = readNonEmpty("  Enter name keyword: ");
                    displayAdventurers(advService.searchByName(kw));
                }
                case 3 -> {
                    String rank = readRank("  Filter by Rank");
                    displayAdventurers(advService.filterByRank(rank));
                }
                case 4 -> {
                    String status = readStatus();
                    displayAdventurers(advService.filterByStatus(status));
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

    private void missionRequests() {
        int choice;
        do {
            sectionHeader("MISSION REQUESTS");
            List<Mission> missions = msnService.getAllMissions();
            printMissionTableHeader();
            for (Mission m : missions) {
                String tag = msnService.isOverdue(m) ? " [OVERDUE]" : "";
                String t   = m.getTitle().length() > 37
                    ? m.getTitle().substring(0, 34) + "..."
                    : m.getTitle();
                System.out.printf("  %-8s %-38s %-10s %3d%%  %-12s%s%n",
                    m.getId(), t, m.getStatus(), m.getProgress(), m.getDeadline(), tag);
            }
            printDivider();

            System.out.println();
            System.out.println("  1. Post Mission");
            System.out.println("  2. Unpost Mission");
            System.out.println("  3. Approve Mission Request");
            System.out.println("  4. Deny Mission Request");
            System.out.println("  5. Add New Mission Request");
            System.out.println("  0. Back");
            System.out.print("\n  > ");
            choice = readInt();

            switch (choice) {
                case 1 -> {
                    String id = readNonEmpty("  Mission ID to POST: ").toUpperCase().trim();
                    if (msnService.getMissionById(id) == null) {
                        System.out.println("  [X] Mission ID '" + id + "' not found.");
                    } else {
                        msnService.postMission(id);
                    }
                }
                case 2 -> {
                    String id = readNonEmpty("  Mission ID to UNPOST: ").toUpperCase().trim();
                    if (msnService.getMissionById(id) == null) {
                        System.out.println("  [X] Mission ID '" + id + "' not found.");
                    } else if (msnService.unpostMission(id)) {
                        System.out.println("  [OK] Mission " + id + " unposted successfully.");
                        System.out.println("  [DB] Status updated in database.");
                    } else {
                        System.out.println("  [X] Could not unpost. Mission must be in POSTED state.");
                    }
                }
                case 3 -> {
                    String id = readNonEmpty("  Mission ID to APPROVE: ").toUpperCase().trim();
                    if (msnService.getMissionById(id) == null) {
                        System.out.println("  [X] Mission ID '" + id + "' not found.");
                    } else if (msnService.approveMission(id)) {
                        System.out.println("  [OK] Mission " + id + " approved!");
                        System.out.println("  [DB] Status updated in database.");
                    } else {
                        System.out.println("  [X] Could not approve. Mission must be in PENDING state.");
                    }
                }
                case 4 -> {
                    String id = readNonEmpty("  Mission ID to DENY: ").toUpperCase().trim();
                    if (msnService.getMissionById(id) == null) {
                        System.out.println("  [X] Mission ID '" + id + "' not found.");
                    } else if (msnService.denyMission(id)) {
                        System.out.println("  [X] Mission " + id + " denied.");
                        System.out.println("  [DB] Status updated in database.");
                    } else {
                        System.out.println("  [X] Could not deny. Mission must be in PENDING state.");
                    }
                }
                case 5  -> addNewMissionRequest();
                case 0  -> {}
                default -> System.out.println("  [X] Invalid choice.");
            }
        } while (choice != 0);
    }

    private void addNewMissionRequest() {
        System.out.println("  -- Register New Mission Request --");

        String title = readNonEmpty("  Mission Title   : ");
        
        String deadline = readDate("  Deadline Date");

        if (msnService.addMission(title, deadline)) {
            System.out.println("  [OK] Mission request registered successfully!");
            System.out.println("  [DB] New mission saved to database.");
        } else {
            System.out.println("  [X] Failed to register mission. Please try again.");
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
        Mission m = msnService.getMissionById(id);
        if (m == null) {
            System.out.println("  [X] Mission not found or expired.");
            return;
        }

        System.out.println();
        m.printDetailed();

        if (msnService.isOverdue(m)) {
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
        Mission m = msnService.getMissionById(id);
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
                if (msnService.updateMissionStatus(id, "COMPLETED", 100)) {
                    if (!m.getAssigned().equals("Unassigned")) {
                        for (String name : m.getAssigned().split(",")) {
                            advService.appendHistory(
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
                if (msnService.updateMissionStatus(id, "FAILED", m.getProgress())) {
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
                if (msnService.updateMissionStatus(id, m.getStatus(), progress)) {
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
        Mission m = msnService.getMissionById(missionId);
        if (m == null) {
            System.out.println("  [X] Mission not found or expired.");
            return;
        }

        String advName = readNonEmpty("  Adventurer Name  : ");
        if (!advService.adventurerExists(advName)) {
            System.out.println("  [X] No adventurer found with the name '" + advName + "'.");
            System.out.println("  [!] Make sure the name matches exactly as registered.");
            return;
        }

        if (msnService.assignAdventurer(missionId, advName)) {
            System.out.println("  [OK] " + advName
                + " has been assigned to mission " + missionId + "!");
            System.out.println("  [DB] Assignment saved to database.");
        } else {
            System.out.println("  [X] Assignment failed. Please try again.");
        }
    }

    private void viewOverdueMissions() {
        sectionHeader("OVERDUE MISSIONS");
        List<Mission> all = msnService.getAllMissions();
        boolean found = false;
        printMissionTableHeader();
        for (Mission m : all) {
            if (msnService.isOverdue(m)) {
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
            List<Object[]> requests = roomService.getAllRoomRequests();

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
                    } else if (roomService.approveRoomRequest(id)) {
                        System.out.println("  [OK] Room request approved and room assigned!");
                        System.out.println("  [DB] Room status updated in database.");
                    }
                }
                case 2 -> {
                    System.out.print("  Request ID to DENY: ");
                    int id = readInt();
                    if (id <= 0) {
                        System.out.println("  [X] Invalid ID.");
                    } else if (roomService.denyRoomRequest(id)) {
                        System.out.println("  [X] Room request denied.");
                        System.out.println("  [DB] Status updated in database.");
                    } else {
                        System.out.println("  [X] Could not deny. Request not found or already processed.");
                    }
                }
                case 3 -> {
                    List<Object[]> pending = roomService.getPendingRoomRequests();
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

        String name = readNonEmpty("  Guest Name      : ");

        String roomType = readRoomType();

        String checkIn = readDate("  Check-in Date");

        String checkOut = readDateAfter("  Check-out Date", checkIn);
        if (checkOut == null) return;

        if (roomService.addRoomRequest(name, roomType, checkIn, checkOut)) {
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

            int autoChecked = roomService.autoCheckoutExpired();
            if (autoChecked > 0) {
                System.out.println("  [Auto] " + autoChecked
                    + " room(s) released after checkout date passed.");
                System.out.println("  [DB] Room statuses updated automatically.");
            }

            int available   = roomService.getRoomCountByStatus("AVAILABLE");
            int occupied    = roomService.getRoomCountByStatus("OCCUPIED");
            int maintenance = roomService.getRoomCountByStatus("MAINTENANCE");
            System.out.printf("  Available: %d  |  Occupied: %d  |  Maintenance: %d%n%n",
                available, occupied, maintenance);

            List<Room> rooms = roomService.getAllRooms();
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
                    if (roomService.checkoutRoom(rid)) {
                        System.out.println("  [OK] Room " + rid
                            + " checked out and is now AVAILABLE.");
                        System.out.println("  [DB] Room status updated in database.");
                    }
                }
                case 2 -> {
                    String rid = readNonEmpty(
                        "  Room ID to set MAINTENANCE: ").toUpperCase().trim();
                    if (roomService.setRoomMaintenance(rid)) {
                        System.out.println("  [OK] Room " + rid + " set to MAINTENANCE.");
                        System.out.println("  [DB] Room status updated in database.");
                    } else {
                        System.out.println(
                            "  [X] Cannot set to maintenance. Room may be OCCUPIED.");
                    }
                }
                case 3 -> {
                    String rid = readNonEmpty(
                        "  Room ID to set AVAILABLE: ").toUpperCase().trim();
                    if (roomService.setRoomAvailable(rid)) {
                        System.out.println("  [OK] Room " + rid + " set to AVAILABLE.");
                        System.out.println("  [DB] Room status updated in database.");
                    } else {
                        System.out.println("  [X] Failed to update room. Room ID not found.");
                    }
                }
                case 4 -> {
                    String rid = readNonEmpty("  Room ID to view: ").toUpperCase().trim();
                    Room r = roomService.getRoomById(rid);
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
        System.out.println(
            "  ------------------------------------------------------------");
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

    private int readInt() {
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return -999;
        }
    }

    private String readNonEmpty(String prompt) {
        String val = "";
        while (val.isEmpty()) {
            System.out.print(prompt);
            val = scanner.nextLine().trim();
            if (val.isEmpty()) {
                System.out.println("  [X] This field cannot be empty. Please try again.");
            }
        }
        return val;
    }

    private String readDate(String label) {
        while (true) {
            System.out.print("  " + label + " (YYYY-MM-DD): ");
            String val = scanner.nextLine().trim();
            if (!val.matches("\\d{4}-\\d{2}-\\d{2}")) {
                System.out.println("  [X] Date must be in YYYY-MM-DD format (e.g. 2025-06-15).");
                continue;
            }
            try {
                LocalDate.parse(val); // throws if not a real date (e.g. 2025-02-30)
                return val;
            } catch (Exception e) {
                System.out.println("  [X] That date does not exist on the calendar. Please re-enter.");
            }
        }
    }

    private String readDateAfter(String label, String checkIn) {
        while (true) {
            String val = readDate(label);
            try {
                LocalDate ciDate = LocalDate.parse(checkIn);
                LocalDate coDate = LocalDate.parse(val);
                if (coDate.isAfter(ciDate)) {
                    return val;
                } else {
                    System.out.println("  [X] Check-out date must be after check-in ("
                        + checkIn + "). Please re-enter.");
                }
            } catch (Exception e) {
                System.out.println("  [X] Invalid date comparison. Please re-enter.");
            }
        }
    }

    private String readRank(String label) {
        while (true) {
            System.out.print("  " + label + " (BRONZE/SILVER/GOLD/PLATINUM): ");
            String val = scanner.nextLine().trim().toUpperCase();
            if (val.equals("BRONZE") || val.equals("SILVER")
                    || val.equals("GOLD") || val.equals("PLATINUM")) {
                return val;
            }
            System.out.println(
                "  [X] Invalid rank. Must be BRONZE, SILVER, GOLD, or PLATINUM.");
        }
    }

    private String readStatus() {
        while (true) {
            System.out.print("  Enter status (ACTIVE/INACTIVE): ");
            String val = scanner.nextLine().trim().toUpperCase();
            if (val.equals("ACTIVE") || val.equals("INACTIVE")) {
                return val;
            }
            System.out.println("  [X] Invalid status. Must be ACTIVE or INACTIVE.");
        }
    }

    private String readRoomType() {
        while (true) {
            System.out.println(
                "  Room Types: 1. Common Quarters   "
                + "2. Private Chamber   3. Noble Suite");
            System.out.print("  Select (1-3): ");
            int choice = readInt();
            switch (choice) {
                case 1 -> { return "Common Quarters"; }
                case 2 -> { return "Private Chamber"; }
                case 3 -> { return "Noble Suite"; }
                default -> System.out.println(
                    "  [X] Invalid choice. Please select 1, 2, or 3.");
            }
        }
    }

    private void pressEnter() {
        System.out.print("\n  [Press ENTER to continue...]");
        scanner.nextLine();
    }
}
