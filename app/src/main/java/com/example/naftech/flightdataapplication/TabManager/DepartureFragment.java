package com.example.naftech.flightdataapplication.TabManager;

import android.app.TimePickerDialog;
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

    private AutoCompleteTextView departArp, arriveArp, departGate, arriveGate, cruiseAltitude,
            flightDistEst, flightTimeEst, departFuel, fuelWeight, grossWeight, payloadWeight,
            departRwy, climbSpeed, cruiseSpeed;
    private EditText departTime, arriveTime;
    private Button saveBtn;

    private DatabaseManager dbMan;
    private Location departLocInfo, arriveLocInfo;
    private static FlightPlan flightPlanInfo;
    private Arrival arriveInfo;
    private Departure departInfo;
    private Runway deprwyInfo, arrrwyInfo;
    private List<Location> locList;
    private List<Runway> arrivalRwyList, departureRwyList;
    private List<String> lList, depRwyList, arrRwyList;
    private Calendar calendar;
    private TimePickerDialog timePicker;
    private CommonMethod cm;

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

        saveBtn.setOnClickListener(saveDataOnClick);
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

    ////////////////////////////////////  Event Listeners definition  //////////////////////////////
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

    private Button.OnClickListener saveDataOnClick = new Button.OnClickListener() {
        @Override
        public void onClick(View view) {
            //save data from fragment to database
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
//            departLocInfo;
//            arriveLocInfo ;
//            deprwyInfo ;
//            arrrwyInfo ;
            if(dbMan.addDeparture(departInfo) && dbMan.addArrival(arriveInfo)) {
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
                cm.save(getContext(),"aircrafts.txt", data);

                data.clear();
                data.add("DEPARTURE_ID;LOCATION_ID;GATE_PARKING;DEPARTURE_TIME");
                for(Departure dep : dbMan.getDepartures()){
                    data.add(dep.listDeparture());
                }
                cm.save(getContext(),"departure.txt", data);

                data.clear();
                data.add("LOCATION_ID;AIRPORT;AIRPORT_NAME;CITY;STATE_NAME;COUNTRY");
                for(Location loc : dbMan.getLocations()){
                    data.add(loc.listLocation());
                }
                cm.save(getContext(),"location.txt", data);

                data.clear();
                data.add("RUNWAY_ID;RUNWAY_NAME;RUNWAY_LENGTH;RUNWAY_SURFACE;RUNWAY_HEADING;" +
                        "RUNWAY_ILS_ID;ILS_FREQUENCY;LOCATION");
                for(Runway rwy : dbMan.getRwys()){
                    data.add(rwy.listRunway());
                }
                cm.save(getContext(),"runway.txt", data);

                data.clear();
                data.add("FLIGHT_PLAN_ID;ESTIMATE_TRIP_TIME;DEPARTURE_FUEL;AIRCRAFT;DEPARTURE" +
                        "ARRIVAL;ACTUAL_NUMBERS;ESTIMATE_TRIP_DISTANCE;CLIMB_SPEED;CRUISE_SPPED;" +
                        "CRUISE_ALTITUDE;PAYLOAD_WEIGHT;FUEL_WEIGHT;GROSS_WEIGHT;FLIGHT_PLAN_STATUS");
                for(FlightPlan fp : dbMan.getFlightPlans()){
                    data.add(fp.listFlightPlan());
                }
                data.add(flightPlanInfo.listFlightPlan());
                cm.save(getContext(),"flightplan.txt", data);

                saveBtn.setVisibility(View.GONE);
                cm.messageToaster(getActivity(),"Flight plan is now in progress");
            }
        }
    };

    private AdapterView.OnItemClickListener onDepartureLocSelected = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
            departLocInfo = locList.get(lList.indexOf(departArp.getText().toString()));

            departArp.setText(departLocInfo.getAirportID() + " " + departLocInfo.getAirportName());
            //messageToaster(String.valueOf(departLocInfo.getLocationID())+ " " + departLocInfo.getAirportID());
            departureRwyList.addAll(dbMan.getRwys(departLocInfo.getLocationID()));
            for(Runway r : departureRwyList){
                depRwyList.add(r.getName() + "   " + r.getLength() + "ft  " + r.getILS_ID()
                        + "   " + r.getILS_Freq() + "Hz   " + r.getHdg());
            }
            ArrayAdapter<String> Ulist = new ArrayAdapter<String>(getContext(),
                    R.layout.support_simple_spinner_dropdown_item, depRwyList);

            departRwy.setAdapter(Ulist); //Attaches the dropdown list Ulist to TopUnit
            //departArp.setMinimumWidth(500);

            departRwy.setDropDownWidth(900);
        }
    };

    private AdapterView.OnItemClickListener onArrivalLocSelected = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
            arriveLocInfo = locList.get(lList.indexOf(arriveArp.getText().toString()));

            arriveArp.setText(arriveLocInfo.getAirportID() + " " + arriveLocInfo.getAirportName());
            arrivalRwyList.addAll(dbMan.getRwys(arriveLocInfo.getLocationID()));
            for(Runway r : arrivalRwyList){
                arrRwyList.add(r.getName() + "   " + r.getLength() + "ft  " + r.getILS_ID()
                        + "   " + r.getILS_Freq() + "Hz   " + r.getHdg());
            }
            ArrayAdapter<String> Ulist = new ArrayAdapter<String>(getContext(),
                    R.layout.support_simple_spinner_dropdown_item, arrRwyList);

            ArrivalFragment.arriveRwy.setAdapter(Ulist); //Attaches the dropdown list Ulist to DownUnit
            //arriveArp.setMinimumWidth(500);

            ArrivalFragment.arriveRwy.setDropDownWidth(900);
        }
    };

    ///////////////////////////////   Helper Methods   /////////////////////////////////
    public static FlightPlan getFlightPlanInfo(){
        return flightPlanInfo;
    }

    /**
     * Populates the choice list for the dropdown list of an autocomplete text editor
     */
    private void dropdownList(){
        ArrayAdapter<String> Ulist = new ArrayAdapter<String>(getContext(),
                R.layout.support_simple_spinner_dropdown_item, lList);

        departArp.setAdapter(Ulist); //Attaches the dropdown list Ulist to TopUnit
        //departArp.setMinimumWidth(500);
        arriveArp.setAdapter(Ulist); //Attaches the dropdown list Ulist to DownUnit
        //arriveArp.setMinimumWidth(500);

        departArp.setDropDownWidth(900);
        arriveArp.setDropDownWidth(900);
    }
}
