package com.seniru.tfm_mytribe;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * A class that provides basic methods to scrape pages
 *
 * @author Seniru
 */
class Scraper {

    //initializing constants
    private static final String H_LOG_URL = "https://atelier801.com/tribe-history?tr=";
    private static final String MEMBER_URL = "https://atelier801.com/tribe-members?tr=";
    private static final String HOME_URL = "https://atelier801.com/tribe?tr=";

    /**
     * Scrapes the history page of the given tribe
     *
     * @param t_id specifies the tribe's id
     * @param page the page that should be scraped
     */
    static Document scrapeHist(String t_id, int page) throws IOException {
        return Jsoup.connect(H_LOG_URL + t_id + "&p=" + page).get();
    }

    /**
     * Scrapes the member page of the given tribe
     *
     * @param t_id specifies the tribe's id
     * @param page the page that should be scraped
     */
    static Document scrapeMembers(String t_id, int page) throws IOException {
        return Jsoup.connect(MEMBER_URL + t_id + "&p=" + page).get();
    }

    /**
     * Scrapes the home page of the given tribe
     *
     * @param t_id specifies the tribe's id
     */
    static Document scrapeHome(String t_id) throws IOException {
        return Jsoup.connect(HOME_URL + t_id).get();
    }
}
