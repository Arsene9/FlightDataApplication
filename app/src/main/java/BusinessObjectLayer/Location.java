package BusinessObjectLayer;

public class Location {
    private long locationID;
    private String airportID; // VARCHAR(6) NOT NULL,
    private String airportName; // VARCHAR(45),
    private String city; // VARCHAR(45),
    private String stateName; // VARCHAR(45),
    private String country; // VARCHAR(45),

    public Location(){
        this.airportID = "";
        this.airportName = "";
        this.city = "";
        this.stateName = "";
        this.country = "";
    }

    public Location(String data){
        String[] dList = data.split(";");
        this.airportID = dList[1];
        this.airportName = dList[2];
        this.city = dList[3];
        this.stateName = dList[4];
        this.country = dList[5];
    }

    public Location(String airportID, String airportName, String city, String stateName, String country) {
        this.airportID = airportID;
        this.airportName = airportName;
        this.city = city;
        this.stateName = stateName;
        this.country = country;
    }

    public String getAirportID() {
        return airportID;
    }

    public long getLocationID() {
        return locationID;
    }

    public void setLocationID(long locationID) {
        this.locationID = locationID;
    }

    public void setAirportID(String airportID) {
        this.airportID = airportID;
    }

    public String getAirportName() {
        return airportName;
    }

    public void setAirportName(String airportName) {
        this.airportName = airportName;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public boolean isSameAs(Location data){
        return (this.airportID.equals(data.getAirportID()) &&
                this.airportName.equals(data.getAirportName()) &&
                this.city.equals(data.getCity()) &&
                this.stateName.equals(data.getStateName()) &&
                this.country.equals(data.getCountry()));
    }

    public String listLocation(){
        return  this.locationID + ";" + this.airportID + ";" + this.airportName + ";" +
                this.city + ";" + this.stateName + ";" + this.country;
    }
}
