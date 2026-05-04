//updated
Adventurer.java

package models;

public class Adventurer {

    private final int    ID;
    private final String NAME;
    private final String RANK;
    private final String JOIN_DATE;
    private final String CLASS_TYPE;
    private final String CONTACT;
    private       String status;   
    private       String history;  
   
    public Adventurer(int id, String name, String rank, String joinDate, String status,
                      String classType, String contact, String history) {
        this.ID         = id;
        this.NAME       = name;
        this.RANK       = rank;
        this.JOIN_DATE  = joinDate;
        this.CLASS_TYPE = classType;
        this.CONTACT    = contact;
        this.status     = status;
        this.history    = history;
    }

    public Adventurer(String name, String rank, String joinDate,
                      String classType, String contact) {
        this.ID         = 0;
        this.NAME       = name;
        this.RANK       = rank;
        this.JOIN_DATE  = joinDate;
        this.CLASS_TYPE = classType;
        this.CONTACT    = contact;
        this.status     = "ACTIVE";
        this.history    = "None";
    }

    public int    getId()        { return ID; }
    public String getName()      { return NAME; }
    public String getRank()      { return RANK; }
    public String getJoinDate()  { return JOIN_DATE; }
    public String getClassType() { return CLASS_TYPE; }
    public String getContact()   { return CONTACT; }
    public String getStatus()    { return status; }
    public String getHistory()   { return history; }

    public void setStatus(String status)   { this.status = status; }
    public void setHistory(String history) { this.history = history; }

    public void printDetailed() {
        String hist = history.length() > 44
            ? history.substring(0, 41) + "..."
            : history;
        System.out.println("  +----------------------------------------------------------+");
        System.out.printf( "  | Name     : %-44s |%n", NAME);
        System.out.printf( "  | Rank     : %-44s |%n", RANK);
        System.out.printf( "  | Class    : %-44s |%n", CLASS_TYPE);
        System.out.printf( "  | Status   : %-44s |%n", status);
        System.out.printf( "  | Joined   : %-44s |%n", JOIN_DATE);
        System.out.printf( "  | Contact  : %-44s |%n", CONTACT);
        System.out.printf( "  | History  : %-44s |%n", hist);
        System.out.println("  +----------------------------------------------------------+");
    }
}
