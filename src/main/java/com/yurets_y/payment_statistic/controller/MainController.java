package com.yurets_y.payment_statistic.controller;

import com.yurets_y.payment_statistic.model.elements.PaymentList;
import com.yurets_y.payment_statistic.model.parser.DocumentParser;
import com.yurets_y.payment_statistic.model.saver.DocumentSaver;
import com.yurets_y.payment_statistic.model.util.*;
import javafx.fxml.FXML;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

/**
 * Created by Admin on 11.04.2018.
 */
@Component
public class MainController implements RootPanel {

    private Stage primaryStage;

    private BorderPane rootLayout;

    private DocumentParser docParserBase;
    private DocumentParser docParserMail;
    private DocumentParser docParserXML;

    private DocumentSaver documentSaver;

    private TableController docController;

    @Resource(name = "messageManager")
    private MessageManager messageManager;

    @Resource(name="mainControllerMessages")
    private Map<String, Message> messagesController;



    @FXML
    public void addURI() {
        Message inputTextMessage = messagesController.get("loadURImessage");
        String uri = messageManager.showInputTextMessage(inputTextMessage,primaryStage);
        if ((uri == null) || uri.equals("")) {
            return;
        }
        try {
            PaymentList list = docParserBase.parseFromURI(uri);
            if (list.getListNumber() == 0) {
                list = docParserMail.parseFromURI(uri);
            }
            list.setFile(new File(java.net.URI.create(uri)));
            docController.getDocuments().add(list);
        } catch (Exception e) {
            e.printStackTrace();
            Message error = messagesController.get("loadURIExceptionMessage");
            error.setContentText(e.getMessage());
            messageManager.showMessage(error,primaryStage);
        }
    }

    @FXML
    public void addFiles() {
        List<File> files = FilesManager.getHTMLFilesToLoad(primaryStage);
        if ((files == null) || (files.size() <= 0)) {
            return;
        }
        int filesCount = 0;
        for (File file : files) {
            try {
                PaymentList list = docParserBase.parseFromFile(file);
                //Изменение способа разбора в случаи ошибки.
                if (list.getListNumber() == 0) {
                    list = docParserMail.parseFromFile(file);
                    list.setFile(file);
                }
                docController.getDocuments().add(list);
                filesCount++;
            } catch (IOException e) {
                e.printStackTrace();
                MessageManagerImpl.getInstance().showErrorMessage(primaryStage,
                        "Ошибка чтения файла",
                        "Не удалось прочитать файл: " + file
                );
            }
        }
        MessageManagerImpl.getInstance().showInfoMessage(primaryStage,
                "Документы успешно загружены",
                "Успешно загружено файлов: " + filesCount);
    }

    @FXML
    public void addXmlFiles() {
        List<File> files = FilesManager.getXMLFilesToLoad(primaryStage);
        if ((files == null) || (files.size() <= 0)) {
            return;
        }
        int filesCount = 0;
        for (File file : files) {
            try {
                PaymentList list = docParserXML.parseFromFile(file);
                //Изменение способа разбора в случаи ошибки.
                if (list.getListNumber() == 0) {
                    list = docParserMail.parseFromFile(file);
                    list.setFile(file);
                }
                docController.getDocuments().add(list);
                filesCount++;
            } catch (IOException e) {
                e.printStackTrace();
                MessageManagerImpl.getInstance().showErrorMessage(primaryStage,
                        "Ошибка чтения файла",
                        "Не удалось прочитать файл: " + file
                );
            }
        }
        MessageManagerImpl.getInstance().showInfoMessage(primaryStage,
                "Документы успешно загружены",
                "Успешно загружено файлов: " + filesCount);
    }

    @FXML
    public void saveSelectedDocuments() {
        File file = FilesManager.getXLSFileToSave(primaryStage);
        if ((file == null)) {
            MessageManagerImpl.getInstance().showWarningMessage(primaryStage,
                    "Документы не сохранены", "");
            return;
        }
        List<PaymentList> paymentLists = docController.getDocuments();

        if (saveDocuments(file, paymentLists)) return;

        saveBackupDocuments(paymentLists);
    }

    @FXML
    public void saveAllDocuments() {
        File file = FilesManager.getXLSFileToSave(primaryStage);
        if ((file == null)) {
            MessageManagerImpl.getInstance().showWarningMessage(primaryStage,
                    "Документы не сохранены", "");
            return;
        }
        List<PaymentList> list = docController.getDocuments();


        if (saveDocuments(file, list)) return;

        saveBackupDocuments(list);
    }

    @FXML
    public void deleteSelected() {


        docController.getDocuments().removeAll(docController.getSelectedDocuments());
        if(!docController.deleteSelectedDocuments()){
            Message message = messagesController.get("deleteDocDenyMessage");
            messageManager.showMessage(message,primaryStage);
        }
    }

    @FXML
    public void deleteAll() {
        ButtonType answer = MessageManagerImpl.getInstance().showConfirmMessage(primaryStage,
                "Удаление документов",
                "Удалить документы",
                "Вы действительно хотите удалить все документы?");
        if (answer == ButtonType.OK) {
            docController.getDocuments().clear();
        }
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    @FXML
    public void showFileInDesktop() {
        PaymentList list = docController.getSelectedItem();
        if (list == null) {
            MessageManagerImpl.getInstance().showInfoMessage(
                    primaryStage,
                    "Документ не выбран",
                    "Выберите документ для отображения");
            return;
        }

        FilesManager.openFileInDesktop(list.getFile());
    }

    private void saveBackupDocuments(List<PaymentList> docList) {
        ButtonType answer = MessageManagerImpl.getInstance().showConfirmMessage(getPrimaryStage(), "Сохранить резервные копии файлов?",
                "Сохранение резервных копий", "");
        if (answer == ButtonType.OK) {
            File backupFolder = PropertiesManager.getInstance().getBackupFolder();
            if ((backupFolder == null) || (!backupFolder.exists()) || (!backupFolder.isDirectory())) {
                MessageManagerImpl.getInstance().showErrorMessage(primaryStage,
                        "Ошибка резервного копирования",
                        "Резервные копии файлов не сохранены");
            }
            int docCount = 0;
            for (PaymentList list : docList) {
                String codeFolder = Integer.toString(list.getPaymentCode());
                String dateFolder = Integer.toString(list.getListNumber()).substring(0, 6);
                String fsp = File.separator;

                Path outPath = new File(backupFolder.getAbsolutePath()
                        + fsp + codeFolder
                        + fsp + dateFolder
                        + fsp + list.getFile().getName())
                        .toPath();
                docCount += FilesManager.copyFileToFolder(list.getFile(), outPath.toFile());
            }
            String title = "Сохранение резервных копий файлов";
            String message = "Сохранено файлов " + docCount + System.lineSeparator()
                    + backupFolder.getAbsolutePath();
            MessageManagerImpl.getInstance().showInfoMessage(getPrimaryStage(), title, message);
        }

  /* Старая версия, но рабочая

        if (answer == ButtonType.OK) {
            List<File> backupFiles = docList
                    .stream()
                    .map(pair -> pair.file)
                    .collect(Collectors.toList());
            File backupFolder = PropertiesManager.getInstance().getBackupFolder();
            if ((backupFolder == null) || (!backupFolder.exists()) || (!backupFolder.isDirectory())) {
                MessageManagerImpl.getInstance().showErrorMessage(primaryStage,
                        "Ошибка резервного копирования",
                        "Резервные копии файлов не сохранены");
            }

            int docCount = documentSaver.copyFilesToFolder(backupFiles, backupFolder);
            String title = "Сохранение резервных копий файлов";
            String message = "Сохранено файлов " + docCount + System.lineSeparator()
                    + backupFolder.getAbsolutePath();
            MessageManagerImpl.getInstance().showInfoMessage(getPrimaryStage(), title, message);
        }*/
    }

    public void exit() {
        System.exit(0);
    }

    @Override
    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }


    private boolean saveDocuments(File file, List<PaymentList> paymentLists) {
        try {
            documentSaver.saveDocumentGroupToFile(paymentLists, file);
        } catch (IOException e) {
            e.printStackTrace();
            MessageManagerImpl.getInstance().showErrorMessage(primaryStage,
                    "Ошибка сохранения",
                    "Ошибка в процессе сохранения документов");
            return true;
        }
        //Запрос на открытие результата:
        ButtonType res = MessageManagerImpl.getInstance().showConfirmMessage(primaryStage,
                "Открыть файл с результатом?",
                "Сохранение реестра", "");
        if (res == ButtonType.OK) {
            FilesManager.openFileInDesktop(file);
        }
        return false;
    }

    public DocumentParser getDocParserBase() {
        return docParserBase;
    }

    public void setDocParserBase(DocumentParser docParserBase) {
        this.docParserBase = docParserBase;
    }

    public DocumentParser getDocParserMail() {
        return docParserMail;
    }

    public void setDocParserMail(DocumentParser docParserMail) {
        this.docParserMail = docParserMail;
    }

    public DocumentParser getDocParserXML() {
        return docParserXML;
    }

    public void setDocParserXML(DocumentParser docParserXML) {
        this.docParserXML = docParserXML;
    }

    public DocumentSaver getDocumentSaver() {
        return documentSaver;
    }

    public void setDocumentSaver(DocumentSaver documentSaver) {
        this.documentSaver = documentSaver;
    }

    public TableController getDocController() {
        return docController;
    }

    public void setDocController(TableController docController) {
        this.docController = docController;
    }

    @Override
    public BorderPane getRootLayout() {
        return rootLayout;
    }

    public void setRootLayout(BorderPane rootLayout) {
        this.rootLayout = rootLayout;
    }


}
