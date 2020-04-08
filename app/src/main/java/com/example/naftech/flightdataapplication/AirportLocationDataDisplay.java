package com.example.naftech.flightdataapplication;

import android.app.Dialog;
import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import BusinessObjectLayer.Location;
import BusinessObjectLayer.Runway;
import DatabaseLayer.DatabaseManager;

import static com.example.naftech.flightdataapplication.MainPage.addAction;
import static com.example.naftech.flightdataapplication.MainPage.cancelAction;
import static com.example.naftech.flightdataapplication.MainPage.restoreDB;
import static com.example.naftech.flightdataapplication.MainPage.saveAction;
import static com.example.naftech.flightdataapplication.MainPage.updateServerDB;
import static com.example.naftech.flightdataapplication.MainPage.resetFP;

public class AirportLocationDataDisplay extends AppCompatActivity {
//    private MenuItem saveAction;
//    private MenuItem addAction;
//    private MenuItem cancelAction;
//    private MenuItem updateServerDB, restoreDB;
    private TextView displayTitle;
    private EditText airportIDET, airportNameET, arpCity, arpState,arpCountry, rwyCount;
    private Toolbar toolbar;

    private EditText rwyName, length, surface, hdg, ilsID, ilsFreq;
    private TextView dialogTitle;
    private Button nextBtn, prevBtn;

    private DatabaseManager dbMan;
    private Location locData;
    private List<Runway> locRwys;
    private volatile int rwyListPosition;
    private CommonMethod cm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_airport_location);

        dbMan = DatabaseManager.getInstance(this);
        cm = new CommonMethod();
        locRwys = new ArrayList<>();
        locData = new Location();
        rwyListPosition = 1;

        displayTitle = (TextView) findViewById(R.id.addAirportLocationTextView);
        airportIDET = (EditText) findViewById(R.id.airportIDEditText);
        airportNameET = (EditText) findViewById(R.id.airportNameEditText);
        arpCity = (EditText) findViewById(R.id.cityEditText);
        arpState = (EditText) findViewById(R.id.stateEditText);
        arpCountry = (EditText) findViewById(R.id.countryEditText);
        rwyCount = (EditText) findViewById(R.id.rwyCountEditText);
        toolbar = (Toolbar) findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
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
        addAction.setVisible(false);
        updateServerDB.setVisible(false);
        restoreDB.setVisible(false);
        resetFP.setVisible(false);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem Item) {
        if (Item == saveAction) {
            if(!rwyCount.getText().toString().isEmpty() &&
                    !airportNameET.getText().toString().isEmpty() &&
                    !airportIDET.getText().toString().isEmpty()) {
                //int countRwy = Integer.valueOf(rwyCount.getText().toString());

                if(saveNewLocation(locData)) {
                    showAddRwysDialog();
                    prevBtn.setVisibility(View.GONE);
                    dialogTitle.setText(rwyListPosition + " of " + rwyCount.getText().toString() + " runways");
                }
                else
                    cm.messageToaster(AirportLocationDataDisplay.this, "Sorry location could not be saved");
            }
        }
        else{//cancel Action
            Intent mainpg = new Intent(AirportLocationDataDisplay.this, MainPage.class);
            startActivity(mainpg);
        }
        return true;
    }

    //////////////////////////////   Helper Methods   //////////////////////////////////////////////

//    private void messageToaster(String msg){
//        Toast.makeText(AirportLocationDataDisplay.this, msg, Toast.LENGTH_LONG).show();
//    }

    public void showAddRwysDialog(){
        final Dialog dialog = new Dialog(AirportLocationDataDisplay.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.rwys);
        Window window = dialog.getWindow();
        window.setLayout(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.WRAP_CONTENT);
        //dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialog.show();
        dialog.setCanceledOnTouchOutside(false);

        dialogTitle = (TextView) dialog.findViewById(R.id.rwyBoxTitleTextView);
        rwyName = (EditText) dialog.findViewById(R.id.rwyNameEditText) ;
        length = (EditText) dialog.findViewById(R.id.lengthEditText);
        surface = (EditText) dialog.findViewById(R.id.surfaceEditText);
        hdg = (EditText) dialog.findViewById(R.id.hdgEditText);
        ilsID = (EditText) dialog.findViewById(R.id.ilsIDEditText);
        ilsFreq = (EditText) dialog.findViewById(R.id.ilsFreqEditText);
        prevBtn = (Button) dialog.findViewById(R.id.prevButton);
        nextBtn = (Button) dialog.findViewById(R.id.nextButton);

        prevBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //rwyListDirection = -1;
                Runway rwy = new Runway();
                if (saveNewRunway(rwy)) {
                    if ((rwyListPosition-1) >= locRwys.size() )
                        locRwys.add(rwy);
                    --rwyListPosition;
                    cm.messageToaster(AirportLocationDataDisplay.this, String.valueOf(locRwys.size()));
                    dialog.dismiss();
                    dialog.show();
                    displayRunway(locRwys.get(rwyListPosition - 1));
                    dialogTitle.setText(rwyListPosition +
                            " of " + rwyCount.getText().toString() +
                            " runways");
                    if(rwyListPosition == 1)
                        prevBtn.setVisibility(View.GONE);
                }
                else
                    cm.messageToaster(AirportLocationDataDisplay.this, "Sorry runway could not be saved");
            }
        });
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //rwyListDirection = 1;
                Runway rwy = new Runway();
                if (saveNewRunway(rwy)) {
                    ++rwyListPosition;

                    //messageToaster(String.valueOf(locRwys.size()));
                    if(rwyListPosition > Integer.valueOf(rwyCount.getText().toString())){
                        cm.messageToaster(AirportLocationDataDisplay.this, "Runways and location have been saved to DB");
                        dialog.dismiss();
                        Intent mainpg = new Intent(AirportLocationDataDisplay.this, MainPage.class);
                        startActivity(mainpg);
                    }
                    else {
                        if((rwyListPosition - 1) < locRwys.size()){
                            dialog.dismiss();
                            dialog.show();
                            displayRunway(locRwys.get(rwyListPosition - 1));
                            dialogTitle.setText(rwyListPosition +
                                    " of " + rwyCount.getText().toString() +
                                    " runways");
                        }
                        else{
                            locRwys.add(rwy);
                            dialog.dismiss();
                            showAddRwysDialog();
                            dialogTitle.setText(rwyListPosition +
                                    " of " + rwyCount.getText().toString() +
                                    " runways");
                            cm.messageToaster(AirportLocationDataDisplay.this, "We are rolling here");
                        }
                        if(rwyListPosition > 1)
                            prevBtn.setVisibility(View.VISIBLE);
                    }
                }
                else
                    cm.messageToaster(AirportLocationDataDisplay.this, "Sorry runway could not be saved");

            }
        });
    }

    public boolean saveNewRunway(Runway rwyData){
        rwyData.setName(rwyName.getText().toString());
        if(!length.getText().toString().equals(""))
            rwyData.setLength( Integer.valueOf(length.getText().toString()));
        rwyData.setSurface(surface.getText().toString());
        if(!hdg.getText().toString().equals(""))
            rwyData.setHdg(Integer.valueOf(hdg.getText().toString()));
        rwyData.setILS_ID(ilsID.getText().toString());
        if(!ilsFreq.getText().toString().equals(""))
            rwyData.setILS_Freq(Float.valueOf(ilsFreq.getText().toString()));
        rwyData.setLocation_ID(locData.getLocationID());

        return dbMan.addRunway(rwyData);
    }

    public void displayRunway(Runway rwyD){
        rwyName.setText(rwyD.getName());
        length.setText(String.valueOf(rwyD.getLength()));
        surface.setText(rwyD.getSurface());
        hdg.setText(String.valueOf(rwyD.getHdg()));
        ilsID.setText(rwyD.getILS_ID());
        ilsFreq.setText(String.valueOf(rwyD.getILS_Freq()));
    }

    public boolean saveNewLocation(Location lD){
        lD.setAirportID(airportIDET.getText().toString());
        lD.setAirportName(airportNameET.getText().toString());
        lD.setCity(arpCity.getText().toString());
        lD.setStateName(arpState.getText().toString());
        lD.setCountry(arpCountry.getText().toString());

        return dbMan.addLocation(lD);
    }
}
