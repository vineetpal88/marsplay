package com.theartofdev.edmodo.cropper;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class CropImageActivtiyDemo extends AppCompatActivity implements
        CropImageView.OnCropImageCompleteListener, CropImageView.OnSetImageUriCompleteListener {

    private CropImageView mCropImageView;

    private View mProgressView;

    private Uri mCropImageUri;

    private TextView mProgressViewText;
    private CropImageOptions mOptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.crop_image_activtiydemo);
        mCropImageView = (CropImageView) findViewById(R.id.CropImageView);
        mProgressView = findViewById(R.id.ProgressView);
        mProgressViewText = (TextView) findViewById(R.id.ProgressViewText);
        Intent intent = getIntent();
        Uri source = intent.getParcelableExtra(CropImage.CROP_IMAGE_EXTRA_SOURCE);
        mOptions = intent.getParcelableExtra(CropImage.CROP_IMAGE_EXTRA_OPTIONS);
        if (savedInstanceState == null) {
            mCropImageView.setImageUriAsync(source);
            mProgressViewText.setText("Loading...");
            mProgressView.setVisibility(View.VISIBLE);
            mOptions.allowRotation = true;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mCropImageView.setOnSetImageUriCompleteListener(this);
        mCropImageView.setOnCropImageCompleteListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mCropImageView.setOnSetImageUriCompleteListener(null);
        mCropImageView.setOnCropImageCompleteListener(null);
    }

//    public void onCropImageClick(View view) {
//        mCropImageView.getCroppedImageAsync( 500, 500, mCropImageView.getCropShape());
//        mProgressViewText.setText("Cropping...");
//        mProgressView.setVisibility(View.VISIBLE);
//
//    }

    @Override
    public void onSetImageUriComplete(CropImageView cropImageView, Uri uri, Exception error) {
        mProgressView.setVisibility(View.INVISIBLE);
        if (error != null) {
            Log.e("Crop", "Failed to load image for cropping", error);
            Toast.makeText(this, "Something went wrong, try again", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onCropImageComplete(CropImageView view, CropImageView.CropResult result) {

    }

//    @Override
//    public void onGetCroppedImageComplete(CropImageView view, Bitmap bitmap, Exception error) {
//        mProgressView.setVisibility(View.INVISIBLE);
//        if (error == null) {
//            if (bitmap != null) {
//                mCropImageView.setImageBitmap(bitmap);
//            }
//        } else {
//            Log.e("Crop", "Failed to crop image", error);
//            Toast.makeText(this, "Something went wrong, try again", Toast.LENGTH_LONG).show();
//        }
//    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (resultCode == Activity.RESULT_OK) {
//            Uri imageUri = CropImage.getPickImageResultUri(this, data);
//
//            // For API >= 23 we need to check specifically that we have permissions to read external storage,
//            // but we don't know if we need to for the URI so the simplest is to try open the stream and see if we get error.
//            boolean requirePermissions = false;
//            if (CropImage.isReadExternalStoragePermissionsRequired(this, imageUri)) {
//
//                // request permissions and handle the result in onRequestPermissionsResult()
//                requirePermissions = true;
//                mCropImageUri = imageUri;
//                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
//            }
//
//            if (!requirePermissions) {
//                mCropImageView.setImageUriAsync(imageUri);
//                mProgressViewText.setText("Loading...");
//                mProgressView.setVisibility(View.VISIBLE);
//            }
//        }
//    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
//        if (mCropImageUri != null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//            mCropImageView.setImageUriAsync(mCropImageUri);
//            mProgressViewText.setText("Loading...");
//            mProgressView.setVisibility(View.VISIBLE);
//        } else {
//            Toast.makeText(this, "Required permissions are not granted", Toast.LENGTH_LONG).show();
//        }
//    }
}
