package BusinessObjectLayer;

import com.example.naftech.flightdataapplication.CommonMethod;

import java.util.ArrayList;
import java.util.List;

import DatabaseLayer.DatabaseManager;

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

    /**
     * Re-initializes the values of Arrival parameters.
     * Sets the values of Arrival object to 0 or null
     */
    public void resetArrival(){
        this.arrivalID = 0;
        this.locationID = 0;
        this.gateParking = null;
        this.arrivalTime = null;
    }

    /**
     * Updates the database and the object's attribute Gate Parking name
     * @param dbMan : The database manager instance
     * @param newValue : The new value for the object's attribute
     */
    public void updateDBGate(DatabaseManager dbMan, String newValue){
        String columnName = "GATE_Parking_Name";
        if(dbMan.updateArrival(String.valueOf(getArrivalID()), newValue, columnName))
            setGateParking(newValue);
    }

    /**
     * Updates the database and the object's attribute Location ID
     * @param dbMan : The database manager instance
     * @param newValue : The new value for the object's attribute
     */
    public void updateDBLocationID(DatabaseManager dbMan, String newValue){
        String columnName = "Location_ID";
        if(dbMan.updateArrival(String.valueOf(getArrivalID()), newValue, columnName))
            setLocationID(Long.parseLong(newValue));
    }

    /**
     * Updates the database and the object's attribute Arrival Time
     * @param dbMan : The database manager instance
     * @param newValue : The new value for the object's attribute
     */
    public void updateDBArriveTime(DatabaseManager dbMan, String newValue){
        String columnName = "Arrival_Time";
        if(dbMan.updateArrival(String.valueOf(getArrivalID()), newValue, columnName))
            setArrivalTime(newValue);
    }

    /**
     * Stores the data of Arrival into an internal file
     * @return true if the action was successful and false when it fails
     */
    public boolean backupEntityData(){
        boolean backedup = false;
        CommonMethod cm = new CommonMethod();
        List<String> data = new ArrayList<>();
        data.add(listArrival());

        if(cm.saveToInternalFile(data, "EntityArrivalData"))
            backedup = true;

        return backedup;
    }

    /**
     * Retrieves the data of Arrival from an internal file
     * @return true if the action was successful and false when it fails
     */
    public boolean restoreEntityData(){
        boolean restored = false;
        CommonMethod cm = new CommonMethod();
        List<String> data = new ArrayList<>();
        data.addAll(cm.getInternalFileData("EntityArrivalData"));
        if(!data.isEmpty()) {
            restored = true;
            String[] dList = data.get(0).split(";");
            try {
                this.arrivalID = Long.parseLong(dList[0]);
                this.locationID = Long.parseLong(dList[1]);
                this.gateParking = dList[2];
                if (dList.length == 4)
                    this.arrivalTime = dList[3];
                else
                    this.arrivalTime = "";
            }
            catch (IndexOutOfBoundsException e){
                this.locationID = 0;
                this.gateParking = "";
                this.arrivalTime = "";
                this.arrivalTime = "";
            }
        }
        return restored;
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
