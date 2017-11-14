package com.example.jakek.ubicomp;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by jakek on 14/11/2017.
 */

public class ShopEntry {
    private int id;
    private String name;
    private String address;
    private String date;
    private String itemName;
    private double itemPrice;
    public ShopEntry()
    {
    }
    public ShopEntry(String name, String address, String itemName, double itemPrice)
    {
        this.name=name;
        this.address=address;
        this.date = new SimpleDateFormat(" HH:mm dd/MM/yyyy").format(new Date());
        this.itemName = itemName;
        this.itemPrice = itemPrice;

    }
    public void setId(int id) {
        this.id = id;
    }
    public int getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }

    public void setAddress(String address) {
        this.address = address;
    }
    public String getAddress() {
        return address;
    }

    public void setDate(String date){ this.date = date; }
    public String getDate(){ return date; }

    public void setItemName(String itemName){ this.itemName = itemName; }
    public String getItemName(){ return itemName; }

    public void setItemPrice(double itemPrice){ this.itemPrice = itemPrice; }
    public double getItemPrice(){ return itemPrice; }

}

