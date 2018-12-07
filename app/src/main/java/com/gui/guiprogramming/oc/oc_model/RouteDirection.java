package com.gui.guiprogramming.oc.oc_model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * <p>RouteDirection is a model class used to hold the
 * all the route directions details.</p>
 * It implements Serializable to pass it as extra along with Intent
 *
 */
public class RouteDirection implements Serializable {
    int RouteNo;
    String RouteLabel;
    String Direction;

    //Time the request was processed. This will be using the format
    //'YYYYMMDDHHMISS' where HH = 24 hour format
    String RequestProcessingTime;

    ArrayList<Trip> trips;//A RouteDirection may have multiple trips,
    //so here hierarchy is maintained by ArrayList

    public int getRouteNo() {
        return RouteNo;
    }

    public void setRouteNo(int routeNo) {
        RouteNo = routeNo;
    }

    public String getRouteLabel() {
        return RouteLabel;
    }

    public void setRouteLabel(String routeLabel) {
        RouteLabel = routeLabel;
    }

    public String getDirection() {
        return Direction;
    }

    public void setDirection(String direction) {
        Direction = direction;
    }

    public String getRequestProcessingTime() {
        return RequestProcessingTime;
    }

    public void setRequestProcessingTime(String requestProcessingTime) {
        RequestProcessingTime = requestProcessingTime;
    }

    public ArrayList<Trip> getTrips() {
        return trips;
    }

    public void setTrips(ArrayList<Trip> trips) {
        this.trips = trips;
    }
}
