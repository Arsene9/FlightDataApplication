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

import BusinessObjectLayer.ActualData;
import BusinessObjectLayer.Arrival;
import BusinessObjectLayer.FlightPlan;
import DatabaseLayer.DatabaseManager;

public class ArrivalFragment extends Fragment {

    public static AutoCompleteTextView arriveRwy, actDepartTime, actArriveTime, actTripTime,
            fuelLeft, fuelused, totalDistance, arrivalGateParking;
    private static Button saveButton, editButton, newButton;

    private DatabaseManager dbMan;
    private static ActualData actualInfo;
    private Calendar calendar;
    private TimePickerDialog timePicker;
    //private static final String FILE_NAME = "example.txt";
    //private String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/TestData";
    private static CommonMethod cm;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbMan = DatabaseManager.getInstance(getContext());
        actualInfo = new ActualData();
        cm = new CommonMethod();
    }

    private static List<String> arrivalFragData = new ArrayList<>();
    private static String activityDataFileName = "ArrivalFragmentData.txt";

    @Override
    public void onPause() {
        super.onPause();
        arrivalFragData.clear();
        arrivalFragData.add(arriveRwy.getText().toString());
        arrivalFragData.add(actDepartTime.getText().toString());
        arrivalFragData.add(actArriveTime.getText().toString());
        arrivalFragData.add(actTripTime.getText().toString());
        arrivalFragData.add(fuelLeft.getText().toString());
        arrivalFragData.add(fuelused.getText().toString());
        arrivalFragData.add(totalDistance.getText().toString());
        arrivalFragData.add(arrivalGateParking.getText().toString());
        arrivalFragData.add(String.valueOf(saveButton.getVisibility()));
        arrivalFragData.add(String.valueOf(editButton.getVisibility()));
        arrivalFragData.add(String.valueOf(newButton.getVisibility()));
        cm.saveToInternalFile(arrivalFragData, activityDataFileName);
    }

    @Override
    public void onResume() {
        super.onResume();
        restoreFragValues();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.arrival_fragment, container,false);

        arriveRwy = view.findViewById(R.id.arrivalRwyACTV);
        actDepartTime = view.findViewById(R.id.actualDepartureTimeACTV2);
        actArriveTime = view.findViewById(R.id.actualArrivalTimeACTV);
        actTripTime = view.findViewById(R.id.totalTripTimeACTV2);
        fuelLeft = view.findViewById(R.id.fuelLeftACTV);
        fuelused = view.findViewById(R.id.fuelUsedACTV);
        totalDistance = view.findViewById(R.id.totalTripDistanceACTV);
        arrivalGateParking = view.findViewById(R.id.arrivalGateACTV3);
        saveButton = view.findViewById(R.id.saveDataButton);
        editButton = view.findViewById(R.id.editArrivalDataButton);
        newButton = view.findViewById(R.id.newArrivalDataButton);

        editButton.setVisibility(View.GONE);
        newButton.setVisibility(View.GONE);

        saveButton.setOnClickListener(onSaveDataRequest);
        editButton.setOnClickListener(editArriveDataOnClick);
        newButton.setOnClickListener(newArriveDataOnClick);
        actArriveTime.setOnClickListener(addArrivalEstimatedTime);
        actDepartTime.setOnClickListener(onDepartTimeEditTextClick);
        actTripTime.setOnClickListener(onTripDurationEditTextClick);
        fuelused.setOnClickListener(addFuelUsed);
        arrivalGateParking.setOnClickListener(onGateClick);

//        File dir = new File(path);
//        dir.mkdir();


        //copyDatabase ("FlightPlan");

        return view;
    }

    /////////////////////////////////////  Event Listeners  ////////////////////////////////////////
    private EditText.OnClickListener onGateClick = new EditText.OnClickListener() {
        @Override
        public void onClick(View view) {
            final double nmile = 0.869;
            double miles = Double.parseDouble(totalDistance.getText().toString());
            miles *= nmile;
            totalDistance.setText(String.valueOf(miles));
        }
    };
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
            actTripTime.setText(dHour + ":" + dMin + ":00");
        }
    };
    private TimePickerDialog.OnTimeSetListener onTimeSetListenerDepartTime = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker timePicker, int dHour, int dMin) {
            actDepartTime.setText(dHour + ":" + dMin + ":00");
        }
    };

    private AdapterView.OnClickListener addFuelUsed = new AdapterView.OnClickListener() {

        @Override
        public void onClick(View view) {
            if (!fuelLeft.getText().toString().isEmpty()) {
                float fOnDep = DepartureFragment.getFlightPlanInfo().getFuelTaken();
                float fLeft = Float.parseFloat(fuelLeft.getText().toString());
                float fuelU = fOnDep - fLeft;
                fuelused.setText( String.valueOf(fuelU));
            }
        }
    };

    private AdapterView.OnClickListener addArrivalEstimatedTime = new AdapterView.OnClickListener(){

        @Override
        public void onClick(View view) {
            //cm.messageToaster(getContext(), actDepartTime.getText().toString()+ " " + actTripTime.getText().toString());
            if(!actDepartTime.getText().toString().isEmpty() && !actTripTime.getText().toString().isEmpty()) {
                if(actDepartTime.getText().toString().contains("/")){
                    String dTime = actDepartTime.getText().toString().replaceFirst("(0[1-9]|1[012])/(0[1-9]|[123][0-9])/([1-9][0-9][0-9][0-9]) ", "");
                    actDepartTime.setText(dTime);
                    //cm.messageToaster(getContext(),dTime);
                }

                String currentDate = (Calendar.getInstance(Locale.US).get(Calendar.MONTH)+1) + "/" +
                        Calendar.getInstance(Locale.US).get(Calendar.DAY_OF_MONTH) + "/" +
                        Calendar.getInstance(Locale.US).get(Calendar.YEAR);

                currentDate += " " + actDepartTime.getText().toString();

                try {
                    Date cDate = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").parse(currentDate);
                    Date flightTime = new SimpleDateFormat("HH:mm:ss").parse(actTripTime.getText().toString());
                    Calendar arrTime = Calendar.getInstance(Locale.US);
                    Calendar fTime = Calendar.getInstance(Locale.US);
                    arrTime.setTime(cDate);
                    fTime.setTime(flightTime);

                    actualInfo.setDepartureTime(new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").
                            format(cDate));
                    actDepartTime.setText(actualInfo.getDepartureTime());

                    arrTime.add(Calendar.SECOND, Integer.parseInt(actTripTime.getText().toString().split(":")[2]));
                    arrTime.add(Calendar.MINUTE, Integer.parseInt(actTripTime.getText().toString().split(":")[1]));
                    arrTime.add(Calendar.HOUR_OF_DAY, Integer.parseInt(actTripTime.getText().toString().split(":")[0]));

                    actualInfo.setArrivalTime(new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").
                            format(arrTime.getTime()));
                    actArriveTime.setText(actualInfo.getArrivalTime());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    /**
     * Edits data saved in the database
     */
    private Button.OnClickListener editArriveDataOnClick = new Button.OnClickListener() {

        @Override
        public void onClick(View v) {
            String alertMSG = "You are about to edit an existing flight plan!";
            String alertTitle = "Edit Flight Plan";
            String posBtn = "Proceed", negBtn = "Cancel";
            //Popup message to notify user of intended action
            //Cancel new data save
            if (!cm.showAlertDialog(getActivity(), alertTitle, alertMSG, posBtn, negBtn)) {
                cm.messageToaster(getActivity(), "Your changes were not saved");
            }
            //Perform new data save
            else {
                //Perform edit data on database
                if (!actDepartTime.getText().equals(actualInfo.getDepartureTime()))
                    dbMan.updateActual(String.valueOf(actualInfo.getActual_ID()), actDepartTime.getText().toString(), "Departure_Time");
                if (!actArriveTime.getText().equals(actualInfo.getArrivalTime()))
                    dbMan.updateActual(String.valueOf(actualInfo.getActual_ID()), actArriveTime.getText().toString(), "Arrival_Time");
                if (!actTripTime.getText().equals(actualInfo.getTotalTripDuration()))
                    dbMan.updateActual(String.valueOf(actualInfo.getActual_ID()), actTripTime.getText().toString(), "Total_Trip_Duration");
                if (!fuelLeft.getText().equals(String.valueOf(actualInfo.getFuelBalance())))
                    dbMan.updateActual(String.valueOf(actualInfo.getActual_ID()), fuelLeft.getText().toString(), "Fuel_Balance");
                if (!fuelused.getText().equals(String.valueOf(actualInfo.getFuelUsed())))
                    dbMan.updateActual(String.valueOf(actualInfo.getActual_ID()), fuelused.getText().toString(), "Fuel_Used");
                if (!totalDistance.getText().equals(String.valueOf(actualInfo.getTotalTripDistance())))
                    dbMan.updateActual(String.valueOf(actualInfo.getActual_ID()), totalDistance.getText().toString(), "Total_Trip_Distance");
                if (!arrivalGateParking.getText().equals(actualInfo.getGateParkingName()))
                    dbMan.updateActual(String.valueOf(actualInfo.getActual_ID()), totalDistance.getText().toString(), "Gate_Parking_Name");
            }
        }
    };

    /**
     * Saves data as new departure data in the database
     */
    private Button.OnClickListener newArriveDataOnClick = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            String alertMSG = "You are about to save this dataset as part of a new flight plan!";
            String alertTitle = "New Flight Plan";
            String posBtn = "Proceed", negBtn = "Cancel";
            //Popup message to notify user of intended action
            //Cancel new data save
            if(!cm.showAlertDialog(getActivity(), alertTitle, alertMSG, posBtn, negBtn)){
                cm.messageToaster(getActivity(), "Saving changes was Canceled");
            }
            //Perform new data save
            else {
                saveToCompleteFlightPlan();
            }
        }
    };

    private Button.OnClickListener onSaveDataRequest = new Button.OnClickListener(){

        @Override
        public void onClick(View view) {
            saveToCompleteFlightPlan();
        }
    };
    ////////////////////////////////////  Helper Methods   /////////////////////////////////////////

    /**
     * Puts the data from the textboxes into the data object for Actual Flight Data
     */
    private void updataeActualFDObject(){
        actualInfo.setArrivalTime(actArriveTime.getText().toString());
        actualInfo.setTotalTripDuration(actTripTime.getText().toString());
        actualInfo.setFuelBalance(Float.parseFloat(fuelLeft.getText().toString()));
        actualInfo.setFuelUsed(Float.parseFloat(fuelused.getText().toString()));
        actualInfo.setTotalTripDistance(Float.parseFloat(totalDistance.getText().toString()));
        actualInfo.setGateParkingName(arrivalGateParking.getText().toString());

    }

    /**
     * Saves the data into the database and external files as required
     */
    private void saveFinalDataSet(){
        List<String> data = new ArrayList<>();
        data.add("FLIGHT_PLAN_ID;ESTIMATE_TRIP_TIME;DEPARTURE_FUEL;AIRCRAFT;DEPARTURE" +
                ";ARRIVAL;ACTUAL_NUMBERS;ESTIMATE_TRIP_DISTANCE;CLIMB_SPEED;CRUISE_SPEED;" +
                "CRUISE_ALTITUDE;PAYLOAD_WEIGHT;FUEL_WEIGHT;GROSS_WEIGHT;FLIGHT_PLAN_STATUS");
        for(FlightPlan fp : dbMan.getFlightPlans()){
            data.add(fp.listFlightPlan());
        }
        cm.saveToExternal("flightplan.txt", data);

        data.clear();
        data.add("ACTUAL_ID;DEPARTURE_TIME;ARRIVAL_TIME;FUEL_BALANCE;FUEL_USED;" +
                "TOTAL_TRIP_TIME;TOTAL_TRIP_DISTANCE;ARRIVAL_GATE_PARKING ");
        for(ActualData ad : dbMan.getActuals()){
            data.add(ad.listActualData());
        }
        cm.saveToExternal("actualdata.txt", data);

        data.clear();
        data.add("ARRIVAL_ID;LOCATION_ID;GATE_PARKING;ARRIVAL_TIME");
        for(Arrival arr : dbMan.getArrivals()){
            data.add(arr.listArrival());
        }
        cm.saveToExternal("arrival.txt", data);

        saveButton.setVisibility(View.GONE);
        editButton.setVisibility(View.VISIBLE);
        newButton.setVisibility(View.VISIBLE);
        cm.messageToaster(getContext(),"Flight plan is now saved and completed");
    }

    /**
     * Saves all arrival and actual data
     */
    private void saveToCompleteFlightPlan(){
        updataeActualFDObject();
        if(dbMan.addActualData(actualInfo)) {
            DepartureFragment.getFlightPlanInfo().setActualID(actualInfo.getActual_ID());
            DepartureFragment.getFlightPlanInfo().setfPStatus("Completed");
            if(dbMan.addFlightPlan(DepartureFragment.getFlightPlanInfo())) {
                saveFinalDataSet();
            }
        }
    }

    /**
     * Replaces all the values in the textboxes of the fragment with an empty string
     */
    public static void clearFragValuesDisplayed(){
        arriveRwy.setText("");
        actDepartTime.setText("");
        actArriveTime.setText("");
        actTripTime.setText("");
        fuelLeft.setText("");
        fuelused.setText("");
        totalDistance.setText("");
        arrivalGateParking.setText("");

        if(saveButton.getVisibility() == View.INVISIBLE || saveButton.getVisibility() == View.GONE ) {
            saveButton.setVisibility(View.VISIBLE);
            editButton.setVisibility(View.GONE);
            newButton.setVisibility(View.GONE);
        }
    }

    /**
     * Replaces all the fragment's visible textbox values with those stored in the storage file
     */
    public static void restoreFragValues(){
        if(cm.getInternalFileData(activityDataFileName) != null) {
            arrivalFragData.addAll(cm.getInternalFileData(activityDataFileName));
            arriveRwy.setText(arrivalFragData.get(0));
            actDepartTime.setText(arrivalFragData.get(1));
            actArriveTime.setText(arrivalFragData.get(2));
            actTripTime.setText(arrivalFragData.get(3));
            fuelLeft.setText(arrivalFragData.get(4));
            fuelused.setText(arrivalFragData.get(5));
            totalDistance.setText(arrivalFragData.get(6));
            arrivalGateParking.setText(arrivalFragData.get(7));
            saveButton.setVisibility(Integer.parseInt(arrivalFragData.get(8)));
            if(arrivalFragData.size() > 9) {
                editButton.setVisibility(Integer.parseInt(arrivalFragData.get(9)));
                newButton.setVisibility(Integer.parseInt(arrivalFragData.get(10)));
            }

            boolean oneEmpty = false;

            for(String datum : arrivalFragData){
                if(!datum.isEmpty())
                    oneEmpty = false;
                else {
                    oneEmpty = true;
                    break;
                }
            }

            if(!oneEmpty){
                actualInfo.setDepartureTime(arrivalFragData.get(1));
                actualInfo.setArrivalTime(arrivalFragData.get(2));
                actualInfo.setTotalTripDuration(arrivalFragData.get(3));
                actualInfo.setFuelBalance(Float.parseFloat(arrivalFragData.get(4)));
                actualInfo.setFuelUsed(Float.parseFloat(arrivalFragData.get(5)));
                actualInfo.setTotalTripDistance(Float.parseFloat(arrivalFragData.get(6)));
                actualInfo.setGateParkingName(arrivalFragData.get(7));
            }


            arrivalFragData.clear();
        }
    }
}
