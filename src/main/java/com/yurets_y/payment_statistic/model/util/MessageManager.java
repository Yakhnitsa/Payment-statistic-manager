package com.yurets_y.payment_statistic.model.util;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;


public interface MessageManager {

    void showMessage(Message message, Stage stage);

    ButtonType showConfirmMessage(Message message, Stage stage);

    String showInputTextMessage(Message message,Stage stage);

    void showExceptionMessage(Throwable tr, Stage stage);

}
