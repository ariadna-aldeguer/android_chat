package com.example.damxat.Model;

/* Classe user */
public class User {
    String id;
    String username;
    String status;
    String token;

    public User(String id, String username, String status) {
        this.id = id;
        this.username = username;
    }

    public User() {
    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getStatus(){
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getToken() {
        return token;
    }
}
