package com.gui.guiprogramming.oc.oc_model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Route is a model class used to hold the
 * information retrieved when the user searches for a routes for a station number.
 * It implements Serializable to pass it as extra along with Intent
 */
public class Route implements Serializable {

    int RouteNo;
    int DirectionID;
    String Direction;
    String RouteHeading;

    ArrayList<RouteDirection> directions; //Route may have multiple Route directions,
    // so here hierarchy is maintained by ArrayList

    public Route() {
    }

    public Route(int routeNo, int directionID, String direction, String routeHeading) {
        RouteNo = routeNo;
        DirectionID = directionID;
        Direction = direction;
        RouteHeading = routeHeading;
    }

    public int getRouteNo() {
        return RouteNo;
    }

    public void setRouteNo(int routeNo) {
        RouteNo = routeNo;
    }

    public int getDirectionID() {
        return DirectionID;
    }

    public void setDirectionID(int directionID) {
        DirectionID = directionID;
    }

    public String getDirection() {
        return Direction;
    }

    public void setDirection(String direction) {
        Direction = direction;
    }

    public String getRouteHeading() {
        return RouteHeading;
    }

    public void setRouteHeading(String routeHeading) {
        RouteHeading = routeHeading;
    }

    public ArrayList<RouteDirection> getDirections() {
        return directions;
    }

    public void setDirections(ArrayList<RouteDirection> directions) {
        this.directions = directions;
    }
}
