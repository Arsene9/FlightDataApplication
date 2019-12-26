package BusinessObjectLayer;

public class Departure {
    private long departureID;
    private long locationID; // INT, //CONSTRAINT FK_Dep_Loc FOREIGN KEY (Location_ID)//REFERENCES Location (Location_ID)
    private String gateParkingName; // VARCHAR(5) NOT NULL,
    private String departureTime; // DATETIME

    public Departure (){
        this.locationID = 0;
        this.gateParkingName = "";
        this.departureTime = "";
    }

    public Departure(String data){
        String[] dList = data.split(";");
        this.locationID = Long.parseLong(dList[1]);
        this.gateParkingName = dList[1];
        this.departureTime = dList[1];
    }

    public Departure(int locationID, String gateParkingName, String departureTime) {
        this.locationID = locationID;
        this.gateParkingName = gateParkingName;
        this.departureTime = departureTime;
    }

    public long getDepartureID() {
        return departureID;
    }

    public void setDepartureID(long departureID) {
        this.departureID = departureID;
    }

    public long getLocationID() {
        return locationID;
    }

    public void setLocationID(long locationID) {
        this.locationID = locationID;
    }

    public String getGateParkingName() {
        return gateParkingName;
    }

    public void setGateParkingName(String gateParkingName) {
        this.gateParkingName = gateParkingName;
    }

    public String getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(String departureTime) {
        this.departureTime = departureTime;
    }

    public boolean isSameAs(Departure dep){
        return(this.locationID == locationID &&
        this.gateParkingName.equals(gateParkingName) &&
        this.departureTime.equals(dep.departureTime));
    }

    public String listDeparture(){
        return this.departureID + ";" + this.locationID + ";" + this.gateParkingName + ";" + this.departureTime;
    }
}
