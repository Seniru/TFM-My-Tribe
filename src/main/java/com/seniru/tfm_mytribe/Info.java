
package com.seniru.tfm_mytribe;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.Properties;

/**
 *This class defines method to read and retrieve info properties used by the program
 * @author Seniru
 */

public class Info {
    
    private static final Properties PROPS = new Properties();
    private static FileReader readerInfo;
    private static boolean infoInit = false;
    
    private static String t_name;
    private static String t_leader;
    private static String t_founder;
    private static String icon;
    private static Date lastUpdate;
    private static int t_id;
    private static Date creationTime;
    private static boolean initialized;
    
    /**
     * Initialize the class and store the properties containing in info.properties file
     * @throws FileNotFoundException
     * @throws IOException
     * @throws ParseException
     */
    public static void init() throws FileNotFoundException, IOException, ParseException {
        if (!infoInit) {
            readerInfo = new FileReader("data/info.properties");
            PROPS.load(readerInfo);
            t_name = PROPS.getOrDefault("tribe_name", "N/A").toString();
            t_leader = PROPS.getOrDefault("leader", "N/A").toString();
            t_founder = PROPS.getOrDefault("founder", "N/A").toString();
            icon = PROPS.getOrDefault("icon", "").toString();
            lastUpdate = DateUtils.milliToDate(PROPS.getOrDefault("last_update", "0").toString());
            t_id = Integer.parseInt(PROPS.getOrDefault("id", "0").toString());
            creationTime = DateUtils.milliToDate(PROPS.getOrDefault("creation_time", "0").toString());
            initialized = Boolean.parseBoolean(PROPS.getOrDefault("initialized", "false").toString());

            infoInit = true;            
        }     
    }
    
    /**
     *
     * @return the tribe name
     */
    public static String getTName() {
        return t_name;
    }
    
    /**
     *
     * @return the tribe leader
     */
    public static String getTLeader() {
        return t_leader;
    }
    
    /**
     *
     * @return the founder of the tribe
     */
    public static String getTFounder() {
        return t_founder;
    }

    /**
     *
     * @return the icon of the tribe
     */
    public static String getIcon() {
        return icon;
    }

    /**
     *
     * @return the last updated time, which is the last moment of reading history logs and members from the forum
     */
    public static Date getLastUpdateTime() {
        return lastUpdate;
    }

    /**
     *
     * @return the tribe id
     */
    public static int getTId() {
        return t_id;
    }

    /**
     *
     * @return the tribe's creation time
     */
    public static Date getCreationTime() {
        return creationTime;
    }

    /**
     *  This method is useful to check if the info has been initialized from the installation utility     * 
     * @return the initialized property of info.property
     */
    public static boolean hasInit() {
        return initialized;
    }
 }

