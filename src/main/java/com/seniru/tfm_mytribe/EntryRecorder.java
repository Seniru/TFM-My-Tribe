package com.seniru.tfm_mytribe;

import java.io.FileWriter;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.jsoup.select.Elements;
import org.xml.sax.SAXException;

/**
 * This class provide convenient methods to record formatted entries recorded
 * from the forum.
 *
 * @author Seniru
 *
 */
public class EntryRecorder {

    private static FileWriter histWriter = null;
    private static MemberXMLManager.Writer member;

    /**
     * Constructor for EntryRecorder
     *
     * @throws IOException
     * @throws SAXException
     * @throws ParserConfigurationException
     *
     */
    public EntryRecorder() throws IOException, SAXException, ParserConfigurationException {
         member = new MemberXMLManager().new Writer();
    }

    /**
     * Writes history logs using the scraped pages. All the logs are prefixed by
     * the time in milliseconds format. If the argument <u>append</u> is true,
     * it will append the logs to the file. Otherwise it will rewrite the logs
     *
     * @param page
     * @param append
     * @throws IOException
     */
    public void recordHist(Elements page, boolean append) throws IOException {
        if (!append) {
            histWriter.write("");
        }
        if (histWriter == null) histWriter = new FileWriter("data\\history_logs.txt");
        for (var entry : page) {
            entry.select("tr").forEach((e) -> {
                try {
                    var k = Long.parseLong(e.selectFirst("td").text().replaceAll("\\D+", ""));
                    var v = e.select("td").get(1).text();
                    histWriter.append(k + ":" + v + "\n");
                } catch (IOException ex) {
                    System.out.println("error:" + ex);
                }
            });
            break;
        }
        histWriter.flush();
        histWriter.close();
    }

    /**
     * Writes the members using the scraped pages of the forum. Collects
     * information like name, gender, rank, rank, etc. and store them with the
     * help of {@linkplain tfm_tribe.MemberXMLManager MemberXMLManager}
     *
     * @param page
     * @param append
     * @throws ParserConfigurationException
     * @throws TransformerException
     */
    public void recordMembers(Elements page, boolean append) throws ParserConfigurationException, TransformerException {       
        for (var entry : page) {
            var gender = "none";
            for (var img : entry.select("img")) {
                if (img.attr("src").equals("/img/icones/garcon.png")) {
                    gender = "male";
                } else if (img.attr("src").equals("/img/icones/fille.png")) {
                    gender = "female";
                }
            }
            member.appendData(entry.select(".cadre-ami-nom.cadre-type-auteur-joueur").text().replaceAll("\\s+", ""), gender, entry.select(".rang-tribu").text(), entry.select("img.default-avatar-50").attr("src"));
        }
        member.save();
    }
    
    public void reinit() {
        member.reinit();
    }
}
