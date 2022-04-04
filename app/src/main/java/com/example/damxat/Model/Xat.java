package com.example.damxat.Model;

/* Classe xat */
public class Xat {

    String sender;
    String receiver;
    String message;

    public Xat(String sender, String message) {
        this.sender = sender;
        this.message = message;
    }


    public Xat() {

    }


    public Xat(String sender, String receiver, String message) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
    }

    public String getSender() {
        return sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public String getMessage() {
        return message;
    }
}
