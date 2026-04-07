package models;

public class Application {
    private int id;
    private String name;
    private String background;
    private String rank;
    private String submissionDate;
    private String status;

    public Application(int id, String name, String background, String rank,
                       String submissionDate, String status) {
        this.id = id;
        this.name = name;
        this.background = background;
        this.rank = rank;
        this.submissionDate = submissionDate;
        this.status = status;
    }

    public Application(String name, String background, String rank, String submissionDate) {
        this.name = name;
        this.background = background;
        this.rank = rank;
        this.submissionDate = submissionDate;
        this.status = "PENDING";
    }

    public int    getId()             { return id; }
    public String getName()           { return name; }
    public String getBackground()     { return background; }
    public String getRank()           { return rank; }
    public String getSubmissionDate() { return submissionDate; }
    public String getStatus()         { return status; }
    public void   setStatus(String s) { this.status = s; }

    @Override
    public String toString() {
        String bg = background.length() > 34 ? background.substring(0, 31) + "..." : background;
        return String.format("%-25s %-35s %-8s %-12s %-10s", name, bg, rank, submissionDate, status);
    }
}
