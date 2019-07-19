package com.example.naftech.flightdataapplication;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import DatabaseLayer.DatabaseManager;

public class AddRwyDialog extends DialogFragment implements View.OnClickListener {
    private Button prevBtn, nextBtn;

    private int locID;
    private DatabaseManager dbMan;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View addRwyDialog = inflater.inflate(R.layout.rwys,null);

        //dbMan = DatabaseManager.getInstance(getActivity());

        //locID = getArguments().getInt("LocationID");
        //ppN = getArguments().getString("ppN", "None");

        //prevBtn = (Button) addRwyDialog.findViewById(R.id.prevButton);
        //nextBtn = (Button) addRwyDialog.findViewById(R.id.nextButton);

        //Toast.makeText(getActivity(), pName, Toast.LENGTH_LONG).show();

        //prevBtn.setOnClickListener(this);
        //nextBtn.setOnClickListener(this);

        setCancelable(true);
        return addRwyDialog;
    }


    @Override
    public void onClick(View view) {

    }
}
