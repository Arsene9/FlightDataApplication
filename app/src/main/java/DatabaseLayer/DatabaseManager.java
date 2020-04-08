package DatabaseLayer;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.se.omapi.Reader;

import java.util.List;

import BusinessObjectLayer.ActualData;
import BusinessObjectLayer.Aircraft;
import BusinessObjectLayer.Arrival;
import BusinessObjectLayer.Departure;
import BusinessObjectLayer.FlightPlan;
import BusinessObjectLayer.Location;
import BusinessObjectLayer.Runway;

public class DatabaseManager extends SQLiteOpenHelper {

    private static DatabaseManager dbHandlerInstance = null;
    private static final String DATABASE_NAME = "FlightPlan";
    private static final int DATABASE_VERSION = 5;

    public static synchronized DatabaseManager getInstance(Context context) {

        if (dbHandlerInstance == null) {
            dbHandlerInstance = new DatabaseManager(context.getApplicationContext());
        }
        return dbHandlerInstance;
    }

    private DatabaseManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }@Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        ActualTable.onCreate(db);
        AircraftTable.onCreate(db);
        ArrivalTable.onCreate(db);
        DepartureTable.onCreate(db);
        FlightPlanTable.onCreate(db);
        LocationTable.onCreate(db);
        RunwayTable.onCreate(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        ActualTable.onUpgrade(db,oldVersion,newVersion);
        AircraftTable.onUpgrade(db,oldVersion,newVersion);
        ArrivalTable.onUpgrade(db,oldVersion,newVersion);
        DepartureTable.onUpgrade(db,oldVersion,newVersion);
        FlightPlanTable.onUpgrade(db,oldVersion,newVersion);
        LocationTable.onUpgrade(db,oldVersion,newVersion);
        RunwayTable.onUpgrade(db,oldVersion,newVersion);
    }

    ////////////////////////////////////  Actual Table Manager  ////////////////////////////////////
    public boolean addActualData(ActualData data){
        SQLiteDatabase db = this.getWritableDatabase();
        return ActualTable.insertItem(db, data);
    }

    public boolean deleteActualData(ActualData data){
        SQLiteDatabase db = this.getWritableDatabase();
        return ActualTable.deleteItem(db, data);
    }

    public boolean updateActual(String actualID, String nVal, String colName){
        SQLiteDatabase db = this.getReadableDatabase();
        return ActualTable.updateItem(db, actualID, nVal, colName);
    }

    public boolean restoreActualData(List<ActualData> actualDataList){
        SQLiteDatabase db = this.getReadableDatabase();
        return ActualTable.restoreDB(db, actualDataList);
    }

    public List<ActualData> getActuals(){
        SQLiteDatabase db = this.getReadableDatabase();
        return ActualTable.getItems(db);
    }

    //////////////////////////////////  Aircraft Table Manager  ////////////////////////////////////
    public boolean addAircraft(Aircraft data){
        SQLiteDatabase db = this.getWritableDatabase();
        return AircraftTable.insertItem(db, data);
    }

    public boolean deleteAircraft(Aircraft data){
        SQLiteDatabase db = this.getWritableDatabase();
        return AircraftTable.deleteItem(db, data);
    }

    public boolean restoreAircraftData(List<Aircraft> aircraftList){
        SQLiteDatabase db = this.getReadableDatabase();
        return AircraftTable.restoreDB(db, aircraftList);
    }

    public List<Aircraft> getAirCrafts(){
        SQLiteDatabase db = this.getReadableDatabase();
        return AircraftTable.getItems(db);
    }

    ///////////////////////////////////  Arrival Table Manager  ////////////////////////////////////
    public boolean addArrival(Arrival data){
        SQLiteDatabase db = this.getWritableDatabase();
        return ArrivalTable.insertItem(db, data);
    }

    public boolean deleteArrival(Arrival data){
        SQLiteDatabase db = this.getWritableDatabase();
        return ArrivalTable.deleteItem(db, data);
    }

    public boolean updateArrival(String arrivalID, String nVal, String colName){
        SQLiteDatabase db = this.getReadableDatabase();
        return ArrivalTable.updateItem(db, arrivalID, nVal, colName);
    }

    public boolean restoreArrivalData(List<Arrival> arrivalList) {
        SQLiteDatabase db = this.getReadableDatabase();
        return ArrivalTable.restoreDB(db, arrivalList);
    }

    public List<Arrival> getArrivals(){
        SQLiteDatabase db = this.getReadableDatabase();
        return ArrivalTable.getItems(db);
    }

    public long getArrivalTableSize(){
        SQLiteDatabase db = this.getReadableDatabase();
        return ArrivalTable.getTableCount(db);
    }

    /////////////////////////////////  Departure Table Manager  ////////////////////////////////////
    public boolean addDeparture(Departure data){
        SQLiteDatabase db = this.getWritableDatabase();
        return DepartureTable.insertItem(db, data);
    }

    public boolean deleteDeparture(Departure data){
        SQLiteDatabase db = this.getWritableDatabase();
        return DepartureTable.deleteItem(db, data);
    }

    public boolean restoreDepartureData(List<Departure> departureList){
        SQLiteDatabase db = this.getReadableDatabase();
        return DepartureTable.restoreDB(db, departureList);
    }

    public boolean updateDeparture(String departureID, String nVal, String colName){
        SQLiteDatabase db = this.getReadableDatabase();
        return DepartureTable.updateItem(db, departureID, nVal, colName);
    }

    public List<Departure> getDepartures(){
        SQLiteDatabase db = this.getReadableDatabase();
        return DepartureTable.getItems(db);
    }

    public long getDepartureTableSize(){
        SQLiteDatabase db = this.getReadableDatabase();
        return DepartureTable.getTableCount(db);
    }
    ///////////////////////////////  Flight Plan Table Manager  ////////////////////////////////////
    public boolean addFlightPlan(FlightPlan data){
        SQLiteDatabase db = this.getWritableDatabase();
        return FlightPlanTable.insertItem(db, data);
    }

    public boolean deleteFlightPlan(FlightPlan data){
        SQLiteDatabase db = this.getWritableDatabase();
        return FlightPlanTable.deleteItem(db, data);
    }

    public boolean updateFlightPlan(String tID, String nVal, String colName){
        SQLiteDatabase db = this.getReadableDatabase();
        return FlightPlanTable.updateItem(db, tID, nVal, colName);
    }

    public boolean restoreFlightPlanData(List<FlightPlan> flightPlanList){
        SQLiteDatabase db = this.getReadableDatabase();
        return FlightPlanTable.restoreDB(db, flightPlanList);
    }

    public List<FlightPlan> getFlightPlans(){
        SQLiteDatabase db = this.getReadableDatabase();
        return FlightPlanTable.getItems(db);
    }

    //////////////////////////////////  Location Table Manager  ////////////////////////////////////
    public boolean addLocation(Location data){
        SQLiteDatabase db = this.getWritableDatabase();
        return LocationTable.insertItem(db, data);
    }

    public boolean deleteLocation(Location data){
        SQLiteDatabase db = this.getWritableDatabase();
        return LocationTable.deleteItem(db, data);
    }

    public boolean restoreLocationData(List<Location> locationList){
        SQLiteDatabase db = this.getReadableDatabase();
        return LocationTable.restoreDB(db, locationList);
    }

    public List<Location> getLocations(){
        SQLiteDatabase db = this.getReadableDatabase();
        return LocationTable.getItems(db);
    }

    ////////////////////////////////////  Runway Table Manager  ////////////////////////////////////
    public boolean addRunway(Runway data){
        SQLiteDatabase db = this.getWritableDatabase();
        return RunwayTable.insertItem(db, data);
    }

    public boolean deleteRunway(Runway data){
        SQLiteDatabase db = this.getWritableDatabase();
        return RunwayTable.deleteItem(db, data);
    }

    public boolean restoreRunwayData(List<Runway> runwayList){
        SQLiteDatabase db = this.getReadableDatabase();
        return RunwayTable.restoreDB(db, runwayList);
    }

    public List<Runway> getRwys(long loc_ID){
        SQLiteDatabase db = this.getReadableDatabase();
        return RunwayTable.getItems(db, loc_ID);
    }

    public List<Runway> getRwys() {
        SQLiteDatabase db = this.getReadableDatabase();
        return RunwayTable.getItems(db);
    }

}
