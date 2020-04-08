package com.example.naftech.flightdataapplication.TabManager;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.naftech.flightdataapplication.CommonMethod;
import com.example.naftech.flightdataapplication.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import BusinessObjectLayer.Aircraft;
import BusinessObjectLayer.Arrival;
import BusinessObjectLayer.Departure;
import BusinessObjectLayer.FlightPlan;
import BusinessObjectLayer.Location;
import BusinessObjectLayer.Runway;
import DatabaseLayer.DatabaseManager;

public class DepartureFragment extends Fragment {

    private static AutoCompleteTextView departArp, arriveArp, departGate, arriveGate, cruiseAltitude,
            flightDistEst, flightTimeEst, departFuel, fuelWeight, grossWeight, payloadWeight,
            departRwy, climbSpeed, cruiseSpeed;
    private static EditText departTime, arriveTime;
    private static Button saveBtn, editBtn, newBtn;

    private DatabaseManager dbMan;
    private static Location departLocInfo;
    private static Location arriveLocInfo;
    private static FlightPlan flightPlanInfo;
    private static Arrival arriveInfo;
    private static Departure departInfo;
    private Runway deprwyInfo, arrrwyInfo;
    private List<Location> locList;
    private List<Runway> arrivalRwyList, departureRwyList;
    private static List<String> lList, depRwyList, arrRwyList;
    private static ArrayAdapter<String> depLoclist, arrLoclist;
    private Calendar calendar;
    private TimePickerDialog timePicker;
    private static CommonMethod cm;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dbMan = DatabaseManager.getInstance(getContext());
        cm = new CommonMethod();
        departLocInfo = new Location();
        arriveLocInfo = new Location();
        flightPlanInfo = new FlightPlan();
        arriveInfo = new Arrival();
        departInfo = new Departure();
        deprwyInfo = new Runway();
        arrrwyInfo = new Runway();
        locList = new ArrayList<>();
        arrivalRwyList = new ArrayList<>();
        departureRwyList = new ArrayList<>();
        arrRwyList = new ArrayList<>();
        depRwyList = new ArrayList<>();
        lList = new ArrayList<>();

//        departureRwyList.addAll(dbMan.getRwys(locInfo.getLocationID()));
//        messageToaster(String.valueOf(departureRwyList.size()));
//        for(Runway r : departureRwyList){
////            depRwyList.add(r.getName() + " " + r.getLength() + "ft");
//            if(dbMan.deleteRunway(r))
//                messageToaster("Deleted " + r.getRunwayID());
//        }

        locList.addAll(dbMan.getLocations());
        for(Location l : locList){
            lList.add(l.getAirportID() + " "+ l.getAirportName() + " " + l.getCity() + " " + l.getCountry());
//            dbMan.deleteLocation(l);
        }

//        arriveInfo.setArrivalTime("7/10/2019 20:16:30");
//        arriveInfo.setGateParking("G12");
//        arriveInfo.setLocationID(3);
//        dbMan.addArrival(arriveInfo);
//        cm.messageToaster(getContext(), arriveInfo.listArrival());
//        List<String> data = new ArrayList<>();
//        for(Arrival arrv : dbMan.getArrivals()){
//            data.add(arrv.listArrival());
//        }
//        cm.save(getContext(), "arrival.txt", data);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.departure_fragment, container,false);

        departArp = view.findViewById(R.id.departureAirportLocationACTV);
        arriveArp = view.findViewById(R.id.destinationAirporACTV);
        departGate = view.findViewById(R.id.departureGateParkingACTV);
        arriveGate = view.findViewById(R.id.arrivalGateParkingACTV);
        cruiseAltitude = view.findViewById(R.id.cruiseAltitudeACTV);
        flightDistEst = view.findViewById(R.id.distanceEstimateACTV5);
        flightTimeEst = view.findViewById(R.id.flightTimeEstACTV);
        departFuel = view.findViewById(R.id.departureFuelACTV);
        fuelWeight = view.findViewById(R.id.fuelWeightACTV);
        grossWeight = view.findViewById(R.id.grossWeightACTV);
        payloadWeight = view.findViewById(R.id.payloadWeightACTV);
        departRwy = view.findViewById(R.id.departureRwyACTV);
        climbSpeed = view.findViewById(R.id.climbSpeedACTV);
        cruiseSpeed = view.findViewById(R.id.cruiseSpeedACTV);
        departTime = view.findViewById(R.id.departureTimeEditText);
        arriveTime = view.findViewById(R.id.arrivalTimeEditText);
        saveBtn = view.findViewById(R.id.saveDataButton);
        editBtn = view.findViewById(R.id.editDepartureDataButton);
        newBtn = view.findViewById(R.id.newDepartureDataButton);

        editBtn.setVisibility(View.GONE);
        newBtn.setVisibility(View.GONE);

        saveBtn.setOnClickListener(saveDataOnClick);
        editBtn.setOnClickListener(editDepartDataOnClick);
        newBtn.setOnClickListener(newDepartDataOnClick);

        arriveArp.setOnItemClickListener(onArrivalLocSelected);
        departArp.setOnItemClickListener(onDepartureLocSelected);
//        ArrivalFragment.arriveRwy.setOnItemClickListener(onArrivalRwySelected);
//        departRwy.setOnItemClickListener(onDepartureRwySelected);
        arriveTime.setOnClickListener(addArrivalEstimatedTime);
        departTime.setOnClickListener(onDepartTimeEditTextClick);
        flightTimeEst.setOnClickListener(onTripDurationEditTextClick);
        dropdownList();

        return view;
    }

    static List<String> departFragData = new ArrayList<>();
    private static String activityDataFileName = "DepartureActivityDataFile.txt";

    @Override
    public void onPause() {
        super.onPause();
        departFragData.clear();
        departFragData.add(departArp.getText().toString());
        departFragData.add(arriveArp.getText().toString());
        departFragData.add(departGate.getText().toString());
        departFragData.add(arriveGate.getText().toString());
        departFragData.add(cruiseAltitude.getText().toString());
        departFragData.add(flightDistEst.getText().toString());
        departFragData.add(flightTimeEst.getText().toString());
        departFragData.add(departFuel.getText().toString());
        departFragData.add(fuelWeight.getText().toString());
        departFragData.add(grossWeight.getText().toString());
        departFragData.add(payloadWeight.getText().toString());
        departFragData.add(departRwy.getText().toString());
        departFragData.add(climbSpeed.getText().toString());
        departFragData.add(cruiseSpeed.getText().toString());
        departFragData.add(departTime.getText().toString());
        departFragData.add(arriveTime.getText().toString());
        departFragData.add(String.valueOf(saveBtn.getVisibility()));
        departFragData.add(String.valueOf(editBtn.getVisibility()));
        departFragData.add(String.valueOf(newBtn.getVisibility()));

        flightPlanInfo.backupEntityData();
        arriveInfo.backupEntityData();
        departInfo.backupEntityData();

        cm.saveToInternalFile(departFragData, activityDataFileName);
    }

    @Override
    public void onResume() {
        super.onResume();
        restoreFragValues();
        int count = 0;
        for(String loc : lList){
            if(!departArp.getText().toString().isEmpty() && loc.startsWith(departArp.getText().toString())) {
                ///Regenerate the Departure runways' List
                departLocInfo = locList.get(lList.indexOf(loc));
                departArp.setText(departLocInfo.getAirportID() + " " + departLocInfo.getAirportName());
                //messageToaster(String.valueOf(departLocInfo.getLocationID())+ " " + departLocInfo.getAirportID());
//                departureRwyList.addAll(dbMan.getRwys(departLocInfo.getLocationID()));
//                if(departureRwyList.isEmpty())
//                    departureRwyList.addAll(dbMan.getRwys(departLocInfo.getLocationID()));
//                else{
                    departureRwyList.clear();
                    depRwyList.clear();
                    departureRwyList.addAll(dbMan.getRwys(departLocInfo.getLocationID()));
                    //get depart info IDs
//                    departInfo.setLocationID(departLocInfo.getLocationID());
//                    departInfo.setDepartureID(dbMan.getDepartureTableSize());
//                }
                for(Runway r : departureRwyList){
                    depRwyList.add(r.getName() + "   " + r.getLength() + "ft  " + r.getILS_ID()
                            + "   " + r.getILS_Freq() + "Hz   " + r.getHdg());
                }
                depLoclist = new ArrayAdapter<String>(getContext(),
                        R.layout.support_simple_spinner_dropdown_item, depRwyList);
                departRwy.setAdapter(depLoclist); //Attaches the dropdown list depLoclist to departRwy
                departRwy.setDropDownWidth(900);
                count++;
            }
            else if(!arriveArp.getText().toString().isEmpty() && loc.startsWith(arriveArp.getText().toString())){
                ///Regenerate the Arrival runways' list
                arriveLocInfo = locList.get(lList.indexOf(loc));
                arriveArp.setText(arriveLocInfo.getAirportID() + " " + arriveLocInfo.getAirportName());
//                arrivalRwyList.addAll(dbMan.getRwys(arriveLocInfo.getLocationID()));
//                if (arrivalRwyList.isEmpty()) {
//                    arrivalRwyList.addAll(dbMan.getRwys(arriveLocInfo.getLocationID()));
//                } else {
                    arrivalRwyList.clear();
                    arrRwyList.clear();
                    arrivalRwyList.addAll(dbMan.getRwys(arriveLocInfo.getLocationID()));
                    //get arrival info IDs
//                    arriveInfo.setLocationID(arriveLocInfo.getLocationID());
//                    arriveInfo.setArrivalID(dbMan.getArrivalTableSize());
//                }
                for(Runway r : arrivalRwyList){
                    arrRwyList.add(r.getName() + "   " + r.getLength() + "ft  " + r.getILS_ID()
                            + "   " + r.getILS_Freq() + "Hz   " + r.getHdg());
                }
                arrLoclist = new ArrayAdapter<String>(getContext(),
                        R.layout.support_simple_spinner_dropdown_item, arrRwyList);
                ArrivalFragment.arriveRwy.setAdapter(arrLoclist); //Attaches the dropdown list arrLoclist to arriveRwy
                ArrivalFragment.arriveRwy.setDropDownWidth(900);
                count++;
            }
            else if(count == 2) break;
        }
        flightPlanInfo.restoreEntityData();
        arriveInfo.restoreEntityData();
        departInfo.restoreEntityData();
    }

    ////////////////////////////////////  Event Listeners Definition  //////////////////////////////
    private EditText.OnClickListener onTripDurationEditTextClick = new EditText.OnClickListener() {
        @Override
        public void onClick(View view) {
            calendar = Calendar.getInstance();

            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);
            timePicker = new TimePickerDialog(getContext(), onTimeSetListenerTripDuration, hour, minute, true);
            timePicker.show();
        }
    };
    private EditText.OnClickListener onDepartTimeEditTextClick = new EditText.OnClickListener() {
        @Override
        public void onClick(View view) {
            calendar = Calendar.getInstance();

            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);
            //int second = calendar.get(Calendar.SECOND);
            timePicker = new TimePickerDialog(getContext(), onTimeSetListenerDepartTime, hour, minute, true);
            timePicker.show();
        }
    };

    private TimePickerDialog.OnTimeSetListener onTimeSetListenerTripDuration = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker timePicker, int dHour, int dMin) {
            flightTimeEst.setText(dHour + ":" + dMin + ":00");
        }
    };
    private TimePickerDialog.OnTimeSetListener onTimeSetListenerDepartTime = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker timePicker, int dHour, int dMin) {
            departTime.setText(dHour + ":" + dMin + ":00");
        }
    };


    private AdapterView.OnClickListener addArrivalEstimatedTime = new AdapterView.OnClickListener(){

        @Override
        public void onClick(View view) {
            if(!departTime.getText().toString().isEmpty() && !flightTimeEst.getText().toString().isEmpty()) {
                String currentDate = (Calendar.getInstance(Locale.US).get(Calendar.MONTH)+1) + "/" +
                        Calendar.getInstance(Locale.US).get(Calendar.DAY_OF_MONTH) + "/" +
                        Calendar.getInstance(Locale.US).get(Calendar.YEAR);
                currentDate += " " + departTime.getText().toString();
                try {
                    Date cDate = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").parse(currentDate);
                    Date flightTime = new SimpleDateFormat("HH:mm:ss").parse(flightTimeEst.getText().toString());
                    Calendar arrTime = Calendar.getInstance(Locale.US);
                    Calendar fTime = Calendar.getInstance(Locale.US);
                    arrTime.setTime(cDate);
                    fTime.setTime(flightTime);

                    departInfo.setDepartureTime(new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(cDate));
                    departTime.setText(departInfo.getDepartureTime());

                    arrTime.add(Calendar.SECOND, Integer.parseInt(flightTimeEst.getText().toString().split(":")[2]));
                    arrTime.add(Calendar.MINUTE, Integer.parseInt(flightTimeEst.getText().toString().split(":")[1]));
                    arrTime.add(Calendar.HOUR_OF_DAY, Integer.parseInt(flightTimeEst.getText().toString().split(":")[0]));

                    arriveInfo.setArrivalTime(new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(arrTime.getTime()));
                    arriveTime.setText(arriveInfo.getArrivalTime());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    /**
     * Edits data saved in the database
     */
    private Button.OnClickListener editDepartDataOnClick = new Button.OnClickListener() {

        @Override
        public void onClick(View v) {
            String alertMSG = "You are about to edit an existing flight plan!";
            String alertTitle = "Edit Flight Plan";
            String posBtn = "Proceed", negBtn = "Cancel";
            //Popup message to notify user of intended action
            //Cancel new data save
            if(!cm.showAlertDialog(getActivity(), alertTitle, alertMSG, posBtn, negBtn)){
                cm.messageToaster(getActivity(), "Your changes were not saved");
            }
            //Perform new data save
            else {
                //Perform edit data on database
                if (!departArp.getText().equals(String.valueOf(departInfo.getLocationID())))
                    dbMan.updateDeparture(String.valueOf(departInfo.getDepartureID()), String.valueOf(departLocInfo.getLocationID()), "Location_ID");
                if (!arriveArp.getText().equals(String.valueOf(arriveInfo.getLocationID())))
                    dbMan.updateArrival(String.valueOf(arriveInfo.getArrivalID()), String.valueOf(arriveInfo.getLocationID()), "Location_ID");
                if (!departGate.getText().equals(String.valueOf(departInfo.getGateParkingName())))
                    dbMan.updateDeparture(String.valueOf(departInfo.getDepartureID()), departGate.getText().toString(), "GATE_Parking_Name");//departInfo.setGateParkingName(String.valueOf(departGate.getText()));
                if (!arriveGate.getText().equals(String.valueOf(arriveInfo.getGateParking())))
                    dbMan.updateArrival(String.valueOf(arriveInfo.getArrivalID()), arriveGate.getText().toString(), "GATE_Parking_Name");//arriveInfo.setGateParking(arriveGate.getText().toString());
                if (!cruiseAltitude.getText().equals(String.valueOf(flightPlanInfo.getCruiseAltitude())))
                    flightPlanInfo.setCruiseAltitude(Integer.valueOf(cruiseAltitude.getText().toString()));
                if (!flightDistEst.getText().equals(String.valueOf(flightPlanInfo.getTripDistanceEstimate())))
                    flightPlanInfo.setTripDistanceEstimate(Float.valueOf(flightDistEst.getText().toString()));
                if (!flightTimeEst.getText().equals(String.valueOf(flightPlanInfo.getTripDurationEstimate())))
                    flightPlanInfo.setTripDurationEstimate(flightTimeEst.getText().toString());
                if (!departFuel.getText().equals(String.valueOf(flightPlanInfo.getFuelTaken())))
                    flightPlanInfo.setFuelTaken(Float.valueOf(departFuel.getText().toString()));
                if (!fuelWeight.getText().equals(String.valueOf(flightPlanInfo.getFuelWeight())))
                    flightPlanInfo.setFuelWeight(Float.valueOf(fuelWeight.getText().toString()));
                if (!grossWeight.getText().equals(String.valueOf(flightPlanInfo.getGrossWeight())))
                    flightPlanInfo.setGrossWeight(Float.valueOf(grossWeight.getText().toString()));
                if (!payloadWeight.getText().equals(String.valueOf(flightPlanInfo.getPayloadWeight())))
                    flightPlanInfo.setPayloadWeight(Float.valueOf(payloadWeight.getText().toString()));
                if (!climbSpeed.getText().equals(String.valueOf(flightPlanInfo.getClimbSpeed())))
                    flightPlanInfo.setClimbSpeed(Integer.valueOf(climbSpeed.getText().toString()));
                if (!cruiseSpeed.getText().equals(String.valueOf(flightPlanInfo.getCruiseSpeed())))
                    flightPlanInfo.setCruiseSpeed(Integer.valueOf(cruiseSpeed.getText().toString()));
                if (!departTime.getText().equals(departInfo.getDepartureTime()))
                    dbMan.updateDeparture(String.valueOf(departInfo.getDepartureID()), departTime.getText().toString(), "Departure_Time");
                if (!arriveTime.getText().equals(arriveInfo.getArrivalTime()))
                    dbMan.updateArrival(String.valueOf(arriveInfo.getArrivalID()), arriveTime.getText().toString(), "Arrival_Time");
                //cm.messageToaster(getActivity(), "Not yet implemented");
            }
        }
    };

    /**
     * Saves data as new departure data in the database
     */
    private Button.OnClickListener newDepartDataOnClick = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            String alertMSG = "You are about to save this dataset as a new flight plan!";
            String alertTitle = "New Flight Plan";
            String posBtn = "Proceed", negBtn = "Cancel";
            //Popup message to notify user of intended action
            //Cancel new data save
            if(!cm.showAlertDialog(getActivity(), alertTitle, alertMSG, posBtn, negBtn)){
                cm.messageToaster(getActivity(), "Saving changes was Canceled");
            }
            //Perform new data save
            else{
                fragmentObjectUpdater();
                if (dbMan.addDeparture(departInfo) && dbMan.addArrival(arriveInfo)) {
                    departureFragFileUpdater();
                }
            }
            //cm.messageToaster(getActivity(), "Not yet implemented");
        }
    };

    private Button.OnClickListener saveDataOnClick = new Button.OnClickListener() {
        @Override
        public void onClick(View view) {
            //save data from fragment to database
            fragmentObjectUpdater();
            if(dbMan.addDeparture(departInfo) && dbMan.addArrival(arriveInfo)) {
                departureFragFileUpdater();
            }
        }
    };

    private AdapterView.OnItemClickListener onDepartureLocSelected = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
            departLocInfo = locList.get(lList.indexOf(departArp.getText().toString()));
            departArp.setText(departLocInfo.getAirportID() + " " + departLocInfo.getAirportName());
            //messageToaster(String.valueOf(departLocInfo.getLocationID())+ " " + departLocInfo.getAirportID());
            if(departureRwyList.isEmpty())
                departureRwyList.addAll(dbMan.getRwys(departLocInfo.getLocationID()));
            else{
                departureRwyList.clear();
                depRwyList.clear();
                departureRwyList.addAll(dbMan.getRwys(departLocInfo.getLocationID()));
            }
            for(Runway r : departureRwyList){
                depRwyList.add(r.getName() + "   " + r.getLength() + "ft  " + r.getILS_ID()
                        + "   " + r.getILS_Freq() + "Hz   " + r.getHdg());
            }
            depLoclist = new ArrayAdapter<String>(getContext(),
                    R.layout.support_simple_spinner_dropdown_item, depRwyList);
            departRwy.setAdapter(depLoclist); //Attaches the dropdown list depLoclist to departRwy
            departRwy.setDropDownWidth(900);
        }
    };

    private AdapterView.OnItemClickListener onArrivalLocSelected = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
            arriveLocInfo = locList.get(lList.indexOf(arriveArp.getText().toString()));
            arriveArp.setText(arriveLocInfo.getAirportID() + " " + arriveLocInfo.getAirportName());
            if (arrivalRwyList.isEmpty()) {
                arrivalRwyList.addAll(dbMan.getRwys(arriveLocInfo.getLocationID()));
            } else {
                arrivalRwyList.clear();
                arrivalRwyList.clear();
                arrivalRwyList.addAll(dbMan.getRwys(arriveLocInfo.getLocationID()));
            }
            for(Runway r : arrivalRwyList){
                arrRwyList.add(r.getName() + "   " + r.getLength() + "ft  " + r.getILS_ID()
                        + "   " + r.getILS_Freq() + "Hz   " + r.getHdg());
            }
            arrLoclist = new ArrayAdapter<String>(getContext(),
                    R.layout.support_simple_spinner_dropdown_item, arrRwyList);
            ArrivalFragment.arriveRwy.setAdapter(arrLoclist); //Attaches the dropdown list arrLoclist to arriveRwy
            ArrivalFragment.arriveRwy.setDropDownWidth(900);
        }
    };

    ///////////////////////////////   Helper Methods   /////////////////////////////////

    /**
     * Replaces all the values in the textboxes of the fragment with an empty string
     */
    public static void clearFragValuesDisplayed(){
        departArp.setText("");
        arriveArp.setText("");
        departGate.setText("");
        arriveGate.setText("");
        cruiseAltitude.setText("");
        flightDistEst.setText("");
        flightTimeEst.setText("");
        departFuel.setText("");
        fuelWeight.setText("");
        grossWeight.setText("");
        payloadWeight.setText("");
        departRwy.setText("");
        climbSpeed.setText("");
        cruiseSpeed.setText("");
        departTime.setText("");
        arriveTime.setText("");

        if(saveBtn.getVisibility() == View.INVISIBLE || saveBtn.getVisibility() == View.GONE) {
            saveBtn.setVisibility(View.VISIBLE);
            editBtn.setVisibility(View.GONE);
            newBtn.setVisibility(View.GONE);
        }

        departRwy.clearListSelection();
        ArrivalFragment.arriveRwy.clearListSelection();
    }

    /**
     * Updates the departInfo, arrivalInfo and flightplaneInfo with the data from the textboxes
     */
    private void fragmentObjectUpdater(){
        departInfo.setLocationID(departLocInfo.getLocationID());
        arriveInfo.setLocationID(arriveLocInfo.getLocationID());
        departInfo.setGateParkingName(departGate.getText().toString());
        arriveInfo.setGateParking(arriveGate.getText().toString());
        flightPlanInfo.setCruiseAltitude(Integer.parseInt(cruiseAltitude.getText().toString()));
        flightPlanInfo.setTripDistanceEstimate(Float.parseFloat(flightDistEst.getText().toString()));
        flightPlanInfo.setTripDurationEstimate(flightTimeEst.getText().toString());
        flightPlanInfo.setFuelTaken(Float.parseFloat(departFuel.getText().toString()));
        flightPlanInfo.setFuelWeight(Float.parseFloat(fuelWeight.getText().toString()));
        flightPlanInfo.setGrossWeight(Float.parseFloat(grossWeight.getText().toString()));
        flightPlanInfo.setPayloadWeight(Float.parseFloat(payloadWeight.getText().toString()));
        flightPlanInfo.setClimbSpeed(Integer.parseInt(climbSpeed.getText().toString()));
        flightPlanInfo.setCruiseSpeed(Integer.parseInt(cruiseSpeed.getText().toString()));
    }

    /**
     * Updates all the files that need to be updated for this Fragment:
     * Aircraft, Departure, Location, FlightPlan, and Runway
     */
    private void departureFragFileUpdater(){
        flightPlanInfo.setArrivalID(arriveInfo.getArrivalID());
        flightPlanInfo.setDepartureID(departInfo.getDepartureID());
        flightPlanInfo.setfPStatus("In Progress");

        ///Save data to file
        List<String> data = new ArrayList<>();
        data.add("AIRCRAFT_ID;AIRLINE_NAME;AIRCRAFT_TYPE;MANUFACTURER;TAIL_NUMBER;" +
                "CALL_SIGN;FLIGHT_NUMBER");
        for(Aircraft airC : dbMan.getAirCrafts()){
            data.add(airC.listAircrafts());
        }
        cm.saveToExternal("aircrafts.txt", data);

        data.clear();
        data.add("DEPARTURE_ID;LOCATION_ID;GATE_PARKING;DEPARTURE_TIME");
        for(Departure dep : dbMan.getDepartures()){
            data.add(dep.listDeparture());
        }
        cm.saveToExternal("departure.txt", data);

        data.clear();
        data.add("LOCATION_ID;AIRPORT;AIRPORT_NAME;CITY;STATE_NAME;COUNTRY");
        for(Location loc : dbMan.getLocations()){
            data.add(loc.listLocation());
        }
        cm.saveToExternal("location.txt", data);

        data.clear();
        data.add("RUNWAY_ID;RUNWAY_NAME;RUNWAY_LENGTH;RUNWAY_SURFACE;RUNWAY_HEADING;" +
                "RUNWAY_ILS_ID;ILS_FREQUENCY;LOCATION");
        for(Runway rwy : dbMan.getRwys()){
            data.add(rwy.listRunway());
        }
        cm.saveToExternal("runway.txt", data);

        data.clear();
        data.add("FLIGHT_PLAN_ID;ESTIMATE_TRIP_TIME;DEPARTURE_FUEL;AIRCRAFT;DEPARTURE;" +
                "ARRIVAL;ACTUAL_NUMBERS;ESTIMATE_TRIP_DISTANCE;CLIMB_SPEED;CRUISE_SPEED;" +
                "CRUISE_ALTITUDE;PAYLOAD_WEIGHT;FUEL_WEIGHT;GROSS_WEIGHT;FLIGHT_PLAN_STATUS");
        for(FlightPlan fp : dbMan.getFlightPlans()){
            data.add(fp.listFlightPlan());
        }
        data.add(flightPlanInfo.listFlightPlan());
        cm.saveToExternal("flightplan.txt", data);

        if(saveBtn.getVisibility() == View.VISIBLE) {
            saveBtn.setVisibility(View.GONE);
            editBtn.setVisibility(View.VISIBLE);
            newBtn.setVisibility(View.VISIBLE);
        }
        cm.messageToaster(getActivity(),"Flight plan is now in progress");
    }

    /**
     * Replaces all the fragment's visible textbox values with those stored in the storage file
     */
    public static void restoreFragValues(){
        //clearFragValuesDisplayed();
        if(cm.getInternalFileData(activityDataFileName) != null) {
            departFragData.clear();
            departFragData.addAll(cm.getInternalFileData(activityDataFileName));
            departArp.setText(departFragData.get(0));
            arriveArp.setText(departFragData.get(1));
            departGate.setText(departFragData.get(2));
            arriveGate.setText(departFragData.get(3));
            cruiseAltitude.setText(departFragData.get(4));
            flightDistEst.setText(departFragData.get(5));
            flightTimeEst.setText(departFragData.get(6));
            departFuel.setText(departFragData.get(7));
            fuelWeight.setText(departFragData.get(8));
            grossWeight.setText(departFragData.get(9));
            payloadWeight.setText(departFragData.get(10));
            departRwy.setText(departFragData.get(11));
            climbSpeed.setText(departFragData.get(12));
            cruiseSpeed.setText(departFragData.get(13));
            departTime.setText(departFragData.get(14));
            arriveTime.setText(departFragData.get(15));
            saveBtn.setVisibility(Integer.parseInt(departFragData.get(16)));
            if(departFragData.size() > 17) {
                editBtn.setVisibility(Integer.parseInt(departFragData.get(17)));
                newBtn.setVisibility(Integer.parseInt(departFragData.get(18)));
            }
            boolean oneEmpty = false;

            for(String datum : departFragData){
                if(!datum.isEmpty())
                    oneEmpty = false;
                else {
                    oneEmpty = true;
                    break;
                }
            }

            //Does not update the instance if even one of its attributes is empty
            if(!oneEmpty)/*(!cruiseAltitude.getText().toString().isEmpty() || !flightDistEst.getText().toString().isEmpty() ||
                    !flightTimeEst.getText().toString().isEmpty() || !departFuel.getText().toString().isEmpty())
                if(!fuelWeight.getText().toString().isEmpty() || !grossWeight.getText().toString().isEmpty() ||
                    !payloadWeight.getText().toString().isEmpty() || !climbSpeed.getText().toString().isEmpty())
                    if(!cruiseSpeed.getText().toString().isEmpty() )*/ {
                        flightPlanInfo.setCruiseAltitude(Integer.parseInt(cruiseAltitude.getText().toString()));
                        flightPlanInfo.setTripDistanceEstimate(Float.parseFloat(flightDistEst.getText().toString()));
                        flightPlanInfo.setTripDurationEstimate(flightTimeEst.getText().toString());
                        flightPlanInfo.setFuelTaken(Float.parseFloat(departFuel.getText().toString()));
                        flightPlanInfo.setFuelWeight(Float.parseFloat(fuelWeight.getText().toString()));
                        flightPlanInfo.setGrossWeight(Float.parseFloat(grossWeight.getText().toString()));
                        flightPlanInfo.setPayloadWeight(Float.parseFloat(payloadWeight.getText().toString()));
                        flightPlanInfo.setClimbSpeed(Integer.parseInt(climbSpeed.getText().toString()));
                        flightPlanInfo.setCruiseSpeed(Integer.parseInt(cruiseSpeed.getText().toString()));

                        departInfo.setLocationID(departLocInfo.getLocationID());
                        departInfo.setDepartureTime(departFragData.get(14));
                        departInfo.setGateParkingName(departFragData.get(2));
                        arriveInfo.setLocationID(arriveLocInfo.getLocationID());
                        arriveInfo.setArrivalTime(departFragData.get(15));
                        arriveInfo.setGateParking(departFragData.get(3));
//                        departInfo.setGateParkingName(departGate.getText().toString());
//                        arriveInfo.setGateParking(arriveGate.getText().toString());
                        //saveBtn.setVisibility(View.GONE);
                    }
            departFragData.clear();
        }
    }

    public static FlightPlan getFlightPlanInfo(){
        return flightPlanInfo;
    }

    /**
     * Populates the choice list for the dropdown list of an autocomplete text editor
     */
    private void dropdownList(){
        ArrayAdapter<String> arplist = new ArrayAdapter<String>(getContext(),
                R.layout.support_simple_spinner_dropdown_item, lList);

        departArp.setAdapter(arplist); //Attaches the dropdown list Ulist to TopUnit
        //departArp.setMinimumWidth(500);
        arriveArp.setAdapter(arplist); //Attaches the dropdown list Ulist to DownUnit
        //arriveArp.setMinimumWidth(500);

        departArp.setDropDownWidth(900);
        arriveArp.setDropDownWidth(900);
    }
}
