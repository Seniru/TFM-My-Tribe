package com.seniru.tfm_mytribe;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.Properties;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.jsoup.nodes.Document;
import org.xml.sax.SAXException;

/**
 *This class consists of methods to initialize and store tribe info
 * @author Seniru
 * 
 */
public class Installation {
    
    private Document histPage1;
    private Document members1;
    private Document home;
    private final int t_id;
    private final Properties infoProps = new Properties();
    private final Properties titleProps = new Properties();
    private boolean initialized;
    
    /**
     * Constructor
     * @param t_id
     * @throws FileNotFoundException
     * @throws IOException
     */
    public Installation(int t_id) throws FileNotFoundException, IOException {
        this.t_id = t_id;
        infoProps.load(new FileReader("data/info.properties"));
        infoProps.setProperty("id", Integer.toString(t_id));
        initialized = Info.hasInit();
        //infoProps.save(info.WRITER,"");
    }        
            
    /**
    * This method has several uses in it. First it reads the home page of the specified tribe and stores it if the app has not been installed yet. Then that stored Document is used to extract basic information of the tribe including <b>Tribe's icon</b>, <b>Tribe's name</b>, <b>Tribe's leader</b>, and <b>creation time</b>
    */
    private void readHomePage() throws IOException {
        try {
            if (!initialized) {               
                home = Scraper.scrapeHome(Integer.toString(this.t_id));
            }
        } catch (IOException io) {
            if (io.getMessage().equals("Read timed out")) {
                System.out.println("An error occured. Trying again");
                readHomePage();
            }
        } 
                infoProps.setProperty("icon", home.select(".avatar-profil > img").attr("src"));
                infoProps.setProperty("tribe_name", home.select(".cadre-tribu-nom").text());               
                infoProps.setProperty("creation_time", Long.toString(new Date(home.select(".cadre-tribu-date-creation").text().replaceAll("\\s", "").split(":")[1]).getTime()));
                infoProps.setProperty("leader", home.select(".element-bouton-profil.bouton-profil-nom.cadre-type-auteur-joueur.nom-utilisateur-scindable").text());
    }
     
    /** 
     * Get the page 1 of history logs and stores it in the field histPage1
     */
    private void getHistory1() throws IOException{      
        histPage1 = Scraper.scrapeHist(Integer.toString(Info.getTId()), 1);        
    }
    
    /** 
     * Get the page 1 of member list and stores it in the filed members1
     */
    private void getMembers1() throws IOException {
        members1 = Scraper.scrapeMembers(Integer.toString(Info.getTId()), 1);     
    }
    
    /**
     * Scrapes the titles from member list and store them in a properties file
     */
    private void getTitles() throws IOException {
        var rNumber = 1;
        for (var rank : members1.select(".cadre-utilisateur").last().getElementsByClass("rang-tribu")) {
            titleProps.setProperty(Integer.toString(rNumber++), rank.text());
        }
        titleProps.store(new FileWriter("data/titles.properties", false), "");
    }
    
    /**
     * Calls all the methods in this class to install and store the app
     * @throws java.text.ParseException
     * @throws javax.xml.parsers.ParserConfigurationException
     * @throws javax.xml.transform.TransformerException
     * @throws org.xml.sax.SAXException
     */
    public void install() throws ParseException, ParserConfigurationException, TransformerException, SAXException {
        try {
            System.out.println("Reading home page...");
            readHomePage();
            System.out.println("Home page read.\nReading page 1 of members");
            getMembers1();
            System.out.println("Reading done!\nExtracting titles...");
            getTitles();
            System.out.println("Titles extracted!\nScraping history page 1");
            PageNavigator.getPageCount(members1, "members");
            PageNavigator.scrapePages("members", true);
            getHistory1();
            PageNavigator.getPageCount(histPage1, "history");
            PageNavigator.scrapePages("history", true);
            initialized = true;
            infoProps.setProperty("initialized", "true");
            infoProps.store(new FileWriter("data/info.properties", true), "");
            System.out.println("Job done!");
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }
}
