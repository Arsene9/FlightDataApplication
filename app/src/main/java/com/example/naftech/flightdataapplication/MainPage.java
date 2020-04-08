package com.example.naftech.flightdataapplication;

import android.content.Intent;
import android.os.Environment;
import android.support.design.widget.TabItem;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.naftech.flightdataapplication.TabManager.AircraftFragment;
import com.example.naftech.flightdataapplication.TabManager.ArrivalFragment;
import com.example.naftech.flightdataapplication.TabManager.DepartureFragment;
import com.example.naftech.flightdataapplication.YunSocketHandler.YunSocketController;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import BusinessObjectLayer.ActualData;
import BusinessObjectLayer.Aircraft;
import BusinessObjectLayer.Arrival;
import BusinessObjectLayer.Departure;
import BusinessObjectLayer.FlightPlan;
import BusinessObjectLayer.Location;
import BusinessObjectLayer.Runway;
import DatabaseLayer.DatabaseManager;

import static com.example.naftech.flightdataapplication.TabManager.AircraftFragment.flightNumACTV;


public class MainPage extends AppCompatActivity {

    private TabLayout tabview;
    private TabItem aircraftTab;
    private TabItem departureTab;
    private TabItem arrivalTab;
    private Fragment aircraftFrag;
    private Fragment departureFrag;
    private Fragment arrivalFrag;
    protected static MenuItem saveAction;
    protected static MenuItem addAction;
    protected static MenuItem cancelAction;
    protected static MenuItem updateServerDB;
    protected static MenuItem restoreDB;
    protected static MenuItem resetFP;
    private Toolbar toolbar;
    private boolean remove;

    private List<Fragment> fragmentList;
    private static CommonMethod cm;

    public static YunSocketController yunThread = new YunSocketController();
    public Thread dispatchThread = null;
    private boolean dispatch = false;

//    private PrintWriter output;
//    private BufferedReader input;
//    public static Thread MainThread = null;
//    private Socket socket = null;
//    private volatile int rx;
//    private final static String ARDUINO_IP_ADDRESS = "192.168.240.1";//"10.0.132.208";   //IP Address of the Arduino yun
//    private final static int PORT = 4444 ;   //Port through which socket communication occurs

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_page);

        cm = new CommonMethod();
        fragmentList = new ArrayList<>();
        FragmentManager fm = getSupportFragmentManager();
        tabview = (TabLayout) findViewById(R.id.fpTabLayout);
        //messageToaster( String.valueOf(tabview.getSelectedTabPosition()));
        aircraftTab = (TabItem) findViewById(R.id.aircraftTab);
        departureTab = (TabItem) findViewById(R.id.departureTab);
        arrivalTab = (TabItem) findViewById(R.id.arrivalTab);
        aircraftFrag = fm.findFragmentById(R.id.aircraftFragment);
        departureFrag = fm.findFragmentById(R.id.departureFragment);
        arrivalFrag =  fm.findFragmentById(R.id.arrivalFragment);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);

        fragmentList.add(aircraftFrag);
        fragmentList.add(departureFrag);
        fragmentList.add(arrivalFrag);

        fm.beginTransaction().hide(departureFrag).commit();
        fm.beginTransaction().hide(arrivalFrag).commit();
        fm.beginTransaction().show(aircraftFrag).commit();

/////////////////////// Creates a new directory and files if they don't already exist //////////////
        dirFileGen();
//        List<String> data = new ArrayList<>();
//        data.add("FLIGHT_PLAN_ID;ESTIMATE_TRIP_TIME;DEPARTURE_FUEL;AIRCRAFT;DEPARTURE" +
//                "ARRIVAL;ACTUAL_NUMBERS;ESTIMATE_TRIP_DISTANCE;CLIMB_SPEED;CRUISE_SPPED;" +
//                "CRUISE_ALTITUDE;PAYLOAD_WEIGHT;FUEL_WEIGHT;GROSS_WEIGHT;FLIGHT_PLAN_STATUS");
//        cm.save(MainPage.this,"actualdata.txt", data);
////////////////////////
        tabview.addOnTabSelectedListener(new TabLayout.BaseOnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                //messageToaster(tab.getText().toString() + " at position " + tab.getPosition());
                FragmentManager fm = getSupportFragmentManager();
                fm.beginTransaction().show(fragmentList.get(tab.getPosition())).commit();
//                MainThread.start();
                if(fragmentList.get(tab.getPosition()) == departureFrag)
                    addAction.setVisible(true);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                FragmentManager fm = getSupportFragmentManager();
                fm.beginTransaction().hide(fragmentList.get(tab.getPosition())).commit();
                if(fragmentList.get(tab.getPosition()) == departureFrag)
                    addAction.setVisible(false);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater mMenuInflater = getMenuInflater();
        mMenuInflater.inflate(R.menu.menu, menu);
        addAction = menu.findItem(R.id.addLocationAction);
        saveAction = menu.findItem(R.id.saveAction);
        cancelAction = menu.findItem(R.id.cancelAction);
        updateServerDB = menu.findItem(R.id.updateServerDBAction);
        restoreDB = menu.findItem(R.id.restoreDBAction);
        resetFP = menu.findItem(R.id.resetFlightPlan);
        saveAction.setVisible(false);
        cancelAction.setVisible(false);
        addAction.setVisible(false);

//        MainThread = new Thread(new ControlThread());
//        MainThread.start();

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem Item) {
        if (Item == addAction) {
            Intent addAirport = new Intent(MainPage.this, AirportLocationDataDisplay.class);
            startActivity(addAirport);
        }
        else if(Item == restoreDB){
            //Restores the database from the data files
            //dirFileGen();
            String externalStorage = Environment.getExternalStorageDirectory().getAbsolutePath();
            File folder = new File(externalStorage + File.separator + "FlightTripData");
            File[] fileList = folder.listFiles();
            for(File trgF : fileList){
                restoreDatabaseFromFile(trgF);
                //new SocketWritter("Need" + trgF);
            }
            cm.messageToaster(MainPage.this, "Flight Data has been restored");
        }
        else if(Item == updateServerDB){
            //Submits the data files to the data server via wifi(private)
            YunSocketController yunThread = new YunSocketController();
            Thread yunControllerThread = new Thread(yunThread);
            yunControllerThread.start();
            while(!yunThread.getStatus().startsWith("D"))
            cm.messageToaster(MainPage.this, yunThread.getStatus());
            String externalStorage = Environment.getExternalStorageDirectory().getAbsolutePath();
            File folder = new File(externalStorage + File.separator + "FlightTripData");//"/data/data/com.example.naftech.flightdataapplication/files");
            File[] fileList = folder.listFiles();
//            String[] fileNList =  {"actualdata.txt", "aircrafts.txt", "arrival.txt",
//                    "departure.txt", "flightplan.txt", "location.txt", "runway.txt"};
            for(File trgF : fileList){//fileNList){
                yunThread.SendMessage("Sending" + trgF.getName());
                cm.messageToaster(MainPage.this, "Sending " + trgF.getName());
                for(String data : getFileDataList(trgF)){
                    yunThread.SendMessage("Data" + data);
                }
                yunThread.SendMessage("Save");
                yunThread.SendMessage("Stop");
            }
            yunThread.DisconnectYun();
            cm.messageToaster(MainPage.this, "ServerUpdate complete");
//
//            //Process Yunmessage
////            if(message.startsWith("Data")){
////                String newData = message.replace("Data", "");
////                data.add(newData);
////            }else if(message.equals("Save")){
////                save(targetFile, data);
////            }else if(message.startsWith("Sending")){
////                String fileName = message.replace("Sending", "");
////                targetFile = cm.getFile(fileName);
////            }else if(message.equals("Done")){
////                rx = 0;
////            }
//            yunThread.DisconnectYun();
//            while(yunThread.getStatus().startsWith("C"));
//            cm.messageToaster(MainPage.this, yunThread.getStatus());
        }
        else if(Item == resetFP){
            if(remove){
                ArrivalFragment.clearFragValuesDisplayed();
                DepartureFragment.clearFragValuesDisplayed();
                AircraftFragment.clearFragValuesDisplayed();
                remove = false;
            }
            else{
                ArrivalFragment.restoreFragValues();
                DepartureFragment.restoreFragValues();
                AircraftFragment.restoreFragValues();
                remove = true;
            }
        }

        return true;
    }

//    class dispatcherThread implements Runnable{
//
//        @Override
//        public void run() {
//            while(!dispatch);
//
//            while(yunThread.getStatus().startsWith("D"));
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    while(!yunThread.getStatus().startsWith("D")) {
//                        if(yunThread.yunMSG.startsWith("M"))
//                            cm.messageToaster(MainPage.this, yunThread.yunMSG);
//                        else
//                            flightNumACTV.setText(yunThread.yunMSG);
//                        yunThread.persueReading();
//                    }
//                }
//            });
//
//        }
//    }

    /// Threads definition for wifi configuration, data reception and transmission

    ///Control Thread
//    class ControlThread implements Runnable {
//        public void run() {
//            try {
//                socket = new Socket(ARDUINO_IP_ADDRESS, PORT);
//                output = new PrintWriter(socket.getOutputStream());
//                input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//
//                rx=1;
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
////                        Toast.makeText(MainPage.this, "Looks like the Control Thread has started", Toast.LENGTH_LONG);
//                        cm.messageToaster(MainPage.this,"Waiting for Yun's Message");
//                    }
//                });
//
//                new Thread(new SocketReader()).start();
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            }/*finally{
//					try {
//						mStop.set(true);
//						if(output != null) output.close();
//						if(input != null) input.close();
//						if(socket != null) socket.close();
//					} catch (IOException e) {
//						e.printStackTrace();
//					}
//			}*/
//        }
//    }

    ///Reader thread
//    class SocketReader implements Runnable {
//        File targetFile = null;
//        List<String> data = new ArrayList();
//
//        /**
//         * Saves the data form the string list to the target output file
//         * @param outputFile
//         * @param data
//         */
//        private void save(File outputFile, List<String> data) {
//            FileOutputStream fos = null;
//            try {
//                fos = new FileOutputStream(outputFile, false);//context.openFileOutput(message, MODE_PRIVATE); //getContext().openFileOutput(message, MODE_PRIVATE);
//                for(String text : data) {
//                    fos.write(text.getBytes());
//                    fos.write("\n".getBytes());
//                }
//
//                //messageToaster(context, "Saved to " + outputFile.getPath());
//            } catch (FileNotFoundException e) {
//                //e.printStackTrace();
//            } catch (IOException e) {
//                //e.printStackTrace();
//            } finally {
//                if (fos != null) {
//                    try {
//                        fos.close();
//                    } catch (IOException e) {
//                        //e.printStackTrace();
//                    }
//                }
//            }
//        }
//
//        @Override
//        public void run() {
//            while (rx == 1) {
//                try {
//                    if(input.ready()) {
//                        final String message = input.readLine();
//                        if(message.startsWith("Data")){
//                            String newData = message.replace("Data", "");
//                            data.add(newData);
//                        }else if(message.equals("Save")){
//                            save(targetFile, data);
//
//                        }else if(message.startsWith("Sending")){
//                            String fileName = message.replace("Sending", "");
//                            targetFile = getFile(fileName);
//                        }else if(!message.isEmpty()){
//                            runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    cm.messageToaster(MainPage.this, message);
//                                }
//                            });
//                        }
//
//                    }
//                    else if(socket == null) {
//                        rx = 0;
//                        MainThread.start();
//                    }
//
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }
//
//    ///Writter Thread
//    class SocketWritter implements Runnable {
//        private String message;
//        File targetFile = null;
//        List<String> data = new ArrayList();
//        SocketWritter(String message) {
//            this.message = message;
//        }
//        @Override
//        public synchronized void run() {
////            if(message.startsWith("Sending")) {
////                output.write(message);
////                output.flush();
////                data.addAll(getFileDataList(targetFile));
////                for (String l : data) {
////                    output.print("Data" + l);
////                    output.flush();
////                    //try {Thread.sleep(100); } catch(InterruptedException e) {}
////                }
////                output.print("Save");
////                output.flush();
////            }else{
//                output.write(message);
//                output.flush();
////            }
////            runOnUiThread(new Runnable() {
////                @Override
////                public void run() {
////                    msgTextView.setText(message);
////                }
////            });
////            if(message.equals("0"))
////                rx = 0;
////            else {
////                rx = 1;
////            }
////            new Thread(new SocketReader()).start();
//        }
//    }

    //////////////////////////////   Helper Methods   //////////////////////////////////////////////
//    private void messageToaster(String msg){
//        Toast.makeText(MainPage.this, msg, Toast.LENGTH_LONG).show();
//    }
    protected static boolean dirFileGen(){
        boolean generated = false;
        String externalStorage = Environment.getExternalStorageDirectory().getAbsolutePath();

        File outputDirectory = new File(externalStorage + File.separator + "FlightTripData" );

        if(!outputDirectory.exists()){
            if(outputDirectory.mkdir())
                generated = true;//cm.messageToaster(, "The Directory was created YaY!! =)");
        }

        //File folder = new File("/data/data/com.example.naftech.flightdataapplication/files");
        String[] fileNList =  {"actualdata.txt", "aircrafts.txt", "arrival.txt",
                "departure.txt", "flightplan.txt", "location.txt", "runway.txt"};
        for (String trgF : fileNList) {
            File outputFile = new File(externalStorage + File.separator +
                    "FlightTripData" + File.separator + trgF);
            try {
                if(!outputFile.exists())
                    outputFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return generated;
    }

    private List<String> getFileDataList(File file){
        String line;
        List<String> data = new ArrayList<>();
        try {
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);

            br.readLine();  //reads the first line so processing can start from the second line
            line = br.readLine();
            while (line != null) {
                //String[] lines = line.split(";");
                data.add(line);
                line = br.readLine();
            }
            fr.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

//    private List<String> getDataList(File file){
//        String line;
//        List<String> data = new ArrayList<>();
//        try {
//            FileReader fr = new FileReader(file);
//            BufferedReader br = new BufferedReader(fr);
//
//            br.readLine();  //reads the first line so processing can start from the second line
//            line = br.readLine();
//            while (line != null) {
//                //String[] lines = line.split(";");
//                data.add(line);
//                line = br.readLine();
//            }
//            fr.close();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return data;
//    }

    private void restoreDatabaseFromFile(File file){
        DatabaseManager dbMan = DatabaseManager.getInstance(MainPage.this);
        switch (file.getName()){
            case "actualdata.txt" :
                List<ActualData> actualDataList = new ArrayList<>();
                for(String d : getFileDataList(file)){
                    actualDataList.add(new ActualData(d));
                }
                dbMan.restoreActualData(actualDataList);
                break;
            case "aircrafts.txt" :
                List<Aircraft> aircraftList = new ArrayList<>();
                for(String d : getFileDataList(file)){
                    aircraftList.add(new Aircraft(d));
                }
                dbMan.restoreAircraftData(aircraftList);
                break;
            case "arrival.txt" :
                List<Arrival> arrivalList = new ArrayList<>();
                for(String d : getFileDataList(file)){
                    arrivalList.add(new Arrival(d));
                }
                dbMan.restoreArrivalData(arrivalList);
                break;
            case "departure.txt" :
                List<Departure> departureList = new ArrayList<>();
                for(String d : getFileDataList(file)){
                    departureList.add(new Departure(d));
                }
                dbMan.restoreDepartureData(departureList);
                break;
            case "flightplan.txt" :
                List<FlightPlan> flightPlanList = new ArrayList<>();
                for(String d : getFileDataList(file)){
                    flightPlanList.add(new FlightPlan(d));
                }
                dbMan.restoreFlightPlanData(flightPlanList);
                break;
            case "location.txt" :
                List<Location> locationList = new ArrayList<>();
                for(String d : getFileDataList(file)){
                    locationList.add(new Location(d));
                }
                dbMan.restoreLocationData(locationList);
                break;
            case "runway.txt" :
                List<Runway> runwayList = new ArrayList<>();
                for(String d : getFileDataList(file)){
                    runwayList.add(new Runway(d));
                }
                dbMan.restoreRunwayData(runwayList);
                break;
        }
    }

    /**
     * Searches for the target file, based on the file's name, in the
     * FlightDataRepo directory
     * @param trgFileName
     * @return The file that bears the same name as the target file name
     * (trgFileName). Otherwise returns null if no match was found
     */
//    public synchronized File getFile (String trgFileName){
////        String externalStorage = Environment.getExternalStorageDirectory().getAbsolutePath();
////        File folder = new File(externalStorage + File.separator +
////                "FlightTripData");
////        if(!folder.exists())dirFileGen();
//
////        File[] fileList = folder.listFiles();
//        File folder = new File("/data/data/com.example.naftech.flightdataapplication/files");
////        String[] fileNList =  {"actualdata.txt", "aircrafts.txt", "arrival.txt",
////                "departure.txt", "flightplan.txt", "location.txt", "runway.txt"};
////        for (String trgF : fileNList) {
////            File outputFile = new File(externalStorage + File.separator +
////                    "FlightTripData" + File.separator + trgF);
////
////            try {
////                if(!outputFile.exists())
////                    outputFile.createNewFile();
////            } catch (IOException e) {
////                e.printStackTrace();
////            }
////        }
//        for(File trgF : folder.listFiles()){
//            if(trgF.getName().equals(trgFileName)){
//                return trgF;
//            }
//        }
//        return null;
//    }
}
