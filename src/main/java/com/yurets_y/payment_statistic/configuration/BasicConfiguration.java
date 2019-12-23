package com.yurets_y.payment_statistic.configuration;


import com.yurets_y.payment_statistic.controller.MainController;
import com.yurets_y.payment_statistic.controller.RootPanel;
import com.yurets_y.payment_statistic.controller.TableController;
import com.yurets_y.payment_statistic.model.parser.DocumentParser;
import com.yurets_y.payment_statistic.model.saver.DocumentSaver;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

import javax.annotation.Resource;

@Configuration
@ComponentScan(basePackages = "com.yurets_y.payment_statistic.model")
@ComponentScan(basePackages = "com.yurets_y.payment_statistic.controller")
@ImportResource(locations = {"classpath:springconfig/app-context.xml"})
public class BasicConfiguration {

    @Resource(name="documentSaverBasic")
    DocumentSaver saver;

    @Resource(name="documentParserBaseHtml")
    DocumentParser baseHTMLParser;

    @Resource(name="documentParserMailHtml")
    DocumentParser mailHtmlParser;

    @Resource(name="documentParserXml")
    DocumentParser xmlParser;

    @Bean
    public RootPanel mainController() throws Exception{
        FXMLLoader rootLoader = new FXMLLoader();
        rootLoader.setLocation(getClass().getResource("/fxml/MainView.fxml"));
        BorderPane rootLayout = rootLoader.load();
        MainController rootController = (rootLoader.getController());
        rootController.setRootLayout(rootLayout);

        FXMLLoader docPanerLoader = new FXMLLoader();
        docPanerLoader.setLocation(getClass().getResource("/fxml/DocumentTableOverview.fxml"));
        Pane docTable = docPanerLoader.load();
        TableController tableController = docPanerLoader.getController();

        rootLayout.setCenter(docTable);
        rootController.setDocController(tableController);

        rootController.setDocParserBase(baseHTMLParser);
        rootController.setDocParserMail(mailHtmlParser);
        rootController.setDocParserXML(xmlParser);
        rootController.setDocumentSaver(saver);

        return rootController;
    }


}
