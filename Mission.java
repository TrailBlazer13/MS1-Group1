package models;

public class Mission {
    private String id;
    private String title;
    private String status;
    private int    progress;
    private String assigned;
    private String deadline;
    private String postedDate;

    public Mission(String id, String title, String status, int progress,
                   String assigned, String deadline, String postedDate) {
        this.id         = id;
        this.title      = title;
        this.status     = status;
        this.progress   = progress;
        this.assigned   = assigned;
        this.deadline   = deadline;
        this.postedDate = postedDate;
    }

    public Mission(String id, String title, String deadline) {
        this.id         = id;
        this.title      = title;
        this.status     = "PENDING";
        this.progress   = 0;
        this.assigned   = "Unassigned";
        this.deadline   = deadline;
        this.postedDate = null;
    }

    public String getId()         { return id; }
    public String getTitle()      { return title; }
    public String getStatus()     { return status; }
    public int    getProgress()   { return progress; }
    public String getAssigned()   { return assigned; }
    public String getDeadline()   { return deadline; }
    public String getPostedDate() { return postedDate; }

    public void setStatus(String s)     { this.status = s; }
    public void setProgress(int p)      { this.progress = p; }
    public void setAssigned(String a)   { this.assigned = a; }
    public void setPostedDate(String d) { this.postedDate = d; }

    public void printDetailed() {
        String t = title.length() > 41 ? title.substring(0, 38) + "..." : title;
        String a = assigned.length() > 41 ? assigned.substring(0, 38) + "..." : assigned;
        System.out.println("  +----------------------------------------------------------+");
        System.out.printf( "  | Mission ID   : %-41s |%n", id);
        System.out.printf( "  | Title        : %-41s |%n", t);
        System.out.printf( "  | Status       : %-41s |%n", status);
        System.out.printf( "  | Progress     : %-41s |%n", progress + "%");
        System.out.printf( "  | Assigned     : %-41s |%n", a);
        System.out.printf( "  | Deadline     : %-41s |%n", deadline);
        System.out.printf( "  | Posted On    : %-41s |%n", postedDate != null ? postedDate : "Not yet posted");
        System.out.println("  +----------------------------------------------------------+");
    }
}
