package com.yurets_y.payment_statistic.model.saver;

import com.yurets_y.payment_statistic.model.elements.PaymentList;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

/**
 * Created by Admin on 25.02.2018.
 */
@Service("documentSaverBasic")
public class DocumentSaverImp implements DocumentSaver {
    private static final String DOC_EXTENSION = ".xls";

    /**
     * Сохранение одиночного документа в файл
     * @param payList - Представление документа в виде класса
     * @param file - файл, в который предстоит сохранение документа
     */
    @Override
    public void saveDocument(PaymentList payList, File file) throws IOException {
        if (!file.getName().toLowerCase().endsWith(DOC_EXTENSION)) {
            String fileName = file.getAbsolutePath() + DOC_EXTENSION;
            file = new File(fileName);
        }
        try (OutputStream fileOutStream = new FileOutputStream(file)) {
            Workbook workbook = new HSSFWorkbook();
            workbook = getWorkbookFromPaymentList(payList,new Pair(workbook,0)).workbook;
            workbook.write(fileOutStream);
        }
    }

    @Override
    public void saveDocumentGroupToFile(List<PaymentList> paymentLists, File file) throws IOException {
        if (!file.getName().toLowerCase().endsWith(DOC_EXTENSION)) {
            String fileName = file.getAbsolutePath() + DOC_EXTENSION;
            file = new File(fileName);
        }

        Workbook workbook = new HSSFWorkbook();
        int rowIndex = 0;
        Pair pair = new Pair(workbook,rowIndex);
        for(PaymentList payList: paymentLists){
            pair = getWorkbookFromPaymentList(payList,pair);
        }

        try (OutputStream fileOutStream = new FileOutputStream(file)) {
            pair.workbook.write(fileOutStream);
        }
    }



    private Pair getWorkbookFromPaymentList(PaymentList paymentList, Pair pair){
        if(paymentList == null){
            throw new NullPointerException("DocumentSaverImp, документ для сохранения не инициализирован");
        }

        Workbook workbook = pair.workbook;
        Sheet sheet = workbook.getSheet("sheet1") == null ?
                workbook.createSheet("sheet1"): workbook.getSheet("sheet1");

        Row row;

        for(Map.Entry<String,List<List<String>>> chartData: paymentList.getTableMap().entrySet()){
            for(List<String> chartRow: chartData.getValue()){
                int cellIndex = 0;
                row = sheet.createRow(pair.index);
                row.createCell(cellIndex++).setCellValue(paymentList.getPaymentCode());
                row.createCell(cellIndex++).setCellValue(paymentList.getListNumber());
                row.createCell(cellIndex++).setCellValue(chartData.getKey());
                for(String chartCell: chartRow){
                    //Добавление пропуска в случаи нестандартных данных
                    if((cellIndex == getLongestRow(paymentList)-2)
                        && chartRow.size() < getLongestRow(paymentList))
                    {cellIndex++;}
                    setCorrectCellValue(row.createCell(cellIndex++),chartCell);
                }
                //Запись ведомостей про платежи
                if(chartData.getKey().contains("Платіжні доручення")){
                    setCorrectCellValue(row.createCell(12),chartRow.get(3));
                }

                pair.index++;
            }
        }

        row = sheet.createRow(pair.index++);
        row.createCell(0).setCellValue(paymentList.getPaymentCode());
        row.createCell(1).setCellValue(paymentList.getListNumber());
        row.createCell(2).setCellValue("Сальдо на початок розрахункової доби");
        row.createCell(12).setCellValue(paymentList.getOpeningBalance());
        //Запись суммы проведенных платежей:
        row = sheet.createRow(pair.index++);
        row.createCell(0).setCellValue(paymentList.getPaymentCode());
        row.createCell(1).setCellValue(paymentList.getListNumber());
        row.createCell(2).setCellValue("Сума проведених платежів");
        row.createCell(12).setCellValue(paymentList.getTotalCosts());

        row = sheet.createRow(pair.index++);
        row.createCell(0).setCellValue(paymentList.getPaymentCode());
        row.createCell(1).setCellValue(paymentList.getListNumber());

        row.createCell(2).setCellValue("Сальдо на кінець розрахункової доби");
        row.createCell(12).setCellValue(paymentList.getClosingBalance());

        //Применение стиля для последних ячеек книги
        CellStyle style = pair.workbook.createCellStyle();
        style.setBorderBottom(CellStyle.BORDER_THIN);
//        row.getCell(0).setCellStyle(style);
//        row.getCell(1).setCellStyle(style);
//        row.getCell(getLongestRow(paymentList)+4).setCellStyle(style);
        row.setRowStyle(style);

        return pair;
    }
    /*
     * Установка значения ячейки в зависимости от типа данных
     */
    private void setCorrectCellValue(Cell cell, String value){
        if(value.matches("-?\\d{1,9}")){
            cell.setCellValue(Integer.parseInt(value));
        }
        else if(value.matches("-?\\d+,\\d{1,2}")){
            cell.setCellValue(Double.parseDouble(value.replaceAll(",",".")));
        }
        else if(value.matches("-?\\d+\\.\\d{1,2}")){
            cell.setCellValue(Double.parseDouble(value));
        }
        else{
            cell.setCellValue(value);
        }

    }

    private int getLongestRow(PaymentList paymentList){

        List<List> flatMap = paymentList.getTableMap()
                .values()
                .stream()
                .flatMap(list -> list.stream())
                .collect(toList());

        final Comparator<List> comp = (e1,e2)
                -> Integer.compare(e1.size(),e2.size());

        return flatMap
                .stream()
                .max(comp)
                .get().size();
    }

    private static class Pair{
        private int index;
        private Workbook workbook;
        private Pair(Workbook paymentList, int index){
            this.index = index;
            this.workbook = paymentList;
        }
    }

}
