package com.gui.guiprogramming.oc.oc_model;

import java.io.Serializable;

/**
 * Trip is a model class used to hold all the details about a trip
 * It implements Serializable to pass it as extra along with Intent
 */
public class Trip implements Serializable {

    String TripDestination;
    String TripStartTime; //start time for the trip. Format HH:MI,where HH = 24 hour format
    String AdjustedScheduleTime; //in minutes - delay

    //If the AdjustmentAge is a negative value,
    // it indicates that the AdjustedScheduleTime contains the planned scheduled time.
    float AdjustmentAge;

    boolean LastTripOfSchedule;
    String BusType;
    double Latitude;
    double Longitude;
    float GPSSpeed;

    public String getTripDestination() {
        return TripDestination;
    }

    public void setTripDestination(String tripDestination) {
        TripDestination = tripDestination;
    }

    public String getTripStartTime() {
        return TripStartTime;
    }

    public void setTripStartTime(String tripStartTime) {
        TripStartTime = tripStartTime;
    }

    public String getAdjustedScheduleTime() {
        return AdjustedScheduleTime;
    }

    public void setAdjustedScheduleTime(String adjustedScheduleTime) {
        AdjustedScheduleTime = adjustedScheduleTime;
    }

    public float getAdjustmentAge() {
        return AdjustmentAge;
    }

    public void setAdjustmentAge(float adjustmentAge) {
        AdjustmentAge = adjustmentAge;
    }

    public boolean isLastTripOfSchedule() {
        return LastTripOfSchedule;
    }

    public void setLastTripOfSchedule(boolean lastTripOfSchedule) {
        LastTripOfSchedule = lastTripOfSchedule;
    }

    public String getBusType() {
        return BusType;
    }

    public void setBusType(String busType) {
        BusType = busType;
    }

    public double getLatitude() {
        return Latitude;
    }

    public void setLatitude(double latitude) {
        Latitude = latitude;
    }

    public double getLongitude() {
        return Longitude;
    }

    public void setLongitude(double longitude) {
        Longitude = longitude;
    }

    public float getGPSSpeed() {
        return GPSSpeed;
    }

    public void setGPSSpeed(float GPSSpeed) {
        this.GPSSpeed = GPSSpeed;
    }
}
