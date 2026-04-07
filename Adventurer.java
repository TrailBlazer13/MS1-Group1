package models;

public class Adventurer {
    private int id;
    private String name;
    private String rank;
    private String joinDate;
    private String status;
    private String classType;
    private String contact;
    private String history;

    public Adventurer(int id, String name, String rank, String joinDate, String status,
                      String classType, String contact, String history) {
        this.id        = id;
        this.name      = name;
        this.rank      = rank;
        this.joinDate  = joinDate;
        this.status    = status;
        this.classType = classType;
        this.contact   = contact;
        this.history   = history;
    }

    public Adventurer(String name, String rank, String joinDate, String classType, String contact) {
        this.name      = name;
        this.rank      = rank;
        this.joinDate  = joinDate;
        this.status    = "ACTIVE";
        this.classType = classType;
        this.contact   = contact;
        this.history   = "None";
    }

    public int    getId()        { return id; }
    public String getName()      { return name; }
    public String getRank()      { return rank; }
    public String getJoinDate()  { return joinDate; }
    public String getStatus()    { return status; }
    public String getClassType() { return classType; }
    public String getContact()   { return contact; }
    public String getHistory()   { return history; }

    public void setStatus(String s)  { this.status = s; }
    public void setHistory(String h) { this.history = h; }

    public void printDetailed() {
        String hist = history.length() > 44 ? history.substring(0, 41) + "..." : history;
        System.out.println("  +----------------------------------------------------------+");
        System.out.printf( "  | Name     : %-44s |%n", name);
        System.out.printf( "  | Rank     : %-44s |%n", rank);
        System.out.printf( "  | Class    : %-44s |%n", classType);
        System.out.printf( "  | Status   : %-44s |%n", status);
        System.out.printf( "  | Joined   : %-44s |%n", joinDate);
        System.out.printf( "  | Contact  : %-44s |%n", contact);
        System.out.printf( "  | History  : %-44s |%n", hist);
        System.out.println("  +----------------------------------------------------------+");
    }
}
