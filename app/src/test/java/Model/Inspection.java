/**
 * An inspections class. Can implement enum classes for Inspection Type, Hazard Rating and Violation lLump.
 */
package Model;

public class Inspection {
    private String trackingNumber;
    private int inspectionDate;
    private String inspectionType;
    private int numCritical;
    private int numNonCritical;
    private String hazardRating;
    private String violLump;

    public Inspection(String trackingNumber, int inspectionDate, String inspectionType, int numCritical, int numNonCritical, String hazardRating, String violLump) {
        this.trackingNumber = trackingNumber;
        this.inspectionDate = inspectionDate;
        this.inspectionType = inspectionType;
        this.numCritical = numCritical;
        this.numNonCritical = numNonCritical;
        this.hazardRating = hazardRating;
        this.violLump = violLump;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public int getInspectionDate() {
        return inspectionDate;
    }

    public String getInspectionType() {
        return inspectionType;
    }

    public int getNumCritical() {
        return numCritical;
    }

    public int getNumNonCritical() {
        return numNonCritical;
    }

    public String getHazardRating() {
        return hazardRating;
    }

    public String getViolLump() {
        return violLump;
    }

    public void showInfo() { //for testing purpose in java.
        System.out.println(
                "Tracking Number: " + trackingNumber +
                        "\nInspection Date: " + inspectionDate +
                        "\nInspection Type: " + inspectionType +
                        "\nCritical Count: " + numCritical +
                        "\nNon-Critical Count: " + numNonCritical +
                        "\nHazard Rating: " + hazardRating +
                        "\nViolation Lump: " + violLump);
    }
}
