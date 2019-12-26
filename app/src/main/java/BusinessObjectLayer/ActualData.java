package BusinessObjectLayer;

public class ActualData {
    private long actual_ID;
    private String departureTime; // TIME(2),
    private String arrivalTime; // TIME(2),
    private float fuelBalance; // FLOAT,
    private float fuelUsed; // FLOAT,
    private String totalTripDuration; // TIME(2),
    private float totalTripDistance; // FLOAT,
    private String gateParkingName; // VARCHAR(5)

    public ActualData(){
        this.actual_ID = 0;
        this.departureTime = "";
        this.arrivalTime = "";
        this.fuelBalance = 0;
        this.fuelUsed = 0;
        this.totalTripDuration = "";
        this.totalTripDistance = 0;
        this.gateParkingName = "";
    }
    public ActualData(String data){
        String[] dList = data.split(";");
        this.actual_ID = Long.parseLong(dList[0]);
        this.departureTime = dList[1];
        this.arrivalTime = dList[2];
        this.fuelBalance = Float.parseFloat(dList[3]);
        this.fuelUsed = Float.parseFloat(dList[4]);
        this.totalTripDuration = dList[5];
        this.totalTripDistance = Float.parseFloat(dList[6]);
        this.gateParkingName = dList[7];
    }

    public ActualData(String departureTime, String arrivalTime, float fuelBalance, float fuelUsed,
                      String totalTripDuration, float totalTripDistance, String gateParkingName) {
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.fuelBalance = fuelBalance;
        this.fuelUsed = fuelUsed;
        this.totalTripDuration = totalTripDuration;
        this.totalTripDistance = totalTripDistance;
        this.gateParkingName = gateParkingName;
    }

    public long getActual_ID() {
        return actual_ID;
    }

    public void setActual_ID(long actual_ID) {
        this.actual_ID = actual_ID;
    }

    public String getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(String departureTime) {
        this.departureTime = departureTime;
    }

    public String getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(String arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public float getFuelBalance() {
        return fuelBalance;
    }

    public void setFuelBalance(float fuelBalance) {
        this.fuelBalance = fuelBalance;
    }

    public float getFuelUsed() {
        return fuelUsed;
    }

    public void setFuelUsed(float fuelUsed) {
        this.fuelUsed = fuelUsed;
    }

    public String getTotalTripDuration() {
        return totalTripDuration;
    }

    public void setTotalTripDuration(String totalTripDuration) {
        this.totalTripDuration = totalTripDuration;
    }

    public float getTotalTripDistance() {
        return totalTripDistance;
    }

    public void setTotalTripDistance(float totalTripDistance) {
        this.totalTripDistance = totalTripDistance;
    }

    public String getGateParkingName() {
        return gateParkingName;
    }

    public void setGateParkingName(String gateParkingName) {
        this.gateParkingName = gateParkingName;
    }

    public boolean isSameAs(ActualData ad){
        return (this.departureTime.equals(ad.departureTime) &&
        this.arrivalTime.equals(ad.arrivalTime) &&
        this.fuelBalance == ad.fuelBalance &&
        this.fuelUsed == ad.fuelUsed &&
        this.totalTripDuration.equals(ad.totalTripDuration) &&
        this.totalTripDistance == ad.totalTripDistance &&
        this.gateParkingName.equals(ad.gateParkingName));
    }

    public String listActualData(){
        return this.actual_ID + ";" + this.departureTime + ";" + this.arrivalTime + ";" +
                this.fuelBalance + ";" + this.fuelUsed + ";" +
                this.totalTripDuration + ";" + this.totalTripDistance + ";" +
                this.gateParkingName;
    }
}
