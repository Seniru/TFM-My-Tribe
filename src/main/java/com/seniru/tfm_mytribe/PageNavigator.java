package com.seniru.tfm_mytribe;

import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.jsoup.nodes.Document;
import org.xml.sax.SAXException;

/**
 * This class provides convenient methods for scraping data from page 1 to the
 * end of the pages
 *
 * @author Seniru
 */
public class PageNavigator {

    private static int currentPage;
    private static int pages_hist;
    private static int pages_members;
    private static MemberXMLManager.Writer memWriter = null;

    /**
     * Sets the number of pages of history or members to the corresponding
     * variable
     *
     * @param html a {@linkplain Document} element containing scraped data of
     * the relevant page.
     * @param type a {@linkplain String} that mentions the type of action.
     * <br>If it is history it will stores the number of pages to the variable
     * pages_hist
     * <br>Else if it is members it will stores the number of pages to the
     * variable pages_members
     */
    public static void getPageCount(Document html, String type) {
        try {
            switch (type) {
                case "history":
                    PageNavigator.pages_hist = Integer.parseInt(html.selectFirst(".cadre-pagination > a").text().replaceAll("\\s+", "").split("/")[1]);
                    break;
                case "members":
                    PageNavigator.pages_members = Integer.parseInt(html.selectFirst(".cadre-pagination > a").text().replaceAll("\\s+", "").split("/")[1]);
                    break;
            }
        } catch (NullPointerException n) {
            if (type.equals("history")) {
                PageNavigator.pages_hist = 1;
            } else {
                PageNavigator.pages_members = 1;
            }
        }

    }

    /**
     * Scrapes the pages from page 1 until the end of the pages
     *
     * @param type a {@linkplain String} that mentions the type of action.
     * Possible values are history and members
     * @param record a {@linkplain Boolean} value. If it is true it will save
     * the scraped values into relevant files.
     * @throws IOException
     * @throws SAXException
     * @throws ParserConfigurationException
     * @throws TransformerException
     */
    public static void scrapePages(String type, boolean record) throws IOException, SAXException, ParserConfigurationException, TransformerException {
        var recorder = new EntryRecorder();
        currentPage = 1;
        while (currentPage <= (type.equals("history") ? pages_hist : pages_members)) {
            if (type.equals("history")) {
                System.out.println("Pages: " + currentPage);
                if (record) {
                    recorder.recordHist(Scraper.scrapeHist(Integer.toString(Info.getTId()), currentPage++).select(".cadre.cadre-tribu > table > tbody"), false);
                } 
            } else {
                if (memWriter == null) memWriter = new MemberXMLManager().new Writer();
                if (record) {
                    memWriter.clearMembers();
                    memWriter.save();
                    //memWriter = new MemberXMLManager().new Writer();
                    recorder.reinit();
                    recorder.recordMembers(Scraper.scrapeMembers(Integer.toString(Info.getTId()), currentPage++).select("#corps > .row > .span9 > .row"), record);
                    
                }

            }
        }
    }

}
