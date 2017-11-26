package com.example.jakek.ubicomp;

/**
 * Created by jakek on 14/11/2017.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class DBHandler extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 12;
    // Database Name
    private static final String DATABASE_NAME = "shopsInfo1";
    // Contacts table name
    private static final String TABLE_SHOPS = "shops";

    // Shopping List Item Table and Column
    public static final String DATABASE_SHOPPING_LIST = "ShoppingListTable";
    public static final String DATABASE_SHOPPING_LIST_COLUMN = "ShoppingListItems";

    // Shops Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_SH_ADDR = "shop_address";
    private static final String KEY_DATE = "date";
    private static final String KEY_ITEM = "item_name";
    private static final String KEY_PRICE = "item_price";


    // Receipt Data for Searching. One string per receipt.
    private static final String TABLE_RECEIPT_DATA_SEARCH = "receiptDataTable";
    private static final String KEY_PHOTO_DIRECTORY = "photoDirectory";
    private static final String KEY_RECEIPT_DATA = "receiptData";
    private static final String KEY_RECEIPT_DATA_TOTAL = "receiptDataTotal";


    // Receipt data for predictions.
    private static final String TABLE_RECEIPT_DATA_ITEMS = "receiptDataTableWithIndividualItems";

    public DBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_SHOPS + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_NAME + " TEXT,"
                + KEY_SH_ADDR + " TEXT," + KEY_DATE + " TEXT," + KEY_ITEM + " TEXT,"
                + KEY_PRICE + " DOUBLE" + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);



        String CREATE_RECEIPT_DATA_TABLE = "CREATE TABLE " + TABLE_RECEIPT_DATA_SEARCH + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_DATE + " TEXT," +
                KEY_RECEIPT_DATA + " TEXT," + KEY_PHOTO_DIRECTORY + " TEXT," + KEY_RECEIPT_DATA_TOTAL + " DOUBLE)";

        db.execSQL(CREATE_RECEIPT_DATA_TABLE);


        String CREATE_INDIVIDUAL_RECEIPT_ITEM_TABLE = "CREATE TABLE " + TABLE_RECEIPT_DATA_ITEMS +
                "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_DATE + " TEXT," +
                KEY_ITEM + " TEXT)";

        db.execSQL(CREATE_INDIVIDUAL_RECEIPT_ITEM_TABLE);




        // create table for shopping list
        db.execSQL("CREATE TABLE " + DATABASE_SHOPPING_LIST + "( ID INTEGER PRIMARY KEY AUTOINCREMENT, " + "ShoppingListItems TEXT);");

    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
// Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SHOPS);
        db.execSQL("DROP TABLE IF EXISTS " + DATABASE_SHOPPING_LIST);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECEIPT_DATA_SEARCH);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECEIPT_DATA_ITEMS);
// Creating tables again
        onCreate(db);
    }

    // ---------------------------------------------------------------------------------------------

    public void addShop(ShopEntry shop) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, shop.getName()); // Shop Name
        values.put(KEY_SH_ADDR, shop.getAddress()); // Shop Phone Number
        values.put(KEY_DATE, shop.getDate());
        values.put(KEY_ITEM, shop.getItemName());
        values.put(KEY_PRICE, shop.getItemPrice());
// Inserting Row
        db.insert(TABLE_SHOPS, null, values);
        db.close(); // Closing database connection
    }

    // ---------------------------------------------------------------------------------------------

    public List<ShopEntry> getAllShops() {
        List<ShopEntry> shopList = new ArrayList<ShopEntry>();
// Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_SHOPS;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
// looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                ShopEntry shop = new ShopEntry();
                shop.setId(Integer.parseInt(cursor.getString(0)));
                shop.setName(cursor.getString(1));
                shop.setAddress(cursor.getString(2));
                shop.setDate(cursor.getString(3));
                shop.setItemName(cursor.getString(4));
                shop.setItemPrice(cursor.getDouble(5));
// Adding contact to list
                shopList.add(shop);
            } while (cursor.moveToNext());
        }
// return contact list
        return shopList;
    }

    // ---------------------------------------------------------------------------------------------

    public List<ShopEntry> getAllUniqueShops() {
        List<ShopEntry> shopList = new ArrayList<>();
        String selectQuery = "SELECT DISTINCT name, shop_address, date FROM " + TABLE_SHOPS;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
// looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                ShopEntry shop = new ShopEntry();
                shop.setName(cursor.getString(0));
                shop.setAddress(cursor.getString(1));
                shop.setDate(cursor.getString(2));

                shopList.add(shop);
            } while (cursor.moveToNext());
        }
// return contact list
        return shopList;
    }

    // ---------------------------------------------------------------------------------------------

    public List<ShopEntry> getItemsFromShop(String name, String address, String date) {
        List<ShopEntry> shopList = new ArrayList<>();
        String selectQuery = "SELECT item_name, item_price FROM " + TABLE_SHOPS
                + " WHERE name = '" + name + "' AND shop_address = '" + address + "' AND date = '" + date + "'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
// looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                ShopEntry shop = new ShopEntry();
                shop.setItemName(cursor.getString(0));
                shop.setItemPrice(cursor.getInt(1));

                shopList.add(shop);
            } while (cursor.moveToNext());
        }
// return contact list
        return shopList;
    }

    // ---------------------------------------------------------------------------------------------

    public int getShopsCount() {
        String countQuery = "SELECT * FROM " + TABLE_SHOPS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();
// return count
        return cursor.getCount();
    }

    // ---------------------------------------------------------------------------------------------

    public int updateShop(ShopEntry shop) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, shop.getName());
        values.put(KEY_SH_ADDR, shop.getAddress());
        values.put(KEY_DATE, shop.getDate());
        values.put(KEY_ITEM, shop.getItemName());
        values.put(KEY_PRICE, shop.getItemPrice());
// updating row
        return db.update(TABLE_SHOPS, values, KEY_ID + " = ?",
                new String[]{String.valueOf(shop.getId())});
    }

    // ---------------------------------------------------------------------------------------------

    // Deleting a shop
    public void deleteShop(ShopEntry shop) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_SHOPS, KEY_ID + " = ?",
                new String[]{String.valueOf(shop.getId())});
        db.close();
    }

    // ---------------------------------------------------------------------------------------------

    // add shopping list items to the database
    public void addShoppingItem(String shoppingItem) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();

        contentValues.put(DATABASE_SHOPPING_LIST_COLUMN, shoppingItem);

        sqLiteDatabase.insert(DATABASE_SHOPPING_LIST, null, contentValues);

        sqLiteDatabase.close();
    }

    // ---------------------------------------------------------------------------------------------

    // delete shopping list item from database
    public void deleteShoppingItem(String shoppingItem) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        sqLiteDatabase.delete(DATABASE_SHOPPING_LIST, "ShoppingListItems = ?", new String[]{shoppingItem});

        sqLiteDatabase.close();

    }

    // ---------------------------------------------------------------------------------------------

    // populate the shopping items list using an ArrayList
    public ArrayList<String> populateShoppingItemList() {
        ArrayList<String> shoppingListItems = new ArrayList<>();

        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();

        Cursor cursor = sqLiteDatabase.query(DATABASE_SHOPPING_LIST, new String[]{DATABASE_SHOPPING_LIST_COLUMN}, null, null, null, null, null);

        int index = cursor.getColumnIndex(DATABASE_SHOPPING_LIST_COLUMN);

        while (cursor.moveToNext()) {
            shoppingListItems.add(cursor.getString(index));
        }
        cursor.close();

        sqLiteDatabase.close();

        return shoppingListItems;
    }

    // ---------------------------------------------------------------------------------------------
    public int spendByMonth(String date) {
        String selectQuery = "SELECT "+KEY_RECEIPT_DATA_TOTAL+" FROM " + TABLE_RECEIPT_DATA_SEARCH
                + " WHERE date LIKE '%" + date + "'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        int total = 0;
        if (cursor.moveToFirst()) {
            do {
                total += cursor.getInt(0);
            } while (cursor.moveToNext());
        }
        return total;
    }

    public ArrayList<String> popularity(){
        String selectQuery = "SELECT "+KEY_ITEM+", COUNT("+KEY_ITEM+") FROM " + TABLE_RECEIPT_DATA_ITEMS
                + " GROUP by "+KEY_ITEM+" ORDER BY COUNT("+KEY_ITEM+") DESC";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        ArrayList<String> values = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                values.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        return values;
    }

    // ---------------------------------------------------------------------------------------------

    public void addReceiptData(ShoppingReceiptData shoppingReceiptData){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_DATE, shoppingReceiptData.getDate());
        values.put(KEY_RECEIPT_DATA, shoppingReceiptData.getReceiptData());
        values.put(KEY_PHOTO_DIRECTORY, shoppingReceiptData.getAbsolutePath());
        values.put(KEY_RECEIPT_DATA_TOTAL, shoppingReceiptData.getReceiptTotal());


        db.insert(TABLE_RECEIPT_DATA_SEARCH, null, values);
        db.close();



    }

    // ---------------------------------------------------------------------------------------------

    public void addItemsFromReceipt(ShoppingListItems shoppingListItems){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_ITEM, shoppingListItems.getItem());
        values.put(KEY_DATE, shoppingListItems.getDate());

        db.insert(TABLE_RECEIPT_DATA_ITEMS, null, values);
        db.close();

    }

    public List<ShoppingReceiptData> getAllPics() throws ParseException {
        List<ShoppingReceiptData> values = new ArrayList<>();
        String selectQuery = "SELECT "+KEY_DATE+","+KEY_RECEIPT_DATA+", "+KEY_PHOTO_DIRECTORY+" FROM " + TABLE_RECEIPT_DATA_SEARCH;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

                if (cursor.moveToFirst()) {
                    do {
                        ShoppingReceiptData pic = new ShoppingReceiptData();
                        pic.setDate(cursor.getString(0));
                        pic.setReceiptData(cursor.getString(1));
                        pic.setAbsolutePath(cursor.getString(2));
                        values.add(pic);
                    }while (cursor.moveToNext());
                }
        return values;
    }


    public List<ShoppingReceiptData> getSelectPics(String query) {
        List<ShoppingReceiptData> values = new ArrayList<>();
        String selectQuery = "SELECT "+KEY_DATE+","+KEY_RECEIPT_DATA+", "+KEY_PHOTO_DIRECTORY+
                " FROM " + TABLE_RECEIPT_DATA_SEARCH+" where "+KEY_RECEIPT_DATA+" LIKE '%"+query+"%'";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                ShoppingReceiptData pic = new ShoppingReceiptData();
                pic.setDate(cursor.getString(0));
                pic.setReceiptData(cursor.getString(1));
                pic.setAbsolutePath(cursor.getString(2));
                values.add(pic);
            }while (cursor.moveToNext());
        }
        return values;

    }

    public void removeEntry(String path) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_RECEIPT_DATA_SEARCH, KEY_PHOTO_DIRECTORY + " = ?", new String[]{path});
        db.close();
//    }

    }

//    public void deleteShop(ShopEntry shop) {
//        SQLiteDatabase db = this.getWritableDatabase();
//        db.delete(TABLE_SHOPS, KEY_ID + " = ?",
//                new String[]{String.valueOf(shop.getId())});
//        db.close();
//    }

}