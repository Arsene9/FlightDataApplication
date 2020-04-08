package BusinessObjectLayer;

import com.example.naftech.flightdataapplication.CommonMethod;

import java.util.ArrayList;
import java.util.List;

public class FlightPlan {
    private long flightPlanID;
    private String tripDurationEstimate; // TIME(2) NOT NULL,
    private float fuelTaken; // FLOAT,
    private long aircraftID; // INT, //CONSTRAINT FK_FP_AirID FOREIGN KEY (Aircraft_ID)//REFERENCES Aircraft (Aircraft_ID)
    private long departureID; // INT, //CONSTRAINT FK_FP_DepID FOREIGN KEY (Departure_ID)//REFERENCES Departure (Departure_ID),
    private long arrivalID; // INT, //CONSTRAINT FK_FP_ArrID FOREIGN KEY (Arrival_ID)//REFERENCES Arrival (Arrival_ID),
    private long actualID; // INT, //CONSTRAINT FK_FP_ActID FOREIGN KEY (Actual_ID)//REFERENCES Actual (Actual_ID)
    private float tripDistanceEstimate; // FLOAT,
    private int climbSpeed; // INT,
    private int cruiseSpeed; // INT,
    private int cruiseAltitude; // INT,
    private float payloadWeight; // FLOAT,
    private float fuelWeight; // FLOAT,
    private float grossWeight; // FLOAT,
    private String fPStatus; // VARCHAR(10),

    public FlightPlan(){
        this.tripDurationEstimate = "";
        this.fuelTaken = 0;
        this.aircraftID = 0;
        this.departureID = 0;
        this.arrivalID = 0;
        this.actualID = 0;
        this.tripDistanceEstimate = 0;
        this.climbSpeed = 0;
        this.cruiseSpeed = 0;
        this.cruiseAltitude = 0;
        this.payloadWeight = 0;
        this.fuelWeight = 0;
        this.grossWeight = 0;
        this.fPStatus = "";
    }

    public FlightPlan(String data){
        String[] dList = data.split(";");
        this.tripDurationEstimate = dList[1];
        this.fuelTaken = Float.parseFloat(dList[2]);
        this.aircraftID = Long.parseLong(dList[3]);
        this.departureID = Long.parseLong(dList[4]);
        this.arrivalID = Long.parseLong(dList[5]);
        this.actualID = Long.parseLong(dList[6]);
        this.tripDistanceEstimate = Float.parseFloat(dList[7]);
        this.climbSpeed = Integer.parseInt(dList[8]);
        this.cruiseSpeed = Integer.parseInt(dList[9]);
        this.cruiseAltitude = Integer.parseInt(dList[10]);
        this.payloadWeight = Float.parseFloat(dList[11]);
        this.fuelWeight = Float.parseFloat(dList[12]);
        this.grossWeight = Float.parseFloat(dList[13]);
        this.fPStatus = dList[14];
    }

    public FlightPlan(String tripDurationEstimate, float fuelTaken, int aircraftID, int departureID,
                      int arrivalID, int actualID, float tripDistanceEstimate, int climbSpeed,
                      int cruiseSpeed, int cruiseAltitude, float payloadWeight, float fuelWeight,
                      float grossWeight, String fPStatus) {
        this.tripDurationEstimate = tripDurationEstimate;
        this.fuelTaken = fuelTaken;
        this.aircraftID = aircraftID;
        this.departureID = departureID;
        this.arrivalID = arrivalID;
        this.actualID = actualID;
        this.tripDistanceEstimate = tripDistanceEstimate;
        this.climbSpeed = climbSpeed;
        this.cruiseSpeed = cruiseSpeed;
        this.cruiseAltitude = cruiseAltitude;
        this.payloadWeight = payloadWeight;
        this.fuelWeight = fuelWeight;
        this.grossWeight = grossWeight;
        this.fPStatus = fPStatus;
    }

    public long getFlightPlanID() {
        return flightPlanID;
    }

    public void setFlightPlanID(long flightPlanID) {
        this.flightPlanID = flightPlanID;
    }

    public String getTripDurationEstimate() {
        return tripDurationEstimate;
    }

    public void setTripDurationEstimate(String tripDurationEstimate) {
        this.tripDurationEstimate = tripDurationEstimate;
    }

    public float getFuelTaken() {
        return fuelTaken;
    }

    public void setFuelTaken(float fuelTaken) {
        this.fuelTaken = fuelTaken;
    }

    public long getAircraftID() {
        return aircraftID;
    }

    public void setAircraftID(long aircraftID) {
        this.aircraftID = aircraftID;
    }

    public long getDepartureID() {
        return departureID;
    }

    public void setDepartureID(long departureID) {
        this.departureID = departureID;
    }

    public long getArrivalID() {
        return arrivalID;
    }

    public void setArrivalID(long arrivalID) {
        this.arrivalID = arrivalID;
    }

    public long getActualID() {
        return actualID;
    }

    public void setActualID(long actualID) {
        this.actualID = actualID;
    }

    public float getTripDistanceEstimate() {
        return tripDistanceEstimate;
    }

    public void setTripDistanceEstimate(float tripDistanceEstimate) {
        this.tripDistanceEstimate = tripDistanceEstimate;
    }

    public int getClimbSpeed() {
        return climbSpeed;
    }

    public void setClimbSpeed(int climbSpeed) {
        this.climbSpeed = climbSpeed;
    }

    public int getCruiseSpeed() {
        return cruiseSpeed;
    }

    public void setCruiseSpeed(int cruiseSpeed) {
        this.cruiseSpeed = cruiseSpeed;
    }

    public int getCruiseAltitude() {
        return cruiseAltitude;
    }

    public void setCruiseAltitude(int cruiseAltitude) {
        this.cruiseAltitude = cruiseAltitude;
    }

    public float getPayloadWeight() {
        return payloadWeight;
    }

    public void setPayloadWeight(float payloadWeight) {
        this.payloadWeight = payloadWeight;
    }

    public float getFuelWeight() {
        return fuelWeight;
    }

    public void setFuelWeight(float fuelWeight) {
        this.fuelWeight = fuelWeight;
    }

    public float getGrossWeight() {
        return grossWeight;
    }

    public void setGrossWeight(float grossWeight) {
        this.grossWeight = grossWeight;
    }

    public String getfPStatus() {
        return fPStatus;
    }

    public void setfPStatus(String fPStatus) {
        this.fPStatus = fPStatus;
    }

    /**
     * Stores the data of FlightPlan into an internal file
     * @return true if the action was successful and false when it fails
     */
    public boolean backupEntityData(){
        boolean backedup = false;
        CommonMethod cm = new CommonMethod();
        List<String> data = new ArrayList<>();
        data.add(listFlightPlan());

        if(cm.saveToInternalFile(data, "EntityFlightPlanData"))
            backedup = true;

        return backedup;
    }

    /**
     * Retrieves the data of FlightPlan from an internal file
     * @return true if the action was successful and false when it fails
     */
    public boolean restoreEntityData(){
        boolean restored = false;
        CommonMethod cm = new CommonMethod();
        List<String> data = new ArrayList<>();
        data.addAll(cm.getInternalFileData("EntityFlightPlanData"));
        if(!data.isEmpty()) {
            restored = true;
            String[] dList = data.get(0).split(";");
            this.tripDurationEstimate = dList[1];
            this.fuelTaken = Float.parseFloat(dList[2]);
            this.aircraftID = Long.parseLong(dList[3]);
            this.departureID = Long.parseLong(dList[4]);
            this.arrivalID = Long.parseLong(dList[5]);
            this.actualID = Long.parseLong(dList[6]);
            this.tripDistanceEstimate = Float.parseFloat(dList[7]);
            this.climbSpeed = Integer.parseInt(dList[8]);
            this.cruiseSpeed = Integer.parseInt(dList[9]);
            this.cruiseAltitude = Integer.parseInt(dList[10]);
            this.payloadWeight = Float.parseFloat(dList[11]);
            this.fuelWeight = Float.parseFloat(dList[12]);
            this.grossWeight = Float.parseFloat(dList[13]);
            if(dList.length > 14)
                this.fPStatus = dList[14];
        }
        return restored;
    }

    public boolean isSameAs(FlightPlan fP){
        return (this.tripDurationEstimate.equals(fP.tripDurationEstimate) &&
        this.fuelTaken == fP.fuelTaken &&
        this.aircraftID == fP.aircraftID &&
        this.departureID == fP.departureID &&
        this.arrivalID == fP.arrivalID &&
        this.actualID == fP.actualID &&
        this.tripDistanceEstimate == fP.tripDistanceEstimate &&
        this.climbSpeed == fP.climbSpeed &&
        this.cruiseSpeed == fP.cruiseSpeed &&
        this.cruiseAltitude == fP.cruiseAltitude &&
        this.payloadWeight == fP.payloadWeight &&
        this.fuelWeight == fP.fuelWeight &&
        this.grossWeight == fP.grossWeight &&
        this.fPStatus.equals(fP.fPStatus));
    }

    public String listFlightPlan(){
        return  this.flightPlanID + ";" + this.tripDurationEstimate + ";" + this.fuelTaken + ";" +
                this.aircraftID + ";" + this.departureID + ";" + this.arrivalID + ";" +
                this.actualID + ";" + this.tripDistanceEstimate + ";" +
                this.climbSpeed + ";" + this.cruiseSpeed + ";" + this.cruiseAltitude + ";" +
                this.payloadWeight + ";" + this.fuelWeight + ";" + this.grossWeight + ";" +
                this.fPStatus;
    }
}
