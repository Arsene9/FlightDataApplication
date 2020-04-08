package com.example.naftech.flightdataapplication.YunSocketHandler;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.example.naftech.flightdataapplication.CommonMethod;
import com.example.naftech.flightdataapplication.MainPage;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class YunSocketController implements Runnable {

    protected static CommonMethod cm;
    protected static PrintWriter output;
    protected static BufferedReader input;
    private Thread ReaderThread = null;
    private YunSocketReader yunRead = null;
    private YunSocketWriter yunWrite = null;
    protected static Socket socket = null;
    protected static volatile int rx;
    public static String yunMSG = "";
    private String status = "";  //Either Connected or Disconnected
    private final static String ARDUINO_IP_ADDRESS = "192.168.240.1";//"10.0.132.208";   //IP Address of the Arduino yun
    private final static int PORT = 4444 ;   //Port through which socket communication occurs

    /**  Constructor  with context initialisation**/
    public YunSocketController() {
        this.status = "Disconnected";
        this.yunRead = new YunSocketReader();
        ReaderThread = new Thread(this.yunRead);
    }

    /**
     *
     **/
    public String getDatafileName(){
        return yunRead.getDataFileName();
    }

    public void persueReading(){
        yunRead.setPersue(true);
    }

    public String getStatus(){
        return status;
    }

    public void DisconnectYun(){
        rx = 0;
    }

    public boolean SendMessage(String message){
        if(socket != null) {
            this.yunWrite = new YunSocketWriter(message);
            new Thread(this.yunWrite).start();
            while(!yunMSG.startsWith("R"));
            persueReading();
            return true;
        }else{
            return false;
        }
    }

    @Override
    public void run() {
        try {
                socket = new Socket(ARDUINO_IP_ADDRESS, PORT);
                output = new PrintWriter(socket.getOutputStream());
                input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                rx=1;
                status = "Connected";
                ReaderThread.start();
                while(rx==1);
            } catch (IOException e) {
                e.printStackTrace();
            }finally{
                try {
                    if(output != null) output.close();
                    if(input != null) input.close();
                    if(socket != null) socket.close();
                    this.status = "Disconnected";
                } catch (IOException e) {
                    e.printStackTrace();
                }
			}
    }
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


}
