package com.example.naftech.flightdataapplication;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;
import static com.example.naftech.flightdataapplication.MainPage.dirFileGen;

public class CommonMethod {

    public CommonMethod() {}

    /**
     * Stores the list of data given it into an external folder called FlightTripData. The file name
     * is provided by the user along with the file extension. The folder could be accessed when the
     * user connects their device to a computer or accessed directly from their phone under the list
     * of folders.
     * @param fileName: Destination file Name with extension
     * @param data: List of strings containing the data to be saved
     * @return true if hte save was successful and false if unsuccessful
     */
    public boolean saveToExternal(String fileName, List<String> data) {
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
            return true;
            //messageToaster(context, "Saved to " + outputFile.getPath());
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
        return false;
    }

    public void messageToaster(Context context, String msg){
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }

    /**
     * Generates a custom alert dialog box
     * @param context
     * @param dialogTitle
     * @param actionMSG
     * @param posBtnName
     * @param negBtnName
     * @return Returns true if the positive button was selected and false if the negative button
     *         was selected
     */
    public boolean showAlertDialog(Context context, String dialogTitle, String actionMSG, String posBtnName, String negBtnName ){
        final boolean[] approve = {false};
        AlertDialog alertDialog = new AlertDialog.Builder(context)
                //set icon
                .setIcon(android.R.drawable.ic_dialog_alert)
                //set title
                .setTitle(dialogTitle)
                //set message
                .setMessage(actionMSG)
                //set positive button
                .setPositiveButton(posBtnName, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        approve[0] = true;
                    }
                })
                //set negative button
                .setNegativeButton(negBtnName, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //Do nothing
                    }
                })
                .show();
        return approve[0];
    }

    /**
     * Searches for the target file, based on the file's name, in the
     * FlightTripData directory
     * @param trgFileName
     * @return The file that bears the same name as the target file name
     * (trgFileName). Otherwise returns null if no match was found
     */
    public synchronized File getFile (String trgFileName){
        String externalStorage = Environment.getExternalStorageDirectory().getAbsolutePath();
        File folder = new File(externalStorage + File.separator +
                "FlightTripData");
        if(!folder.exists())dirFileGen();
//        File[] fileList = folder.listFiles();
////        File folder = new File("/data/data/com.example.naftech.flightdataapplication/files");
//        String[] fileNList =  {"actualdata.txt", "aircrafts.txt", "arrival.txt",
//                "departure.txt", "flightplan.txt", "location.txt", "runway.txt"};
//        for (String trgF : fileNList) {
//            File outputFile = new File(externalStorage + File.separator +
//                    "FlightTripData" + File.separator + trgF);
//
//            try {
//                if(!outputFile.exists())
//                    outputFile.createNewFile();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
        for(File trgF : folder.listFiles()){
            if(trgF.getName().equals(trgFileName)){
                return trgF;
            }
        }
        return null;
    }

    /**
     * Stores the data provided to it on to an internal folder with the filename provided by the user
     * @param data: List of strings containing the data to be saved
     * @param fileName: Destination file Name with extension
     * @return true if the the data was stored successfully and false if unsuccessful
     */
    public boolean saveToInternalFile(@NonNull List<String> data, String fileName){
        FileOutputStream fos = null;
        File outputFile = new File("/data/data/com.example.naftech.flightdataapplication/files/" + fileName);

        try {
            fos = new FileOutputStream(outputFile, false);//context.openFileOutput(fileName, MODE_PRIVATE); //getContext().openFileOutput(fileName, MODE_PRIVATE);
            for(String text : data) {
                fos.write(text.getBytes());
                fos.write("\n".getBytes());
            }
            return true;
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
        return false;
    }

    /**
     * Retrieves the data stored in an internal file. Each items in the returned list is a line
     * in from the internal file
     * @param fileName: Destination file Name with extension
     * @return null if the file is empty. Otherwise returns the list a string consisting of the data
     */
    public List<String> getInternalFileData(String fileName){
        List<String> data = new ArrayList<>();
        FileReader fr = null;
        File inputFile = new File("/data/data/com.example.naftech.flightdataapplication/files/" + fileName);
        String line = null;
        try {
            fr = new FileReader(inputFile);
            BufferedReader br = new BufferedReader(fr);

            line = br.readLine();
            while (line != null) {
                data.add(line);
                line = br.readLine();
            }
            //return data;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (fr != null) {
                try {
                    fr.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return data;
        }
    }

    /**
     * Stores the list of data given it into an internal folder called files. The file name
     * is provided by the user along with the file extension.
     * @param fileName: Destination file with extension
     * @param data: List of strings containing the data to be saved
     * @return true if the save was successful and false if unsuccessful
     */
//    public boolean saveToInternalFile(String fileName, @NonNull List<String> data) {
//        String externalStorage = Environment.getExternalStorageDirectory().getAbsolutePath();
//        FileOutputStream fos = null;
//        File outputFile = new File("/data/data/com.example.naftech.flightdataapplication/files" + fileName);
//
//        try {
//            fos = new FileOutputStream(outputFile, false);
//            for(String text : data) {
//                fos.write(text.getBytes());
//                fos.write("\n".getBytes());
//            }
//            //messageToaster(context,"Saved to " + context.getFilesDir() + "/" + fileName);
//            return true;
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
//        return false;
//    }

    /**
     * Stores the database into an internal folder called databases. The file name
     * is provided by the user along without the file extension.
     * @param databaseName
     * @return true if hte save was successful and false if unsuccessful
     */
    public boolean copyDatabase (String databaseName){
        final String inFileName = "/data/data/com.example.naftech.flightdataapplication/databases/database.db";
        final String outFileName = Environment.getExternalStorageDirectory() + databaseName + ".db";
        File dbFile = new File(inFileName);
        FileInputStream fis = null;
        OutputStream output = null;
        try {
            fis = new FileInputStream(dbFile);
            Log.d(TAG, "copyDatabase: outFile = " + outFileName);

            // Open the empty db as the output stream
            output = new FileOutputStream(outFileName);

            // Transfer bytes from the inputfile to the outputfile
            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }
            return true;
        }catch (Exception e){
            Log.d(TAG, "copyDatabase: backup error");
        }finally {
            // Close the streams
            try {
                output.flush();
                output.close();
                fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * Retrieves the database from an internal folder called databases. The file name
     * is provided by the user along without the file extension.
     * @param databaseName
     * @return true if hte save was successful and false if unsuccessful
     */
    public boolean restoreDatabase (String databaseName){
        final String inFileName = "/data/data/com.example.naftech.flightdataapplication/databases/database.db";
        final String outFileName = Environment.getExternalStorageDirectory() + databaseName + ".db";
        File dbFile = new File(inFileName);
        FileInputStream fis = null;
        OutputStream output = null;

        try {
            fis = new FileInputStream(dbFile);
            Log.d(TAG, "copyDatabase: outFile = " + outFileName);

            // Open the empty db as the output stream
            output = new FileOutputStream(outFileName);

            // Transfer bytes from the inputfile to the outputfile
            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }
            return true;
        }catch (Exception e){
            Log.d(TAG, "copyDatabase: backup error");
        }finally {
            // Close the streams
            try {
                output.flush();
                output.close();
                fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }


//    public boolean isExternalStorageWritable() {
//        String state = Environment.getExternalStorageState();
//        if (Environment.DIRECTORY_DCIM.equals(state)) {
//            return true;
//        }
//        return false;
//    }

//    public File getPublicAlbumStorageDir(String fileName) {
//        // Get the directory for the user's public pictures directory.
//        File file = new File(Environment.getExternalStoragePublicDirectory(
//                Environment.DIRECTORY_DCIM), fileName);
//        if (!file.mkdirs()) {
//            cm.messageToaster(getContext(),"Directory not created");
//        }
//        return file;
//    }
}
