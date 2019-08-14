package com.seniru.tfm_mytribe;

import java.util.Date;

/**
 *  This class provides a convenient way to handle members
 * @author Seniru
*/
public class Member {
    
    /**
     * The name of the member
     */
    public String name;
    private String title;
    private Date joinDate;
    private String pfp = "";
    private String gender;
    
    /**
     * Constructor
     * @param name the name of the member
     */
    public Member(String name) {       
        this.name = name;
    }
    
    /**
     * Sets the title of the member
     * @param title the title to be set
     */
    public void setTitle(String title) {
        this.title = title;
    }
    
    /**
     * Sets the "date of join" property of the member
     * @param joinDate the data of joining
     */
    public void setJoinDate(Date joinDate) {
        this.joinDate = joinDate;
    }
    
    /**
     * sets the profile picture of the member
     * @param pfp the profile picture
     */
    public void setPfp(String pfp) {
        this.pfp = pfp;
    }
    
    /**
     * Sets the gender of the member.The gender should be one of male, female or none
     * @param g the gender of the member
     * @throws java.lang.Exception if the gender is not valid
     */
    public void setGender(String g) throws Exception {
        if (g.matches("(male|female|none)")) {
            this.gender = g;
        } else {
            throw new Exception("Invalid gender type");
        }
    }
    
    /**
     *
     * @return the title
     */
    public String getTitle() {
        return this.title;
    }
    
    /**
     *
     * @return the "date of join"
     */
    public Date getJoinDate() {
        return this.joinDate;
    }
    
    /**
     *
     * @return the profile picture
     */
    public String getPfp() {
        return this.pfp;
    }
    
    /**
     *
     * @return the gender
     */
    public String getGender() {
        return this.gender;
    }
    
    /**
     *
     * @return an array of the member containing values such as the name, gender, title, date of join and profile picture
     */
    public String[] toArray() {
        return new String[] {this.name, this.gender, this.title, this.joinDate.toString(), this.pfp};
    }
      
    @Override
    public String toString() {
        return this.name + "[ " + this.title + ", " + this.joinDate + ", " + this.gender + " ]";
    }
}
