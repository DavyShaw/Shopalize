package com.example.jakek.ubicomp;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionRequest;
import com.google.api.services.vision.v1.VisionRequestInitializer;
import com.google.api.services.vision.v1.model.AnnotateImageRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;
import com.google.api.services.vision.v1.model.EntityAnnotation;
import com.google.api.services.vision.v1.model.Feature;
import com.google.api.services.vision.v1.model.Image;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ShoppingList extends AppCompatActivity {

    ArrayList<String> shoppingItemList;
    private static final String CLOUD_VISION_API_KEY = "AIzaSyDsXD1sQ3digY49e9bBzf2Z_B_1aRd-AhQ";
    public static final String FILE_NAME = "shoppingListItem.jpg";
    private static final String ANDROID_CERT_HEADER = "X-Android-Cert";
    private static final String ANDROID_PACKAGE_HEADER = "X-Android-Package";

    // logging errors to console
    private static final String TAG = ShoppingList.class.getSimpleName();

    // permission request codes used to retrieve results from intents
    public static final int CAMERA_PERMISSIONS_REQUEST = 0;
    public static final int CAMERA_IMAGE_REQUEST = 1;


    // instantiate shoppingListItemHelper
    DBHandler DBHandler;

    private ArrayAdapter<String> mAdapter;
    private ListView shoppingItemListView;
    ArrayList<String> values;
    Button suggest;

    // ---------------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_list);
        getSupportActionBar().setTitle("Shopping List");
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);


        // button that prompts to use the camera or text input
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.addShoppingListButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // dialog to choose between camera or text input
                AlertDialog.Builder builder = new AlertDialog.Builder(ShoppingList.this);
                builder
                        .setMessage(R.string.dialog_select_prompt)
                        .setPositiveButton(R.string.dialog_select_text, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // start text entry if the user selects this option
                                startTextEntry();
                            }
                        })
                        .setNegativeButton(R.string.dialog_select_camera, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // start the camera if the user selects this
                                startCamera();
                            }
                        });
                builder.create().show();
            }
        });


        // create database and populate shopping list ListView from database
        DBHandler = new DBHandler(this);
        shoppingItemListView = (ListView) findViewById(R.id.shopping_items_list);
        populateShoppingList();


        Button deleteButton = (Button) findViewById(R.id.remove_item_button);
//        deleteButton.setOnClickListener(new View.OnClickListener(){
//
//            @Override
//            public void onClick(View view) {
////                View parent = (View) view.getParent();
//                TextView shoppingItemTextView = parent.findViewById(R.id.shopping_list_item);
//                String shoppingItem = String.valueOf(shoppingItemTextView.getText());
//                shoppingListDatabaseHelper.deleteShoppingItem(shoppingItem);
//                populateShoppingList();
//            }
//        });
        values = DBHandler.popularity();
        setprediction();

    }

    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_bar, menu);
        return true;
    }

    // ---------------------------------------------------------------------------------------------

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.ReceiptHistory:
                startActivity(new Intent(this, ReceiptHistory.class));
                return true;
            case R.id.ShoppingList:
                startActivity(new Intent(this, ShoppingList.class));
                return true;
            case R.id.StatsPage:
                startActivity(new Intent(this, StatsActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    // ---------------------------------------------------------------------------------------------

    // method to launch the text entry method to add items to the list
    public void startTextEntry() {
        final EditText itemEdit = new EditText(ShoppingList.this);
        android.support.v7.app.AlertDialog dialog = new android.support.v7.app.AlertDialog.Builder(ShoppingList.this)
                .setTitle("Add an Item to the list")
                .setView(itemEdit)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String item = String.valueOf(itemEdit.getText());
                        SQLiteDatabase db = DBHandler.getWritableDatabase();
                        ContentValues values = new ContentValues();
                        values.put(DBHandler.DATABASE_SHOPPING_LIST_COLUMN, item);
                        db.insertWithOnConflict(DBHandler.DATABASE_SHOPPING_LIST, null, values, SQLiteDatabase.CONFLICT_REPLACE);
                        db.close();
                        populateShoppingList();
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
        dialog.show();
    }

    // ---------------------------------------------------------------------------------------------

    // creates intent to launch the camera. Passes permission checks to the PermissionUtils class
    public void startCamera() {
        // verify the permissions. Request if not already granted
        if (PermissionUtils.requestPermission(
                this,
                CAMERA_PERMISSIONS_REQUEST,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA)) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); // request launch of camera
            Uri photoUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", getCameraFile()); // get the uri of the image
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri); // assign the image to the uri, as an image cannot be retrieved from the getExtra() method
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); // the camera will be allowed to read the uri in the data sent with the intent
            startActivityForResult(intent, CAMERA_IMAGE_REQUEST); // open the activity and pass the result parameter

        }
    }

    // ---------------------------------------------------------------------------------------------

    // gets the file from the photos directory and returns the file name
    public File getCameraFile() {
        File dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return new File(dir, FILE_NAME);
    }

    // ---------------------------------------------------------------------------------------------

    // method is called when the image is returned from the camera
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // checks if the request code matches the one sent to the intent to launch the camera
        // also checks the user confirmed to select that photo
        if (requestCode == CAMERA_IMAGE_REQUEST && resultCode == RESULT_OK) {
            // gets the uri of the photo
            Uri photoUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", getCameraFile());
            uploadImage(photoUri);
        }
    }

    // ---------------------------------------------------------------------------------------------

    // method that receives the result of the permissions check
    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);


        if (requestCode == CAMERA_PERMISSIONS_REQUEST){
            if (PermissionUtils.permissionGranted(requestCode, CAMERA_PERMISSIONS_REQUEST, grantResults)) {
                startCamera();
            }

        }
    }

    // ---------------------------------------------------------------------------------------------

    // method that initiate the upload process of the image for object detection
    public void uploadImage(Uri uri) {
        if (uri != null) {
            try {
                // scale the image to save on bandwidth
                Bitmap bitmap = scaleBitmapDown(
                        MediaStore.Images.Media.getBitmap(getContentResolver(), uri),
                        1200);

                // pass the bitmap image to the cloud vision api function
                callCloudVision(bitmap);

            } catch (IOException e) {
                Log.d(TAG, "Image picking failed because " + e.getMessage());
                Toast.makeText(this, R.string.image_picker_error, Toast.LENGTH_LONG).show();
            }
        } else {
            Log.d(TAG, "Image picker returned a null image.");
            Toast.makeText(this, R.string.image_picker_error, Toast.LENGTH_LONG).show();
        }
    }

    // ---------------------------------------------------------------------------------------------

    private void callCloudVision(final Bitmap bitmap) throws IOException {
        // Update the temporary textview to inform users that the photo is uploading

        Context context = getApplicationContext();
        Toast.makeText(context, R.string.loading_message, Toast.LENGTH_LONG).show();
//        mImageDetails.setText(R.string.loading_message);

        // perform the upload in an async task
        new AsyncTask<Object, Void, String>() {
            @Override
            protected String doInBackground(Object... params) {
                try {
                    HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
                    JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

                    VisionRequestInitializer requestInitializer =
                            new VisionRequestInitializer(CLOUD_VISION_API_KEY) {

                                // used so that restricted API keys can be used on the api. Utilises SHA-1 fingerprint. Also restricted to Android apps only.
                                @Override
                                protected void initializeVisionRequest(VisionRequest<?> visionRequest)
                                        throws IOException {
                                    super.initializeVisionRequest(visionRequest);

                                    String packageName = getPackageName();
                                    visionRequest.getRequestHeaders().set(ANDROID_PACKAGE_HEADER, packageName);

                                    // signature retrieved from the PackageManagerUtils - retrieves SHA-1 signature. - restricted API
                                    String sig = com.example.jakek.ubicomp.PackageManagerUtils.getSignature(getPackageManager(), packageName);

                                    visionRequest.getRequestHeaders().set(ANDROID_CERT_HEADER, sig);
                                }
                            };

                    Vision.Builder builder = new Vision.Builder(httpTransport, jsonFactory, null);
                    builder.setVisionRequestInitializer(requestInitializer);

                    Vision vision = builder.build();

                    BatchAnnotateImagesRequest batchAnnotateImagesRequest =
                            new BatchAnnotateImagesRequest();
                    batchAnnotateImagesRequest.setRequests(new ArrayList<AnnotateImageRequest>() {{
                        AnnotateImageRequest annotateImageRequest = new AnnotateImageRequest();

                        // Add the image
                        Image base64EncodedImage = new Image();

                        // Convert the bitmap to a JPEG in case Android does not understand the format
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
                        byte[] imageBytes = byteArrayOutputStream.toByteArray();

                        // Base64 encode the JPEG
                        base64EncodedImage.encodeContent(imageBytes);
                        annotateImageRequest.setImage(base64EncodedImage);

                        // select the top rated label feature
                        annotateImageRequest.setFeatures(new ArrayList<Feature>() {{
                            Feature labelDetection = new Feature();
                            labelDetection.setType("LABEL_DETECTION");
                            labelDetection.setMaxResults(1);
                            add(labelDetection);
                        }});

                        // Add the list of one thing to the request
                        add(annotateImageRequest);
                    }});

                    Vision.Images.Annotate annotateRequest =
                            vision.images().annotate(batchAnnotateImagesRequest);
                    // Due to a bug: requests to Vision API containing large images fail when GZipped.
                    annotateRequest.setDisableGZipContent(true);
                    Log.d(TAG, "created Cloud Vision request object, sending request");

                    BatchAnnotateImagesResponse response = annotateRequest.execute();
                    return convertResponseToString(response);

                } catch (GoogleJsonResponseException e) {
                    Log.d(TAG, "failed to make API request because " + e.getContent());
                } catch (IOException e) {
                    Log.d(TAG, "failed to make API request because of other IOException " +
                            e.getMessage());
                }
                return "Cloud Vision API request failed. Check logs for details.";
            }

            protected void onPostExecute(String result) {
                populateShoppingList();
//                mImageDetails.setText(result);
            }
        }.execute();
    }

    // ---------------------------------------------------------------------------------------------

    // reduce the size of the bitmap - saves on uploading time
    public Bitmap scaleBitmapDown(Bitmap bitmap, int maxDimension) {

        int originalWidth = bitmap.getWidth();
        int originalHeight = bitmap.getHeight();
        int resizedWidth = maxDimension;
        int resizedHeight = maxDimension;

        if (originalHeight > originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = (int) (resizedHeight * (float) originalWidth / (float) originalHeight);
        } else if (originalWidth > originalHeight) {
            resizedWidth = maxDimension;
            resizedHeight = (int) (resizedWidth * (float) originalHeight / (float) originalWidth);
        } else if (originalHeight == originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = maxDimension;
        }
        return Bitmap.createScaledBitmap(bitmap, resizedWidth, resizedHeight, false);
    }

    // ---------------------------------------------------------------------------------------------

    /// convert the response from the api to a string
    private String convertResponseToString(BatchAnnotateImagesResponse response) {
        String message = "";

        List<EntityAnnotation> labels = response.getResponses().get(0).getLabelAnnotations();
        if (labels != null) {
            for (EntityAnnotation label : labels) {
                message += String.format(Locale.ENGLISH, "%s", label.getDescription());

                DBHandler.addShoppingItem(message);
            }
        } else {
            Context context = getApplicationContext();
            Toast.makeText(context, "No item was found in the picture", Toast.LENGTH_LONG).show();
        }

        return message;
    }

    // ---------------------------------------------------------------------------------------------

    // populate the ListView with shopping list items from the database
    private void populateShoppingList(){
        shoppingItemList = DBHandler.populateShoppingItemList();
        if(mAdapter == null){
            mAdapter = new ArrayAdapter<String>(this, R.layout.shopping_list_row, R.id.shopping_list_item, shoppingItemList);
            shoppingItemListView.setAdapter(mAdapter);
        }else{
            mAdapter.clear();
            mAdapter.addAll(shoppingItemList);
        }
    }

    // ---------------------------------------------------------------------------------------------


    public void deleteShoppingItem(View v){
        View parent = (View) v.getParent();
        TextView shoppingItemTextView = parent.findViewById(R.id.shopping_list_item);
        String shoppingItem = String.valueOf(shoppingItemTextView.getText());
        SQLiteDatabase db = DBHandler.getWritableDatabase();
        db.delete(DBHandler.DATABASE_SHOPPING_LIST, DBHandler.DATABASE_SHOPPING_LIST_COLUMN + " = ?",
                new String[]{shoppingItem});
        populateShoppingList();
    }

    public void setprediction(){
        Button suggest = (Button) findViewById(R.id.suggestion);
        Button removeSuggest = (Button) findViewById(R.id.removeSuggestion);
        String prediction = "";
        int i = 0;
        Log.i("SIZE", values.size()+"");
        while (prediction == ""){
            if (i == values.size()) {
                suggest.setVisibility(View.GONE);
                removeSuggest.setVisibility(View.GONE);
                break;
            }
            else if (shoppingItemList.contains(values.get(i)))
                i++;
            else
                prediction = values.get(i);
        }

        suggest.setText(prediction);
    }

    public void addPrediction(View view){
        Log.i("Clicked", "YUP");
        suggest = (Button) findViewById(R.id.suggestion);
        String message = (String) suggest.getText();
        DBHandler.addShoppingItem(message);
        mAdapter.add(message);
        mAdapter.notifyDataSetChanged();
        setprediction();
    }

    public void removePrediction(View view){
        suggest = (Button) findViewById(R.id.suggestion);
        String message = (String) suggest.getText();
        values.remove(message);
        setprediction();
    }

}