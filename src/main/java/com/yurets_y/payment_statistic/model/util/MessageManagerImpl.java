package com.yurets_y.payment_statistic.model.util;

import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import org.springframework.stereotype.Component;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.Optional;

/**
 * Created by Admin on 20.05.2017.
 */
@Component("messageManager")
public class MessageManagerImpl implements MessageManager {
    private static MessageManagerImpl ourInstance = new MessageManagerImpl();

    public static MessageManagerImpl getInstance() {
        return ourInstance;
    }

    private MessageManagerImpl() {
    }

    @Override
    public void showMessage(Message message, Stage stage) {
        Alert alert = new Alert(message.getAlertType());
        alert.initOwner(stage);
        alert.setTitle(message.getTitle());
        alert.setHeaderText(message.getMessage());
        alert.setContentText(message.getContentText());
        alert.showAndWait();
    }

    @Override
    public ButtonType showConfirmMessage(Message message, Stage stage) {
        Alert alert = new Alert(message.getAlertType());
        alert.setTitle(message.getTitle());
        alert.setHeaderText(message.getMessage());
        alert.setContentText(message.getContentText());
        alert.initOwner(stage);

        Optional<ButtonType> result = alert.showAndWait();
        return result.get();
    }

    @Override
    public String showInputTextMessage(Message message, Stage stage) {
        Alert alert = new Alert(message.getAlertType());
        alert.setHeaderText(message.getMessage());
        alert.setTitle(message.getTitle());
        alert.initOwner(stage);

        TextField textField = getTextField(alert);

        alert.showAndWait();
        return textField.getText();
    }

    private TextField getTextField(Alert alert) {
        Label label = new Label("Вводные данные:");

        TextField textField = new TextField();
        textField.setEditable(true);

        textField.setMaxWidth(Double.MAX_VALUE);
        textField.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textField, Priority.ALWAYS);
        GridPane.setHgrow(textField, Priority.ALWAYS);

        alert.getDialogPane().setContent(textField);
        return textField;
    }

    @Override
    public void showExceptionMessage(Throwable ex, Stage owner) {
        ex.printStackTrace();
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(ex.getMessage());
        alert.initOwner(owner);

        //Пролучение стактрейс ошибки
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        String exceptionText = sw.toString();

        Label label = new Label("The exception stacktrace was:");

        TextArea textArea = new TextArea(exceptionText);
        textArea.setEditable(false);
        textArea.setWrapText(true);

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(label, 0, 0);
        expContent.add(textArea, 0, 1);

        //Добавление панели исключения на панель ошибки
        alert.getDialogPane().setExpandableContent(expContent);

        alert.showAndWait();
    }



    private void showMessage(Stage stage, Alert.AlertType messageType, String message, String title, String contentText) {
        Alert alert = new Alert(messageType);
        alert.initOwner(stage);
        alert.setTitle(title);
        alert.setHeaderText(message);
        alert.setContentText(contentText);
        alert.showAndWait();
    }

    public void showSaveMessage(Stage stage, String message, String contentText) {
        showMessage(stage, Alert.AlertType.INFORMATION, message, "Сохранение документа", contentText);
    }

    public void showInfoMessage(Stage stage, String message, String contentText) {
        showMessage(stage, Alert.AlertType.INFORMATION, message, "Информация", contentText);
    }

    public void showLoadMessage(Stage stage, String message, String contentText) {
        showMessage(stage, Alert.AlertType.INFORMATION, message, "Загрузка документа", contentText);
    }

    public void showErrorMessage(Stage stage, String message, String contentText) {
        showMessage(stage, Alert.AlertType.ERROR, message, "Ошибка!!!", contentText);
    }

    public void showWarningMessage(Stage stage, String message, String contentText) {
        showMessage(stage, Alert.AlertType.INFORMATION, message, "Предупреждение", contentText);
    }



    public ButtonType showConfirmMessage(Stage stage, String message, String tytle, String contentText) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(tytle);
        alert.setHeaderText(message);
        alert.setContentText(contentText);
        alert.initOwner(stage);

        Optional<ButtonType> result = alert.showAndWait();
        return result.get();
    }

    public void showDetailedMessage(String title, List<String> detailes, Stage owner) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setHeaderText(title);
        alert.initOwner(owner);

        String information = "";

        for(String s:detailes){
            information += s + System.lineSeparator();
        }

        Label label = new Label("Детали:");

        TextArea textArea = new TextArea(information);
        textArea.setEditable(false);
        textArea.setWrapText(true);

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(label, 0, 0);
        expContent.add(textArea, 0, 1);

        //Добавление панели исключения на панель ошибки
        alert.getDialogPane().setExpandableContent(expContent);

        alert.showAndWait();
    }

    public String showInputTextMessage(String title, Stage owner) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setHeaderText(title);
        alert.initOwner(owner);

        TextField textField = getTextField(alert);

        alert.showAndWait();
        return textField.getText();
    }
}
