package com.example.ai;

public class Message {
    public static String SENT_BY_ME = "me";
    public static String SENT_BY_BOT = "bot";

    private String massage;
    private String sendby;
    private String timestamp; // ✅ Added timestamp

    public Message(String massage, String sendby, String timestamp) {
        this.massage = massage;
        this.sendby = sendby;
        this.timestamp = timestamp;
    }

    public String getMassage() {
        return massage;
    }

    public void setMassage(String massage) {
        this.massage = massage;
    }

    public String getSendby() {
        return sendby;
    }

    public void setSendby(String sendby) {
        this.sendby = sendby;
    }

    public String getTimestamp() { // ✅ Getter for timestamp
        return timestamp;
    }

    public void setTimestamp(String timestamp) { // ✅ Setter for timestamp
        this.timestamp = timestamp;
    }
}
