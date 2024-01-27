package com.example.ai;

public class Message {
        public static String SENT_BY_ME="me";
        public static String SENT_BY_BOT="bot";

        String massage;
        String sendby;

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

        public Message(String massage, String sendby) {
            this.massage = massage;
            this.sendby = sendby;
        }

    }
