package com.adamwilson.golf.TeeTime;

/**
 * Created by adam on 12/30/15.
 */
public class TeeTime {
    public int[] date;
    public int[] time;
    public String courseName;

    public TeeTime(String siteKey){
        date = null;
        time = null;
        courseName = null;

        if (siteKey.equalsIgnoreCase("Bedfordhills")){
            System.out.println("bfh");
        }
    }

    public void submit(){
        System.out.println("submitting tee time");
    }

}
