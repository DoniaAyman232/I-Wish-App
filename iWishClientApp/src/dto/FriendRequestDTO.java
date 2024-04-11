/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dto;

import java.sql.Date;

/**
 *
 * @author ahmed
 */
public class FriendRequestDTO {
    private String sender_email ;
    private String receiver_email ;
    private java.sql.Date requestDate;
    boolean is_accepted;

    // Constructors, getters, and setters

    // Example constructors (you may need to create more)

    public FriendRequestDTO(String sender_email, String receiver_email, Date requestDate, boolean is_accepted) {
        this.sender_email = sender_email;
        this.receiver_email = receiver_email;
        this.requestDate = requestDate;
        this.is_accepted = is_accepted;
    }

    public String getSender_email() {
        return sender_email;
    }

    public void setSender_email(String sender_email) {
        this.sender_email = sender_email;
    }

    public FriendRequestDTO() {
    }

    public String getReceiver_email() {
        return receiver_email;
    }

    public void setReceiver_email(String receiver_email) {
        this.receiver_email = receiver_email;
    }

    public Date getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(Date requestDate) {
        this.requestDate = requestDate;
    }

    public boolean isIs_accepted() {
        return is_accepted;
    }

    public void setIs_accepted(boolean is_accepted) {
        this.is_accepted = is_accepted;
    }
   
}
