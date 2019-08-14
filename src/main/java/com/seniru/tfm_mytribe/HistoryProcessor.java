package com.seniru.tfm_mytribe;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;
import java.util.TreeMap;
import java.util.regex.*;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.xml.sax.SAXException;

/**
 * This class contains various methods that can be used to process and retrieve
 * useful information and to retrieve history
 *
 * @author Seniru
 */
public class HistoryProcessor {

    // Structure to store the history in order of date
    private final TreeMap<Long, String> HIST = new TreeMap();

    // Patterns to process info using history logs
    public static final String NAME_MATCHER = "(?<name>\\w+(#\\d{1,4})?)";
    public static final String NAME_MATCHER_FRAG = "\\w+(#\\d{1,4})";
    public static final Pattern TRIBE_CREATION = Pattern.compile(NAME_MATCHER + " a créé la tribu .*");
    public static final Pattern JOIN = Pattern.compile(NAME_MATCHER + " a rejoint la tribu\\.$");
    public static final Pattern RANK_CHANGE = Pattern.compile(NAME_MATCHER_FRAG + " a changé le rang de " + NAME_MATCHER + " vers (?<title>(.+))\\.");
    public static final Pattern TRIBE_LEAVE = Pattern.compile(NAME_MATCHER + " a quitté la tribu\\.");
    public static final Pattern MEMBER_EXCLUDE = Pattern.compile(NAME_MATCHER_FRAG + " a exclu " + NAME_MATCHER_FRAG +" de la tribu\\.");

    // Reader to read logs saved in local
    private final HistoryManager histMan;
    private final String logs;
    private final Properties info = new Properties();
    private final MemberXMLManager.Writer member;

    /**
     * Constructor
     *
     * @throws FileNotFoundException
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws SAXException
     */
    public HistoryProcessor() throws FileNotFoundException, IOException, ParserConfigurationException, SAXException {
        histMan = new HistoryManager();
        logs = histMan.getLogs();
        member = new MemberXMLManager().new Writer();

        info.load(new FileReader("data/info.properties"));
        ///prop.WRITER.write("");
        //prop.WRITER.write("");
        // prop.WRITER.flush();

        for (var line : logs.split("\n")) {
            var kv = line.split(":");
            HIST.put(Long.parseLong(kv[0]), kv[1]);
        }

    }

    /**
     * Processes the history and extract info from it. Extracted information are
     * also stored in a properties file<br>
     *
     * @param HIST a treemap which contains mapped time and relevant logs
     */
    public void processHistory(TreeMap<Long, String> HIST) {
        try {
            //prop.writer.write("");
            HIST.forEach((k, v) -> {
                //try {
                //prop.writer.append(k+ ":" + v + "\n");
                //prop.writer.flush();
                //} catch (IOException ex) {
                //Logger.getLogger(HistoryProcessor.class.getName()).log(Level.SEVERE, null, ex);
                //}
                if (TRIBE_CREATION.matcher(v).matches()) {
                    var m = TRIBE_CREATION.matcher(v);
                    m.matches();
                    info.setProperty("founder", m.group("name"));
                    info.setProperty("creation_time", Long.toString(k));
                    //info.
                    //info.store(prop.WRITER, "");

                } else if (JOIN.matcher(v).matches()) {
                    var m = JOIN.matcher(v);
                    m.matches();
                    try {
                        member.addProperty(m.group("name"), "join", Long.toString(k));
                    } catch (TransformerException ex) {
                        //Logger.getLogger(HistoryProcessor.class.getName()).log(Level.SEVERE, null, ex);
                        System.out.println("Error: " + ex);
                    }
                }

            });
            info.setProperty("last_update", HIST.lastKey().toString());
            info.store(new FileWriter("data/info.properties"), "");
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }

    /**
     * @return the tree map which maps the time and relevant log
     *
     */
    public TreeMap<Long, String> getStoredLogs() {
        return this.HIST;
    }

    public Pattern getPattern(String patternType) {
        Pattern pat = null;
        switch (patternType.toLowerCase()) {
            case "tribe creation":
                pat = TRIBE_CREATION;
                break;
            case "join":
                pat = JOIN;
                break;
            case "rank change":
                pat = RANK_CHANGE;
                break;
            case "leave":
                pat = TRIBE_LEAVE;
                break;
            case "exclude":
                pat = MEMBER_EXCLUDE;
                break;
        }
        return pat;
    }

}
