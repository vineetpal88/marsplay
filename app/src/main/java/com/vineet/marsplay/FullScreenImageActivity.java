package com.vineet.marsplay;

import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.vineet.marsplay.util.Constant;
import com.vineet.marsplay.util.TouchImageView;
import com.vineet.marsplay.util.Utility;

public class FullScreenImageActivity extends AppCompatActivity {

    private TouchImageView imageView;
    private Matrix matrix = new Matrix();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_fullscreen);
        imageView = (TouchImageView) findViewById(R.id.imageView);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String imageUrl = extras.getString(Constant.IMAGEURL, "");
            String originalImageUrlThumbnail = Utility.getInstance(this).getOriginalImageUrlThumbnail(imageUrl);
            Glide.with(this)
                    .load(Uri.parse(originalImageUrlThumbnail))
                    .placeholder(R.drawable.gallery)
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .into(imageView);
        } else {
            finish();
        }
    }

}
