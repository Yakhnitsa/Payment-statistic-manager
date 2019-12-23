package com.yurets_y.payment_statistic.controller;

import com.yurets_y.payment_statistic.model.elements.PaymentList;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.util.List;


/**
 * Created by Admin on 12.04.2018.
 */
public class DocumentTableController implements TableController {

    @FXML
    private TableView<PaymentList> documentTable;
    @FXML
    private TableColumn<PaymentList, Integer> docNumberColumn;
    @FXML
    private TableColumn<PaymentList, Integer> paymentCodeColumn;
    @FXML
    private TableColumn<PaymentList, String> docNameColumn;
    @FXML
    private TableColumn<PaymentList, String> innerBalanceColumn;
    @FXML
    private TableColumn<PaymentList, String> closingBalanceColumn;
    @FXML
    private TableColumn<PaymentList, String> totalPaymentColumn;
    @FXML
    private TableColumn<PaymentList, String> fileNameColumn;

    @FXML
    private void initialize() {

        documentTable.setItems(FXCollections.observableArrayList());
        // Инициализация таблицы документов
        docNumberColumn.setCellValueFactory(cellData
                -> new SimpleIntegerProperty(cellData.getValue().getListNumber()).asObject());
        paymentCodeColumn.setCellValueFactory(cellData
                -> new SimpleIntegerProperty(cellData.getValue().getPaymentCode()).asObject());
        innerBalanceColumn.setCellValueFactory(cellData ->
                new SimpleObjectProperty<>(cellData.getValue().getOpeningBalance()).asString("%.2f"));
        closingBalanceColumn.setCellValueFactory(cellData ->
                new SimpleObjectProperty<>(cellData.getValue().getClosingBalance()).asString("%.2f"));
        totalPaymentColumn.setCellValueFactory(cellData ->
                new SimpleObjectProperty<>(cellData.getValue().getTotalCosts()).asString("%.2f"));
        fileNameColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getFile().getName()));

        documentTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        documentTable.setTableMenuButtonVisible(true);
        //TODO documentTable.setPlaceholder(getPlaceHolder());
    }

    @Override
    public List<PaymentList> getDocuments() {
        return documentTable.getItems();
    }

    @Override
    public List<PaymentList> getSelectedDocuments() {
        return documentTable
                .getSelectionModel()
                .getSelectedItems();

    }

    @Override
    public PaymentList getSelectedItem() {
        return documentTable.getSelectionModel().getSelectedItem();
    }


    @Override
    public void addDocuments(List<PaymentList> paymentLists) {
        documentTable.getItems().addAll(paymentLists);

    }

    @Override
    public boolean deleteSelectedDocuments() {
        Integer[] selected = documentTable.getSelectionModel().getSelectedIndices().toArray(new Integer[0]);
        if(selected.length <= 0){

            return false;
        }
        for(int i = selected.length -1 ; i >=0; i --){
            int current = selected[i];
            documentTable.getItems().remove(current);
        }
        return true;
    }
}
