package com.vineet.marsplay;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.mikhaellopez.circularprogressbar.CircularProgressBar;
import com.theartofdev.edmodo.cropper.CropImage;
import com.vineet.marsplay.event.ImageModel;
import com.vineet.marsplay.event.ProgressPercentage;
import com.vineet.marsplay.event.RefershEvent;
import com.vineet.marsplay.provider.ImageProvider;
import com.vineet.marsplay.service.MyUploadIntentService;
import com.vineet.marsplay.util.Constant;
import com.vineet.marsplay.util.Utility;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    public static final int REQUEST_PERMISSION_STORAGE = 0x1;
    public static final int REQUEST_PERMISSION_CAMERA = 0x2;
    public static final int REQUEST_PERMISSION_STORAGE_CROP = 0 * 3;
    private Button btn_uploadimage, btn_ViewImage;
    private CircularProgressBar circle_pgbar;
    private Toolbar toolbar_custom;
    private String userChoosenTask;
    String imageFilePath;
    private Uri mImageCaptureUri;
    private ImageProvider imageProvider;
    private ImageProvider.ImageDataBaseHelper imageDataBaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findId();
        imageProvider = new ImageProvider();
        imageDataBaseHelper = imageProvider.new ImageDataBaseHelper(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    public void findId() {
        toolbar_custom = (Toolbar) findViewById(R.id.toolbar_custom);
        btn_uploadimage = (Button) findViewById(R.id.btn_uploadimage);
        btn_ViewImage = (Button) findViewById(R.id.btn_ViewImage);
        circle_pgbar = (CircularProgressBar) findViewById(R.id.circle_pgbar);
        toolbar_custom.setTitle("Main");
        setSupportActionBar(toolbar_custom);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        circle_pgbar.setProgressMax(100.0f);
        circle_pgbar.setVisibility(View.GONE);
        btn_ViewImage.setOnClickListener(this);
        btn_uploadimage.setOnClickListener(this);
    }

    private void selectImage() {
        final CharSequence[] items = {Constant.TAKE_PHOTO, Constant.CHOOSE_PHOTO,
                Constant.CANCEL};
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals(Constant.TAKE_PHOTO)) {
                    userChoosenTask = Constant.TAKE_PHOTO;
                    openActivity();
                } else if (items[item].equals(Constant.CHOOSE_PHOTO)) {
                    userChoosenTask = Constant.CHOOSE_PHOTO;
                    openActivity();
                } else if (items[item].equals(Constant.CANCEL)) {
                    userChoosenTask = Constant.CANCEL;
                }
            }
        });
        builder.show();
    }

    private void storageRequestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_PERMISSION_STORAGE);
        }
    }

    public void storageRequestPermissionCrop() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_PERMISSION_STORAGE_CROP);
        }
    }

    public void cameraRequestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.CAMERA},
                    REQUEST_PERMISSION_CAMERA);
        }
    }


    public void openActivity() {
        if (userChoosenTask.equalsIgnoreCase(Constant.TAKE_PHOTO)) {
            cameraIntent();
        } else if (userChoosenTask.equalsIgnoreCase(Constant.CHOOSE_PHOTO)) {
            galleryIntent();
        }
    }

    private void galleryIntent() {
        Intent i = new Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, SELECT_FILE);
    }

    private File createImageFile() throws IOException {
        String timeStamp =
                new SimpleDateFormat("yyyyMMdd_HHmmss",
                        Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";
        File storageDir =
                getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
        imageFilePath = image.getAbsolutePath();

        return image;
    }

    private void cameraIntent() {
        Intent pictureIntent = new Intent(
                MediaStore.ACTION_IMAGE_CAPTURE);
        if (pictureIntent.resolveActivity(getPackageManager()) != null) {
            //Create a file to store the image
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {

            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this, getString(R.string.file_provider_authority), photoFile);
                pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        photoURI);
                startActivityForResult(pictureIntent,
                        REQUEST_CAMERA);
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_uploadimage:
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) !=
                        PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    storageRequestPermission();
                } else if (ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    cameraRequestPermission();
                } else {
                    selectImage();
                }
                break;
            case R.id.btn_ViewImage:
                startActivity(new Intent(this, ViewImageActivity.class));
                break;

            default:

                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this,
                            Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        cameraRequestPermission();
                    } else {
                        selectImage();
                    }
                } else {
                    Toast.makeText(this, "storage permission declined", Toast.LENGTH_SHORT).show();
                }
                break;
            case REQUEST_PERMISSION_CAMERA:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) !=
                            PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        storageRequestPermission();
                    } else {
                        selectImage();
                    }
                } else {
                    Toast.makeText(this, "storage permission declined", Toast.LENGTH_SHORT).show();
                }
                break;
            case REQUEST_PERMISSION_STORAGE_CROP:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openCropActivity(imageFilePath);
                } else {
                    Toast.makeText(this, "storage permission declined", Toast.LENGTH_SHORT).show();
                }
                break;

            default:
                break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE) {
                mImageCaptureUri = data.getData();
                imageFilePath = Utility.getInstance(this).getRealPathFromURI(mImageCaptureUri);
                Log.e("chk_imagepath", imageFilePath + "");
                cropActivityStart(imageFilePath);
            } else if (requestCode == REQUEST_CAMERA) {
                Log.e("chk_imagepath", imageFilePath + "");
                cropActivityStart(imageFilePath);
            } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == RESULT_OK) {
                    Uri resultUri = result.getUri();
                    String path = resultUri.getPath();
                    showProgressbar(true, 0);
                    Toast.makeText(this, "Image upload started!!!", Toast.LENGTH_SHORT).show();
                    startImageUpload(path);
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Exception error = result.getError();
                }
            }
        }
    }

    public void cropActivityStart(String imagePath) {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            storageRequestPermissionCrop();
        } else {
            openCropActivity(imagePath);
        }
    }

    public void openCropActivity(String imagePath) {
        Uri uri = Uri.fromFile(new File(imagePath));
        Log.e("chk_uri", uri.toString());
        CropImage.activity(uri)
                .start(this);
    }

    public void startImageUpload(String imagePath) {
        Intent intent = new Intent(this, MyUploadIntentService.class);
        intent.putExtra(MyUploadIntentService.KEY_IMAGE_PATH, imagePath);
        startService(intent);
    }

    private void showProgressbar(boolean show, int progress) {
        if (show) {
            circle_pgbar.setVisibility(View.VISIBLE);
            circle_pgbar.setProgress(progress);
        } else {
            circle_pgbar.setVisibility(View.GONE);
        }
    }

    private void onSuccess() {
        circle_pgbar.setProgress(0f);
        circle_pgbar.setVisibility(View.GONE);
    }

    public void onFail() {
        circle_pgbar.setProgress(0f);
        circle_pgbar.setVisibility(View.GONE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onShowPercentageProgressBar(ProgressPercentage progressPercentage) {
        Log.e("chk", "onShowPercentageProgressBar");
        try {
            showProgressbar(progressPercentage.isShow(), progressPercentage.getProgress());
        } catch (Exception exception) {
            showProgressbar(false, 0);
            Log.e("chk", exception.toString());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRefreshEvent(RefershEvent refershEvent) {
        Log.e("chk", "refershEvent");
        try {
            if (refershEvent.getStatus().equalsIgnoreCase(MyUploadIntentService.UPLOAD_SUCCESS)) {
                onSuccess();
                Toast.makeText(this, "Image upload done successfully", Toast.LENGTH_SHORT).show();
                imageDataBaseHelper.insertImageDetail(refershEvent.getImageUrl());
            } else if (refershEvent.getStatus().equalsIgnoreCase(MyUploadIntentService.UPLOAD_FAIL)) {
                onFail();
            }
        } catch (Exception exception) {
            onFail();
            Log.e("chk", exception.toString());
        }
    }


}
