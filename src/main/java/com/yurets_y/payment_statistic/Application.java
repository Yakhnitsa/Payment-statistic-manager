package com.yurets_y.payment_statistic;

import com.yurets_y.payment_statistic.configuration.BasicConfiguration;
import com.yurets_y.payment_statistic.controller.RootPanel;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import com.yurets_y.payment_statistic.model.util.ExceptionHandler;
import com.yurets_y.payment_statistic.model.util.PropertiesManager;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.IOException;

/**
 * Created by Admin on 20.02.2018.
 */
public class Application extends javafx.application.Application {
    private Stage primaryStage;
    private BorderPane rootLayout;

    public static void main(String[] args) throws IOException {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle(PropertiesManager.getInstance().getProgramName());
//        Thread.currentThread().setUncaughtExceptionHandler(new ExceptionHandler());
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle(PropertiesManager.getInstance().getProgramName());
        String url = "file:src/main/resources/icons/DocParserIcon.png";
        this.primaryStage.getIcons().add(new Image(url));


        ApplicationContext context = new AnnotationConfigApplicationContext(BasicConfiguration.class);
        RootPanel rootPanel = context.getBean("mainController",RootPanel.class);
        rootPanel.setPrimaryStage(primaryStage);
        // Отображаем сцену, содержащую корневой макет.
        Scene scene = new Scene(rootPanel.getRootLayout());
        primaryStage.setScene(scene);
        primaryStage.show();


    }

}
