package BusinessObjectLayer;

public class Arrival {
    private long arrivalID;
    private long locationID; // INT, //CONSTRAINT FK_Arr_Loc FOREIGN KEY (Location_ID) //REFERENCES Location (Location_ID)
    private String gateParking; // VARCHAR(5),
    private String arrivalTime;

    public Arrival(){
        this.locationID = 0;
        this.gateParking = "";
        this.arrivalTime = "";
    }

    public Arrival(String data){
        String[] dList = data.split(";");
        this.locationID = Long.parseLong(dList[1]);
        this.gateParking = dList[2];
        if(dList.length == 4)
            this.arrivalTime = dList[3];
        else
            this.arrivalTime = "";
    }

    public Arrival(int locationID, String gateParkingName_Expected, String arrivalTime) {
        this.locationID = locationID;
        this.gateParking = gateParkingName_Expected;
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

    public String getGateParking() {
        return this.gateParking;
    }

    public void setGateParking(String gateParkingName_Expected) {
        this.gateParking = gateParkingName_Expected;
    }

    public String getArrivalTime() {
        return this.arrivalTime;
    }

    public void setArrivalTime(String arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public Boolean isSameAs(Arrival ad){
        return (this.locationID == ad.locationID &&
        this.gateParking.equals(ad.gateParking) &&
        this.arrivalTime.equals(ad.arrivalTime));
    }

    public String listArrival(){
        return this.arrivalID + ";" + this.locationID + ";" + this.gateParking + ";" + this.arrivalTime;
    }
}
