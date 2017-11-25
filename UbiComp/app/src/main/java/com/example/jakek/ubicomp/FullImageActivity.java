package com.example.jakek.ubicomp;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;

public class FullImageActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.full_image);

        // get intent data
//        Intent i = getIntent();

        // Selected image id
//        int position = i.getExtras().getInt("id");

        ImageView imageView = (ImageView) findViewById(R.id.full_image_view);
        Intent i = getIntent();
        String test = i.getStringExtra("id");
        ArrayList<Uri> abs = new ArrayList<>();
        Uri myUri = Uri.parse(test);

        abs.add(myUri);
        ImageAdapter imageAdapter = new ImageAdapter(this, abs);

        Log.i("round 2", test);
        Log.i("Round 3", myUri.toString());
        imageView.setImageURI(abs.get(0));
    }

    public void killPic(View view) {
        finish();
    }
}