package com.yurets_y.payment_statistic.model.util;

import javax.swing.*;

/**
 * Обработчик ошибок приложения
 */
public class ExceptionHandler implements Thread.UncaughtExceptionHandler{
    /*
     * Обработки ошибки
     */
    public static void handleException(Throwable e){
        JOptionPane.showMessageDialog(null,e.getMessage());
        e.printStackTrace();
    }
    /*
     * Отображения сообщения об ошибке
     */
    public static void showMessage(String message){
        JOptionPane.showMessageDialog(null,message);
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        MessageManagerImpl.getInstance().showErrorMessage(null,e.getMessage(),"");
        e.printStackTrace();
    }

}
