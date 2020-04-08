package BusinessObjectLayer;

import com.example.naftech.flightdataapplication.CommonMethod;

import java.util.ArrayList;
import java.util.List;

public class Runway {
    private long runwayID;
    private String name; // VARCHAR(6),
    private int length; // INT,
    private String surface; // VARCHAR(20),
    private int hdg; // INT,
    private String ILS_ID; // VARCHAR(10),
    private float ILS_Freq; // FLOAT,
    private long location_ID;

    //Default Constructor
    public Runway(){
        this.name = "";
        this.length = 0;
        this.surface = "";
        this.hdg = 0;
        this.ILS_ID = "";
        this.ILS_Freq = 0;
        this.location_ID = 0;
    }

    public Runway(String data){
        String[] dList = data.split(";");
        this.name = dList[1];
        this.length = Integer.parseInt(dList[2]);
        this.surface = dList[3];
        this.hdg = Integer.parseInt(dList[4]);
        this.ILS_ID = dList[5];
        this.ILS_Freq = Float.parseFloat(dList[6]);
        this.location_ID = Long.parseLong(dList[7]);
    }


    public Runway(String name, int length, String surface, int hdg, String ILS_ID, float ILS_Freq, int location_ID) {
        this.name = name;
        this.length = length;
        this.surface = surface;
        this.hdg = hdg;
        this.ILS_ID = ILS_ID;
        this.ILS_Freq = ILS_Freq;
        this.location_ID = location_ID;
    }

    public long getRunwayID() {
        return runwayID;
    }

    public void setRunwayID(long runwayID) {
        this.runwayID = runwayID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public String getSurface() {
        return surface;
    }

    public void setSurface(String surface) {
        this.surface = surface;
    }

    public int getHdg() {
        return hdg;
    }

    public void setHdg(int hdg) {
        this.hdg = hdg;
    }

    public String getILS_ID() {
        return ILS_ID;
    }

    public void setILS_ID(String ILS_ID) {
        this.ILS_ID = ILS_ID;
    }

    public float getILS_Freq() {
        return ILS_Freq;
    }

    public void setILS_Freq(float ILS_Freq) {
        this.ILS_Freq = ILS_Freq;
    }

    public long getLocation_ID() {
        return location_ID;
    }

    public void setLocation_ID(long location_ID) {
        this.location_ID = location_ID;
    }

    /**
     * Stores the data of Runway into an internal file
     * @return true if the action was successful and false when it fails
     */
    public boolean backupEntityData(){
        boolean backedup = false;
        CommonMethod cm = new CommonMethod();
        List<String> data = new ArrayList<>();
        data.add(listRunway());

        if(cm.saveToInternalFile(data, "EntityRunwayData"))
            backedup = true;

        return backedup;
    }

    /**
     * Retrieves the data of Runway from an internal file
     * @return true if the action was successful and false when it fails
     */
    public boolean restoreEntityData(){
        boolean restored = false;
        CommonMethod cm = new CommonMethod();
        List<String> data = new ArrayList<>();
        data.addAll(cm.getInternalFileData("EntityRunwayData"));
        if(!data.isEmpty()) {
            restored = true;
            String[] dList = data.get(0).split(";");
            this.name = dList[1];
            this.length = Integer.parseInt(dList[2]);
            this.surface = dList[3];
            this.hdg = Integer.parseInt(dList[4]);
            this.ILS_ID = dList[5];
            this.ILS_Freq = Float.parseFloat(dList[6]);
            this.location_ID = Long.parseLong(dList[7]);
        }
        return restored;
    }

    public boolean isSameAs(Runway data){
        return (this.name.equals(data.name) &&
                this.length == data.length &&
                this.surface.equals(data.surface) &&
                this.hdg == data.hdg &&
                this.ILS_ID.equals(data.ILS_ID) &&
                this.ILS_Freq == data.ILS_Freq &&
                this.location_ID == data.location_ID);
    }

    public String listRunway(){
        return this.runwayID + ";" + this.name + ";" + this.length + ";" + this.surface + ";" + this.hdg + ";" +
                this.ILS_ID + ";" + this.ILS_Freq + ";" + this.location_ID;
    }
}
