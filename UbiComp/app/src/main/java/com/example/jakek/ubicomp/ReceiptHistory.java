package com.example.jakek.ubicomp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class ReceiptHistory extends AppCompatActivity implements AdapterView.OnItemClickListener {


    static final int REQUEST_IMAGE_CAPTURE = 1;

    String mCurrentPhotoPath;
    ArrayList<String> shopNames;
    List<ShopEntry> shops;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getFilesDir();
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePictureIntent() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, REQUEST_IMAGE_CAPTURE);

        }
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Toast.makeText(this, "error", Toast.LENGTH_LONG);
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt_history);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
            }
        });


        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);


        DBHandler db = new DBHandler(this);

//        List<ShopEntry> empty = db.getAllShops();
//        for (ShopEntry shop: empty)
//            db.deleteShop(shop);
//
////        Log.d("Insert: ", "Inserting ..");
//        db.addShop(new ShopEntry("Dunnes", " Cabinteely", "Socks", 3));
//        db.addShop(new ShopEntry("Dunnes", " Cabinteely", "Cheese", 1.5));
//        db.addShop(new ShopEntry("Dunnes", " Cabinteely", "Cheese", 1.5));
//        db.addShop(new ShopEntry("Dunnes", " Cabinteely", "Cheese", 1.5));
//        db.addShop(new ShopEntry("Dunnes", " Cabinteely", "Cheese", 1.5));
//        db.addShop(new ShopEntry("Dunnes", " Cabinteely", "Cheese", 1.5));
//        db.addShop(new ShopEntry("Dunnes", " Cabinteely", "Cheese", 1.5));
//        db.addShop(new ShopEntry("Dunnes", " Cabinteely", "Cheese", 1.5));
//        db.addShop(new ShopEntry("Dunnes", " Cabinteely", "Cheese", 1.5));
//        db.addShop(new ShopEntry("Dunnes", " Cabinteely", "Cheese", 1.5));
//        db.addShop(new ShopEntry("Dunnes", " Cabinteely", "Milk", 1.2));
//        db.addShop(new ShopEntry("Tesco", "Ballybrack", "Milk", 4));
//        db.addShop(new ShopEntry("Lidl", "Deansgrane", "Ham", 5));
//        db.addShop(new ShopEntry("Aldi", "Sandyford", "Chairs", 23));

        shops = db.getAllUniqueShops();
        Set<ShopEntry> uniqueShops = new LinkedHashSet<>(shops);
        shops = new ArrayList<>(uniqueShops);

        shopNames = new ArrayList<>();
        for (ShopEntry shop: shops)
            shopNames.add(shop.getName()+"\t" + shop.getAddress()+shop.getDate());

//                    +shop.getItemName()+shop.getItemPrice());

        ListView lv = (ListView)findViewById(R.id.list_receipts);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, shopNames);
        lv.setOnItemClickListener(this);
        lv.setAdapter(arrayAdapter);
        lv.setTextFilterEnabled(true);


    }

//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode == CAMERA_PIC_REQUEST) {
//            Bitmap image = (Bitmap) data.getExtras().get("data");
//            ImageView imageview = (ImageView) findViewById(R.id.ImageView01); //sets imageview as the bitmap
//            imageview.setImageBitmap(image);
//        }
//    }

    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_bar, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.ReceiptHistory:
                startActivity(new Intent(this, ReceiptHistory.class));
                return true;
            case R.id.ShoppingList:
                startActivity(new Intent(this, ShoppingList.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        Intent intent = new Intent(this, ListItemActivity.class);
        intent.putExtra("shop_name", shops.get((int)l).getName());
        intent.putExtra("shop_address", shops.get((int)l).getAddress());
        intent.putExtra("date", shops.get((int)l).getDate());

//        intent.putExtra("shop_name", shopNames.get((int)l));
        startActivity(intent);
    }
}
