package com.example.naftech.flightdataapplication;

import android.content.Intent;
import android.support.design.widget.TabItem;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
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

import java.util.ArrayList;
import java.util.List;

public class MainPage extends AppCompatActivity {

    private TabLayout tabview;
    private TabItem aircraftTab;
    private TabItem departureTab;
    private TabItem arrivalTab;
    private android.support.v4.app.Fragment aircraftFrag;
    private android.support.v4.app.Fragment departureFrag;
    private Fragment arrivalFrag;
    private MenuItem saveAction;
    private MenuItem addAction;
    private MenuItem cancelAction;
    private Toolbar toolbar;

    private List<Fragment> fragmentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_page);

        fragmentList = new ArrayList<>();
        FragmentManager fm = getSupportFragmentManager();
        tabview = (TabLayout) findViewById(R.id.fpTabLayout);
        //messageToaster( String.valueOf(tabview.getSelectedTabPosition()));
        aircraftTab = (TabItem) findViewById(R.id.aircraftTab);
        departureTab = (TabItem) findViewById(R.id.departureTab);
        arrivalTab = (TabItem) findViewById(R.id.arrivalTab);
        aircraftFrag = fm.findFragmentById(R.id.aircraftFragment);
        departureFrag = fm.findFragmentById(R.id.departureFragment);
        arrivalFrag = fm.findFragmentById(R.id.arrivalFragment);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);

        fragmentList.add(aircraftFrag);
        fragmentList.add(departureFrag);
        fragmentList.add(arrivalFrag);

        fm.beginTransaction().hide(departureFrag).commit();
        fm.beginTransaction().hide(arrivalFrag).commit();
        fm.beginTransaction().show(aircraftFrag).commit();



        tabview.addOnTabSelectedListener(new TabLayout.BaseOnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                //messageToaster(tab.getText().toString() + " at position " + tab.getPosition());
                FragmentManager fm = getSupportFragmentManager();
                fm.beginTransaction().show(fragmentList.get(tab.getPosition())).commit();
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
        saveAction.setVisible(false);
        cancelAction.setVisible(false);
        addAction.setVisible(false);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem Item) {
        if (Item == addAction) {
            Intent addAirport = new Intent(MainPage.this, AirportLocationDataDisplay.class);
            startActivity(addAirport);
        }

        return true;
    }

    //////////////////////////////   Helper Methods   //////////////////////////////////////////////

    private void messageToaster(String msg){
        Toast.makeText(MainPage.this, msg, Toast.LENGTH_LONG).show();
    }
}
