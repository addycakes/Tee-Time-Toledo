package com.adamwilson.golf.TeeTime;

/**
 * Created by adam on 12/30/15.
 */
public class TeeTimeFactory {
    public TeeTime createTeeTime(String courseName){
        TeeTime newTeeTime = null;

        if (courseName.equalsIgnoreCase("Irish") ||
                courseName.equalsIgnoreCase("Wolverine") ||
                courseName.equalsIgnoreCase("Buckeye")) {
            newTeeTime = new TeeTime("Bedfordhills");
        }

        return newTeeTime;
    }
}
