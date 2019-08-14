package com.seniru.tfm_mytribe;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class provides useful methods for retrieving history logs and translate
 * them to supported languages.<br><br>
 * Supported language translations include,
 * <ul>
 * <li>French - English</li>
 * </ul>
 * <br>
 * <i>Help is needed for translating following languages</i>
 * <ul>
 * <li>French - Spanish</li>
 * <li>French - Arab</li>
 * <li>French - Portuguese</li>
 * </ul>
 * @author Seniru
 */
public class HistoryManager {
    
    // class fields indicating constant values to be called in getLogsBefore method
    public final static int TODAY = 0;
    public final static int ONE_DAY = 1;
    public final static int THREE_DAY = 3;
    public final static int ONE_WEEK = 7;
    public final static int ONE_MONTH = 30;
    public final static int FIVE_MONTHS = 150;
    public final static int ONE_YEAR = 365;
        
    private final FileInputStream reader;
    private final String logs;

    // Regexp patterns to detect the common french phrases in history log
    private final ArrayList<String> PATTERNS = new ArrayList(Arrays.asList(new String[]{
        // Patterns with high priority
        "a changé le message d'accueil\\.",
        "a rejoint la tribu\\.",
        "changé le rang de",
        "a quitté la tribu\\.",
        "a changé le code de la maison de tribu",
        // Patterns with low priority
        " a ",
        " créé ",
        " la ",
        " tribu(\\s+|\\.)",
        " vers ",
        " exclu ",
        " de "

    }));

    // Array to hold words and expressions corresponding to the french words according to the order of the PATTERNS
    private final String[] ENGLISH = new String[]{
        "has changed the tribe message.",
        "has joined the tribe.",
        "changed the rank of",
        "has left the tribe",
        "has changed the tribe\'s map",
        " has ",
        " created ",
        " the ",
        " tribe ",
        " to ",
        " kicked out ",
        " from "
    };

    /**
     * Constructor
     * @throws FileNotFoundException
     * @throws IOException
     */
    public HistoryManager() throws FileNotFoundException, IOException {
        //reading the history logs and appending it to the string logs
        reader = new FileInputStream("data/history_logs.txt");       
        logs = new String(reader.readAllBytes(), Charset.forName("UTF-8"));
        

    }

    /**
     * Returns history logs
     * @return logs - History logs which are stored when installing
     */
    public String getLogs() {
        return this.logs;
    }
    
    /**
     * This method returns a String after translating according to the language specified
     * @param language the resulting language after translation
     * @param logs the logs to be translated. If it is stated as default, then the class field {@linkplain #logs} will be used. Otherwise it will translate the given logs.
     * @return translated version of history logs according to the language specified
     * @throws Exception if the given language is not supported
     */
    public String translateLogs(String language, String logs) throws Exception {
        var format = logs.equals("default") ? this.logs : logs;
        switch (language) {
            // todo: create test cases for each pattern and fix this bug
            case "ENG":
                for (var pattern : this.PATTERNS) {
                    format = format.replaceAll(pattern, ENGLISH[PATTERNS.indexOf(pattern)]);

                }                
                break;
            case "FR":
                break;
            default:
                throw new Exception("Language not supported!");
        }
        return format;
    }
    
    public ArrayList<String[]> getLogsSeparated(String logs) {        
        var kv = new ArrayList<String[]>();         
            for (var line : logs.split("\n")) {
                kv.add(line.split(":"));
            }
            return kv;            
        
    }

    public ArrayList<String[]> getTranslatedAndSeparatedLogs(String lang, String logs)  {
        var plogs = "";
        try {
            plogs = translateLogs(lang, logs);
        } catch (Exception ex) {
            Logger.getLogger(HistoryManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return getLogsSeparated(plogs);
    }
    
    public ArrayList<String[]> getLogsWithinRange(Calendar from, Calendar to, String lang) {
        var sepLogs = getTranslatedAndSeparatedLogs(lang, "default");
        sepLogs.removeIf(x -> !(from.getTimeInMillis() <= Long.parseLong(x[0]) && Long.parseLong(x[0]) <= to.getTimeInMillis()));       
        return sepLogs;        
    }
    
    public ArrayList<String[]> getLogsBefore(int before, String lang) {
        var from = Calendar.getInstance();
        var to = Calendar.getInstance();
        from.setTimeInMillis(Duration.ofMillis(System.currentTimeMillis()).minusDays(1 * before).toMillis());
        return getLogsWithinRange(from, to, lang);
    }
    
    public ArrayList<String[]> searchLogs(String time, String query, String lang, ArrayList<String[]> logs) {
        if (logs == null || logs.isEmpty()) return new ArrayList<>();
        var sLogs = new StringBuilder();
        logs.forEach(x -> {
            sLogs.append(x[0]).append(":").append(x[1]).append("\n");
        });
        return searchLogs(time, query, lang, sLogs.toString());
    }
    
    public ArrayList<String[]> searchLogs(String time, String query, String lang, String logs) {
        if (query.equals("")) return getTranslatedAndSeparatedLogs(lang, logs);
        
        var res = new ArrayList<String[]>();
        var scores = new HashMap<String[], Integer>();
        getTranslatedAndSeparatedLogs(lang, logs).forEach(x -> {
            scores.put(x, 0);
            if (x[1].toLowerCase().contains(query.toLowerCase())) {
                for (var cD : x[1].split("")) {
                    for (var cQ : query.split("")) {
                        if (cD.equalsIgnoreCase(cQ)) {
                            scores.replace(x, scores.get(x) + 1);
                        }
                    }
                }
            }
            if (x[0].contains(time)) {
                for (var tD : x[0].split("")) {
                    for (var tQ : time.split("")) {
                        if (tD.equals(tQ)) {
                            scores.replace(x, scores.get(x) + 1);
                        }
                    }
                }
            }
        });
        scores.entrySet().stream().sorted(Map.Entry.comparingByValue()).filter(x -> !x.getValue().equals(0)).forEachOrdered(x -> res.add(x.getKey()));
        Collections.reverse(res);
        return res;
    }
}
