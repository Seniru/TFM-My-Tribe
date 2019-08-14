package com.seniru.tfm_mytribe;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.xml.sax.SAXException;

/**
 *
 * @author Seniru
 */
public class Updator {

    private FileInputStream histReader;
    private FileWriter histWriter;
    private String logs;
    private long lastUpdate;
    private Properties props = new Properties();
    private HistoryProcessor histProc;
    private Properties titleProps = new Properties();

    public Updator() {
        try {
            histReader = new FileInputStream("data\\history_logs.txt");
            logs = new String(histReader.readAllBytes(), Charset.forName("UTF-8"));
            histWriter = new FileWriter("data\\history_logs.txt");
            props.load(new FileReader("data\\info.properties"));
        } catch (IOException ex) {
            Logger.getLogger(Updator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void updateHist() {
        System.out.println("Updating hist");
        try {
            var page = 0;
            var entries = Scraper.scrapeHist(Integer.toString(Info.getTId()), 1).select(".cadre.cadre-tribu > table > tbody > tr");
            var curEntry = Long.parseLong(entries.get(0).selectFirst("td").text().replaceAll("\\D+", ""));
            var memberUpdate = false;
            lastUpdate = curEntry;
            var newLogs = new String("".getBytes(Charset.forName("UTF-8")), Charset.forName("UTF-8"));
            outerloop:
            while (curEntry != Info.getLastUpdateTime().getTime()) {
                entries = Scraper.scrapeHist(Integer.toString(Info.getTId()), page++).select(".cadre.cadre-tribu > table > tbody > tr");
                for (var e : entries) {
                    if (Long.parseLong(e.selectFirst("td").text().replaceAll("\\D+", "")) == Info.getLastUpdateTime().getTime()) {
                        break outerloop;
                    }
                    var temp = e.select("td").get(1).text();
                    var log = (Long.parseLong(e.selectFirst("td").text().replaceAll("\\D+", "")) + ":" + temp + "\n");
                    System.out.println(log);
                    if (!memberUpdate && (temp.matches(HistoryProcessor.JOIN.pattern()) || temp.matches(HistoryProcessor.MEMBER_EXCLUDE.pattern()) || temp.matches(HistoryProcessor.RANK_CHANGE.pattern()) || temp.matches(HistoryProcessor.TRIBE_LEAVE.pattern()))) {
                        memberUpdate = true;
                        System.out.println("Should update members");
                    }
                    newLogs += log;
                    curEntry = Long.parseLong(e.selectFirst("td").text().replaceAll("\\D+", ""));
                }

            }
            //System.out.println(newLogs + logs);
            histWriter.write(new String(newLogs.getBytes(Charset.forName("UTF-8")), Charset.forName("UTF-8")) + logs);
            props.setProperty("last_update", Long.toString(lastUpdate));
            props.store(new FileWriter("data\\info.properties"), "");
            histWriter.flush();
            histWriter.close();

            if (memberUpdate) {
                System.out.println("Updatin members");
                var pages = Scraper.scrapeMembers(Integer.toString(Info.getTId()), 1);
                PageNavigator.getPageCount(pages, "members");
                var rNumber = 1;
                for (var rank : pages.select(".cadre-utilisateur").last().getElementsByClass("rang-tribu")) {
                    titleProps.setProperty(Integer.toString(rNumber++), rank.text());
                }
                titleProps.store(new FileWriter("data/titles.properties", false), "");
                try {
                    System.out.println("Updating members");
                    PageNavigator.scrapePages("members", true);
                } catch (SAXException | ParserConfigurationException | TransformerException ex) {
                    Logger.getLogger(Updator.class.getName()).log(Level.SEVERE, null, ex);
                }
                try {
                    histProc = new HistoryProcessor();
                } catch (FileNotFoundException | ParserConfigurationException | SAXException ex) {
                    Logger.getLogger(Updator.class.getName()).log(Level.SEVERE, null, ex);
                }
                histProc.processHistory(histProc.getStoredLogs());
            }

        } catch (IOException e) {
            System.out.println(e);
        }
    }
}
