package com.example.testrestapi.application;

import com.ainirobot.coreservice.client.Definition;
import com.ainirobot.coreservice.client.robotsetting.RobotSettingApi;
import com.example.testrestapi.MainActivity;
import com.example.testrestapi.model.StatusUpdate;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class Status implements Runnable{

    private MainActivity ma;
    private static final int SLEEP_TIME_STATUS = 60 * 1000;

    public Status(MainActivity ma) {
        this.ma = ma;
    }

    @Override
    public void run() {
        System.out.println("status run.......");
        StatusUpdate su = new StatusUpdate();
        while(true) {

            String BatteryLvl = RobotSettingApi.getInstance().getRobotString(Definition.ROBOT_SETTINGS_BATTERY_INFO);
            su.setBatteryLevel(BatteryLvl);
            sendStatus(su);
            try {
                Thread.sleep(SLEEP_TIME_STATUS);
            }
            catch(InterruptedException e) {
                System.out.println(e.getMessage());
            }
        }

    }

    private void sendStatus(StatusUpdate su) {
        try {
            URL url = new URL("http:/192.168.10.53:80/api/v1/status");

            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            con.setDoOutput(true);

            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.create();

            String statusStr = gson.toJson(su);
            try(OutputStream os = con.getOutputStream()) {
                byte[] input = statusStr.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

        }
        catch(IOException e) {
            System.out.println(e.getMessage());
        }

    }
}
