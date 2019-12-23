package com.yurets_y.payment_statistic.model.util;

import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Created by Yuriy on 14.08.2016.
 * Диалог для выбора файлов в системе
 * полностью завершен!!!
 *
 * @see JFileChooser
 */
public class FilesManager {
    /*
     * Новые методы основанные на javaFX платформе
     */
    public static List<File> getHTMLFilesToLoad(Stage primaryStage) {
        File defFile = PropertiesManager.getInstance().getDefaultAddFolder();
        return getFilesListToLoadFX(primaryStage, defFile, "*.html", "html files");
    }

    public static List<File> getXMLFilesToLoad(Stage primaryStage) {
        File defFile = PropertiesManager.getInstance().getDefaultAddFolder();
        return getFilesListToLoadFX(primaryStage, defFile, "*.xml", "xml files");
    }

    public static File getXMLFileToSave(Stage primaryStage, File defaultFile) {
        return getFileToSaveFX(primaryStage, defaultFile, "*.xml", "xml file");
    }

    public static File getExcelFileToLoad(Stage primaryStage){
        File defFile = PropertiesManager.getInstance().getDefaultAddFolder();
        return getFileToLoadFX(primaryStage, defFile, "*.xls", "xls excel files");
    }

    public static File getXMLFileToLoad(Stage primaryStage, File defaultFile) {
        return getFileToLoadFX(primaryStage, defaultFile, "*.xml", "xml file");
    }

    public static File getXLSFileToSave(Stage primaryStage) {
        File defSavepath = PropertiesManager.getInstance().getDefaultSaveFolder();
        return getFileToSaveFX(primaryStage, defSavepath, "*.xls", "exccel xls files");
    }

    public static File getFolderToLoad(Stage primaryStage) {
        File defaultLoadFolder = PropertiesManager.getInstance().getDefaultAddFolder();
        return getFolder(primaryStage,defaultLoadFolder);
    }

    public static void openFileInDesktop(File file) {
        Desktop desktop = null;
        if (Desktop.isDesktopSupported()) {
            desktop = Desktop.getDesktop();
        }
        try {
            assert desktop != null;
            desktop.open(file);
        } catch (IOException ioe) {
            MessageManagerImpl.getInstance().showExceptionMessage(ioe, null);
            ioe.printStackTrace();
        }
    }

    public static File getFolder(Stage primaryStage,File defaultPath){

        DirectoryChooser directoryChooser = new DirectoryChooser();
        if ((defaultPath != null)&&(defaultPath.exists())) {
            if(!defaultPath.isDirectory()){
                defaultPath = defaultPath.getParentFile();
            }
            directoryChooser.setInitialDirectory(defaultPath);
        }
        return directoryChooser.showDialog(primaryStage);
    }

    private static File getFileToSaveFX(Stage primaryStage, File defaultPath, String fileExtension, String fileDescription) {
        FileChooser fileChooser = new FileChooser();

        checkNullReferenseAndSetFilter(defaultPath, fileExtension, fileDescription, fileChooser);

        // Показываем диалог сохранения файла

        return fileChooser.showSaveDialog(primaryStage);
    }

    private static List<File> getFilesListToLoadFX(Stage primaryStage, File defaultPath, String fileExtension, String fileDescription) {

        FileChooser fileChooser = new FileChooser();

        checkNullReferenseAndSetFilter(defaultPath, fileExtension, fileDescription, fileChooser);

        return fileChooser.showOpenMultipleDialog(primaryStage);
    }

    private static void checkNullReferenseAndSetFilter(File defaultPath, String fileExtension, String fileDescription, FileChooser fileChooser) {
        if ((defaultPath != null)&&(defaultPath.exists())) {
            if(!defaultPath.isDirectory()){
                defaultPath = defaultPath.getParentFile();
            }
            fileChooser.setInitialDirectory(defaultPath);
        }
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
                fileDescription, fileExtension);
        fileChooser.getExtensionFilters().add(extFilter);
    }

    private static File getFileToLoadFX(Stage primaryStage, File defaultPath, String fileExtension, String fileDescription) {

        FileChooser fileChooser = new FileChooser();
        checkNullReferenseAndSetFilter(defaultPath, fileExtension, fileDescription, fileChooser);

        // Показываем диалог сохранения файла
        return fileChooser.showOpenDialog(primaryStage);
    }

    public static int copyFilesToFolder(List<File> files, File folder){
        int savedFiles = 0;
        for(File sortFile: files){
            Path outPath = new File(folder.getAbsolutePath() + File.separator + sortFile.getName()).toPath();
            try {
                Files.copy(sortFile.toPath(), outPath);
                savedFiles++;
            } catch (IOException e) {
                e.printStackTrace();
                ExceptionHandler.handleException(e);
            }
        }
        return savedFiles;
    }

    public static int copyFileToFolder(File file, File outFile){
        try {
            if(!outFile.getParentFile().exists()){
                Files.createDirectories(outFile.getParentFile().toPath());
            }
            Files.copy(file.toPath(), outFile.toPath());
            return 1;
        } catch (IOException e) {
            e.printStackTrace();
            ExceptionHandler.handleException(e);
            return 0;
        }
    }

}