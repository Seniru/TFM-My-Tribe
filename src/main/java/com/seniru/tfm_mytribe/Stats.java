package com.seniru.tfm_mytribe;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TreeMap;
import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javax.swing.JPanel;
import javax.xml.parsers.ParserConfigurationException;
import org.knowm.xchart.PieChart;
import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.XYChart;
import org.xml.sax.SAXException;
import org.knowm.xchart.*;

/**
 * This class is designed to provide charts with statistical info of the tribe
 * @author Seniru
 */
public class Stats {

    private static XYChart memberStats;
    private static PieChart genderStats;
    private static PieChart rankStats;
    private static HistoryProcessor hist;
    private static Pattern join;
    private static Pattern leave;
    private static Pattern tCreation;
    private static Pattern exclude;
    private static MemberAnalyzer mem;

    /**
     * Initialize all the components of the class
     */
    public static void init() {
        try {
            Info.init();
            memberStats = new XYChart(815, 515);
            genderStats = new PieChart(815, 515);  
            rankStats = new PieChart(815,515);
            try {
                hist = new HistoryProcessor();
                try {
                    mem = new MemberAnalyzer();
                } catch (Exception ex) {
                    Logger.getLogger(Stats.class.getName()).log(Level.SEVERE, null, ex);
                }
            } catch (FileNotFoundException | ParserConfigurationException | SAXException ex) {
                System.out.println(ex);
            }
            join = hist.getPattern("join");
            leave = hist.getPattern("leave");
            tCreation = hist.getPattern("tribe creation");
            exclude = hist.getPattern("exclude");
        } catch (IOException | ParseException ex) {
            System.out.println(ex);
        }

    }

    /**
     * This method can be used to get a Chart holding the member count on various periods.
     * <br>The number of members are determined by joining, leaving and excluding of members.
     * @return A {@linkplain JPanel} which holds a {@linkplain XYChart} of member count on several periods
     * @see JPanel
     * @see XYChart
     */
    public static JPanel getMemberStats() {
        var count = new TreeMap<String, Integer>((Object o1, Object o2) -> {
            var kv1 = o1.toString().split(":");
            var kv2 = o2.toString().split(":");
            //checking if the two months lies in the same year
            if (kv1[1].equals(kv2[1])) {
                return Integer.compare(Integer.parseInt(kv1[0]), Integer.parseInt(kv2[0]));
            } else {
                return Integer.compare(Integer.parseInt(kv1[1]), Integer.parseInt(kv2[1]));
            }
        });
        var cal = Calendar.getInstance();
        cal.setTime(Info.getCreationTime());
        hist.getStoredLogs().keySet().forEach((k) -> {
            var date = new Date(k);
            var month = date.getMonth() + 1;
            var year = date.getYear() + 1900;
            count.put(month + ":" + year, 0);

        });
        hist.getStoredLogs().forEach((k, v) -> {
            var date = new Date(k);
            var month = date.getMonth() + 1;
            var year = date.getYear() + 1900;
            if (tCreation.matcher(v).matches()) {
                count.replace(month + ":" + year, count.get(month + ":" + year) + 1);
            } else if (join.matcher(v).matches()) {
                count.replace(month + ":" + year, count.get(month + ":" + year) + 1);
            } else if (leave.matcher(v).matches()) {
                count.replace(month + ":" + year, count.get(month + ":" + year) - 1);
            } else if (exclude.matcher(v).matches()) {
                count.replace(month + ":" + year, count.get(month + ":" + year) - 1);
            }
        });
        var memList = new ArrayList<Integer>();
        count.forEach((k, v) -> {
            if (memList.isEmpty()) {
                memList.add(v);
            } else {
                memList.add(memList.get(memList.size() - 1) + v);
            }
        });
        memberStats.addSeries("Members", memList);
        return new XChartPanel(memberStats);
    }

    /**
     * This method can be used to create a pie chart that compares each gender (male, female and none) in the tribe
     * The number of members belong to each gender is calculated with the help of {@linkplain MemberAnalyzer} class
     * @return a {@linkplain JPanel} which holds a {@linkplain PieChart} that compares the genders.
     * @see JPanel
     * @see PieChart
     * @see MemberAnalyzer#getAccordingToGenders(java.util.TreeMap, java.lang.String...) 
     */
    public static JPanel getGenderComparisonPanel() {
        genderStats.addSeries("Male", mem.getAccordingToGenders(mem.memberData, "male").size());
        genderStats.addSeries("Female", mem.getAccordingToGenders(mem.memberData, "female").size());
        genderStats.addSeries("None", mem.getAccordingToGenders(mem.memberData, "none").size());
        return new XChartPanel(genderStats);
    }
   
    /**
     * This method can be used to create a pie chart that compares each rank in the tribe
     * The number of members belong to each rank is calculated with the help of {@linkplain MemberAnalyzer} class
     * @return a {@linkplain JPanel} which holds a {@linkplain PieChart} that compares the titles.
     * @see JPanel
     * @see PieChart
     * @see MemberAnalyzer#getAccordingToTitles(java.util.TreeMap, java.lang.String...) 
     */
    public static JPanel getRankComparisonPanel() {
        for (var rank : mem.getTitles()) {
            rankStats.addSeries(rank, mem.getAccordingToTitles(mem.memberData, rank).size());
        }
              
        return new XChartPanel(rankStats);
    }
}
