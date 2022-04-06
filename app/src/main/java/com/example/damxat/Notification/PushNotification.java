package com.example.damxat.Notification;

public class PushNotification {
    String to;
    NotificationModel notification;

    public PushNotification(String to, NotificationModel notification) {
        this.to = to;
        this.notification = notification;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public NotificationModel getNotification() {
        return notification;
    }

    public void setNotification(NotificationModel notification) {
        this.notification = notification;
    }
}
