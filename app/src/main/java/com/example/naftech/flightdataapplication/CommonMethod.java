package com.example.naftech.flightdataapplication;

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class CommonMethod {
    public CommonMethod() {
    }

    public void save(Context context, String fileName, List<String> data) {
        //String text = "We are testing this thing";
        String externalStorage = Environment.getExternalStorageDirectory().getAbsolutePath();
        FileOutputStream fos = null;
        File outputFile = new File(externalStorage + File.separator +
                "FlightTripData" + File.separator + fileName); //
        // "/data/data/com.example.naftech.flightdataapplication/files" + fileName);

        try {
            fos = new FileOutputStream(outputFile, false);//context.openFileOutput(fileName, MODE_PRIVATE); //getContext().openFileOutput(fileName, MODE_PRIVATE);
            for(String text : data) {
                fos.write(text.getBytes());
                fos.write("\n".getBytes());
            }

            messageToaster(context, "Saved to " + outputFile.getPath());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void messageToaster(Context context, String msg){
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }

}
