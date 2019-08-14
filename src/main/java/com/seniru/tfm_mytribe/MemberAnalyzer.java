package com.seniru.tfm_mytribe;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.TreeMap;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

/**
 * This class provides useful methods for counting, sorting, categorizing and
 * searching members
 *
 * @author Seniru
 */
public final class MemberAnalyzer {

    private final Properties titleData = new Properties();
    public TreeMap<String, Member> memberData;
    private final ArrayList<String> titles;

    /**
     * Constructor
     *
     * @throws FileNotFoundException
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws ParseException
     */
    public MemberAnalyzer() throws FileNotFoundException, IOException, ParserConfigurationException, SAXException, ParseException, Exception {
        titleData.load(new FileReader("data/titles.properties"));
        titles = new ArrayList(Arrays.asList(getTitles()));
        memberData = new MemberXMLManager().new Reader().getMemberData();
    }

    public String[] getTitles() {
        var t = new String[titleData.size()];
        titleData.forEach((x, y) -> {
            t[Integer.parseInt(x.toString()) - 1] = y.toString();
        });
        return t;
    }

    /**
     *
     * @return the total number of members
     */
    public int getMemberLength() {
        return this.memberData.size();
    }

    /**
     * This method sorts the members according to their names in either
     * ascending or descending order
     *
     * @param tree {@linkplain java.util.TreeMap TreeMap} containing mapped
     * pairs of name and {@linkplain tfm_tribe.Member Member} objects.
     * @param asc boolean value indicating the way of sorting names. If it is
     * true sorts the names according to the ascending order. Otherwise to the
     * descending order.
     * @return an {@linkplain ArrayList} containing sorted members
     * @see #sortNames(java.util.ArrayList, boolean)
     */
    public ArrayList<Member> sortNames(TreeMap<String, Member> tree, boolean asc) {
        var mem = new ArrayList<Member>();
        tree.values().forEach(mem::add);
        return sortNames(mem, asc);
    }

    /**
     * This method sorts the members according to their names in either
     * ascending or descending order
     *
     * @param members {@linkplain ArrayList} containing {@linkplain Member}
     * objects
     * @param asc boolean value indicating the way of sorting names. If it is
     * true sorts the names according to the ascending order. Otherwise to the
     * descending order.
     * @return an {@linkplain  ArrayList} containing sorted members
     * @see #sortNames(java.util.TreeMap, boolean)
     */
    public ArrayList<Member> sortNames(ArrayList<Member> members, boolean asc) {
        members.sort((o1, o2) -> {
            if (asc) {
                return o1.name.compareTo(o2.name);
            }
            return o2.name.compareTo(o1.name);
        });
        return members;
    }

    /**
     * This method sorts the members according to their title in the tribe in
     * either ascending or descending order
     *
     * @param tree {@linkplain ArrayList} containing mappings of name and
     * {@linkplain Member} objects
     * @param asc boolean value indicating the way of sorting names. If it is
     * true sorts from the least rank to the highest rank and vice-versa
     * @return an {@linkplain  ArrayList} containing sorted members
     * @see #sortTitles(java.util.ArrayList, boolean)
     */
    public ArrayList<Member> sortTitles(TreeMap<String, Member> tree, boolean asc) {
        var mem = new ArrayList<Member>();
        tree.values().forEach(mem::add);
        return sortTitles(mem, asc);
    }

    /**
     * This method sorts the members according to their title in the tribe in
     * either ascending or descending order
     *
     * @param members {@linkplain ArrayList} containing {@linkplain Member}
     * objects
     * @param asc boolean value indicating the way of sorting names. If it is
     * true sorts from the least rank to the highest rank and vice-versa
     * @return an {@linkplain  ArrayList} containing sorted members
     * @see #sortTitles(java.util.TreeMap, boolean)
     */
    public ArrayList<Member> sortTitles(ArrayList<Member> members, boolean asc) {
        members.sort((o1, o2) -> {
            if (!asc) {
                return titles.indexOf(o2.getTitle()) - titles.indexOf(o1.getTitle());
            }
            return titles.indexOf(o1.getTitle()) - titles.indexOf(o2.getTitle());
        });
        return members;
    }

    /**
     * This method sorts members according to their date of join
     *
     * @param tree {@linkplain ArrayList} containing mappings of name and
     * {@linkplain Member} objects
     * @param asc boolean value indicating the way of sorting names. If it is
     * true sorts from the earliest member to the latest member and vice-versa
     * @return an {@linkplain  ArrayList} containing sorted members
     * @see #sortDays(java.util.ArrayList, boolean)
     */
    public ArrayList<Member> sortDays(TreeMap<String, Member> tree, boolean asc) {
        var mem = new ArrayList<Member>();
        tree.values().forEach(mem::add);
        return sortDays(mem, asc);
    }

    /**
     * This method sorts members according to their date of join
     *
     * @param members {@linkplain ArrayList} containing {@linkplain Member}
     * objects
     * @param asc boolean value indicating the way of sorting names. If it is
     * true sorts from the earliest member to the latest member and vice-versa
     * @return an {@linkplain  ArrayList} containing sorted members
     * @see #sortDays(java.util.TreeMap, boolean)
     */
    public ArrayList<Member> sortDays(ArrayList<Member> members, boolean asc) {
        members.sort((o1, o2) -> {
            if (asc) {
                return o1.getJoinDate().compareTo(o2.getJoinDate());
            }
            return o2.getJoinDate().compareTo(o1.getJoinDate());
        });
        return members;
    }

    /**
     * This methods returns a portion of members which satisfies the gender list
     * provided
     *
     * @param tree {@linkplain TreeMap} containing mappings of name and
     * {@linkplain Member} objects
     * @param genders an array of {@linkplain String} containing genders
     * @return an {@linkplain  ArrayList} containing {@linkplain Member} objects
     * which satisfies the gender provided
     * @see #getAccordingToGenders(java.util.ArrayList, java.lang.String...)
     */
    public ArrayList<Member> getAccordingToGenders(TreeMap<String, Member> tree, String... genders) {
        var mem = new ArrayList<Member>();
        tree.values().forEach(mem::add);
        return getAccordingToGenders(mem, genders);
    }

    /**
     * This methods returns a portion of members which satisfies the gender list
     * provided
     *
     * @param member {@linkplain ArrayList} containing {@linkplain Member}
     * objects
     * @param genders an array of {@linkplain String} containing genders
     * @return an {@linkplain  ArrayList} containing {@linkplain Member} objects
     * which satisfies the gender provided
     * @see #getAccordingToGenders(java.util.TreeMap, java.lang.String...)
     */
    public ArrayList<Member> getAccordingToGenders(ArrayList<Member> member, String... genders) {
        var members = new ArrayList<Member>();
        var genderList = Arrays.asList(genders);
        member.forEach((v) -> {
            if (genderList.contains(v.getGender())) {
                members.add(v);
            }
        });
        return members;
    }

    /**
     * This methods returns a portion of members which satisfies the titles
     * provided
     *
     * @param tree {@linkplain TreeMap} containing mappings of name and
     * {@linkplain Member} objects
     * @param titles an array of {@linkplain String} containing titles
     * @return an {@linkplain  ArrayList} containing {@linkplain Member} objects
     * which satisfies the titles provided
     * @see #getAccordingToTitles(java.util.ArrayList, java.lang.String...)
     */
    public ArrayList<Member> getAccordingToTitles(TreeMap<String, Member> tree, String... titles) {
        var mem = new ArrayList<Member>();
        tree.values().forEach(mem::add);
        return getAccordingToTitles(mem, titles);
    }

    /**
     * This methods returns a portion of members which satisfies the titles list
     * provided
     *
     * @param member {@linkplain ArrayList} containing {@linkplain Member}
     * objects
     * @param titles an array of {@linkplain String} containing titles
     * @return an {@linkplain  ArrayList} containing {@linkplain Member} objects
     * which satisfies the titles provided
     * @see #getAccordingToTitles(java.util.TreeMap, java.lang.String...)
     */
    public ArrayList<Member> getAccordingToTitles(ArrayList<Member> member, String... titles) {
        var members = new ArrayList<Member>();
        var titleList = Arrays.asList(titles);
        member.forEach(v -> {
            if (titleList.contains(v.getTitle())) {
                members.add(v);
            }
        });
        return members;
    }

    /**
     * This method can be used to search members according to their name and
     * hashtag. The order of the member's name is determined using a scoring
     * system. This will return null if the arguments contains spaces
     *
     * @param query {@linkplain String} containing the name of members
     * @param hashtag {@linkplain String} containing the hashtag
     * @return a portion of members that includes the provided parameters in their names
     */
    public ArrayList<Member> searchMembers(String query, String hashtag) throws IllegalArgumentException {
        if (query.contains(" ") || hashtag.contains(" ")) {
            throw new IllegalArgumentException("Spaces are not allowed!");
        } 
        if (query.equals("")) return new ArrayList<>(this.memberData.values());
        
        var mem = new ArrayList<Member>();
        var scores = new HashMap<Member, Integer>();

        this.memberData.forEach((k, v) -> {
            scores.put(v, 0);
            if (k.toLowerCase().contains(query.toLowerCase())) {
                //Arrays.stream(k.split("")).filter(x -> x.);
                for (var qL : query.split("")) {
                    for (var kVal : k.split("")) {                       
                        if (kVal.equals("#")) {
                            break;
                        }
                        if (qL.toLowerCase().equals(kVal.toLowerCase())) {
                            scores.replace(v, scores.get(v) + 1);
                        }
                    }
                }
            }
            if (k.contains(hashtag)) {
                for (var hash : hashtag.split("")) {
                    for (var kVal : k.split("")) {
                        if (hash.toLowerCase().equals(kVal.toLowerCase())) {
                            scores.replace(v, scores.get(v) + 1);
                        }
                    }
                }
            }
        });

        scores.entrySet().stream().sorted(Entry.comparingByValue()).filter(x -> !x.getValue().equals(0)).forEachOrdered(x -> mem.add(x.getKey()));
        Collections.reverse(mem);
        return mem;
    }

}
