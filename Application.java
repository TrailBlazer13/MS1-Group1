//updated
package models;

public class Application {

    private final int    ID;
    private final String NAME;
    private final String BACKGROUND;
    private final String RANK;
    private final String SUBMISSION_DATE;
    private       String status;  


    public Application(int id, String name, String background, String rank,
                       String submissionDate, String status) {
        this.ID              = id;
        this.NAME            = name;
        this.BACKGROUND      = background;
        this.RANK            = rank;
        this.SUBMISSION_DATE = submissionDate;
        this.status          = status;
    }


    public Application(String name, String background, String rank, String submissionDate) {
        this.ID              = 0;
        this.NAME            = name;
        this.BACKGROUND      = background;
        this.RANK            = rank;
        this.SUBMISSION_DATE = submissionDate;
        this.status          = "PENDING";
    }


    public int    getId()             { return ID; }
    public String getName()           { return NAME; }
    public String getBackground()     { return BACKGROUND; }
    public String getRank()           { return RANK; }
    public String getSubmissionDate() { return SUBMISSION_DATE; }
    public String getStatus()         { return status; }

   
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        String bg = BACKGROUND.length() > 34
            ? BACKGROUND.substring(0, 31) + "..."
            : BACKGROUND;
        return String.format("%-25s %-35s %-8s %-12s %-10s",
            NAME, bg, RANK, SUBMISSION_DATE, status);
    }
}
