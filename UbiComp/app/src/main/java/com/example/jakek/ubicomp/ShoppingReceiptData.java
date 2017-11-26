package com.example.jakek.ubicomp;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Tom on 22/11/2017.
 */

public class ShoppingReceiptData {
    private String date;
    private String receiptData;
    private String absolutePath;
    private double receiptTotal;


    public ShoppingReceiptData(){

    }


    public ShoppingReceiptData(String receiptData, String absolutePath, double receiptTotal){
        this.date = new SimpleDateFormat(" HH:mm dd/MM/yyyy").format(new Date());
        this.receiptData = receiptData;
        this.absolutePath = absolutePath;
        this.receiptTotal = receiptTotal;
    }

    public ShoppingReceiptData(String receiptData, String absolutePath, double receiptTotal, String date){
        this.date = date;
        this.receiptData = receiptData;
        this.absolutePath = absolutePath;
        this.receiptTotal = receiptTotal;
    }

    public String getDate(){
        return this.date;
    }

    public void setDate(){
        this.date = new SimpleDateFormat(" HH:mm dd/MM/yyyy").format(new Date());
    }
    public void setDate(String date){
        this.date = date;
    }

    public String getReceiptData(){
        return this.receiptData;
    }

    public void setReceiptData(String receiptData){
        this.receiptData = receiptData;
    }

    public String getAbsolutePath(){
        return this.absolutePath;
    }

    public void setAbsolutePath(String absolutePath){
        this.absolutePath = absolutePath;
    }

    public void setReceiptTotal(double receiptTotal){
        this.receiptTotal = receiptTotal;
    }

    public double getReceiptTotal(){
        return this.receiptTotal;
    }


}
