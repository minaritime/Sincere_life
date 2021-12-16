package kr.example.sincere_life.RecyclerView;

public class Plan {

    private String sDate;
    private String sContents;
    private String sStarttime;
    private String sEndtime;
    private Boolean bcheck;

    public Plan(String sDate, String sContents, String sStarttime, String sEndtime, Boolean bcheck){
        this.sDate = sDate;
        this.sContents = sContents;
        this.sStarttime = sStarttime;
        this.sEndtime = sEndtime;
        this.bcheck = bcheck;
    }

    public String getsDate() { return sDate;}
    public String getsContents() { return sContents; }
    public String getsStarttime() { return sStarttime; }
    public String getsEndtime() { return sEndtime; }
    public Boolean getbcheck() { return bcheck; }
}
