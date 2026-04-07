package models;


public class Room {

    private final String ROOM_ID;
    private final String ROOM_TYPE;
    private       String status;       
    private       String occupant;     
    private       String checkoutDate; 

    public Room(String roomId, String roomType, String status,
                String occupant, String checkoutDate) {
        this.ROOM_ID      = roomId;
        this.ROOM_TYPE    = roomType;
        this.status       = status;
        this.occupant     = occupant;
        this.checkoutDate = checkoutDate;
    }

    public String getRoomId()       { return ROOM_ID; }
    public String getRoomType()     { return ROOM_TYPE; }
    public String getStatus()       { return status; }
    public String getOccupant()     { return occupant; }
    public String getCheckoutDate() { return checkoutDate; }

    public void setStatus(String status)           { this.status = status; }
    public void setOccupant(String occupant)       { this.occupant = occupant; }
    public void setCheckoutDate(String checkoutDate) { this.checkoutDate = checkoutDate; }

    @Override
    public String toString() {
        return String.format("  %-6s | %-18s | %-12s | %-22s | %s",
            ROOM_ID, ROOM_TYPE, status, occupant, checkoutDate);
    }
}
