package BusinessObjectLayer;

import com.example.naftech.flightdataapplication.CommonMethod;

import java.util.ArrayList;
import java.util.List;

import DatabaseLayer.DatabaseManager;

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
        this.gateParkingName = dList[2];
        if(dList.length == 4)
            this.departureTime = dList[3];
        else
            this.departureTime = "";
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

    /**
     * Re-initializes the values of Departure parameters.
     * Sets the values of Departure object to 0 or null
     */
    public void resetDeparture(){
        this.departureID = 0;
        this.locationID = 0;
        this.gateParkingName = null;
        this.departureTime = null;
    }

    /**
     * Updates the database and the object's attribute Gate Parking name
     * @param dbMan : The database manager instance
     * @param newValue : The new value for the object's attribute
     */
    public void updateDBGate(DatabaseManager dbMan, String newValue){
        String columnName = "GATE_Parking_Name";
        if(dbMan.updateDeparture(String.valueOf(getDepartureID()), newValue, columnName))
            setGateParkingName(newValue);
    }

    /**
     * Updates the database and the object's attribute Location ID
     * @param dbMan : The database manager instance
     * @param newValue : The new value for the object's attribute
     */
    public void updateDBLocationID(DatabaseManager dbMan, String newValue){
        String columnName = "Location_ID";
        if(dbMan.updateDeparture(String.valueOf(getDepartureID()), newValue, columnName))
            setLocationID(Long.parseLong(newValue));
    }

    /**
     * Updates the database and the object's attribute Departure Time
     * @param dbMan : The database manager instance
     * @param newValue : The new value for the object's attribute
     */
    public void updateDBDepartTime(DatabaseManager dbMan, String newValue){
        String columnName = "Departure_Time";
        if(dbMan.updateDeparture(String.valueOf(getDepartureID()), newValue, columnName))
            setDepartureTime(newValue);
    }

    /**
     * Stores the data of Departure into an internal file
     * @return true if the action was successful and false when it fails
     */
    public boolean backupEntityData(){
        boolean backedup = false;
        CommonMethod cm = new CommonMethod();
        List<String> data = new ArrayList<>();
        data.add(listDeparture());

        if(cm.saveToInternalFile(data, "EntityDepartureData"))
            backedup = true;

        return backedup;
    }

    /**
     * Retrieves the data of Departure from an internal file
     * @return true if the action was successful and false when it fails
     */
    public boolean restoreEntityData(){
        boolean restored = false;
        CommonMethod cm = new CommonMethod();
        List<String> data = new ArrayList<>();
        data.addAll(cm.getInternalFileData("EntityDepartureData"));
        if(!data.isEmpty()) {
            restored = true;
            String[] dList = data.get(0).split(";");
            try {
                this.departureID = Long.parseLong(dList[0]);
                this.locationID = Long.parseLong(dList[1]);
                this.gateParkingName = dList[2];
                if (dList.length == 4)
                    this.departureTime = dList[3];
                else
                    this.departureTime = "";
            }
            catch(IndexOutOfBoundsException e){
                this.locationID = 0;
                this.gateParkingName = "";
                this.departureTime = "";
                this.departureTime = "";
            }
        }
        return restored;
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
