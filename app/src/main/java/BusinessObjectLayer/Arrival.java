package BusinessObjectLayer;

public class Arrival {
    private long arrivalID;
    private long locationID; // INT, //CONSTRAINT FK_Arr_Loc FOREIGN KEY (Location_ID) //REFERENCES Location (Location_ID)
    private String gateParkingNameExpected; // VARCHAR(5),
    private String arrivalTime;

    public Arrival(){
        this.locationID = 0;
        this.gateParkingNameExpected = "";
        this.arrivalTime = "";
    }

    public Arrival(int locationID, String gateParkingName_Expected, String arrivalTime) {
        this.locationID = locationID;
        this.gateParkingNameExpected = gateParkingName_Expected;
        this.arrivalTime = arrivalTime;
    }

    public long getArrivalID() {
        return this.arrivalID;
    }

    public void setArrivalID(long arrivalID) {
        this.arrivalID = arrivalID;
    }

    public long getLocationID() {
        return this.locationID;
    }

    public void setLocationID(long locationID) {
        this.locationID = locationID;
    }

    public String getGateParkingNameExpected() {
        return this.gateParkingNameExpected;
    }

    public void setGateParkingNameExpected(String gateParkingName_Expected) {
        this.gateParkingNameExpected = gateParkingName_Expected;
    }

    public String getArrivalTime() {
        return this.arrivalTime;
    }

    public void setArrivalTime(String arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public Boolean isSameAs(Arrival ad){
        return (this.locationID == ad.locationID &&
        this.gateParkingNameExpected.equals(ad.gateParkingNameExpected) &&
        this.arrivalTime.equals(ad.arrivalTime));
    }
}
