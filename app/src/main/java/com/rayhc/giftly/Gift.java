package com.rayhc.giftly;

import java.util.HashMap;

/**
 * Gift objects to be stored in the database
 * These get configured to JSON objects in the database
 *
 * Will look like this in the DB:
 * gifts:
 * - *user id of recipient*
 * - - *all of the gift data*
 *
 * Gifts will go through "validation" as follows:
 * 1. the gift being opened was actually sent to the user (matching user id of recipient)
 * 2. double checking the sender & recipient ID's match
 */
public class Gift {
    //attributes
//    private String id;                                  //synonymous to pin
    private String link;                                //nulled out if not sending a link
    private HashMap<String, String> contentType;        /*i think it'll be something like "1" is a link,
                                                        "2" is a multimedia file, etc.
                                                        Need to be a map of strings (but will hold ints) for sending multiple
                                                        "gifts" (many images, some images and some videos, etc.) in one gift object*/

    private HashMap<String, String> giftType;           //follows the same principle as contentType
    private String sender;                              //user id of the gift's sender from firebase authentication
    private String receiver;                            //user id of the gift's sender from firebase authentication
    private boolean isEncrypted;
    private String hashValue;
    private String qrCode;
    private boolean opened;                             //look at this value when opening the app + unopened gift page

    // comment by ray: :)
    // comment by uhuru (:

    /**
     * Default Constructor
     */
    public Gift(){}

    /**
     * Value constructor
     */
    public Gift(String link, HashMap<String, String> contentType, HashMap<String, String> giftType,
                String sender, String receiver, boolean isEncrypted, String hashValue, String qrCode, boolean opened){
        this.link = link;
        this.contentType = contentType;
        this.giftType = giftType;
        this.sender = sender;
        this.receiver = receiver;
        this.isEncrypted = isEncrypted;
        this.hashValue = hashValue;
        this.qrCode = qrCode;
        this.opened = opened;
    }

    //TODO: Probably need an intermediate constructor that doesn't take every parameter?
    //RESOLVED: just use intermediate setters as the gift is being built up

    /**
     * Getters & Setters
     */
//    public String getId() {
//        return id;
//    }

    public String getLink() {
        return link;
    }

    public HashMap<String, String> getContentType() {
        return contentType;
    }

    public HashMap<String, String> getGiftType() {
        return giftType;
    }

    public String getSender() {
        return sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public boolean isEncrypted() {
        return isEncrypted;
    }

    public boolean isOpened() {
        return opened;
    }

    public String getQrCode() {
        return qrCode;
    }

    public String getHashValue() {
        return hashValue;
    }

//    public void setId(String id) {
//        this.id = id;
//    }

    public void setLink(String link) {
        this.link = link;
    }

    public void setContentType(HashMap<String, String> contentType) {
        this.contentType = contentType;
    }

    public void setGiftType(HashMap<String, String> giftType) {
        this.giftType = giftType;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public void setEncrypted(boolean encrypted) {
        isEncrypted = encrypted;
    }

    public void setHashValue(String hashValue) {
        this.hashValue = hashValue;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public void setOpened(boolean opened) {
        this.opened = opened;
    }

    //for testing purposes
    @Override
    public String toString() {
        return "Gift From " + sender;
        /*return "Gift{" +
                "id='" + id + '\'' +
                ", link='" + link + '\'' +
                ", file=" + file +
                ", contentType=" + contentType +
                ", giftType=" + giftType +
                ", sender='" + sender + '\'' +
                ", receiver='" + receiver + '\'' +
                ", isEncrypted=" + isEncrypted +
                ", hashValue='" + hashValue + '\'' +
                ", qrCode='" + qrCode + '\'' +
                ", opened=" + opened +
                '}';*/
    }
}
