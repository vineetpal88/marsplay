package com.vineet.marsplay.service;

import android.app.Activity;
import android.app.IntentService;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.ExifInterface;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.cloudinary.Transformation;
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.cloudinary.android.policy.TimeWindow;
import com.vineet.marsplay.MarsPlayApplication;
import com.vineet.marsplay.event.ProgressPercentage;
import com.vineet.marsplay.event.RefershEvent;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class MyUploadIntentService extends IntentService {

    public static final String KEY_IMAGE_PATH = "key_image_path";
    public static final String UPLOAD_SUCCESS = "success";
    public static final String UPLOAD_FAIL = "fail";
    long totalSize = 0;
    private String imagePath;
    int progress = 0;

    public MyUploadIntentService() {
        super("MyUploadIntentService");
    }

    @Override
    protected synchronized void onHandleIntent(Intent intent) {
        Log.e("chk", "myuploadintentservice called on handle intent");
        if (intent.getExtras() != null) {
            MediaManager.get().cancelAllRequests();
            imagePath = intent.getStringExtra(KEY_IMAGE_PATH);
            uploadFileUsingCloudinary(getresize(imagePath));
        }
    }

    public String getresize(String originalImage) {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/MarsPlay/Media/Marsplay Images/Sent/";
        createNoMediaFile(path);
        File mediaFile = new File(path);
        File resizedImage = null;
        try {
            resizedImage = new Resizer(this)
                    .setTargetLength(1080)
                    .setQuality(100)
                    .setOutputFormat("JPEG")
                    .setOutputDirPath(mediaFile.getAbsolutePath())
                    .setSourceImage(new File(originalImage))
                    .getResizedFile();
            getOrientationOfImage(new File(originalImage).getAbsolutePath(), resizedImage.getAbsolutePath());

        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d("imagePath", resizedImage.getPath());
        return resizedImage.getPath();
    }

    public void getOrientationOfImage(String sourceFilePath, String newFilePath) {
        ExifInterface ei = null;
        try {
            ei = new ExifInterface(sourceFilePath);
            String orientation = ei.getAttribute(ExifInterface.TAG_ORIENTATION);
            Log.e("orientation1: %s", orientation + "");
            if (ei != null) {
                ExifInterface newExif = new ExifInterface(newFilePath);
                newExif.setAttribute(ExifInterface.TAG_ORIENTATION, orientation);
                newExif.saveAttributes();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean createNoMediaFile(String path) {
        boolean isCreated = false;
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        File noMediaFile = new File(file.getAbsolutePath() + "/" + ".nomedia");
        if (noMediaFile.exists()) {
            isCreated = true;
        } else {
            try {
                noMediaFile.createNewFile();
                isCreated = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return isCreated;
    }


    public synchronized void uploadFileUsingCloudinary(final String imagePath) {
        String imageType = "image";

        Map<String, Object> options = new HashMap<>();
        options.put("resource_type", imageType);
        Log.e("imageuploadurl", imagePath);

        options.put("transformation", new Transformation().quality("auto:best"));

        MediaManager.get().upload(imagePath).constrain(TimeWindow.immediate()).options(options).callback(new UploadCallback() {
            @Override
            public synchronized void onStart(String requestId) {
                Log.d("Success", "onStart");
            }

            @Override
            public synchronized void onProgress(String requestId, long bytes, long totalBytes) {

                Log.e("imageuploadurl", requestId + "/onprogress");

                progress = (int) (bytes * 100 / totalBytes);
                EventBus.getDefault().post(
                        new ProgressPercentage(true, progress));
                Log.d("Success_Progress", "upload bytes" + bytes + " /n total bytes" + totalBytes);

            }

            @Override
            public synchronized void onSuccess(String requestId, Map resultData) {
                Log.e("imageuploadurl", requestId + "/" + resultData.toString());
                Log.d("Success", "" + resultData.toString());
                String imageUrl = resultData.get("secure_url").toString();
                Log.e("imageuploadurl", imageUrl);
                SendFileUplaodStatus(UPLOAD_SUCCESS, imageUrl);
            }

            @Override
            public synchronized void onError(String requestId, ErrorInfo error) {
                Log.d("Error", "" + error.toString());
                SendFileUplaodStatus(UPLOAD_FAIL, null);
            }

            @Override
            public synchronized void onReschedule(String requestId, ErrorInfo error) {
                Log.d("Error", "" + error.toString());
                SendFileUplaodStatus(UPLOAD_FAIL, null);
            }
        }).dispatch();


    }

    public synchronized void SendFileUplaodStatus(String status, String responseMessage) {
        MediaManager.get().cancelAllRequests();
        if (status.equalsIgnoreCase(UPLOAD_FAIL))
            showAlertDialogWithOneButton("Upload Failed!", "Oops! The adventure failed to get posted. Please try again.");
        EventBus.getDefault().post(
                new RefershEvent(status, responseMessage));

    }

    public void showAlertDialogWithOneButton(final String title, final String messageText) {
        Handler mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message message) {
                final Activity currentActivity = MarsPlayApplication.getInstance().getCurrentActivity();
                AlertDialog.Builder alert = new AlertDialog.Builder(currentActivity);
                alert.setTitle(title);
                alert.setMessage(messageText);
                alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                alert.show();
            }
        };
        Message msg = new Message();
        msg.obj = "Show dialog";
        mHandler.sendMessage(msg);
    }
}
