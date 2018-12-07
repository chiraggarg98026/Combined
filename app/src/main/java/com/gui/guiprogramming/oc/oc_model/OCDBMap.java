package com.gui.guiprogramming.oc.oc_model;

/**
 * Used to hold each row values, retrieved from database.
 */
public class OCDBMap {
    int stopNo;
    String stopName, direction, routeLabel, reqProcessingTime;
    int routeNo;
    Trip trip;

    public int getStopNo() {
        return stopNo;
    }

    public void setStopNo(int stopNo) {
        this.stopNo = stopNo;
    }

    public String getStopName() {
        return stopName;
    }

    public void setStopName(String stopName) {
        this.stopName = stopName;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getRouteLabel() {
        return routeLabel;
    }

    public void setRouteLabel(String routeLabel) {
        this.routeLabel = routeLabel;
    }

    public int getRouteNo() {
        return routeNo;
    }

    public void setRouteNo(int routeNo) {
        this.routeNo = routeNo;
    }

    public String getReqProcessingTime() {
        return reqProcessingTime;
    }

    public void setReqProcessingTime(String reqProcessingTime) {
        this.reqProcessingTime = reqProcessingTime;
    }

    public Trip getTrip() {
        return trip;
    }

    public void setTrip(Trip trip) {
        this.trip = trip;
    }
}
