package models;

public class Mission {

    private final String ID;
    private final String TITLE;
    private final String DEADLINE;
    private       String status;      
    private       int    progress;   
    private       String assigned;    
    private       String postedDate;  
    

    public Mission(String id, String title, String status, int progress,
                   String assigned, String deadline, String postedDate) {
        this.ID         = id;
        this.TITLE      = title;
        this.DEADLINE   = deadline;
        this.status     = status;
        this.progress   = progress;
        this.assigned   = assigned;
        this.postedDate = postedDate;
    }


    public Mission(String id, String title, String deadline) {
        this.ID         = id;
        this.TITLE      = title;
        this.DEADLINE   = deadline;
        this.status     = "PENDING";
        this.progress   = 0;
        this.assigned   = "Unassigned";
        this.postedDate = null;
    }

    
    public String getId()         { return ID; }
    public String getTitle()      { return TITLE; }
    public String getDeadline()   { return DEADLINE; }
    public String getStatus()     { return status; }
    public int    getProgress()   { return progress; }
    public String getAssigned()   { return assigned; }
    public String getPostedDate() { return postedDate; }

  
    public void setStatus(String status)       { this.status = status; }
    public void setProgress(int progress)      { this.progress = progress; }
    public void setAssigned(String assigned)   { this.assigned = assigned; }
    public void setPostedDate(String postedDate) { this.postedDate = postedDate; }

    public void printDetailed() {
        String t = TITLE.length()    > 41 ? TITLE.substring(0, 38)    + "..." : TITLE;
        String a = assigned.length() > 41 ? assigned.substring(0, 38) + "..." : assigned;
        System.out.println("  +----------------------------------------------------------+");
        System.out.printf( "  | Mission ID   : %-41s |%n", ID);
        System.out.printf( "  | Title        : %-41s |%n", t);
        System.out.printf( "  | Status       : %-41s |%n", status);
        System.out.printf( "  | Progress     : %-41s |%n", progress + "%");
        System.out.printf( "  | Assigned     : %-41s |%n", a);
        System.out.printf( "  | Deadline     : %-41s |%n", DEADLINE);
        System.out.printf( "  | Posted On    : %-41s |%n",
            postedDate != null ? postedDate : "Not yet posted");
        System.out.println("  +----------------------------------------------------------+");
    }
}
