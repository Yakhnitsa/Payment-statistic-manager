package com.yurets_y.payment_statistic.model.util;

import javafx.scene.control.Alert;

public class Message{
    private Alert.AlertType alertType;
    private String title;
    private String message;
    private String contentText;

    public Alert.AlertType getAlertType() {
        return alertType;
    }

    public void setAlertType(Alert.AlertType alertType) {
        this.alertType = alertType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getContentText() {
        return contentText;
    }

    public void setContentText(String contentText) {
        this.contentText = contentText;
    }
}