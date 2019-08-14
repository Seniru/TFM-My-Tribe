package com.seniru.tfm_mytribe;

import java.io.IOException;
import java.text.ParseException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.xml.sax.SAXException;

/**
 * The main class of the application
 *
 * @author Seniru
 */
public class Main {

    private static Installation install;

    /**
     * main method
     *
     * @param args
     * @throws ParseException
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws TransformerException
     * @throws SAXException
     */
    public static void main(String[] args) throws ParseException, IOException, ParserConfigurationException, TransformerException, SAXException, Exception {

        System.out.println("Reading Info...");
        Info.init();
        System.out.println("Done!");
        if (!Info.hasInit()) {
            System.out.print("App has not been initialized. \n Please enter your tribe id: ");
            install = new Installation(new java.util.Scanner(System.in).nextInt());
            install.install();
        } else {
            var connection = java.lang.Runtime.getRuntime().exec("ping atelier801.com").waitFor();
            if (connection == 0) {
                System.out.println("Connection Successful, ");
                System.out.println("Updating!");
                var updator = new Updator();
                updator.updateHist();
            } else {
                System.out.println("Internet Not Connected, ");
            }
            System.out.println("App has been initialized. \n Loading the GUI...");            
           new GUI().setVisible(true);
            System.out.println("GUI Loaded!");

        }
    }

}
