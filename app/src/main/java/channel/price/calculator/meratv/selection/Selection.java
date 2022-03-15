package channel.price.calculator.meratv.selection;

public class Selection {
    private int selectionUid;
    private String[] entryUids;
    private String name;
    private double totalPrice;
    private double mtnCharge;
    private boolean gst;
    private long time;
    private int monthlyReport;

    public Selection(int selectionUid, String[] entryUids, String name, double totalPrice, double mtnCharge, boolean gst, long time, int monthlyReport) {
        this.selectionUid = selectionUid;
        this.entryUids = entryUids;
        this.name = name;
        this.totalPrice = totalPrice;
        this.mtnCharge = mtnCharge;
        this.gst = gst;
        this.time = time;
        this.monthlyReport = monthlyReport;
    }

    public int getSelectionUid() {
        return selectionUid;
    }

    public void setSelectionUid(int selectionUid) {
        this.selectionUid = selectionUid;
    }

    public String[] getEntryUids() {
        return entryUids;
    }

    public void setEntryUids(String[] entryUids) {
        this.entryUids = entryUids;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public double getMtnCharge() {
        return mtnCharge;
    }

    public void setMtnCharge(double mtnCharge) {
        this.mtnCharge = mtnCharge;
    }

    public boolean isGst() {
        return gst;
    }

    public void setGst(boolean gst) {
        this.gst = gst;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getMonthlyReport() {
        return monthlyReport;
    }

    public void setMonthlyReport(int monthlyReport) {
        this.monthlyReport = monthlyReport;
    }
}
