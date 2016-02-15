package fr.sims.coachingproject;

/**
 * Created by damien on 2016/2/14.
 */
public class Message {
    private String aSender;
    private String aSubject;

    public Message() {
    }

    public Message(String aSender, String aSubject) {
        this.aSender = aSender;
        this.aSubject = aSubject;
    }

    public String getSender() {
        return aSender;
    }

    public String getSubject() {
        return aSubject;
    }


    public void setaSender(String aSender) {
        this.aSender = aSender;
    }

    public void setaSubject(String aSubject) {
        this.aSubject = aSubject;
    }

}

