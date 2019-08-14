package com.seniru.tfm_mytribe;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

;

/**
 * A class that provides read/write facilities to XML containing member data
 *
 * @author Seniru
 */
public class MemberXMLManager {

    private final DocumentBuilderFactory docFactory;
    private final DocumentBuilder docBuilder;
    private Document doc;
    private Node parent;

    /**
     * Constructor
     *
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     */
    public MemberXMLManager() throws IOException, SAXException, ParserConfigurationException {
        docFactory = DocumentBuilderFactory.newInstance();
        docBuilder = docFactory.newDocumentBuilder();
        doc = docBuilder.parse(new File("data/members.xml"));
    }

    /**
     * An inner class of the {@linkplain MemberXMLManager}. Provides methods for
     * writing, appending, setting and save properties of members
     */
    public class Writer {

        /**
         * Constructor
         */
        public Writer() {
            parent = doc.getFirstChild();
        }

        /**
         * This method can be used to append member data containing other
         * information at once
         *
         * @param name the name of the member. This will be referred by an
         * attribute named 'name' in members.xml
         * @param gender the gender of the member (one of male, female and none)
         * @param rank the rank of the member
         * @param pfp the profile picture of the member
         * @throws ParserConfigurationException
         */
        public void appendData(String name, String gender, String rank, String pfp) throws ParserConfigurationException {
            var member = doc.createElement("member");
            member.setAttribute("name", name);

            var genderTag = doc.createElement("gender");
            genderTag.appendChild(doc.createTextNode(gender));

            var rankTag = doc.createElement("rank");
            rankTag.appendChild(doc.createTextNode(rank));

            var pfpTag = doc.createElement("Profile_picture");
            pfpTag.appendChild(doc.createTextNode(pfp));

            member.appendChild(genderTag);
            member.appendChild(rankTag);
            member.appendChild(pfpTag);

            parent.appendChild(member);

        }

        public void clearMembers() throws ParserConfigurationException {
            try {
                while (parent.hasChildNodes()) {
                    parent.removeChild(parent.getFirstChild());
                }
                save();
                reinit();
               
            } catch (TransformerException ex) {
                Logger.getLogger(MemberXMLManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        public void reinit() {
            try {
                doc = docBuilder.parse(new File("data\\members.xml"));
                parent = doc.getFirstChild();
            } catch (SAXException | IOException ex) {
                Logger.getLogger(MemberXMLManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        /**
         * Adds a new property to the members. A new child node will be created
         * if there wasn't one before. Otherwise it will alter that value of the
         * existing child node
         *
         * @param owner the owner (or the name of the member) that the property
         * should be added
         * @param property the property
         * @param value the value corresponding to the property
         * @throws TransformerException
         */
        public void addProperty(String owner, String property, String value) throws TransformerException {
            var childs = doc.getElementsByTagName("member");
            var propertyExists = false;
            for (var m = 0; m < childs.getLength(); m++) {
                if (childs.item(m).getAttributes().getNamedItem("name").getTextContent().equals(owner)) {
                    var tags = childs.item(m).getChildNodes();
                    for (var child = 0; child < tags.getLength(); child++) {
                        if (tags.item(child).getNodeName().equals(property)) {
                            propertyExists = true;
                            tags.item(child).setTextContent(value);
                        }
                    }
                    if (!propertyExists) {
                        var tag = doc.createElement(property);
                        tag.setTextContent(value);
                        childs.item(m).appendChild(tag);
                    }
                }
                save();

            }
        }

        /**
         * Saves the written data to the member.xml file
         *
         * @throws TransformerException
         */
        public void save() throws TransformerException {
            var transformerFactory = TransformerFactory.newInstance();
            var transformer = transformerFactory.newTransformer();
            var source = new DOMSource(doc);
            var result = new StreamResult(new File("data/members.xml"));
            transformer.transform(source, result);
        }
    }

    /**
     * An inner class of {@linkplain MemberXMLManager} that contains various
     * methods for reading member data
     */
    public class Reader {

        /**
         *
         * @return a {@linkplain TreeMap} containing mapping of member names and
         * {@linkplain Member} objects
         * @throws ParseException
         * @throws Exception
         */
        public TreeMap<String, Member> getMemberData() throws ParseException, Exception {

            var members = new TreeMap<String, Member>();
            var mList = doc.getElementsByTagName("member");

            for (var m = 0; m < mList.getLength(); m++) {
                //extracting the attribute name from the tag
                var name = mList.item(m).getAttributes().getNamedItem("name").getTextContent();
                var gender = "";
                var title = "";
                var joinDate = "";
                var pfp = "";
                var props = mList.item(m).getChildNodes();

                for (var i = 0; i < props.getLength(); i++) {
                    switch (props.item(i).getNodeName()) {
                        case "gender":
                            gender = props.item(i).getTextContent();
                            break;
                        case "rank":
                            title = props.item(i).getTextContent();
                            break;
                        case "join":
                            joinDate = props.item(i).getTextContent();
                            break;
                        case "Profile_picture":
                            pfp = props.item(i).getTextContent();
                            break;
                    }
                }

                var member = new Member(name);
                member.setGender(gender);
                member.setTitle(title);
                member.setJoinDate(DateUtils.milliToDate(joinDate));
                member.setPfp(pfp);
                members.put(name, member);
            }

            return members;
        }
    }
}
