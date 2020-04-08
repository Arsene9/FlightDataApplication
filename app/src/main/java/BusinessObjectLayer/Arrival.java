package BusinessObjectLayer;

import com.example.naftech.flightdataapplication.CommonMethod;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
            this.locationID = Long.parseLong(dList[1]);
            this.gateParking = dList[2];
            if(dList.length == 4)
                this.arrivalTime = dList[3];
            else
                this.arrivalTime = "";
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
