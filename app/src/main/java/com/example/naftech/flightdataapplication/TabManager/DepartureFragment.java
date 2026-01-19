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
    static Arrival arriveInfo;
    static Departure departInfo;
    private List<Location> locList;
    private List<Runway> arrivalRwyList, departureRwyList;
    private static List<String> lList, depRwyList, arrRwyList;
    private static ArrayAdapter<String> depLocList, arrLocList;
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
        locList = new ArrayList<>();
        arrivalRwyList = new ArrayList<>();
        departureRwyList = new ArrayList<>();
        arrRwyList = new ArrayList<>();
        depRwyList = new ArrayList<>();
        lList = new ArrayList<>();

        locList.addAll(dbMan.getLocations());
        for(Location l : locList){
            lList.add(l.getAirportID() + " "+ l.getAirportName() + " " + l.getCity() + " " + l.getCountry());
        }

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
        //Restore the dropdown lists
        for(String loc : lList){
            if(!departArp.getText().toString().isEmpty() && loc.startsWith(departArp.getText().toString())) {
                ///Regenerate the Departure runways' List
                departLocInfo = locList.get(lList.indexOf(loc));
                departArp.setText(departLocInfo.getAirportID() + " " + departLocInfo.getAirportName());
                    departureRwyList.clear();
                    depRwyList.clear();
                    departureRwyList.addAll(dbMan.getRwys(departLocInfo.getLocationID()));
                for(Runway r : departureRwyList){
                    depRwyList.add(r.getName() + "   " + r.getLength() + "ft  " + r.getILS_ID()
                            + "   " + r.getILS_Freq() + "Hz   " + r.getHdg());
                }
                depLocList = new ArrayAdapter<String>(getContext(),
                        R.layout.support_simple_spinner_dropdown_item, depRwyList);
                departRwy.setAdapter(depLocList); //Attaches the dropdown list depLocList to departRwy
                departRwy.setDropDownWidth(900);
                count++;
            }
            else if(!arriveArp.getText().toString().isEmpty() && loc.startsWith(arriveArp.getText().toString())){
                ///Regenerate the Arrival runways' list
                arriveLocInfo = locList.get(lList.indexOf(loc));
                arriveArp.setText(arriveLocInfo.getAirportID() + " " + arriveLocInfo.getAirportName());
                    arrivalRwyList.clear();
                    arrRwyList.clear();
                    arrivalRwyList.addAll(dbMan.getRwys(arriveLocInfo.getLocationID()));
                for(Runway r : arrivalRwyList){
                    arrRwyList.add(r.getName() + "   " + r.getLength() + "ft  " + r.getILS_ID()
                            + "   " + r.getILS_Freq() + "Hz   " + r.getHdg());
                }
                arrLocList = new ArrayAdapter<String>(getContext(),
                        R.layout.support_simple_spinner_dropdown_item, arrRwyList);
                ArrivalFragment.arriveRwy.setAdapter(arrLocList); //Attaches the dropdown list arrLoclist to arriveRwy
                ArrivalFragment.arriveRwy.setDropDownWidth(900);
                count++;
            }
            else if(count == 2) break;
        }
//        flightPlanInfo.restoreEntityData();
//        arriveInfo.restoreEntityData();
//        departInfo.restoreEntityData();
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
     * Edits data saved to object
     */
    private Button.OnClickListener editDepartDataOnClick = new Button.OnClickListener() {

        @Override
        public void onClick(View v) {
            String alertMSG = "You are about to edit an existing flight plan!";
            String alertTitle = "Edit Flight Plan";
            String posBtn = "Proceed", negBtn = "Cancel";
            //Popup message to notify user of intended action
            new AlertDialog.Builder(getActivity())
                    //set icon
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    //set title
                    .setTitle(alertTitle)
                    //set message
                    .setMessage(alertMSG)
                    //set positive button
                    .setPositiveButton(posBtn, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //Perform edit data on database
                            if (!departArp.getText().toString().equals(String.valueOf(departInfo.getLocationID()))) {
                                departInfo.setLocationID(departLocInfo.getLocationID());
                            }
                            if (!arriveArp.getText().toString().equals(String.valueOf(arriveInfo.getLocationID()))) {
                                arriveInfo.setLocationID(arriveLocInfo.getLocationID());
                            }
                            if (!departGate.getText().toString().equals(String.valueOf(departInfo.getGateParkingName()))) {
                                departInfo.setGateParkingName(departGate.getText().toString());
                            }
                            if (!arriveGate.getText().toString().equals(String.valueOf(arriveInfo.getGateParking()))) {
                                arriveInfo.setGateParking(arriveGate.getText().toString());
                            }
                            if (!cruiseAltitude.getText().toString().equals(String.valueOf(flightPlanInfo.getCruiseAltitude())))
                                flightPlanInfo.setCruiseAltitude(Integer.parseInt(cruiseAltitude.getText().toString()));

                            if (!flightDistEst.getText().toString().equals(String.valueOf(flightPlanInfo.getTripDistanceEstimate())))
                                flightPlanInfo.setTripDistanceEstimate(Float.parseFloat(flightDistEst.getText().toString()));

                            if (!flightTimeEst.getText().toString().equals(String.valueOf(flightPlanInfo.getTripDurationEstimate())))
                                flightPlanInfo.setTripDurationEstimate(flightTimeEst.getText().toString());

                            if (!departFuel.getText().toString().equals(String.valueOf(flightPlanInfo.getFuelTaken())))
                                flightPlanInfo.setFuelTaken(Float.parseFloat(departFuel.getText().toString()));

                            if (!fuelWeight.getText().toString().equals(String.valueOf(flightPlanInfo.getFuelWeight())))
                                flightPlanInfo.setFuelWeight(Float.parseFloat(fuelWeight.getText().toString()));

                            if (!grossWeight.getText().toString().equals(String.valueOf(flightPlanInfo.getGrossWeight())))
                                flightPlanInfo.setGrossWeight(Float.parseFloat(grossWeight.getText().toString()));

                            if (!payloadWeight.getText().toString().equals(String.valueOf(flightPlanInfo.getPayloadWeight())))
                                flightPlanInfo.setPayloadWeight(Float.parseFloat(payloadWeight.getText().toString()));

                            if (!climbSpeed.getText().toString().equals(String.valueOf(flightPlanInfo.getClimbSpeed())))
                                flightPlanInfo.setClimbSpeed(Integer.parseInt(climbSpeed.getText().toString()));

                            if (!cruiseSpeed.getText().toString().equals(String.valueOf(flightPlanInfo.getCruiseSpeed())))
                                flightPlanInfo.setCruiseSpeed(Integer.parseInt(cruiseSpeed.getText().toString()));

                            if (!departTime.getText().toString().equals(departInfo.getDepartureTime())) {
                                departInfo.setDepartureTime(departTime.getText().toString());
                            }
                            if (!arriveTime.getText().toString().equals(arriveInfo.getArrivalTime())) {
                                arriveInfo.setArrivalTime(arriveTime.getText().toString());
                            }
                            departureFragFileUpdater();
                            cm.messageToaster(getActivity(), "Changes have been applied as needed to current flight plan");
                        }
                    })
                    //set negative button
                    .setNegativeButton(negBtn, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            cm.messageToaster(getActivity(), "Your changes were not saved");
                        }
                    })
                    .show();
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
            new AlertDialog.Builder(getActivity())
                    //set icon
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    //set title
                    .setTitle(alertTitle)
                    //set message
                    .setMessage(alertMSG)
                    //set positive button
                    .setPositiveButton(posBtn, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //Perform new data save
                            fragmentObjectUpdater();
                            //if (dbMan.addDeparture(departInfo) && dbMan.addArrival(arriveInfo)) {
                                departureFragFileUpdater();
                            //}
                        }
                    })
                    //set negative button
                    .setNegativeButton(negBtn, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            cm.messageToaster(getActivity(), "Saving changes was Canceled");
                        }
                    })
                    .show();
        }
    };

    private Button.OnClickListener saveDataOnClick = new Button.OnClickListener() {
        @Override
        public void onClick(View view) {
            //save data from fragment to database
            fragmentObjectUpdater();
            departureFragFileUpdater();
        }
    };

    private AdapterView.OnItemClickListener onDepartureLocSelected = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
            departLocInfo = locList.get(lList.indexOf(departArp.getText().toString()));
            departArp.setText(departLocInfo.getAirportID() + " " + departLocInfo.getAirportName());
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
            depLocList = new ArrayAdapter<String>(getContext(),
                    R.layout.support_simple_spinner_dropdown_item, depRwyList);
            departRwy.setAdapter(depLocList); //Attaches the dropdown list depLocList to departRwy
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
            arrLocList = new ArrayAdapter<String>(getContext(),
                    R.layout.support_simple_spinner_dropdown_item, arrRwyList);
            ArrivalFragment.arriveRwy.setAdapter(arrLocList); //Attaches the dropdown list arrLocList to arriveRwy
            ArrivalFragment.arriveRwy.setDropDownWidth(900);
        }
    };

    ///////////////////////////////   Helper Methods   /////////////////////////////////

    /**
     * clears the values of the departure objects
     */
    public static void clearDepartObjects(){
        departInfo.backupEntityData();
        arriveInfo.backupEntityData();
        flightPlanInfo.backupEntityData();

        departInfo.resetDeparture();
        arriveInfo.resetArrival();
        flightPlanInfo.resetFlightPlan();
    }

    /**
     * restores the values of the departure objects
     */
    public static void restoreDepartObjects(){
        departInfo.restoreEntityData();
        arriveInfo.restoreEntityData();
        flightPlanInfo.restoreEntityData();
    }

    /**
     * Replaces all the values in the textBoxes of the fragment with an empty string
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
        data.clear();

        if(saveBtn.getVisibility() == View.VISIBLE) {
            saveBtn.setVisibility(View.GONE);
            editBtn.setVisibility(View.VISIBLE);
            newBtn.setVisibility(View.VISIBLE);
        }
        cm.messageToaster(getActivity(),"Flight plan is now in progress");
    }

    /**
     * Replaces all the fragment's visible textBox values with those stored in the storage file
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
            //saveBtn.setVisibility(Integer.parseInt(departFragData.get(16)));
            if(departFragData.size() > 16) {
                saveBtn.setVisibility(Integer.parseInt(departFragData.get(16)));
                editBtn.setVisibility(Integer.parseInt(departFragData.get(17)));
                newBtn.setVisibility(Integer.parseInt(departFragData.get(18)));
            }
//            boolean oneEmpty = false;
//
//            for(String datum : departFragData){
//                if(datum.isEmpty()){
//                    oneEmpty = true;
//                    break;
//                }
//            }

            //Does not update the instance if even one of its attributes is empty
            //if(!oneEmpty){
                flightPlanInfo.setCruiseAltitude(Integer.parseInt(cruiseAltitude.getText().toString().isEmpty() ? "0" : cruiseAltitude.getText().toString()));
                flightPlanInfo.setTripDistanceEstimate(Float.parseFloat(flightDistEst.getText().toString().isEmpty() ? "0" : flightDistEst.getText().toString()));
                flightPlanInfo.setTripDurationEstimate(flightTimeEst.getText().toString());
                flightPlanInfo.setFuelTaken(Float.parseFloat(departFuel.getText().toString().isEmpty()? "0" : departFuel.getText().toString()));
                flightPlanInfo.setFuelWeight(Float.parseFloat(fuelWeight.getText().toString().isEmpty() ? "0" : fuelWeight.getText().toString()));
                flightPlanInfo.setGrossWeight(Float.parseFloat(grossWeight.getText().toString().isEmpty() ? "0" : grossWeight.getText().toString()));
                flightPlanInfo.setPayloadWeight(Float.parseFloat(payloadWeight.getText().toString().isEmpty() ? "0" : payloadWeight.getText().toString()));
                flightPlanInfo.setClimbSpeed(Integer.parseInt(climbSpeed.getText().toString().isEmpty() ? "0" : climbSpeed.getText().toString()));
                flightPlanInfo.setCruiseSpeed(Integer.parseInt(cruiseSpeed.getText().toString().isEmpty() ? "0" : cruiseSpeed.getText().toString()));

                departInfo.setLocationID(departLocInfo.getLocationID());
                departInfo.setDepartureTime(departFragData.get(14));
                departInfo.setGateParkingName(departFragData.get(2));
                arriveInfo.setLocationID(arriveLocInfo.getLocationID());
                arriveInfo.setArrivalTime(departFragData.get(15));
                arriveInfo.setGateParking(departFragData.get(3));
            //}
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
        ArrayAdapter<String> arpList = new ArrayAdapter<String>(getContext(),
                R.layout.support_simple_spinner_dropdown_item, lList);

        departArp.setAdapter(arpList); //Attaches the dropdown list arpList to departArp
        arriveArp.setAdapter(arpList); //Attaches the dropdown list arpList to arriveArp

        departArp.setDropDownWidth(900);
        arriveArp.setDropDownWidth(900);
    }
}
