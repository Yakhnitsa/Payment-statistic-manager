package com.yurets_y.payment_statistic.controller;

import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public interface RootPanel {
    void setPrimaryStage(Stage primaryStage);

    BorderPane getRootLayout();
}
