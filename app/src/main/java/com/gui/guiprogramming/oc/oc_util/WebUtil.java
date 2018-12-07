package com.gui.guiprogramming.oc.oc_util;

/**
 * WebUtil class contains URL constants, can be used to anywhere in module
 */
public class WebUtil {

    //appID=223eb5c3
    //apiKey=ab27db5b435b8c8819ffb8095328e775

    //HOST URL
    private static final String DOMAIN_OC_TRANSPO = "https://api.octranspo1.com/v1.2/";

    //URL for getting route summary for station
    public static final String GET_ROUTE_SUMMARY = DOMAIN_OC_TRANSPO
            + "GetRouteSummaryForStop?appID=223eb5c3&&apiKey=ab27db5b435b8c8819ffb8095328e775";

    //URL for getting next trips from station
    public static final String GET_NEXT_TRPIS = DOMAIN_OC_TRANSPO +
            "GetNextTripsForStop?appID=223eb5c3&&apiKey=ab27db5b435b8c8819ffb8095328e775";

}
