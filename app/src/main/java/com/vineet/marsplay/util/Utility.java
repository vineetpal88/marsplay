package com.vineet.marsplay.util;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;

import com.cloudinary.Transformation;
import com.cloudinary.android.MediaManager;

import java.io.File;

public class Utility {
    private static Utility utility = null;
    private Context context;
    private Utility(Context context) {
        this.context = context;
    }

    public static Utility getInstance(Context context) {
        if (utility == null) {
            utility = new Utility(context);
        }
        return utility;
    }
    public String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = context.getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) {
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    public String getOriginalImageUrlThumbnail(String linkurl) {
        String originalImageUrl = "";
        if (linkurl != null && !linkurl.equalsIgnoreCase("")) {
            String fileName = new File(linkurl).getName();
            fileName = fileName.substring(0, fileName.lastIndexOf('.')) + ".jpg";
            String generate = MediaManager.get().url().transformation(new Transformation()
                    .quality(100).fetchFormat("auto")).generate(fileName);
            originalImageUrl = generate;


        }
        return originalImageUrl;
    }
}
