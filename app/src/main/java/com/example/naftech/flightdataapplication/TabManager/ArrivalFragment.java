package com.example.naftech.flightdataapplication.TabManager;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.graphics.drawable.DrawerArrowDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.naftech.flightdataapplication.CommonMethod;
import com.example.naftech.flightdataapplication.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import BusinessObjectLayer.ActualData;
import BusinessObjectLayer.Aircraft;
import BusinessObjectLayer.Arrival;
import BusinessObjectLayer.Departure;
import BusinessObjectLayer.FlightPlan;
import DatabaseLayer.DatabaseManager;

import static android.content.Context.MODE_PRIVATE;
import static android.support.constraint.Constraints.TAG;

public class ArrivalFragment extends Fragment {

    public static AutoCompleteTextView arriveRwy, actDepartTime, actArriveTime, actTripTime,
            fuelLeft, fuelused, totalDistance, arrivalGateParking;
    private Button saveButton;

    private DatabaseManager dbMan;
    private ActualData actualInfo;
    private Calendar calendar;
    private TimePickerDialog timePicker;
    private static final String FILE_NAME = "example.txt";
    private String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/TestData";
    private CommonMethod cm;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbMan = DatabaseManager.getInstance(getContext());
        actualInfo = new ActualData();
        cm = new CommonMethod();
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

        saveButton.setOnClickListener(onSaveDataRequest);
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
            if(!actDepartTime.getText().toString().isEmpty() && !actTripTime.getText().toString().isEmpty()) {
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


    private Button.OnClickListener onSaveDataRequest = new Button.OnClickListener(){

        @Override
        public void onClick(View view) {
            actualInfo.setTotalTripDuration(actTripTime.getText().toString());
            actualInfo.setFuelBalance(Float.parseFloat(fuelLeft.getText().toString()));
            actualInfo.setFuelUsed(Float.parseFloat(fuelused.getText().toString()));
            actualInfo.setTotalTripDistance(Float.parseFloat(totalDistance.getText().toString()));
            actualInfo.setGateParkingName(arrivalGateParking.getText().toString());

            if(dbMan.addActualData(actualInfo)) {
                DepartureFragment.getFlightPlanInfo().setActualID(actualInfo.getActual_ID());
                DepartureFragment.getFlightPlanInfo().setfPStatus("Completed");
                if(dbMan.addFlightPlan(DepartureFragment.getFlightPlanInfo())) {
                    List<String> data = new ArrayList<>();
                    data.add("FLIGHT_PLAN_ID;ESTIMATE_TRIP_TIME;DEPARTURE_FUEL;AIRCRAFT;DEPARTURE" +
                            ";ARRIVAL;ACTUAL_NUMBERS;ESTIMATE_TRIP_DISTANCE;CLIMB_SPEED;CRUISE_SPEED;" +
                            "CRUISE_ALTITUDE;PAYLOAD_WEIGHT;FUEL_WEIGHT;GROSS_WEIGHT;FLIGHT_PLAN_STATUS");
                    for(FlightPlan fp : dbMan.getFlightPlans()){
                        data.add(fp.listFlightPlan());
                    }
                    cm.save(getContext(),"flightplan.txt", data);

                    data.clear();
                    data.add("ACTUAL_ID;DEPARTURE_TIME;ARRIVAL_TIME;FUEL_BALANCE;FUEL_USED;" +
                            "TOTAL_TRIP_TIME;TOTAL_TRIP_DISTANCE;ARRIVAL_GATE_PARKING ");
                    for(ActualData ad : dbMan.getActuals()){
                        data.add(ad.listActualData());
                    }
                    cm.save(getContext(),"actualdata.txt", data);

                    data.clear();
                    data.add("ARRIVAL_ID;LOCATION_ID;GATE_PARKING;ARRIVAL_TIME");
                    for(Arrival arr : dbMan.getArrivals()){
                        data.add(arr.listArrival());
                    }
                    cm.save(getContext(),"arrival.txt", data);

                    saveButton.setVisibility(View.GONE);
                    cm.messageToaster(getContext(),"Flight plan is now saved and completed");
                }
            }

        }
    };
    ////////////////////////////////////  Helper Methods   /////////////////////////////////////////
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.DIRECTORY_DCIM.equals(state)) {
            return true;
        }
        return false;
    }

    public File getPublicAlbumStorageDir(String fileName) {
        // Get the directory for the user's public pictures directory.
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM), fileName);
        if (!file.mkdirs()) {
            cm.messageToaster(getContext(),"Directory not created");
        }
        return file;
    }

/////////////////////////////////////////////////////////////////////
//    public void save(String fileName, List<String> data) {
//        //String text = "We are testing this thing";
//        FileOutputStream fos = null;
//
//        try {
//            fos = getContext().openFileOutput(fileName, MODE_PRIVATE);
//            for(String text : data) {
//                fos.write(text.getBytes());
//                fos.write("\n".getBytes());
//            }
//
//            cm.messageToaster(getContext(),"Saved to " + getContext().getFilesDir() + "/" + fileName);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            if (fos != null) {
//                try {
//                    fos.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }
//    void copyDatabase (String databaseName){
//        try {
//            final String inFileName = "/data/data/com.example.naftech.flightdataapplication/databases/database.db";
//            final String outFileName = Environment.getExternalStorageDirectory() + databaseName + ".db";
//            File dbFile = new File(inFileName);
//            FileInputStream fis = new FileInputStream(dbFile);
//
//            Log.d(TAG, "copyDatabase: outFile = " + outFileName);
//
//            // Open the empty db as the output stream
//            OutputStream output = new FileOutputStream(outFileName);
//
//            // Transfer bytes from the inputfile to the outputfile
//            byte[] buffer = new byte[1024];
//            int length;
//            while ((length = fis.read(buffer)) > 0) {
//                output.write(buffer, 0, length);
//            }
//
//            // Close the streams
//            output.flush();
//            output.close();
//            fis.close();
//        }catch (Exception e){
//            Log.d(TAG, "copyDatabase: backup error");
//        }
//    }
//
//    void restoreDatabase (String databaseName){
//        try {
//            final String inFileName = Environment.getExternalStorageDirectory() + databaseName + ".db";
//            final String outFileName = "/data/data/com.example.naftech.flightdataapplication/databases/database.db";
//            File dbFile = new File(inFileName);
//            FileInputStream fis = new FileInputStream(dbFile);
//
//            Log.d(TAG, "copyDatabase: outFile = " + outFileName);
//
//            // Open the empty db as the output stream
//            OutputStream output = new FileOutputStream(outFileName);
//
//            // Transfer bytes from the inputfile to the outputfile
//            byte[] buffer = new byte[1024];
//            int length;
//            while ((length = fis.read(buffer)) > 0) {
//                output.write(buffer, 0, length);
//            }
//
//            // Close the streams
//            output.flush();
//            output.close();
//            fis.close();
//        }catch (Exception e){
//            Log.d(TAG, "copyDatabase: backup error");
//        }
//    }
}
