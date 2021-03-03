package space.pxls.packets.socket;

public class ServerReceivedReport {
    public final String type = "received_report";

    private final Integer reportID;
    private final String reportType;

    public ServerReceivedReport(Integer reportID, String reportType) {
        this.reportID = reportID;
        this.reportType = reportType;
    }

    public Integer getReportID() {
        return reportID;
    }

    public String getReportType() {
        return reportType;
    }
}
