package com.example.naftech.flightdataapplication.YunSocketHandler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.util.ArrayList;
import java.util.List;

import static com.example.naftech.flightdataapplication.TabManager.AircraftFragment.flightNumACTV;
import static com.example.naftech.flightdataapplication.YunSocketHandler.YunSocketController.cm;
import static com.example.naftech.flightdataapplication.YunSocketHandler.YunSocketController.input;
import static com.example.naftech.flightdataapplication.YunSocketHandler.YunSocketController.rx;
import static com.example.naftech.flightdataapplication.YunSocketHandler.YunSocketController.socket;
import static com.example.naftech.flightdataapplication.YunSocketHandler.YunSocketController.yunMSG;

class YunSocketReader implements Runnable {
    private File targetFile = null;
    private String  dataFileName = "DataFile.txt";
    private boolean persue;
    private List<String> data = new ArrayList();

    /**
     * Saves the data form the string list to the target output file
     * @param outputFile
     * @param data
     */
    private void save(File outputFile, List<String> data) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(outputFile, false);//context.openFileOutput(message, MODE_PRIVATE); //getContext().openFileOutput(message, MODE_PRIVATE);
            for(String text : data) {
                fos.write(text.getBytes());
                fos.write("\n".getBytes());
            }

            //messageToaster(context, "Saved to " + outputFile.getPath());
        } catch (FileNotFoundException e) {
            //e.printStackTrace();
        } catch (IOException e) {
            //e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    //e.printStackTrace();
                }
            }
        }
    }

    public String getDataFileName() {
        return dataFileName;
    }

    public boolean isPersue() {
        return persue;
    }

    public void setPersue(boolean persue) {
        this.persue = persue;
    }

    @Override
    public void run() {
        FileOutputStream fos = null;
        try {
            File outputFile = new File("/data/data/com.example.naftech.flightdataapplication/files/" + dataFileName);
            fos = new FileOutputStream(outputFile, false);
            setPersue(false);
            while (rx == 1) {
                if (input.ready()) {
                    final String message = input.readLine();
                    if (!message.isEmpty()) {
                        yunMSG = message;
                        //Halted until instructed to persue
                        while(!isPersue());
                        setPersue(false);
                        //setYunMsg(message);
//                        fos.write(message.getBytes());
//                        fos.write("\n".getBytes());
                    }
                } else if (socket == null) {
                    rx = 0;
                }
            }
        } catch (FileNotFoundException e) {
            //e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            rx = 0;
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    //e.printStackTrace();
                }
            }
        }
    }
}

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
