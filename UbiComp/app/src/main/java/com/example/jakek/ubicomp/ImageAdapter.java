package com.example.jakek.ubicomp;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.ArrayList;

/**
 * Created by jakek on 23/11/2017.
 */

class ImageAdapter extends BaseAdapter {
    private Context mContext;
    ArrayList<Uri> absPath;

    public ImageAdapter(Context c, ArrayList<Uri> absPath) {
        this.absPath = absPath;
        mContext = c;



    }
//    public void updateAbsPath(){
//        List<ShoppingReceiptData> pics = new ArrayList<>();
//        DBHandler db = new DBHandler(mContext);
//        try {
//            pics = db.getAllPics();
//        } catch (ParseException e) {
//            e.printStackTrace();
//
//        }
//        absPath = new Uri[pics.size()];
//
//        for (int i=0; i<absPath.length; i++){
//            String dir = pics.get(i).getAbsolutePath();
//            File f = new File(dir);
//            Uri imageUri = Uri.fromFile(f);
//
//            absPath[i] = imageUri;
//        }
//    }

    public int getCount() {
        return absPath.size();
    }

//    public void addItem(File image){
//        Uri adder = Uri.fromFile(image);
//        absPath.add(adder);
//    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8, 8, 8, 8);
        } else {
            imageView = (ImageView) convertView;
        }

        imageView.setImageURI(absPath.get(position));

        return imageView;
    }

    //    Drawable d =  Drawable.createFromPath("file:///data/user/0/com.example.jakek.ubicomp/files/Pictures/JPEG/_20171123_152247_1709976594343150848.jpg");
//    String dir = "/storage/emulated/0/Pictures/";
//    String path = dir+"1511445269789.jpg";
//    File f = new File(path);
//    Uri imageUri = Uri.fromFile(f);
////    Uri imgUri = Uri.parse("file:///data/user/0/com.example.jakek.ubicomp/files/JPEG_20171123_160252_.jpg");
//    Bitmap BitmapmyBitmap = BitmapFactory.decodeFile(f.getAbsolutePath());
//
//    //    int d1
//
//    // references to our images
//    public Uri[] mThumbIds = {
//            imageUri
//    };
}
