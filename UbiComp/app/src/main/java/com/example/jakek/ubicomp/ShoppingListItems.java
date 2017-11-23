package com.example.jakek.ubicomp;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Tom on 22/11/2017.
 */

public class ShoppingListItems {
    private String date;
    private String item;


    public ShoppingListItems(){

    }

    public ShoppingListItems(String item){
        this.date = new SimpleDateFormat(" HH:mm dd/MM/yyyy").format(new Date());
        this.item = item;
    }


    public String getDate(){
        return this.date;
    }

    public void setDate(){
        this.date = new SimpleDateFormat(" HH:mm dd/MM/yyyy").format(new Date());
    }

    public String getItem(){
        return this.item;
    }

    public void setItem(String item){
        this.item = item;
    }
}
