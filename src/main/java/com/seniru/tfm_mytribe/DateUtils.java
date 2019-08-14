package com.seniru.tfm_mytribe;

import java.util.Date;

/**
 * A class to convert unusable and unreadable time units in atelier forum to a readable one.
 * @author Seniru
 */
public class DateUtils {

    
    /**
     * Converts milliseconds in string format to a Date Object.
     * @param millis
     * @return
     */
    public static Date milliToDate(String millis) {       
        return new Date(Long.parseLong(millis));
    }

}
