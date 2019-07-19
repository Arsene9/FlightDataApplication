package BusinessObjectLayer;

public class Aircraft {
    private long aircraftID;
    private String airlineName; // VARCHAR(30) NOT NULL,
    private String aircraftType; // VARCHAR(9) NOT NULL,
    private String manufacturer; // VARCHAR(20) NOT NULL,
    private String tailNum; // VARCHAR(10) NOT NULL,
    private String callSign; // VARCHAR(10),
    private String flightNum; // VARCHAR(15)

    public Aircraft(){
        this.airlineName = "";
        this.aircraftType = "";
        this.manufacturer = "";
        this.tailNum = "";
        this.callSign = "";
        this.flightNum = "";
    }

    public Aircraft(String airlineName, String aircraftType, String manufacturer, String tailNum, String callSign, String flightNum) {
        this.airlineName = airlineName;
        this.aircraftType = aircraftType;
        this.manufacturer = manufacturer;
        this.tailNum = tailNum;
        this.callSign = callSign;
        this.flightNum = flightNum;
    }

    public long getAircraftID() {
        return aircraftID;
    }

    public void setAircraftID(long aircraftID) {
        this.aircraftID = aircraftID;
    }

    public String getAirlineName() {
        return airlineName;
    }

    public void setAirlineName(String airlineName) {
        this.airlineName = airlineName;
    }

    public String getAircraftType() {
        return aircraftType;
    }

    public void setAircraftType(String aircraftType) {
        this.aircraftType = aircraftType;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getTailNum() {
        return tailNum;
    }

    public void setTailNum(String tailNum) {
        this.tailNum = tailNum;
    }

    public String getCallSign() {
        return callSign;
    }

    public void setCallSign(String callSign) {
        this.callSign = callSign;
    }

    public String getFlightNum() {
        return flightNum;
    }

    public void setFlightNum(String flightNum) {
        this.flightNum = flightNum;
    }

    public boolean isSameAs(Aircraft data){
        return (this.airlineName.equals(data.airlineName) &&
                this.aircraftType.equals(data.aircraftType) &&
                this.manufacturer.equals(data.manufacturer) &&
                this.tailNum.equals(data.tailNum) &&
                this.callSign.equals(data.callSign) &&
                this.flightNum.equals(data.flightNum));
    }
}
