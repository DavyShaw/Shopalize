package com.example.jakek.ubicomp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.Text;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class ReceiptHistory extends AppCompatActivity implements AdapterView.OnItemClickListener {


    private static final int CAMERA_PERMISSIONS_REQUEST = 1;
    private static final int PHOTO_RESULT = 2;
    private Uri imageUri;
    private TextRecognizer textRecognizer;
    private TextView scanResults;
    private static final String LOG_TAG = "Text API";

    ArrayList<String> shopNames;
    List<ShopEntry> shops;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt_history);

        scanResults = (TextView) findViewById(R.id.results);

        textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startCamera();
            }
        });


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

    private void startCamera() {
        // verify the permissions. Request if not already granted
        if (PermissionUtils.requestPermission(
                this,
                CAMERA_PERMISSIONS_REQUEST,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            File photo = new File(Environment.getExternalStorageDirectory(), "shoppingReceipt.jpg");
            imageUri = FileProvider.getUriForFile(ReceiptHistory.this,
                    BuildConfig.APPLICATION_ID + ".provider", photo);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            startActivityForResult(intent, PHOTO_RESULT);

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PHOTO_RESULT && resultCode == RESULT_OK) {
            launchMediaScanIntent();
            try {
                Bitmap bitmap = decodeBitmapUri(this, imageUri);
                if (textRecognizer.isOperational() && bitmap != null) {
                    Frame frame = new Frame.Builder().setBitmap(bitmap).build();
                    SparseArray<TextBlock> textBlocks = textRecognizer.detect(frame);
                    String blocks = "";
                    String lines = "";
                    String words = "";
                    for (int index = 0; index < textBlocks.size(); index++) {
                        //extract scanned text blocks here
                        TextBlock tBlock = textBlocks.valueAt(index);
                        blocks = blocks + tBlock.getValue() + "\n" + "\n";
                        for (Text line : tBlock.getComponents()) {
                            //extract scanned text lines here
                            lines = lines + line.getValue() + "\n";
                            for (Text element : line.getComponents()) {
                                //extract scanned text words here
                                words = words + element.getValue() + ", ";
                            }
                        }
                    }
                    if (textBlocks.size() == 0) {
                        scanResults.setText("Scan Failed: Found nothing to scan");
                    } else {
                        scanResults.setText(scanResults.getText() + lines + "\n");
                    }
                } else {
                    scanResults.setText("Could not set up the detector!");
                }
            } catch (Exception e) {
                Toast.makeText(this, "Failed to load Image", Toast.LENGTH_SHORT)
                        .show();
                Log.e(LOG_TAG, e.toString());
            }
        }
    }


    private void launchMediaScanIntent() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(imageUri);
        this.sendBroadcast(mediaScanIntent);
    }


    private Bitmap decodeBitmapUri(Context ctx, Uri uri) throws FileNotFoundException {
        int targetW = 600;
        int targetH = 600;
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(ctx.getContentResolver().openInputStream(uri), null, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;

        return BitmapFactory.decodeStream(ctx.getContentResolver()
                .openInputStream(uri), null, bmOptions);
    }


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
