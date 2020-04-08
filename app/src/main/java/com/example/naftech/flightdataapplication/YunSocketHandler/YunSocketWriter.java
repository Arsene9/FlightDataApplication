package com.example.naftech.flightdataapplication.YunSocketHandler;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.example.naftech.flightdataapplication.YunSocketHandler.YunSocketController.output;

public class YunSocketWriter implements Runnable {

    private String message;
    File targetFile = null;
    List<String> data = new ArrayList();

    YunSocketWriter(String message) {
        this.message = message;
    }
    @Override
    public synchronized void run() {
        output.write(message);
        output.flush();
    }
}
