package com.example.testrestapi.model;

public class StatusUpdate {
    public String batteryLevel;

    public StatusUpdate(String batteryLevel) {
        this.batteryLevel = batteryLevel;
    }

    public StatusUpdate() {
    }

    public String getBatteryLevel() {
        return batteryLevel;
    }

    public void setBatteryLevel(String batteryLevel) {
        this.batteryLevel = batteryLevel;
    }
}


