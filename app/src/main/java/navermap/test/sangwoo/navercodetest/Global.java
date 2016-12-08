package navermap.test.sangwoo.navercodetest;

import android.content.Context;

/**
 * Created by sangwoo-pc on 2016. 12. 6..
 */

public class Global {
    public static final String PREFERENCE_NAME = "Naver";

    public static final int PREFERNCES_MANBO_COUNT_DEFAULT_VALUE = 0;



    public static final String PREFERENCES_LATITUDE_NAME = "Latitude";
    public static final String PREFERENCES_LONGITUDE_NAME = "Longitude";

    public static final int FAIL_GET_GPS_INFOMATION = -1;
    public static final double MAX_ONE_SETP_MOVE_DISTANCE = 2;



    public static  String  getMeterOrKillmeter(Context context,double value){

        if(1000>value)
                return String.valueOf(Global.meter(value)) + context.getString(R.string.meter);
        else
            return String.valueOf(Global.meterToKillmeter(value)) + context.getString(R.string.kilometer);
    }


    public static  String  getManBOcount(Context context,int count){

        return count + context.getString(R.string.man_bo_gi_count);
    }


    public static double meterToKillmeter(double value){

        value = value / 1000d;
        value = Math.round(value*100d) / 100d;
        return  value;
    }

    public static double meter(double value){


        value = Math.round(value*100d) / 100d;
        return  value;
    }

    // This function converts decimal degrees to radians
    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    // This function converts radians to decimal degrees
    private static double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }
}
