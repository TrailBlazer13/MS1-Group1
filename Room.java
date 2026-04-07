package models;

public class Room {
    private final String roomId;
    private final String roomType;
    private final String status;
    private final String occupant;
    private final String checkoutDate;

    public Room(String roomId, String roomType, String status,
                String occupant, String checkoutDate) {
        this.roomId       = roomId;
        this.roomType     = roomType;
        this.status       = status;
        this.occupant     = occupant;
        this.checkoutDate = checkoutDate;
    }

    public String getRoomId()       { return roomId; }
    public String getRoomType()     { return roomType; }
    public String getStatus()       { return status; }
    public String getOccupant()     { return occupant; }
    public String getCheckoutDate() { return checkoutDate; }

    public void setStatus(String s)       { this.status = s; }
    public void setOccupant(String o)     { this.occupant = o; }
    public void setCheckoutDate(String d) { this.checkoutDate = d; }

    @Override
    public String toString() {
        return String.format("  %-6s | %-18s | %-12s | %-22s | %s",
            roomId, roomType, status, occupant, checkoutDate);
    }
}
