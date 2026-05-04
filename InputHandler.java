
package ui;
 
import java.time.LocalDate;
import java.util.Scanner;
 
/**
 * InputHandler — centralized, crash-proof input utility.
 * All user input in the system flows through this class.
 * Prevents: NumberFormatException, NullPointerException,
 *           StringIndexOutOfBoundsException, infinite loops.
 */
public class InputHandler {  // NEW CODE
 
    private static final int MAX_INPUT_LENGTH = 100;
    private final Scanner scanner;
 
    public InputHandler(Scanner scanner) {
        this.scanner = scanner;
    }
 
    // NEW CODE — sanitize raw input: trim, length-cap, null-safe
    public static String sanitizeInput(String input) {
        if (input == null) return "";
        String trimmed = input.trim();
        if (trimmed.length() > MAX_INPUT_LENGTH) {
            System.out.println(" [!] Input too long. Trimmed to " + MAX_INPUT_LENGTH + " characters.");
            trimmed = trimmed.substring(0, MAX_INPUT_LENGTH);
        }
        return trimmed;
    }
 
    // NEW CODE — safe integer read; returns -999 on any invalid input
    public int readInt() {
        try {
            String raw = scanner.nextLine();
            String sanitized = sanitizeInput(raw);
            if (sanitized.isEmpty()) return -999;
            return Integer.parseInt(sanitized);
        } catch (NumberFormatException e) {
            System.out.println(" [X] Invalid input. Please enter a number.");
            return -999;
        } catch (Exception e) {
            System.out.println(" [X] Unexpected input error. Please try again.");
            return -999;
        }
    }
 
    // NEW CODE — safe non-empty string read with length cap
    public String readNonEmpty(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                String raw = scanner.nextLine();
                String val = sanitizeInput(raw);
                if (val.isEmpty()) {
                    System.out.println(" [X] This field cannot be empty. Please try again.");
                } else {
                    return val;
                }
            } catch (Exception e) {
                System.out.println(" [X] Input error. Please try again.");
            }
        }
    }
 
    // NEW CODE — safe date read, strict YYYY-MM-DD format + calendar validity
    public String readDate(String label) {
        while (true) {
            try {
                System.out.print(" " + label + " (YYYY-MM-DD): ");
                String raw = scanner.nextLine();
                String val = sanitizeInput(raw);
 
                if (!val.matches("\\d{4}-\\d{2}-\\d{2}")) {
                    System.out.println(" [X] Date must be in YYYY-MM-DD format (e.g. 2025-06-15).");
                    continue;
                }
                LocalDate.parse(val); // throws if calendar-invalid (e.g. 2025-02-30)
                return val;
            } catch (java.time.format.DateTimeParseException e) {
                System.out.println(" [X] That date does not exist on the calendar. Please re-enter.");
            } catch (Exception e) {
                System.out.println(" [X] Invalid date input. Please try again.");
            }
        }
    }
 
    // NEW CODE — safe date read that enforces checkOut > checkIn
    public String readDateAfter(String label, String checkIn) {
        while (true) {
            try {
                String val = readDate(label);
                LocalDate ciDate = LocalDate.parse(checkIn);
                LocalDate coDate = LocalDate.parse(val);
                if (coDate.isAfter(ciDate)) {
                    return val;
                } else {
                    System.out.println(" [X] Check-out must be after check-in ("
                            + checkIn + "). Please re-enter.");
                }
            } catch (Exception e) {
                System.out.println(" [X] Date comparison error. Please re-enter.");
            }
        }
    }
 
    // NEW CODE — safe positive double read (used for reward/amount inputs)
    public double readPositiveDouble(String prompt) {
        while (true) {
            try {
                System.out.print(" " + prompt + ": ");
                String raw = scanner.nextLine();
                String sanitized = sanitizeInput(raw);
                double value = Double.parseDouble(sanitized);
                if (value <= 0) {
                    System.out.println(" [X] Amount must be greater than zero. Please try again.");
                } else {
                    return value;
                }
            } catch (NumberFormatException e) {
                System.out.println(" [X] Invalid amount. Please enter a number (e.g. 250.00).");
            } catch (Exception e) {
                System.out.println(" [X] Unexpected error. Please try again.");
            }
        }
    }
 
    // NEW CODE — safe rank selector
    public String readRank(String label) {
        while (true) {
            try {
                System.out.print(" " + label + " (BRONZE/SILVER/GOLD/PLATINUM): ");
                String raw = scanner.nextLine();
                String val = sanitizeInput(raw).toUpperCase();
                if (val.equals("BRONZE") || val.equals("SILVER")
                        || val.equals("GOLD") || val.equals("PLATINUM")) {
                    return val;
                }
                System.out.println(" [X] Invalid rank. Must be BRONZE, SILVER, GOLD, or PLATINUM.");
            } catch (Exception e) {
                System.out.println(" [X] Input error. Please try again.");
            }
        }
    }
 
    // NEW CODE — safe status selector
    public String readStatus() {
        while (true) {
            try {
                System.out.print(" Enter status (ACTIVE/INACTIVE): ");
                String raw = scanner.nextLine();
                String val = sanitizeInput(raw).toUpperCase();
                if (val.equals("ACTIVE") || val.equals("INACTIVE")) return val;
                System.out.println(" [X] Invalid status. Must be ACTIVE or INACTIVE.");
            } catch (Exception e) {
                System.out.println(" [X] Input error. Please try again.");
            }
        }
    }
 
    // NEW CODE — safe room type selector
    public String readRoomType() {
        while (true) {
            try {
                System.out.println(" Room Types: 1. Common Quarters  2. Private Chamber  3. Noble Suite");
                System.out.print(" Select (1-3): ");
                String raw = scanner.nextLine();
                String sanitized = sanitizeInput(raw);
                int choice = Integer.parseInt(sanitized);
                switch (choice) {
                    case 1 -> { return "Common Quarters"; }
                    case 2 -> { return "Private Chamber"; }
                    case 3 -> { return "Noble Suite"; }
                    default -> System.out.println(" [X] Invalid choice. Please select 1, 2, or 3.");
                }
            } catch (NumberFormatException e) {
                System.out.println(" [X] Please enter a number (1, 2, or 3).");
            } catch (Exception e) {
                System.out.println(" [X] Input error. Please try again.");
            }
        }
    }
 
    // NEW CODE — safe press-enter
    public void pressEnter() {
        try {
            System.out.print("\n [Press ENTER to continue...]");
            scanner.nextLine();
        } catch (Exception e) {
            // silently ignore — just continue
        }
    }
}
