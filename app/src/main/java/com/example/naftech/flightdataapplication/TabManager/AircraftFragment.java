package com.example.naftech.flightdataapplication.TabManager;

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
import android.widget.Toast;

import com.example.naftech.flightdataapplication.CommonMethod;
import com.example.naftech.flightdataapplication.R;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

import BusinessObjectLayer.Aircraft;
import BusinessObjectLayer.Departure;
import DatabaseLayer.DatabaseManager;

public class AircraftFragment extends Fragment {

    private Button saveDataBtn;
    private AutoCompleteTextView airlineNACTV, aircraftTypeACTV, manufACTV, tailNumACTV, callSignACTV,
                                    flightNumACTV;

    private DatabaseManager dbMan;
    private Aircraft aircraftData;
    List<Aircraft> planeList;
    List<String> pList;
    private CommonMethod cm;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //messageToaster("You are currently in the Aircraft Tab");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.aircraft_fragment, container,false);
        dbMan = DatabaseManager.getInstance(getContext());
        cm = new CommonMethod();
        aircraftData = new Aircraft();
        planeList = new ArrayList<>();
        pList = new ArrayList<>();

        planeList.addAll(dbMan.getAirCrafts());

        for(Aircraft a : planeList){
            pList.add(a.getAirlineName() + " " + a.getAircraftType() + " " + a.getTailNum());
        }

        airlineNACTV = (AutoCompleteTextView) view.findViewById(R.id.airLineNameACTV);
        aircraftTypeACTV = (AutoCompleteTextView) view.findViewById(R.id.aircraftTypeACTV);
        manufACTV = (AutoCompleteTextView) view.findViewById(R.id.manufacturerACTV);
        tailNumACTV = (AutoCompleteTextView) view.findViewById(R.id.tailNumberACTV);
        callSignACTV = (AutoCompleteTextView) view.findViewById(R.id.callSignACTV);
        flightNumACTV = (AutoCompleteTextView) view.findViewById(R.id.flightNumberACTV);
        saveDataBtn = (Button) view.findViewById(R.id.saveDataButton);

        saveDataBtn.setOnClickListener(saveDataOnClick);
        airlineNACTV.setOnItemClickListener(onItemSelected);
        aircraftTypeACTV.setOnClickListener(onEditClick);
        aircraftTypeACTV.setOnClickListener(onEditClick);
        manufACTV.setOnClickListener(onEditClick);
        tailNumACTV.setOnClickListener(onEditClick);
        callSignACTV.setOnClickListener(onEditClick);
        flightNumACTV.setOnClickListener(onEditClick);
        dropdownList();

        return view;
    }

    private Button.OnClickListener saveDataOnClick = new Button.OnClickListener() {
        @Override
        public void onClick(View view) {
            //save data from fragment to database
            aircraftData.setAirlineName(airlineNACTV.getText().toString());
            aircraftData.setAircraftType(aircraftTypeACTV.getText().toString());
            aircraftData.setManufacturer(manufACTV.getText().toString());
            aircraftData.setTailNum(tailNumACTV.getText().toString());
            aircraftData.setCallSign(callSignACTV.getText().toString());
            aircraftData.setFlightNum(flightNumACTV.getText().toString());

            DepartureFragment.getFlightPlanInfo().setAircraftID(aircraftData.getAircraftID());

            if(dbMan.addAircraft(aircraftData)){
                saveDataBtn.setVisibility(View.GONE);
                cm.messageToaster(getActivity(), aircraftData.getAirlineName()+ " " + aircraftData.getAircraftType()
                        + " " + aircraftData.getTailNum() + " was added successfully");
            }
        }
    };

    private AdapterView.OnItemClickListener onItemSelected = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
            aircraftData = planeList.get(pList.indexOf(airlineNACTV.getText().toString()));

            airlineNACTV.setText(aircraftData.getAirlineName());
            aircraftTypeACTV.setText(aircraftData.getAircraftType());
            manufACTV.setText(aircraftData.getManufacturer());
            tailNumACTV.setText(aircraftData.getTailNum());
            callSignACTV.setText(aircraftData.getCallSign());
            flightNumACTV.setText(aircraftData.getFlightNum());

            DepartureFragment.getFlightPlanInfo().setAircraftID(aircraftData.getAircraftID());

            saveDataBtn.setVisibility(View.GONE);
        }
    };

    private AdapterView.OnClickListener onEditClick = new AdapterView.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(saveDataBtn.getVisibility() == View.GONE){
                saveDataBtn.setVisibility(View.VISIBLE);
            }
        }
    };

    /**
     * Populates the choice list for the dropdown list of an autocomplete text editor
     */
    private void dropdownList(){
       ArrayAdapter<String> Ulist = new ArrayAdapter<String>(getContext(),
                R.layout.support_simple_spinner_dropdown_item, pList);

        airlineNACTV.setAdapter(Ulist); //Attaches the dropdown list Ulist to TopUnit
        airlineNACTV.setMinimumWidth(500);
        //DownUnit.setAdapter(Ulist); //Attaches the dropdown list Ulist to DownUnit
        //DownUnit.setMinimumWidth(200);

        airlineNACTV.setDropDownWidth(900);
        //DownUnit.setDropDownWidth(300);
    }

//    private void messageToaster(String msg){
//        Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
//    }
}
